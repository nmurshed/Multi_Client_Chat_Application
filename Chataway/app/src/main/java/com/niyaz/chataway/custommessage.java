package com.niyaz.chataway;

/**
 * Created by niyaz on 2016-11-30.
 */

public class custommessage  {

    public boolean mine;
    public String sender;
    public String message;
    public String id;

    public custommessage(boolean left,String id, String message,String sender) {
        super();
        this.mine = left;
        this.message = message;
        if(left)
            this.sender ="Me";
        else
            this.sender = sender;
        this.id = id;
    }
}