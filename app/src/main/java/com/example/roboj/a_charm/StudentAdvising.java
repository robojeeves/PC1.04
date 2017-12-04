package com.example.roboj.a_charm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.concurrent.ExecutionException;

public class StudentAdvising extends AppCompatActivity {

    private DonutProgress dp_completionStatus;
    private TextView tv_completionStatus, tv_core, tv_degreeCore, tv_lowerDivision, tv_upperDivision, tv_supportCourses;
    private ProgressBar pb_core, pb_degreeCore, pb_lowerDivision, pb_upperDivision, pb_supportCourses;
    private Bundle bundle;
    private String email, password, major;
    private String[][] completeCourseInfo;
    private String[][] degreePlanInfo;
    private ArrayList<String> notYetTakenInfo, takenInfo;

    //For splitting the notYetTakenInfo and takenInfo arrays into different sections
    private ArrayList<String> majReq, lowerDivReq, upperDivReq, coreReq, supportReq;
    private ArrayList<String> majComp, lowerDivComp, upperDivComp, coreComp, supportComp;

    private int creditsEarned, creditsRequired = 120;
    private double majorRatio, coreRatio, upperRatio, lowerRatio, supportRatio;
    private double overallRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        bundle = getIntent().getExtras();
        email = bundle.getString("email");
        password = bundle.getString("password");

