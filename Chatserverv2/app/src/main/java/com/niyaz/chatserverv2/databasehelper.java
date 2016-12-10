package com.niyaz.chatserverv2;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by niyaz on 2016-11-21.
 * The Database consist of 3 tables which are users, clientgroup and messages which have been created manually.
 * MySQL has been used.
 */

public class databasehelper {

    static final String DB_URL = "jdbc:mysql://localhost/groupchat";
    static final String USER = "root";
    static final String PASS = "admin";

    public static void main(String[] args) throws  Exception {

    }

    public static boolean registeruser(String user, String pass) throws  Exception{

        try {
            Connection connection= getconnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password) VALUES ('"+user+"','"+pass+"')");
            statement.executeUpdate();
            System.out.println("user registered");
            connection.close();
            return true;
        }catch (Exception e){System.out.println("Error :" + e);}
        return false;
    }

    public static boolean addgroup(String gname,String name) throws  Exception{
        try {
            Connection connection = getconnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO clientgroup (groupname,username) VALUES ('"+gname+"','"+name+"')");
            statement.executeUpdate();
            System.out.println("New group added !!");
            insertusertogroup("admin",gname);
            insertchatmessage("admin","Welcome to the group "+gname+"  !!!",gname);
            connection.close();
            return true;
        }catch (Exception e){System.out.println("Error :" + e);}


        return  false;
    }


    public static int checkgroup(String gname) throws Exception{
        try {
            Connection connection = getconnection();
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) from  clientgroup where groupname = '"+gname+"'");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
                return resultSet.getInt(1);
        }catch (Exception e){System.out.println("Error :" + e);}
        return 0;
    }

    public static boolean insertusertogroup(String uname,String gname) throws  Exception{
        try {
            Connection connection = getconnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO clientgroup (username,groupname) VALUES ('"+uname+"','"+gname+"')");
            statement.executeUpdate();
            System.out.println("New user in group added !!");
            connection.close();
            return true;
        }catch (Exception e){System.out.println("Error :" + e);}

        return  false;
    }
    public static boolean deleteuserfromgroup(String uname, String gname) throws Exception{
        try {
            Connection connection = getconnection();
            PreparedStatement statement = connection.prepareStatement("delete from clientgroup where groupname='"+gname+"' AND username='"+uname+"'");
            statement.executeUpdate();
            System.out.println("user removed from group !!");
            connection.close();
            return true;
        }catch (Exception e){System.out.println("Error :" + e);}
        return false;
    }

    public static int getMsgID() throws Exception {
        int msgID = -1;
        try {
            Connection connection = getconnection();
            PreparedStatement statement = connection.prepareStatement("select max(msgid) as maxid from messages");
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                msgID = result.getInt("MAXID");
            }
            msgID = msgID + 1;

            return msgID;

        }catch (Exception e){System.out.println("Error :" + e);}

        msgID = msgID + 1;

        return msgID;
    }



    public  static boolean insertchatmessage(String user, String msg, String  gname) throws  Exception{
        try {
            Connection connection = getconnection();
            int msgid = getMsgID();
            String tem= user + ":" + msg;
            System.out.println(tem);
            PreparedStatement statement = connection.prepareStatement("insert into messages (msgid,msg, groupname,username) values ('"+msgid+"','"+tem+"','"+gname+"','"+user+"')");
            statement.executeUpdate();
            System.out.println("New message added !!");
            connection.close();
            return true;
        }catch (Exception e){System.out.println("Error :" + e);}
        return false;
    }

    public static ArrayList<String> getgroups(String name) throws Exception{
        ArrayList<String> array = new ArrayList<String>();
        try {
            Connection connection = getconnection();
            PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT (groupname) from clientgroup where username!='"+name+"'");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                array.add(result.getString("groupname"));
            }
            connection.close();
            return array;
        }catch (Exception e){System.out.println("Error :" + e);}


        return null;
    }


    public static ArrayList<String>   getuserlist(String gname) throws Exception{
        ArrayList<String> array = new ArrayList<String>();
        try {
            Connection connection = getconnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * from clientgroup where groupname= '"+gname+"'");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                array.add(result.getString("username"));
                System.out.println(result.getString("username"));
            }
            connection.close();
            return array;
        }catch (Exception e){System.out.println("Error :" + e);}

        return null;
    }

    public static int checkuseringroup(String name,String gname) throws Exception{
        try {
            Connection connection = getconnection();
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) from  clientgroup where groupname ='"+gname+"' and username='"+name+"'");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                System.out.println("resultSet.getInt(1)  " + resultSet.getInt(1));

                return resultSet.getInt(1);
            }
        }catch (Exception e){System.out.println("Error :" + e);}

        return 0;
    }


    public static ArrayList<String> getchatmessages(String gname) throws  Exception{
        ArrayList<String> array = new ArrayList<String>();
        try {
            Connection connection = getconnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * from messages where groupname= '"+gname+"' order by msgid ");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                array.add(result.getString("msg"));
              //  array.add(result.getString("msg"));
            }
            connection.close();
            return array;
        }catch (Exception e){System.out.println("Error :" + e);}

        return null;
    }



    public static int getgrouptotal(String name) throws  Exception{

        try {
            Connection connection = getconnection();
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM clientgroup where username='"+name+"'");
             ResultSet result= statement.executeQuery();
            while (result.next())
            return result.getInt(1);
        }catch (Exception e){System.out.println("Error :" + e);}
        return 0;
    }
    public static String loginuser(String user) throws Exception{
        String password=null;
        try {
            Connection connection=getconnection();
            PreparedStatement statement =connection.prepareStatement("SELECT password FROM users WHERE username = '"+user+"'");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                password = resultSet.getString("password");
            }
            connection.close();
            return password;
        }catch (Exception e){System.out.println("Error :"+ e);}
            return  null;
    }




    public static Connection getconnection() throws Exception{

        Connection conn;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to dB");
            return conn;
        } catch (Exception e) {
            System.out.println("Error : " + e);
        }

        return null;
    }

    public ArrayList<String> getgroupbyuser(String b) {

        ArrayList<String> array = new ArrayList<String>();
        try {
            Connection connection = getconnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * from clientgroup where username = '"+b+"'");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                array.add(result.getString("groupname"));
            }
            connection.close();
            return array;
        }catch (Exception e){System.out.println("Error :" + e);}


        return null;


    }
}





