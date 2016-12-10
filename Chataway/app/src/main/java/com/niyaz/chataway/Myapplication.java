package com.niyaz.chataway;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.niyaz.chataway.Myapplication.br;
import static com.niyaz.chataway.Myapplication.bw;
import static com.niyaz.chataway.Myapplication.connectionthread;
import static com.niyaz.chataway.Myapplication.ischatpage;
import static com.niyaz.chataway.Myapplication.isgrouppage;
import static com.niyaz.chataway.Myapplication.onclick;
import static com.niyaz.chataway.Myapplication.rthread;
import static com.niyaz.chataway.Myapplication.sendmessage;
import static com.niyaz.chataway.Myapplication.serverresponse;
import static com.niyaz.chataway.Myapplication.text1;
import static com.niyaz.chataway.Myapplication.toolbarbreak;

/**
 * Created by niyaz on 2016-11-28.
 */

public class Myapplication extends Application {
    public static boolean isfirstrun,ischatpage,isgrouppage,onclick;
    public static Thread rthread,connectionthread;
    public static Socket socket;
    public static int SERVER_PORT;
    public static String SERVER_IP;
    public static BufferedReader br;
    public static BufferedWriter bw;
    public static String serverresponse,text1,text2;

    @Override
    public void onCreate(){
        super.onCreate();
        SERVER_IP = "10.20.247.251";
        SERVER_PORT= 62000;
        isfirstrun=true;
        ischatpage=false;
        isgrouppage=false;
        onclick=true;

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // connectionthread = new connection(this,socket,SERVER_IP,SERVER_PORT);
       //  connectionthread.run();
    }
    public static void toolbarbreak(){
        group.toolbar_group.setSubtitle("test");
    }
    public static void toolbarback(){

    }
    public static void sendmessage(String message){
        try {
            bw.write(message);
            bw.newLine();
            bw.flush();
            System.out.println("Sent : " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class connection extends Thread{
    private Socket socket;
    private String serverip;
    private int serverport;
    private Context context;

    public connection(Context context,Socket socket,String Serverip,int serverport){
        this.socket=socket;
        this.serverip=Serverip;
        this.serverport=serverport;
        this.context=context;
    }
    public void run(){

        while (true){
            try {
                socket=new Socket(serverip,serverport);
                System.out.println("Connection Established");

                onclick=true;
                if(isgrouppage) {
                    group.toolbar_group.setBackgroundColor(0xFF095F03);
                }
                if(ischatpage){
                    chatpage.toolbar_chat.setBackgroundColor(0xFF095F03);

                }


                bw= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
                rthread = new receive(context,socket,br);
                rthread.start();
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
class receive extends Thread{
    private Socket socket;
    private BufferedReader br;
    private Handler handler = new Handler();
    String message;
    private Context context;
    public receive(Context context, Socket socket, BufferedReader br){
        this.socket = socket;
        this.br = br;
        this.context=context;
    }
    public void run(){
        while (true) {
            try {
              if( (message=br.readLine())==null){
                  handler.post(new Runnable() {
                      @Override
                      public void run() {
                          try {
                              bw.close();
                              br.close();
                          } catch (IOException e) {
                              e.printStackTrace();
                          }
                          System.out.println(ischatpage+"    "+isgrouppage);
                          onclick=false;
                          Toast.makeText(context,"Connection Down ! Wait for Connection to be UP",Toast.LENGTH_LONG).show();
                            if(isgrouppage) {
                                group.toolbar_group.setSubtitle("Connection Down..Click to connect !");
                                group.toolbar_group.setBackgroundColor(0xffff0000);
                                group.toolbar_group();
                            }
                            if(ischatpage){
                                chatpage.toolbar_chat.setSubtitle("Connection Down..Click to connect !");
                                chatpage.toolbar_chat.setBackgroundColor(0xffff0000);
                                chatpage.toolbar_chat();
                          }
                      }
                  });
                  return;
              }
                    System.out.println("Received message : " + message);
                    StringTokenizer token = new StringTokenizer(message, "|");
                    String a = token.nextToken();
                    final String b = token.nextToken();
                    final String c = token.nextToken();
                    final String d = token.nextToken();
                    switch (a) {
                        case "1": // get true or false from server
                            synchronized (this) {
                                MainActivity.response = b;
                                notify();
                                System.out.println("notified");
                            }
                            break;
                        case "2": // get group list from server
                            synchronized (this) {
                                System.out.println(b);
                                if (b.equals("end")) {
                                    notify();
                                    System.out.println("notified");
                                    break;
                                }
                                group.all_groups.add(b);
                            }
                            break;
                        case "3": // get users in the group
                            synchronized (this) {
                                System.out.println(b);
                                if (b.equals("end")) {
                                    notify();
                                    System.out.println("notified");
                                    break;
                                }
                                group.contacts.add(b);
                            }
                            break;
                        case "4":// get group size
                            synchronized (this) {
                                group.groupsize = Integer.valueOf(b);
                                notify();
                                System.out.println("notified");
                            }
                            break;
                        case "5": //  get chat messages from dB
                            synchronized (this) {
                                System.out.println(b);
                                if (b.equals("end")) {
                                    notify();
                                    System.out.println("notified");
                                    break;
                                }
                                group.chatlist.add(b);

                            }

                            break;
                        case "6":// receive real time chat
                            final String temp = b;
                            final String t = c;
                            synchronized (this) {
                                System.out.println("group_array.size()" + group.group_array.size());

                                for (int k = 0; k < group.group_array.size(); k++) {
                                    if (c.equals(group.group_array.get(k))) {

                                        System.out.println(chatpage.checkactivity);
                                        if (chatpage.checkactivity) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    System.out.println(d.equals(MainActivity.name));
                                                    //  chatpage.adapter.add(temp);
                                                    if (d.equals(MainActivity.name)) {
                                                        System.out.println("I sent");
                                                        chatpage.adapter.add(new custommessage(true, "0", temp, d));

                                                    } else {
                                                        System.out.println("who sent");
                                                        chatpage.adapter.add(new custommessage(false, "0", temp, d));
                                                    }
                                                    //   chatpage.adapter.setmessageList(chatpage.chatmsges);

                                                }
                                            });
                                            for (int j = 0; j < group.group_array.size(); j++) {
                                                if (c.equals(group.group_array.get(j))) {
                                                    ArrayList<String> temp_array = new ArrayList<>();
                                                    temp_array = group.map.get(group.group_array.get(j));
                                                    temp_array.add(d + ":" + temp);
                                                    group.map.put(group.group_array.get(j), temp_array);
                                                    break;
                                                }
                                            }
                                        }

                                        if (!chatpage.checkactivity) {

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MediaPlayer mp = MediaPlayer.create(context, R.raw.m);
                                                    mp.start();
                                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                                                    builder.setSmallIcon(android.R.drawable.sym_action_chat);
                                                    Intent intent = new Intent(context, chatpage.class);
                                                    intent.putExtra("groupname", c);
                                                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                                                    builder.setContentIntent(pendingIntent);
                                                    builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo));
                                                    builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                                                    builder.setContentTitle("New Message");
                                                    builder.setContentText(temp);
                                                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                                                    notificationManager.notify(1, builder.build());
                                                }
                                            });


                                            for (int j = 0; j < group.group_array.size(); j++) {
                                                if (c.equals(group.group_array.get(j))) {
                                                    ArrayList<String> temp_array = new ArrayList<>();
                                                    temp_array = group.map.get(group.group_array.get(j));
                                                    temp_array.add(d + ":" + temp);
                                                    group.map.put(group.group_array.get(j), temp_array);
                                                    break;
                                                }


                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        case "7": {   // get response from server
                            synchronized (this) {
                                serverresponse = b;
                                notify();
                                System.out.println("notified");
                            }
                            break;
                        }
                        case "8": {   // check if group already exist
                            synchronized (this) {
                                group.groupstatus = b;
                                notify();
                                System.out.println("notified");
                            }

                            break;
                        }
                        case "9": // get user grouplist

                            synchronized (this) {
                                System.out.println(b);
                                if (b.equals("end")) {
                                    notify();
                                    System.out.println("notified");
                                    break;
                                }
                                group.group_array.add(b);
                            }
                            break;
                        default:
                            System.out.println("Why I am in default ??");
                            break;
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


