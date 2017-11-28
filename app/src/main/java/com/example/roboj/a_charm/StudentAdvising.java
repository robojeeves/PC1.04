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
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, takenArray);
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
        Log.e("The raw data: ", completeCoursesResult);
        Log.e("The raw data: ", degreePlanResult);

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
                if (!taken)
                    notYetTakenInfo.add(degreePlanInfo[j][0] + "     " + degreePlanInfo[j][1]);
                else
                    takenInfo.add(degreePlanInfo[j][0] + "     " + degreePlanInfo[j][1]);
            }
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
/*
package com.example.roboj.a_charm;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.concurrent.ExecutionException;

public class StudentAdvising extends AppCompatActivity {

    private DonutProgress dp_completionStatus;
    private TextView tv_completionStatus, tv_core, tv_degreeCore, tv_lowerDivision, tv_upperDivision, tv_supportCourses;
    private ProgressBar pb_core, pb_degreeCore, pb_lowerDivision, pb_upperDivision, pb_supportCourses;
    private Bundle bundle;
    private String email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_advisory_scroll_view);

        bundle = getIntent().getExtras();
        email = bundle.getString("email");
        password = bundle.getString("password");

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

        fetchCourses();
        fetchComplete();
        updateProgress();
    }

    private void fetchCourses()
    {
        DBConnector dbConnector = new DBConnector(this);
        String data = null;
        String dataDump = "";
        int creditHrs = 0;
        try {
            data = dbConnector.execute(DBConnector.DEGREE_PLAN_FILE, email, password).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.e("The raw data: ", data);
        String classesrequired[] =  data.split("~RECORD~");
        String classinfo[][] = new String[classesrequired.length][];
        for(int x = 0; x < classesrequired.length; x++)
        {
            classinfo[x] = classesrequired[x].split("~FIELD~");

            if (classinfo[x].length >= 4 && classinfo[x][2].contains(".")) {
                creditHrs += Character.getNumericValue(classinfo[x][2].charAt(0));
                dataDump += "\n" + classinfo[x][0] + " " + classinfo[x][1];
            }
            for (int i = 0; i < classinfo[x].length; i++) {
                //Log.e(classinfo[x][0], classinfo[x][i]);
            }
            //Log.e("Credit Hours", "CH = " + creditHrs);
        }
        showAlertbox(dataDump);
    }

    private void fetchComplete()
    {
        DBConnector dbConnector = new DBConnector(this);
        String completedData = null;
        try {
            completedData = dbConnector.execute(DBConnector.COMPLETE_FILE, email, password).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.e("The raw data: ", completedData);
        String classestaken[] =  completedData.split("~RECORD~");
        String completedinfo[][] = new String[classestaken.length][];
        for(int x = 0; x < classestaken.length; x++)
        {
            completedinfo[x] = classestaken[x].split("~FIELD~");
            for (int i = 0; i < completedinfo[x].length; i++) {
                Log.e(completedinfo[x][0], completedinfo[x][i]);
            }
        }
    }

    private void updateProgress() {
        //Set Values for each progress bar
        String progress = Integer.toString(56); // Sets progress for completion status bar
        int core = 100, degreeCore = 85, lowerDivision = 50, upperDivision = 35, supportCourses = 45;
        dp_completionStatus.setDonut_progress(progress);
        dp_completionStatus.setText(progress + "%");

        //Assign values to the progress bars
        pb_core.setProgress(core);
        pb_degreeCore.setProgress(degreeCore);
        pb_lowerDivision.setProgress(lowerDivision);
        pb_upperDivision.setProgress(upperDivision);
        pb_supportCourses.setProgress(supportCourses);

        // set labels for each bar
        tv_completionStatus.setText("Completion Status\n 67/120 Hrs");
        tv_core.setText(R.string.core);
        tv_core.append(" " + Integer.toString(core) + "%");
        tv_degreeCore.setText(R.string.degreeCore);
        tv_degreeCore.append(" " + Integer.toString(degreeCore) + "%");
        tv_lowerDivision.setText(R.string.lowerDivision);
        tv_lowerDivision.append(" " + Integer.toString(lowerDivision) + "%");
        tv_upperDivision.setText(R.string.upperDivision);
        tv_upperDivision.append(" " + Integer.toString(upperDivision) + "%");
        tv_supportCourses.setText(R.string.supportCourses);
        tv_supportCourses.append(" " + Integer.toString(supportCourses) + "%");


    }

    public void showAlertbox(String title) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_info);
        dialog.setCanceledOnTouchOutside(false);
        TextView alertbox_title = (TextView) dialog
                .findViewById(R.id.alertbox_title);
        alertbox_title.setText(title);

        Button yes = (Button) dialog.findViewById(R.id.alertbox_yes);
        Button no = (Button) dialog.findViewById(R.id.alertbox_no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //code the functionality when YES button is clicked
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //code the functionality when NO button is clicked
            }
        });

        dialog.show();
    }
}
*/