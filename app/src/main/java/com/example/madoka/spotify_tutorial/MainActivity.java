package com.example.madoka.spotify_tutorial;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;


//public class MainActivity extends AppCompatActivity {
//
//    private Button connect;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        setTitle("Sortify");
//
//        System.out.println("Access token: " + accessToken);
//
//        if (!accessToken.equals("")) {
//            startApplication();
//        }
//
//        connect = (Button) findViewById(R.id.connectToSpotify);
//        connect.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                startAuthentication();
//            }
//        });
//    }
//
//
//    void startAuthentication() {
//        Intent intent = new Intent(this, AuthenticateActivity.class);
//        startActivityForResult(intent, 1);
//    }
//
//    void startApplication() {
//        initSpotify();
//
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // Check which request we're responding to
//        if (requestCode == 1) {
//            // Make sure the request was successful
//            if (resultCode == RESULT_OK) {
//                startApplication();
//            }
//        }
//    }
//}



public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    //my client ID
    private static final String CLIENT_ID = "5e4e2aa628ff4601b5c620ae826cdf6c";
    //my redirect URI
    private static final String REDIRECT_URI = "http://mysite.com/callback/";

    private ImageButton mStartButton;


// Request code that will be used to verify if the result comes from correct activity
// Can be any integer
        private static final int REQUEST_CODE = 1337;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                    AuthenticationResponse.Type.TOKEN,
                    REDIRECT_URI);
            builder.setScopes(new String[]{"user-read-private", "streaming"});
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);


            //set a button to start
            mStartButton = (ImageButton) findViewById(R.id.start_button);
            mStartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), MusicController.class);
                    startActivity(intent);
                }
            });
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);

            }
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this); super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent){
        Log.d("MainActivity","playback error received:" + playerEvent.name());
        switch (playerEvent){
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error){
        Log.d("MainActivity","Playback error received" + error.name());
        switch (error){
            default:break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("MainActivity", "Login failed");
    }

    public void onLoginFailed(int i) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }


}
