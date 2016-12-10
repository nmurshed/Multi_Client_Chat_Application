package com.niyaz.chataway;

import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niyaz on 2016-11-30.
 */

public class customadapter extends ArrayAdapter<custommessage> {

    private TextView chatText;
    private List<custommessage> chatMessageList = null;
    private Context context;
    LinearLayout singleMessageContainer;

    @Override
    public void add(custommessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public void setmessageList(ArrayList<custommessage> messages){
        if(chatMessageList == null){
            chatMessageList = messages;
        }
    }

    public customadapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public custommessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        custommessage chatMessageObj = getItem(position);
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.msg_text_right, parent, false);
        }
        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        chatText = (TextView) row.findViewById(R.id.view1);
        chatText.setText(chatMessageObj.message);
        chatText.setBackgroundResource(chatMessageObj.mine ? R.drawable.right : R.drawable.left);
        singleMessageContainer.setGravity(chatMessageObj.mine ? Gravity.RIGHT : Gravity.LEFT);






        chatText.setText(Html.fromHtml("<b>"+chatMessageObj.sender+"</b><br>"+chatMessageObj.message));
        return row;
    }


}