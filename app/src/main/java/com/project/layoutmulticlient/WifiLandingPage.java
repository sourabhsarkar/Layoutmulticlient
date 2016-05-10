package com.project.layoutmulticlient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

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
