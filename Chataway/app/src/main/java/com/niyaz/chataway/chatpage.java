package com.niyaz.chataway;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.niyaz.chataway.Myapplication.SERVER_IP;
import static com.niyaz.chataway.Myapplication.ischatpage;
import static com.niyaz.chataway.Myapplication.sendmessage;

public class chatpage extends AppCompatActivity {

    public static Toolbar toolbar_chat;
    public static ArrayList<custommessage> chatmsges ;
    ListView msg_list;
    public static Context chatcontext;
    Button sendb;
    EditText newmsg;
    public String grp;
    Myapplication myapplication;
   // public  static ArrayAdapter<String> adapter;
    public  static customadapter adapter;
    public static boolean checkactivity = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatpage);
        chatcontext=this;
        ischatpage=true;
        chatmsges = new ArrayList<>();
        grp = getIntent().getStringExtra("groupname");
        toolbar_chat = (Toolbar)findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar_chat);
        toolbar_chat.setSubtitle(" Connection : UP");
        toolbar_chat.setBackgroundColor(0xFF095F03);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Group :" + grp);
        msg_list = (ListView)findViewById(R.id.msg_list);
        sendb = (Button)findViewById(R.id.sendb);
        newmsg = (EditText)findViewById(R.id.newmsg);
        myapplication.sendmessage("8|" + MainActivity.name + "|" + grp + "|" + null);
        group.buffer = new StringBuffer();
        System.out.println("group.group_array.size()   "+group.group_array.size());
       // adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, group.map.get(grp));
        adapter = new customadapter(getApplicationContext(),R.layout.msg_text_right);

        for (int i = 0 ;i<group.map.get(grp).size();i++){
            StringTokenizer token = new StringTokenizer(group.map.get(grp).get(i).toString(), ":");
            String user = token.nextToken();
            String msg = token.nextToken();
            System.out.println(user);
            if (user.equals(MainActivity.name)) {
                chatmsges.add(new custommessage(true,"0",msg,user));
            }
            else {
                chatmsges.add(new custommessage(false,"0",msg,user));
            }
        }
        adapter.setmessageList(chatmsges);
        msg_list.setAdapter(adapter);
    }
    public static void toolbar_chat(){

        toolbar_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar_chat.setSubtitle(" Connection : UP");
                sendmessage("2|" + MainActivity.name + "|" + MainActivity.password + "|" + null);
                Myapplication.connectionthread.run();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkactivity = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkactivity=false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showusers: {


                AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
                builderSingle.setTitle("Available Contacts");
                group.contacts = new ArrayList<>();
                myapplication.sendmessage("10|" + grp + "|" + null + "|" + null);
                synchronized (myapplication.rthread) {
                    try {
                        myapplication.rthread.wait(2000);
                        System.out.println("thread release : get user list");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                for (int i =0;i<group.contacts.size();i++)
                    System.out.println("contacts.get(i)"+group.contacts.get(i));

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,group.contacts);
                builderSingle.setNegativeButton(
                        "cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builderSingle.setAdapter(arrayAdapter,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        System.out.println(strName);
                    }
                });
                builderSingle.show();
                break;
            }
            case R.id.leavegroup:{
                myapplication.sendmessage("9|" + MainActivity.name + "|" + grp + "|" + null);
                Intent intent = new Intent(getApplicationContext(), group.class);
                group.adapter.remove(grp);
                group.map.remove(grp);
                startActivity(intent);
                break;
            }
            case R.id.logout1:{
               logout();
                break;
            }

            default :{
                System.out.println("why are you in default");
            }

        }
        return false;
    }
    public void logout(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
    public void onclicksend(View view){
        Button b = (Button)view;
        String message = newmsg.getText().toString();
        if (message.isEmpty())
            message=".";
        myapplication.sendmessage("7|" + MainActivity.name + "|" + message + "|" + grp);
        newmsg.setText("");
    }
}
