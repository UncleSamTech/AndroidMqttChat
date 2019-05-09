package com.handcarryapp.ustech.seamfixchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<MessageObject> {
    MessageViewHolder holder;
    public MessageAdapter(@NonNull Context context, ArrayList<MessageObject> messageObjects) {
        super(context, 0, messageObjects);
    }

    //this method is overriden for getting the inflated view and returns a view
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //our simple pojo class is instantiated and passed the getItem position value to get the value
        MessageObject messageObject = getItem(position);
        //the view class for holding value to be inflated is also declared and instantiated
        View mView = convertView;
        //a check is carried out to make sure the view havent been inflated already
        if(mView==null){
            mView = LayoutInflater.from(getContext()).inflate(R.layout.list_of_messages,parent,false);
            holder = createView(mView);
            mView.setTag(holder);
        }
        holder = (MessageViewHolder)mView.getTag();
        holder.tv_mqqt_topic.setText(messageObject.getMqqt_topic());
        holder.tv_mqqt_messge.setText(messageObject.getMessage());
        holder.tv_time.setText(messageObject.getTime());
        return mView;
    }

    //this class is used for referencing the contents of the view once and for all

    public static class MessageViewHolder{
        TextView tv_mqqt_topic = null;
        TextView tv_mqqt_messge = null;
        TextView tv_time = null;

        public MessageViewHolder(TextView tv_mqqt_topic, TextView tv_mqqt_messge, TextView tv_time) {
            this.tv_mqqt_topic = tv_mqqt_topic;
            this.tv_mqqt_messge = tv_mqqt_messge;
            this.tv_time = tv_time;

        }
    }
    //this method creates the views for our listview and returns the constructor of our MessageViewHolder class
    //it is passed a view parameter during run time
    public MessageViewHolder createView(View v){
        TextView message = (TextView)v.findViewById(R.id.tv_message);
        TextView topic = (TextView)v.findViewById(R.id.tv_mqqt_topic);
        TextView time = (TextView)v.findViewById(R.id.tv_time);

        return new MessageViewHolder(topic,message,time);
    }


}
