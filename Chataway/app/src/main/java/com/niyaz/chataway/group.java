package com.niyaz.chataway;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.niyaz.chataway.Myapplication.isgrouppage;
import static com.niyaz.chataway.Myapplication.onclick;
import static com.niyaz.chataway.Myapplication.rthread;
import static com.niyaz.chataway.Myapplication.sendmessage;

public class group extends AppCompatActivity {

    public  static Toolbar toolbar_group;
    ListView group_list;
    Myapplication myapplication;
    public static ArrayAdapter<String> adapter;
    public static ArrayList<String> group_array = new ArrayList<>();
    public static ArrayList<String> all_groups;
    public static String selectedFromList;
    public static ArrayList<String> chatlist,contacts;

    public static StringBuffer buffer;
    public static int groupsize;
    public static String groupstatus;
    public static Map<String,ArrayList> map = new HashMap<String, ArrayList>();
    public static Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group);
        toolbar_group = (Toolbar) findViewById(R.id.toolbar_group);
        setSupportActionBar(toolbar_group);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Welcome "+MainActivity.name+" to ChatAway");
        toolbar_group.setBackgroundColor(0xFF095F03);
        group_list = (ListView) findViewById(R.id.group_list);
        chatlist=new ArrayList<>();
        all_groups = new ArrayList<>();
        buffer = new StringBuffer();
        context=this;
        isgrouppage=true;
      //  toolbar_group.setSubtitle(" Welcome " + MainActivity.name + " : Select Group to Join");
        toolbar_group.setSubtitle(" Connection : UP");



        if (myapplication.isfirstrun) {

            myapplication.sendmessage("12|" + MainActivity.name + "|" + null + "|" + null); //group list for this user
            synchronized (rthread) {
                try {
                    rthread.wait(2000);
                    System.out.println("thread release : get group name");
                    adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, group_array);
                    group_list.setAdapter(adapter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            myapplication.sendmessage("4|" + MainActivity.name + "|" + null + "|" + null); // get group count
            synchronized (rthread) {
                try {
                    rthread.wait(2000);
                    System.out.println("thread release : get group count");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (int i =0 ; i<groupsize;i++){
                chatlist = new ArrayList<>();
                myapplication.sendmessage("6|" + group_array.get(i) + "|" + null + "|" + null);
                System.out.println(group_array.get(i) );
                synchronized (rthread) {
                    try {
                        rthread.wait(2000);
                        System.out.println("thread release : get chat");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                map.put(group_array.get(i),chatlist);

            }
        }
        myapplication.isfirstrun = false;


        group_list.setAdapter(adapter);
        registerForContextMenu(group_list);
        group_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (onclick) {
                    selectedFromList = (String) (group_list.getItemAtPosition(position));
                    System.out.println(selectedFromList);
                    Intent intent = new Intent(getApplicationContext(), chatpage.class);
                    intent.putExtra("groupname", selectedFromList);
                    startActivity(intent);
                }
                if (!onclick){
                    return;
                }

            }
        });

    }


    public static void toolbar_group(){

        toolbar_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar_group.setSubtitle(" Connection : UP");
                sendmessage("2|" + MainActivity.name + "|" + MainActivity.password + "|" + null);
                Myapplication.connectionthread.run();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add("List Available Contacts");
        menu.add("Leave Group");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;
        if (item.getTitle().equals("List Available Contacts")) {

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
            builderSingle.setTitle("Available Contacts");
            contacts = new ArrayList<>();
            myapplication.sendmessage("10|" + group_list.getItemAtPosition(listPosition) + "|" + null + "|" + null);
            synchronized (rthread) {
                try {
                    rthread.wait(2000);
                    System.out.println("thread release : get user list");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (int i =0;i<contacts.size();i++)
                System.out.println("contacts.get(i)"+contacts.get(i));

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,contacts);
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
        }
        if (item.getTitle().equals("Leave Group")) {
            myapplication.sendmessage("9|" + MainActivity.name + "|" + group_list.getItemAtPosition(listPosition).toString() + "|" + null);
            map.remove(group_list.getItemAtPosition(listPosition));
            adapter.remove(group_list.getItemAtPosition(listPosition).toString());
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.joingroup: {
                joingroup();
                break;
            }
            case R.id.addgroup :{
                addgroup();
                break;
            }
            case R.id.logout :{

                logout();
                Toast.makeText(this,"you pressed logout", Toast.LENGTH_SHORT).show();
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
    public void addgroup(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Add New Group");
       // alertDialog.setMessage("Enter Password");

        final EditText input = new EditText(group.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });


        alertDialog.setView(input);
       // alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("ADD",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                         String grpname = input.getText().toString();
                        if (grpname.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Enter Group Name", Toast.LENGTH_SHORT).show();
                        }
                        else {

                           myapplication.sendmessage("11|" + grpname + "|" + null + "|" + null);  ///check if group exist
                            synchronized (rthread) {
                                try {
                                    rthread.wait(2000);
                                    System.out.println("thread release : get group count");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (groupstatus.equals("true")){
                                Toast.makeText(getApplicationContext(), "Group Already Exists", Toast.LENGTH_SHORT).show();
                            }
                            else{

                                myapplication.sendmessage("3|" + grpname + "|" + MainActivity.name + "|" + null + "|" + null);
                                adapter.add(grpname);
                                chatlist=new ArrayList<String>();
                                myapplication.sendmessage("6|" + grpname + "|" + MainActivity.name + "|" + null);
                                synchronized (rthread) {
                                    try {
                                       rthread.wait(2000);
                                        System.out.println("thread release : get chat");
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                map.put(grpname,chatlist);
                            }
                        }
                    }
                });
        alertDialog.show();
    }
    public void joingroup(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Available Groups");
        myapplication.sendmessage("5|" + MainActivity.name + "|" + null + "|" + null);
        all_groups.clear();
        synchronized (rthread) {
            try {
               rthread.wait(2000);
                System.out.println("thread release : get group name");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,all_groups);

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
                        if (group_array.contains(strName)) {
                            Toast.makeText(group.this,"You are already in the group !", Toast.LENGTH_SHORT).show();
                            return;

                        }
                        chatlist=new ArrayList<String>();
                        myapplication.sendmessage("6|" + strName + "|" + null + "|" + null);
                        synchronized (rthread) {
                            try {
                             rthread.wait(2000);
                                System.out.println("thread release : get chat");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        map.put(strName,chatlist);
                        adapter.add(strName);
                    }
                });
        builderSingle.show();
    }
}