        fetchCourses();
        progressView();


    }

    private void progressView(){

        setContentView(R.layout.student_advisory_scroll_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tv_viewTitle = (TextView) findViewById(R.id.tv_viewTitle);
        tv_viewTitle.setText("Overview");
        // toolbar.setTitle("");
        // toolbar.setSubtitle("");


        // Create references to all progress bars
        dp_completionStatus = (DonutProgress) findViewById(R.id.dpCompletionStatus );
        pb_core = (ProgressBar) findViewById(R.id.pb_CoreCurriculum);
        pb_degreeCore = (ProgressBar) findViewById(R.id.pb_DegreeCore);
        pb_lowerDivision = (ProgressBar) findViewById(R.id.pb_LowerDivision);
        pb_upperDivision = (ProgressBar) findViewById(R.id.pb_UpperDivision);
        pb_supportCourses = (ProgressBar) findViewById(R.id.pb_SupportCourses);

        // Create references to Text Views
        tv_completionStatus = (TextView) findViewById(R.id.tv_CompletionStatus);
        tv_core = (TextView) findViewById(R.id.tv_CoreCurriculum);
        tv_degreeCore = (TextView) findViewById(R.id.tv_DegreeCore);
        tv_lowerDivision = (TextView) findViewById(R.id.tv_LowerDivision);
        tv_upperDivision = (TextView) findViewById(R.id.tv_UpperDivision);
        tv_supportCourses = (TextView) findViewById(R.id.tv_SupportCourses);

        //Set Values for each progress bar
        String progress = Integer.toString((int)(overallRatio * 100)); // Sets progress for completion status bar
        dp_completionStatus.setDonut_progress(progress);
        dp_completionStatus.setText(progress + "%");

        //Assign values to the progress bars
        pb_core.setProgress((int)(coreRatio * 100));
        pb_degreeCore.setProgress((int)(majorRatio * 100));
        pb_lowerDivision.setProgress((int)(lowerRatio * 100));
        pb_upperDivision.setProgress((int)(upperRatio * 100));
        pb_supportCourses.setProgress((int)(supportRatio * 100));

        // set labels for each bar
        tv_completionStatus.setText("Completion Status\n " + creditsEarned + "/" + creditsRequired + " Hrs");
        tv_core.setText(R.string.core);
        tv_core.append(" " + Integer.toString((int)(coreRatio * 100)) + "%");
        tv_degreeCore.setText(major);//R.string.degreeCore);
        tv_degreeCore.append(" Core " + Integer.toString((int)(majorRatio * 100)) + "%");
        tv_lowerDivision.setText(R.string.lowerDivision);
        tv_lowerDivision.append(" " + Integer.toString((int)(lowerRatio * 100)) + "%");
        tv_upperDivision.setText(R.string.upperDivision);
        tv_upperDivision.append(" " + Integer.toString((int)(upperRatio * 100)) + "%");
        tv_supportCourses.setText(R.string.supportCourses);
        tv_supportCourses.append(" " + Integer.toString((int)(supportRatio * 100)) + "%");
    }

    private void completedView(){
        setContentView(R.layout.completed_course_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tv_viewTitle = (TextView) findViewById(R.id.tv_viewTitle);
        tv_viewTitle.setText("Complete Courses");

        String [] takenArray = takenInfo.toArray(new String[takenInfo.size()]);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, R.layout.simple_listview, takenArray);
        ListView listView = (ListView) findViewById(R.id.lv_completed_courses);
        listView.setAdapter(adapter);


    }
    private void remainingView(){
        setContentView(R.layout.required_course_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tv_viewTitle = (TextView) findViewById(R.id.tv_viewTitle);
        tv_viewTitle.setText("Requirements");

        String [] notYetTakenArray = notYetTakenInfo.toArray(new String[notYetTakenInfo.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notYetTakenArray);
        ListView listView = (ListView) findViewById(R.id.req_course_list);
        listView.setAdapter(adapter);

    }

    private void fetchCourses()
    {
        DBConnector dbConnectorC = new DBConnector(this);
        DBConnector dbConnectorD = new DBConnector(this);
        completeCourseInfo = null;
        degreePlanInfo = null;

        String completeCoursesResult = null;
        String degreePlanResult = null;
        try {
            completeCoursesResult = dbConnectorC.execute(DBConnector.COMPLETE_FILE, email, password).get();
            degreePlanResult = dbConnectorD.execute(DBConnector.DEGREE_PLAN_FILE, email, password).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Log.e("The raw data: ", completeCoursesResult);
        //Log.e("The raw data: ", degreePlanResult);

        // Put complete course info into array
        if (completeCoursesResult.contains(DBConnector.RECORD_DIVIDER))
        {
            String[] completeCourses = completeCoursesResult.split(DBConnector.RECORD_DIVIDER);
            completeCourseInfo = new String[completeCourses.length][];

            for (int i = 0; i < completeCourses.length; i++)
            {
                completeCourseInfo[i] = completeCourses[i].split(DBConnector.FIELD_DIVIDER);
            }
        }

        // Put degree plan course info into array
        if (degreePlanResult.contains(DBConnector.RECORD_DIVIDER))
        {
            String degreePlan[] = degreePlanResult.split(DBConnector.RECORD_DIVIDER);
            degreePlanInfo = new String[degreePlan.length][];

            for (int x = 0; x < degreePlan.length; x++)
            {
                degreePlanInfo[x] = degreePlan[x].split(DBConnector.FIELD_DIVIDER);

                //Uncomment the following lines to display raw data from server
                //for (int i = 0; i < degreePlanInfo[x].length; i++)
                //{
                //    Log.e(degreePlanInfo[x][0], degreePlanInfo[x][i]);
                //}
            }
        }

        double creditsCount = 0.0;
        int majorCount = 0, lowerDivCount = 0, upperDivCount = 0, coreCount = 0, supportCount = 0;
        int majorComplete = 0, lowerDivComplete = 0, upperDivComplete = 0, coreComplete = 0, supportComplete = 0;

        // Counting degree plan courses and credits loop
        if (degreePlanInfo != null && degreePlanInfo.length > 4)
        {
            major = degreePlanInfo[0][4];
            for (int i = 0; i < degreePlanInfo.length; i++)
            {
                if (degreePlanInfo[i][3].equalsIgnoreCase("RQ 1156"))
                    majorCount++;
                else if (degreePlanInfo[i][3].equalsIgnoreCase("RQ 1157"))
                    lowerDivCount++;
                else if (degreePlanInfo[i][3].equalsIgnoreCase("RQ 1158"))
                    upperDivCount++;
                else if (degreePlanInfo[i][3].equalsIgnoreCase("RQ 1159"))
                    coreCount++;
                else if (degreePlanInfo[i][3].equalsIgnoreCase("RQ 1160"))
                    coreCount++;
                else if (degreePlanInfo[i][3].equalsIgnoreCase("RQ 1161"))
                    supportCount++;
                else if (degreePlanInfo[i][3].equalsIgnoreCase("RQ 1162"))
                    supportCount++;
            }
        }
        // Checking complete courses loop
        notYetTakenInfo = new ArrayList<>();
        takenInfo = new ArrayList<>();

        //To be used in the notYetTakenInfo array list
        majReq = new ArrayList<>();
        upperDivReq = new ArrayList<>();
        lowerDivReq = new ArrayList<>();
        coreReq = new ArrayList<>();
        supportReq = new ArrayList<>();

        //To be used in the takenInfo array list
        majComp = new ArrayList<>();
        upperDivComp = new ArrayList<>();
        lowerDivComp = new ArrayList<>();
        coreComp = new ArrayList<>();
        supportComp = new ArrayList<>();

        if (completeCourseInfo != null && degreePlanInfo != null)
        {
            for (int j = 0; j < degreePlanInfo.length; j++)
            {
                boolean taken = false;
                for (int i = 0; i < completeCourseInfo.length; i++)
                {
                    // Entry in complete course info matches entry in degree plan info
                    if (completeCourseInfo[i][0].equalsIgnoreCase(degreePlanInfo[j][0]))
                    {
                        if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1156"))
                            majorComplete++;
                        else if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1157"))
                            lowerDivComplete++;
                        else if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1158"))
                            upperDivComplete++;
                        else if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1159"))
                            coreComplete++;
                        else if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1160"))
                            coreComplete++;
                        else if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1161"))
                            supportComplete++;
                        else if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1162"))
                            supportComplete++;
                        taken = true;
                        creditsCount += Double.parseDouble(degreePlanInfo[j][2]);
                    }
                }
                if (!taken){
                    //notYetTakenInfo.add(degreePlanInfo[j][0] + "     " + degreePlanInfo[j][1]);

                    //Testing to see about about adding splits in requirement course screen
                    if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1156"))
                        majReq.add(degreePlanInfo[j][0] + "     " + degreePlanInfo[j][1]);
                    else if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1157"))
                        lowerDivReq.add(degreePlanInfo[j][0] + "     " + degreePlanInfo[j][1]);
                    else if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1158"))
                        upperDivReq.add(degreePlanInfo[j][0] + "     " + degreePlanInfo[j][1]);
                    else if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1159") || degreePlanInfo[j][3].equalsIgnoreCase("RQ 1160"))
                        coreReq.add(degreePlanInfo[j][0] + "     " + degreePlanInfo[j][1]);
                    else if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1161") || degreePlanInfo[j][3].equalsIgnoreCase("RQ 1162"))
                        supportReq.add(degreePlanInfo[j][0] + "     " + degreePlanInfo[j][1]);
                }
                else {
                    //takenInfo.add(degreePlanInfo[j][0] + "     " + degreePlanInfo[j][1]);
                    if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1156"))
                        majComp.add(degreePlanInfo[j][0] + "     " + degreePlanInfo[j][1]);
                    else if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1157"))
                        lowerDivComp.add(degreePlanInfo[j][0] + "     " + degreePlanInfo[j][1]);
                    else if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1158"))
                        upperDivComp.add(degreePlanInfo[j][0] + "     " + degreePlanInfo[j][1]);
                    else if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1159") || degreePlanInfo[j][3].equalsIgnoreCase("RQ 1160"))
                        coreComp.add(degreePlanInfo[j][0] + "     " + degreePlanInfo[j][1]);
                    else if (degreePlanInfo[j][3].equalsIgnoreCase("RQ 1161") || degreePlanInfo[j][3].equalsIgnoreCase("RQ 1162"))
                        supportComp.add(degreePlanInfo[j][0] + "     " + degreePlanInfo[j][1]);
                }
            }

            //Adds the different sections to the required courses screen
            notYetTakenInfo.add("MAJOR CLASSES");
            notYetTakenInfo.addAll(majReq);
            notYetTakenInfo.add("\nLOWER DIVISION CLASSES");
            notYetTakenInfo.addAll(lowerDivReq);
            notYetTakenInfo.add("\nUPPER DIVISION CLASSES");
            notYetTakenInfo.addAll(upperDivReq);
            notYetTakenInfo.add("\nCORE CLASSES");
            notYetTakenInfo.addAll(coreReq);
            notYetTakenInfo.add("\nSUPPORT CLASSES");
            notYetTakenInfo.addAll(supportReq);
            //Adds extra lines for better spacing at bottom of screen
            notYetTakenInfo.add("");
            notYetTakenInfo.add("");
            notYetTakenInfo.add("");

            //Adds the different sections to the completed courses screen
            takenInfo.add("MAJOR CLASSES");
            takenInfo.addAll(majComp);
            takenInfo.add("\nLOWER DIVISION CLASSES");
            takenInfo.addAll(lowerDivComp);
            takenInfo.add("\nUPPER DIVISION CLASSES");
            takenInfo.addAll(upperDivComp);
            takenInfo.add("\nCORE CLASSES");
            takenInfo.addAll(coreComp);
            takenInfo.add("\nSUPPORT CLASSES");
            takenInfo.addAll(supportComp);
            //Adds extra lines for better spacing at bottom of screen
            takenInfo.add("");
            takenInfo.add("");
            takenInfo.add("");

        }

        // Set credits strings
        creditsEarned = (int) creditsCount;
        // Finally, set progress bar ratios
        // Keep 1.0 or lower, don't divide by zero
        majorRatio = Math.min(1.0, (double)majorComplete / Math.max(1, majorCount));
        coreRatio = Math.min(1.0, (double)coreComplete / Math.max(1, coreCount));
        lowerRatio = Math.min(1.0, (double)lowerDivComplete / Math.max(1, lowerDivCount));
        upperRatio = Math.min(1.0, (double)upperDivComplete / Math.max(1, upperDivCount));
        supportRatio = Math.min(1.0, (double)supportComplete / Math.max(1, supportCount));
        overallRatio = Math.min(1.0, (double)(majorComplete + coreComplete + lowerDivComplete
                + upperDivComplete + supportComplete) /
                Math.max(1, creditsRequired));
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.remaining_courses) {
            remainingView();
        }
        else if (id == R.id.completed_course_list){
            completedView();
        }
        else if (id == R.id.overview){
            progressView();
        }

        return super.onOptionsItemSelected(item);
    }



}
