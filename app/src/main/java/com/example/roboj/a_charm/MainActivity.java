package com.example.roboj.a_charm;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ResourceBundle;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private Button b_login;
    private EditText tf_email, tf_password;
    private TextView tv_forgotPassword;
    private ConnectSQL sql_connector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        b_login = (Button) findViewById(R.id.b_login);
        tf_email = (EditText) findViewById(R.id.tf_email);
        tf_password = (EditText) findViewById(R.id.tf_password);
        tv_forgotPassword = (TextView) findViewById(R.id.tv_forgotPassword);
        tv_forgotPassword.setMovementMethod(LinkMovementMethod.getInstance());

        // Uncomment next line to test Student Advisory page without log-in each time
       //OpenStudentAdvisory();

        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sql_connector = new ConnectSQL();
                String email = tf_email.getText().toString();
                String password = tf_password.getText().toString();

                if (!email.contains("@uttyler.edu"))
                {
                    email+="@uttyler.edu";
                }

                //make a connection, if valid user/password then open Advisory page
                String type = "login";
                DBConnector dbConnector = new DBConnector(MainActivity.this);
                dbConnector.execute(DBConnector.LOGIN_FILE, email, password);

            }
        });


    }


    @Override
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
