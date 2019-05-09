package com.handcarryapp.ustech.seamfixchat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SeamfixChatDialog.AddNewMessageInterfaceListener{
    //the class is accesed as a context using the variable c declared
    Context c = MainActivity.this;
    //the MQTTAndroid Client is declared
    MqttAndroidClient client;
    //the connectivity manager class for managing network state is declared
    private ConnectivityManager connMgr;
    //the network info class is declared
    NetworkInfo networkInfo;
    //the class MQTTMessage is declared as a global variable
    MqttMessage message;

    //declare the adapter
    MessageAdapter messageAdapter;
//declare Menu;
    Menu menu;

     String current_time = "";



    //declare the resorces class

    String clientId = "SeamfixChatId";
    final String serverUri = "tcp://broker.hivemq.com:1883";
    //declare the array list
    ListView lView;
    //declare the ArrayList
    ArrayList<MessageObject> messageObjects;

    //declare menuItem
    MenuItem menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //the NetworkInfo class gets the current state of the device network connection
        networkInfo = connMgr.getActiveNetworkInfo();


        messageObjects = new ArrayList<>();
        //messageObjects.add(new MessageObject("Sample Topic", "Sample Message"));
        messageAdapter = new MessageAdapter(c,messageObjects);
        lView = findViewById(R.id.lv_messages);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                messageAdapter.remove(messageObjects.get(position));

            }
        });
        lView.setAdapter(messageAdapter);

        //call on the method for establishing connection
        establishConnection();

        getFb(R.id.fb_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        //set the onclick listener on the send button and then pass the publish message




    }


     public void showDialog(){
     DialogFragment dialogFragment = new SeamfixChatDialog();
     dialogFragment.show(getSupportFragmentManager(),"Start Chat");
     }



     @Override
     public void onDialogPositiveClick(String topic, String message, String time) throws MqttException {

     //a check is done for empty and displays a toast message for users to fill parameters
     if(TextUtils.isEmpty(topic) && TextUtils.isEmpty(message) && TextUtils.isEmpty(time)){
     Toast.makeText(c, "Please fill up the fields", Toast.LENGTH_SHORT).show();
     }
     //this condition executes if the first condition sets to false
     else{
     //the publishMessage method is called
     publishMessage(topic,message);
     subscribe(topic,0,time);

     }

     }

     @Override
     public void onDialogNegativeClick(DialogFragment dialogFragment) {
     dialogFragment.dismiss();

     }





    public void establishConnection(){
        // a check is carried out to check for internet connection on the device
        if (networkInfo != null && networkInfo.isConnected() && networkInfo.isConnectedOrConnecting()) {
//a random userid for the client is generated at this point

            //an instance of MqttAndroidClient is created for binding to the PahoAndroid Service
            client = new MqttAndroidClient(this.getApplicationContext(), serverUri,
                    clientId);

            try {
                //the client tries to connect to the MQTT broker which returns a token
                IMqttToken token = client.connect();
                //the token will be used to make a call to the various callbacks to get notified on a
                //succesful or failed connection
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Toast.makeText(c, "Connection to HiveMQ was succesful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Toast.makeText(c, "Connection to HiveMQ was unsuccesful", Toast.LENGTH_SHORT).show();

                    }
                });


            } catch (MqttException e) {
                Toast.makeText(c, "Error as a result of : ", Toast.LENGTH_SHORT).show();
            }
        }

        else{
            //a toast message is displayed to the user that no internet was found
            Toast.makeText(c, "Oops ! !..No Internet Connection Found", Toast.LENGTH_SHORT).show();
        }
    }
    //this method is created with two arguements namely the topic and the message in string format
    public void publishMessage(String topic, String typed_message){
        //the encodedPayload is declared
        byte[] encodedPayload;
        try {
            /**the encodedpayload stores the message entered by the user which have been
             gotten in bytes form*/
            encodedPayload = typed_message.getBytes("UTF-8");
            //the mqttmessge is instantiated with the encoded payload passed as an arguement
            message = new MqttMessage(encodedPayload);
            //the setretained method retains the message
            message.setRetained(true);
            //the client is finnally published
            client.publish(topic, message);

            Toast.makeText(c, "Message Published Successfully " ,Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException | MqttException e) {
            Toast.makeText(c, "Error as a result of : " + e.getMessage(),Toast.LENGTH_SHORT).show();
        }



    }


    public void disconnect(){

        try {
            //the client is called on the disconnet method which is saved on the IMqttToken for registering several callbcks
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Toast.makeText(c, "Disconnected successfully "   ,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(c, "Disconnected unsuccessfully as a result of : " + exception.getMessage()   ,Toast.LENGTH_SHORT).show();
                    // something went wrong, but probably we are disconnected anyway
                }
            });
        } catch (MqttException e) {
            Toast.makeText(c, "Error in disconnection as a result of : " + e.getMessage()  ,Toast.LENGTH_SHORT).show();
        }
    }

    //the subscrbe method uses three parameters which are the topic and qos for subscribing

    public void subscribe(String topic, int qos, final String time) throws MqttException {
        //the subscribe is called on the client which is used to be saved on the subtoken


        try{



            IMqttToken subToken =  client.subscribe(topic,qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Toast.makeText(c, "Subscription successful ! "   ,Toast.LENGTH_SHORT).show();

                    // The message was published
                    client.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {
                            Toast.makeText(c, " connection lost as a result of : " + cause.getMessage(),Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {


                            messageObjects.add(new MessageObject(topic.trim(),new String(message.getPayload()),setTimer(Long.parseLong(time)) ));
                            messageAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(c, "Subscription failed as a result of : " + exception.getMessage(),Toast.LENGTH_SHORT).show();
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            Toast.makeText(c, "Error as a result of : " + e.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }

//this method will be used for referencing all edittext views




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.disc_menu,menu);
       // setTimer(30000);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_disconnect:
                disconnect();
                break;
            case R.id.item_connect:
                establishConnection();
                break;



        }
        return super.onOptionsItemSelected(item);
    }

    private FloatingActionButton getFb(int id){
        return findViewById(id);
    }

    public String setTimer(long timer){
        CountDownTimer cTimer;


//start timer function
        cTimer = new CountDownTimer(timer,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                current_time = "time to elapse in " + String.valueOf(millisUntilFinished / 1000) + " secs";
                lView.setEnabled(false);
                getFb(R.id.fb_message).setEnabled(false);
                menuItem = menu.findItem(R.id.item_topic);
                menuItem.setTitle(current_time);

            }

            @Override
            public void onFinish() {
                lView.setEnabled(true);
                getFb(R.id.fb_message).setEnabled(true);

            }
        };
        cTimer.start();
return current_time;

        }






}

