package com.niyaz.chataway;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;

import static com.niyaz.chataway.Myapplication.SERVER_IP;
import static com.niyaz.chataway.Myapplication.SERVER_PORT;
import static com.niyaz.chataway.Myapplication.connectionthread;
import static com.niyaz.chataway.Myapplication.rthread;
import static com.niyaz.chataway.Myapplication.socket;

public class MainActivity extends AppCompatActivity {


    Button login, register;
    EditText uname,upass;
    Myapplication myapplication;
    public static String response,name,password;
   // public static Thread rthread,connectionthread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uname=(EditText)findViewById(R.id.uname);
        upass=(EditText)findViewById(R.id.upass);
        login=(Button)findViewById(R.id.login);
        register=(Button)findViewById(R.id.register);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        myapplication.isfirstrun=true;
        group.group_array = new ArrayList<>();
    }
    public void onclickmain(View view){
        Button b= (Button)view;
        name = uname.getText().toString();
        password=upass.getText().toString();

        connectionthread = new connection(this,socket,SERVER_IP,SERVER_PORT);
        connectionthread.run();


        if (b.getId()==R.id.login){
            if (name.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"User name or password cannot be blank",Toast.LENGTH_SHORT ).show();
            }
            else {
                myapplication.sendmessage("1|" + name + "|" + password + "|" + null);
                synchronized (rthread){
                    try {
                        rthread.wait(2000);
                        System.out.println(response);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (response.equals("true")){
                    Intent intent = new Intent(getApplicationContext(),group.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(this, "Username & Password do not match !", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (b.getId()==R.id.register){
            if (name.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"User name or password cannot be blank", Toast.LENGTH_SHORT ).show();
            }
            else {
                myapplication.sendmessage("2|" + name + "|" + password + "|" + null);
                synchronized (rthread){
                    try {
                        rthread.wait();
                        System.out.println(myapplication.serverresponse);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (myapplication.serverresponse.equals("true")){
                    Toast.makeText(this, "User Registered Successfully !", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Username Already Exist !", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}


