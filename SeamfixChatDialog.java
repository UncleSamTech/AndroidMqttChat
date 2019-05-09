package com.handcarryapp.ustech.seamfixchat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttException;

public class SeamfixChatDialog extends DialogFragment implements TextView.OnEditorActionListener {

    EditText message_entered;
    EditText mqqt_topic;
    EditText time;
    ArrayAdapter stateSpinnerAdapter;
    AlertDialog.Builder builder;
    View v;

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }

    public interface AddNewMessageInterfaceListener{
        void onDialogPositiveClick(String topic, String message, String time) throws MqttException;
        void onDialogNegativeClick(DialogFragment dialogFragment);

    }

    SeamfixChatDialog.AddNewMessageInterfaceListener messageInterfaceListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
// Instantiate the NoticeDialogListener so we can send events to the
            messageInterfaceListener = (SeamfixChatDialog.AddNewMessageInterfaceListener) context;
        } catch (ClassCastException e) {
// The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    /**
     * This method is used for creating our dialog and inflating the view
     * @param savedInstanceState
     * @return
     */

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());
        try{
        LayoutInflater inflater = getActivity().getLayoutInflater();
            v  = inflater.inflate(R.layout.message_list,null);}catch (NullPointerException nu){
            nu.printStackTrace();
        }

        message_entered = v.findViewById(R.id.edt_message);
        mqqt_topic = v.findViewById(R.id.edt_mqqt_topic);
        time = v.findViewById(R.id.edt_time);
try{
message_entered.setOnEditorActionListener(this);
mqqt_topic.setOnEditorActionListener(this);
time.setOnEditorActionListener(this);}
catch (NullPointerException nu){nu.printStackTrace();}
builder.setView(v).
        setPositiveButton(R.string.mqtt_send_hint, new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {

        try {
            messageInterfaceListener.onDialogPositiveClick(message_entered.getText().toString().trim(),mqqt_topic.getText().toString().trim(),time.getText().toString().trim());
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}).setNegativeButton(R.string.msg_clear_hint, new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
messageInterfaceListener.onDialogNegativeClick(SeamfixChatDialog.this);
    }
});
        return builder.create();
    }
}
