package com.niyaz.chatserverv2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity {


    private static ServerSocket serverSocket = null;
    private static Socket socket = null;

    private static final int maxClientCount = 50;
    private static final clientThread[] threads = new clientThread[maxClientCount];


    public static void main(String args[]) {
        int portnumber = 62000;

        try {
            serverSocket = new ServerSocket(portnumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Waiting for client !");
        while (true) {

            try {
                socket = serverSocket.accept();
                for (int i = 0; i < maxClientCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new clientThread(socket, threads)).start();
                        break;
                    }
                    if (i == maxClientCount) {
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        bw.write("Server busy, Please Try later !");
                        bw.newLine();
                        bw.flush();
                        socket.close();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class clientThread extends Thread {

    private Socket clientsocket = null;
    private final clientThread[] threads;
    private int maxclientcount;
    private BufferedWriter bw;
    private BufferedReader br;
    private String clientname=null;

    public clientThread(Socket clientsocket, clientThread[] threads) {
        this.clientsocket = clientsocket;
        this.threads = threads;
        maxclientcount = threads.length;
    }


    public void run() {
        int maxclientcount = this.maxclientcount;
        clientThread[] threads = this.threads;
        String rmsg;

        databasehelper db = new databasehelper();

        try {
            bw = new BufferedWriter(new OutputStreamWriter(clientsocket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
          while (true) {
              rmsg = br.readLine().toString();
              System.out.println(rmsg);
              StringTokenizer token = new StringTokenizer(rmsg,"|");
              String a = token.nextToken();
              String b = token.nextToken();
              String c = token.nextToken();
              String d = token.nextToken();
              switch (a) {
                  case "1": //check user login
                      synchronized (this){
                          String p;
                          clientname = b;
                          p = db.loginuser(b);
                          clientname=b;
                          if (c.equals(p)) {
                              bw.write("1|true|null|null");
                              bw.newLine();
                              bw.flush();
                              System.out.println("true");
                              break;
                          } else {
                              bw.write("1|false|null|null");
                              bw.newLine();
                              bw.flush();
                              System.out.println("false");
                              break;
                          }
                      }


                  case "2": //register new user
                      synchronized (this){
                          boolean res;
                          res=db.registeruser(b, c);
                          bw.write("7|"+res+"|null|null");
                          bw.newLine();
                          bw.flush();
                      }

                      break;
                  case "3": //Add new group
                      synchronized (this){
                          System.out.println("group name received:"+ b);
                          db.addgroup(b,c);
                      }

                      break;
                  case "4": //returns number of groups in dB
                      synchronized (this){
                          bw.write("4|"+String.valueOf(db.getgrouptotal(b))+"|null|null");
                          bw.newLine();
                          bw.flush();
                      }

                      break;
                  case "5":  //send group list
                      synchronized (this){
                          ArrayList<String> arrayList = new ArrayList<String>();
                          arrayList=db.getgroups(b);
                          int s = arrayList.size();
                          System.out.println(s);
                          for(int i =0;i<s;i++){
                              bw.write("2|" + arrayList.get(i)+"|null|null");
                              bw.newLine();
                          }
                          bw.write("2|end|null|null");
                          bw.newLine();
                          bw.flush();
                      }

                      break;
                  case "6":

                      synchronized (this){
                          ArrayList<String> array = new ArrayList<String>();
                          array = db.getchatmessages(b);
                          int y = array.size();
                          System.out.println(y);
                          for(int i =0;i<y;i++){
                              bw.write("5|"+array.get(i)+"|null|null");
                              bw.newLine();
                          }
                          bw.write("5|end|null|null");
                          bw.newLine();
                          bw.flush();
                      }

                      break;
                  case "7":
                      synchronized (this) {
                          for (int i = 0; i < maxclientcount; i++) {
                              if (threads[i] != null && threads[i].clientname !=null) {
                                  System.out.println(threads[i].clientname+"    "+d);
                                    if (db.checkuseringroup(threads[i].clientname,d)>0) {
                                        System.out.println("thread no" + i);
                                        //threads[i].bw.write("6|" + b + ": " + c + "|" + d +"|"+b);
                                        threads[i].bw.write("6|" + c + "|" + d +"|"+b);
                                        threads[i].bw.newLine();
                                        threads[i].bw.flush();
                                        System.out.println("Message sent to thread " + i);
                                    }
                              }

                          }
                      }
                          db.insertchatmessage(b,c,d);

                      break;
                  case "8":
                      synchronized (this){
                          db.insertusertogroup(b,c);
                      }

                      break;
                  case "9":
                      synchronized (this){
                          db.deleteuserfromgroup(b,c);
                      }

                      break;
                  case "10":
                      ArrayList<String> users = new ArrayList<String>();
                      users= db.getuserlist(b);
                      int k = users.size();
                      System.out.println(k);
                      for(int i =0;i<k;i++){
                          bw.write("3|"+users.get(i)+"|null|null");
                          bw.newLine();
                      }
                      bw.write("3|end|null|null");
                      bw.newLine();
                      bw.flush();
                      break;
                  case "11":
                            int gcount = db.checkgroup(b);
                      if (gcount>0){
                          bw.write("8|true|null|null");
                          bw.newLine();
                          bw.flush();

                      }
                      else {
                          bw.write("8|false|null|null");
                          bw.newLine();
                          bw.flush();
                      }

                      break;
                  case "12":  //send group list
                      synchronized (this){
                          ArrayList<String> grouparray = new ArrayList<String>();
                          grouparray=db.getgroupbyuser(b);
                          int t = grouparray.size();
                          System.out.println(t);
                          for(int i =0;i<t;i++){
                              bw.write("9|" + grouparray.get(i)+"|null|null");
                              bw.newLine();
                          }
                          bw.write("9|end|null|null");
                          bw.newLine();
                          bw.flush();
                      }

                      break;
                  default:
                      System.out.println("I am in default !!");
                      break;

              }
          }

            }catch(IOException e){
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
        synchronized (this) {
            for (int i = 0; i < maxclientcount; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
                    System.out.println("null set to thread :" + i);
                }
            }
        }

        try {
            clientsocket.close();
            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    }







