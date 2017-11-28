package com.example.roboj.a_charm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.lzyzsd.circleprogress.DonutProgress;


public class required_course_list extends AppCompatActivity {

    private Bundle bundle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.required_course_list);

        bundle = getIntent().getExtras();


    }

}
