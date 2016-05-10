package com.project.layoutmulticlient;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.project.layoutmulticlient.Realm.ContestHost;
import com.project.layoutmulticlient.Realm.Marks;
import com.project.layoutmulticlient.Realm.Participant;
import com.scand.realmbrowser.RealmBrowser;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class WifiLandingPage extends Activity {

    Intent intent;
    Button viewHistory;

    Realm realm;
    RealmConfiguration realmConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_landing_page);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        viewHistory = (Button)findViewById(R.id.viewHistory);

        requestPermissions();

        // Create a RealmConfiguration which is to locate Realm file in package's "files" directory.
        realmConfig = new RealmConfiguration.Builder(this).build();
        // Get a Realm instance for this thread
        realm = Realm.getInstance(realmConfig);

        viewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(realm != null) {
                    new RealmBrowser.Builder(WifiLandingPage.this)
                            .add(realm, ContestHost.class)
                            .add(realm, Participant.class)
                            .add(realm, Marks.class)
                            .show();
                }
            }
        });
    }

    private void requestPermissions() {
        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("To read excel sheet from memory you have to allow access to external memory.");
                builder.setTitle("External Read");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(WifiLandingPage.this, permissions, 0);
                    }
                });

                builder.show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "You have not given required permissions!",Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //direct the user to the server activity
    public void sv_btn_click(View v) {
        intent= new Intent(this,NsdChatActivity.class);
        intent.putExtra("flag","server");
        startActivity(intent);
    }

    //direct the user to the client page
    public void cl_btn_click(View v) {
        intent = new Intent(this,NsdChatActivity.class);
        intent.putExtra("flag","client");
        startActivity(intent);
    }
}
