package com.project.layoutmulticlient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class NsdChatActivity extends Activity {

    Intent intent;
    NsdHelper mNsdHelper;

    //private TextView mStatusView;
    private EditText con_name, con_password, con_details;
    private EditText con_join_name, con_join_pass;
    private EditText part_name, part_ph, part_email;
    public static Button btn_join;
    protected ProgressBar progressBar;
    private Uri uri;

    public static final String TAG = "NsdChat";
    public static String mUserChoice;
    public static String mServiceName;
    public static String server_pass, client_pass;

    ChatConnection mConnection;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Creating chat activity");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Checking if the user's role

        mUserChoice = getIntent().getStringExtra("flag");
        if(mUserChoice.equals("server")) {
            setContentView(R.layout.activity_contest__creation);

            con_name= (EditText)findViewById(R.id.con_name);
            con_password= (EditText)findViewById(R.id.con_password);
            con_details= (EditText)findViewById(R.id.con_details);
        }
        else if(mUserChoice.equals("client")) {
            setContentView(R.layout.activity_join_contest);
            con_join_name = (EditText)findViewById(R.id.con_join_name);
            con_join_pass = (EditText)findViewById(R.id.con_join_pass);
            part_name = (EditText)findViewById(R.id.part_name);
            part_ph = (EditText)findViewById(R.id.part_ph);
            part_email = (EditText)findViewById(R.id.part_email);
            btn_join = (Button)findViewById(R.id.btn_join);
            progressBar = (ProgressBar)findViewById(R.id.progress_spinner);
            setVisibilityButton(false);
        }
    }

    public void con_create_click(View v){

        server_pass = con_password.getText().toString();
        if(con_name.getText().toString().equals("") || server_pass.equals("")) {
            Toast.makeText(this,"One or more fields is empty",Toast.LENGTH_LONG).show();
        }
        else {
            mServiceName = con_name.getText().toString();
            clickAdvertise();
            intent = new Intent(this, Contest_Upload.class);
            startActivity(intent);
        }
    }

    public void search_contest(View v)
    {

        if(con_join_name.getText().toString().equals("") || con_join_pass.getText().toString().equals("")) {
            Toast.makeText(this,"One or more fields is empty",Toast.LENGTH_LONG).show();
        }
        else {
            mServiceName = con_join_name.getText().toString();
            clickDiscover();
        }
    }

    public void setVisibilityButton(boolean flag)
    {
        if(flag)
            btn_join.setVisibility(View.VISIBLE);
        else
            btn_join.setVisibility(View.GONE);
    }

    public void join_contest(View v)
    {
        client_pass = con_join_pass.getText().toString();
        if(con_join_name.getText().toString().equals("") || client_pass.equals("")) {
            Toast.makeText(this, "One or more fields is empty", Toast.LENGTH_LONG).show();
        }
        else {
            clickConnect();
            /*
            intent = new Intent(this, Participant_details.class);
            startActivity(intent);
            */
        }
    }

    //Initiating service registration
    public void clickAdvertise() {
        // Register service
        if(mConnection.getLocalPort() > -1) {
            mNsdHelper.registerService(mConnection.getLocalPort());
        } else {
            Log.d(TAG, "ServerSocket isn't bound.");
            Toast.makeText(this, "ServerSocket isn't bound",Toast.LENGTH_SHORT).show();
        }
    }

    //Initiating service discovery
    public void clickDiscover() {
        mNsdHelper.discoverServices();
    }

    //Initializing client connectivity to the service
    public void clickConnect() {
        NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
        if (service != null) {
            Log.d(TAG, "Connecting...");
            Toast.makeText(this, "Connecting...",Toast.LENGTH_SHORT).show();
            Socket s = null;
            mConnection.commonConnection(service.getHost(), service.getPort(), s);
        } else {
            Log.d(TAG, "No service to connect to!");
            Toast.makeText(this, "No service to connect to!",Toast.LENGTH_SHORT).show();
        }
    }

    /*
    public void clickSend(View v) {
        EditText messageView = (EditText) this.findViewById(R.id.chatInput);
        if (messageView != null) {
            String messageString = messageView.getText().toString();
            if (!messageString.isEmpty()) {
                mConnection.sendMessage(messageString);
            }
            messageView.setText("");
        }
    }

    public void addChatLine(String line) {
        //mStatusView.append("\n" + line);
    }
    */

    public void addQues(View view) {
        Intent chooseExcelIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseExcelIntent.setType("application/vnd.ms-excel");
        startActivityForResult(chooseExcelIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Result");

        if (resultCode == RESULT_OK && requestCode == 1) {
                if (data == null) {
                    Toast.makeText(this, "No file found!", Toast.LENGTH_LONG).show();
                } else {
                    uri = data.getData();
                    Log.i(TAG, "Excel URI: " + uri);
                    ReadExcelFile.readExcelFile(this, uri);
                }
        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, "There was an error reading the file!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "Starting.");
        //creating an object of the ChatConnection class
        mConnection = new ChatConnection(this);
        if(mNsdHelper != null)
            mNsdHelper.tearDown();
        mNsdHelper = new NsdHelper(this);
        mNsdHelper.initializeNsd();
        super.onStart();
    }


    @Override
    protected void onPause() {
        Log.d(TAG, "Pausing.");
        if (mNsdHelper != null)
            //mNsdHelper.stopDiscovery();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Resuming.");
        super.onResume();
        if (mNsdHelper != null && !NsdHelper.flag) {
            //mNsdHelper.discoverServices();
        }
    }


    // For KitKat and earlier releases, it is necessary to remove the
    // service registration when the application is stopped.  There's
    // no guarantee that the onDestroy() method will be called (we're
    // killable after onStop() returns) and the NSD service won't remove
    // the registration for us if we're killed.

    // In L and later, NsdService will automatically unregister us when
    // our connection goes away when we're killed, so this step is
    // optional (but recommended).


    @Override
    protected void onStop() {
        Log.d(TAG, "Being stopped.");
        if(isFinishing()) {
            Log.d(TAG, "Is finishing!");
            if (mNsdHelper != null)
                mNsdHelper.stopDiscovery();
            if (mNsdHelper != null)
                mNsdHelper.tearDown();
            if (mConnection != null)
                mConnection.tearDown();
            mNsdHelper = null;
            mConnection = null;
        }
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "Being destroyed.");
        if(mNsdHelper != null)
            mNsdHelper.tearDown();
        if(mConnection != null)
            mConnection.tearDown();
        mNsdHelper = null;
        mConnection = null;
        super.onDestroy();
    }
}
