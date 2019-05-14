package com.example.testcognito;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.scribejava.apis.TrelloApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.oauth.OAuthService;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class trello extends AppCompatActivity {

    private static final String API_KEY = "941f820631dac5989506b35ea5f36669";
    private static final String API_SECRET = "a19f0c3e4f37767e4339daa11f6f4487d40300cb57eb53dfe425fd5967032bba";
    private static final String PROTECTED_RESOURCE_URL = "https://trello.com/1/members/me";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trello);
       /* Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://trello.com/1/authorize?expiration=1day&name=MyPersonalToken&scope=read&response_type=token&key=941f820631dac5989506b35ea5f36669"));
        startActivity(browserIntent);*/
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.i("ANDREA", consoleMessage.message());
                return true;
            }
        });
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setDatabaseEnabled(true);
        myWebView.loadUrl("https://trello.com/1/authorize?expiration=1day&name=MyPersonalToken&scope=read&response_type=token&key=941f820631dac5989506b35ea5f36669");


    }

    public static void getToken() throws IOException, InterruptedException, ExecutionException {
        final OAuth10aService service = new ServiceBuilder(API_KEY)
                .apiSecret(API_SECRET)
                .build(TrelloApi.instance());
        final Scanner in = new Scanner(System.in);

        Log.i("ANDREA","=== Trello's OAuth Workflow ===");


        // Obtain the Request Token
        Log.i("ANDREA","Fetching the Request Token...");
        OAuth1RequestToken requestToken = service.getRequestToken();
        Log.i("ANDREA","Got the Request Token!");


        Log.i("ANDREA","Now go and authorize ScribeJava here:");
        Log.i("ANDREA",service.getAuthorizationUrl(requestToken));
        Log.i("ANDREA","And paste the verifier here");
        System.out.print(">>");
       String oauthVerifier = in.nextLine();


        // Trade the Request Token and Verfier for the Access Token
        Log.i("ANDREA","Trading the Request Token for an Access Token...");
         OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauthVerifier);
        Log.i("ANDREA","Got the Access Token!");
        Log.i("ANDREA","(The raw response looks like this: " + accessToken.getRawResponse() + "')");


        // Now let's go and ask for a protected resource!
        Log.i("ANDREA","Now we're going to access a protected resource...");
        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        final Response response = service.execute(request);
        Log.i("ANDREA","Got it! Lets see what we found...");

        Log.i("ANDREA",response.getBody());


        Log.i("ANDREA","Thats it man! Go and build something awesome with ScribeJava! :)");
    }

}
