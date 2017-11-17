package com.example.roboj.a_charm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

public class StudentAdvising extends AppCompatActivity {

    private DonutProgress dp_completionStatus;
    private TextView tv_completionStatus, tv_core, tv_degreeCore, tv_lowerDivision, tv_upperDivision, tv_supportCourses;
    private ProgressBar pb_core, pb_degreeCore, pb_lowerDivision, pb_upperDivision, pb_supportCourses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_advisory);

        setContentView(R.layout.student_advisory);

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
}
