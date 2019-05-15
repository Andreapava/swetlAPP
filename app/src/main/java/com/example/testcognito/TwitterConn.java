package com.example.testcognito;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class TwitterConn extends AppCompatActivity {

    private TwitterLoginButton loginButton;
    private String token;
    private String secret;
    private final String CONSUMER_SECRET = "6SLqOZxNV22gDOmPSSJKQSeWWHfGVKwuk2aTf78HO0qfipwaof";

    private final String CONSUMER_KEY = "sFOOM7Ln3yEF3pzwibMv16OKs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. PRIMA IMPOSTI L'SDK
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);

        setContentView(R.layout.activity_twitter);

        //2. CALLBACK AL BOTTONE DI LOGIN
        loginButton = findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                SetConnectorActivity.secret = authToken.secret;
                SetConnectorActivity.token = authToken.token;
                SetConnectorActivity.tTokenSet=true;

                TwitterAuthClient authClient = new TwitterAuthClient();
                authClient.requestEmail(session, new Callback<String>() {
                    @Override
                    public void success(Result<String> result) {

                      // Toast.makeText(getApplicationContext(), token+" "+secret, Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                Toast.makeText(getApplicationContext(), "Successfully logged-in, press back to return to your connectors", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), "FAIL LOGIN :(", Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(!SetConnectorActivity.tTokenSet){
            Intent intent = new Intent(TwitterConn.this,ConnectorActivity.class);
           TwitterConn.this.startActivity(intent);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // PASSA IL RISULTATO AL BOTTONE
        loginButton.onActivityResult(requestCode, resultCode, data);

        /*
         Usa questo nei fragment

         Fragment fragment = getFragmentManager().findFragmentById(R.id.your_fragment_id);
         if (fragment != null) {
             fragment.onActivityResult(requestCode, resultCode, data);
         }
        */

    }
}
