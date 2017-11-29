package com.example.roboj.a_charm;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by lwu on 11/10/2017.
 */


public class DBConnector extends AsyncTask<String,Void,String> {
    public static final String LOGIN_FILE = "login.php";
    public static final String COMPLETE_FILE = "getcomplete.php";
    public static final String DEGREE_PLAN_FILE = "getdegreeplan.php";
    public static final String RECORD_DIVIDER = "~RECORD~";
    public static final String FIELD_DIVIDER = "~FIELD~";
    Context context;
    AlertDialog alertDialog;
    private String email;
    private String password;
    private String phpFile;
    DBConnector (Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        phpFile = params[0];
        String result="";
        // DO NOT CHANGE cosc5384.us
        // replace yourTeamUsername
        String login_url = "http://cosc5384.us/teamthree/"+phpFile;
        try {
            email = params[1];
            password = params[2];
            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"
                    +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String line="";
            while((line = bufferedReader.readLine())!= null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(String result) {

        alertDialog.setMessage(result);
        //alertDialog.show();
        //if connection error occurs, throw an error message

//            Log.i("abcxyz", result);
        if(phpFile.contains(LOGIN_FILE)) {
            if (!result.contains("true")) {
                Toast toast = Toast.makeText(context, "Login Error", Toast.LENGTH_SHORT);
                View view = toast.getView();
                view.setBackgroundResource(android.R.drawable.toast_frame);

//Get the TextView for the toast message so you can customize
                TextView toastMessage = (TextView) view.findViewById(android.R.id.message);

//Set background color for the text.
                toastMessage.setBackgroundColor((Color.parseColor("#646464")));
                toast.show();
            }

            //if connection succeeds, move to your next activity
            else {
                Toast toast = Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT);
                View view = toast.getView();
                view.setBackgroundResource(android.R.drawable.toast_frame);

//Get the TextView for the toast message so you can customize
                TextView toastMessage = (TextView) view.findViewById(android.R.id.message);

//Set background color for the text.
                toastMessage.setBackgroundColor((Color.parseColor("#646464")));
                toast.show();
                Intent intent = new Intent(context, StudentAdvising.class);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                context.startActivity(intent);
            }
        }



    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
