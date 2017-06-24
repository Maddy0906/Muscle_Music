package com.example.madoka.spotify_tutorial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.squareup.picasso.Picasso;

import java.util.Set;

import static android.content.ContentValues.TAG;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MusicController extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    //my client ID
    private static final String CLIENT_ID = "CLIENT_ID";
    //my redirect URI
    private static final String REDIRECT_URI = "http://mysite.com/callback/";
    //Playlist
    private static final String TEST_PLAYLIST_URI = "spotify:user:spotify:playlist:4Jt88XxhP2Jldzh1t3QbyF";    @SuppressWarnings("SpellCheckingInspection")

    private Player mPlayer;
    //Need to play buttons
    private PlaybackState mCurrentPlaybackState;

    private Metadata mMetadata;
    private TextView mMetadataText;

    private final Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            logStatus("OK!");
        }

        @Override
        public void onError(Error error) {
            logStatus("ERROR:" + error);
        }
    };
    /**
     * Print a status message from a callback (or some other place) to the TextView in this
     * activity
     *
     * @param status Status message
     */
    private void logStatus(String status) {
        Log.i(TAG, status);
    }
   //Until here

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a reference to any UI widgets that we'll need to use later
        mMetadataText = (TextView) findViewById(R.id.metadata);

        setContentView(R.layout.activity_music_controller);

        //Animation play button
        Animation anim_alpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);
        ImageButton btnAlpha1 = (ImageButton)findViewById(R.id.play_playlist_button);
        final Animation finalAnim_alpha1 = anim_alpha;
        btnAlpha1.setOnClickListener(new ImageButton.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                onPlayButtonClicked(arg0);
                arg0.startAnimation(finalAnim_alpha1);

            }});

        //Animation previous button
        anim_alpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        ImageButton btnAlpha2 = (ImageButton) findViewById(R.id.skip_prev_button);
        Animation finalAnim_alpha = anim_alpha;
        final Animation finalAnim_alpha3 = finalAnim_alpha;
        btnAlpha2.setOnClickListener(new ImageButton.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                arg0.startAnimation(finalAnim_alpha3);
                onSkipToPreviousButtonClicked(arg0);
            }});

        //Animation pause button
        anim_alpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        ImageButton btnAlpha3 = (ImageButton) findViewById(R.id.pause_button);
        finalAnim_alpha = anim_alpha;
        final Animation finalAnim_alpha2 = finalAnim_alpha;
        btnAlpha3.setOnClickListener(new ImageButton.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                arg0.startAnimation(finalAnim_alpha2);
                onPauseButtonClicked(arg0);
            }});

        //Animation next button
        anim_alpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        ImageButton btnAlpha4 = (ImageButton) findViewById(R.id.skip_next_button);
        finalAnim_alpha = anim_alpha;
        final Animation finalAnim_alpha4 = finalAnim_alpha;
        btnAlpha4.setOnClickListener(new ImageButton.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                arg0.startAnimation(finalAnim_alpha4);
                onSkipToNextButtonClicked(arg0);
            }});

        //animation untile here

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(MusicController.this);
                        mPlayer.addNotificationCallback(MusicController.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    //UI event
    public void onPlayButtonClicked(View view) {

        String uri;
        switch (view.getId()) {
            case R.id.play_playlist_button:
                uri = TEST_PLAYLIST_URI;
                break;
            default:
                throw new IllegalArgumentException("View ID does not have an associated URI to play");
        }

        logStatus("Starting playback for " + uri);
        mPlayer.playUri(null, uri, 0, 0);
    }

    public void onPauseButtonClicked(View view) {

        Log.d("MusicControllerActivity", "pause");
        if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying) {
            mPlayer.pause(mOperationCallback);
        } else {
            mPlayer.resume(mOperationCallback);
        }
    }

    public void onSkipToPreviousButtonClicked(View view) {
        mPlayer.skipToPrevious(mOperationCallback);
    }

    public void onSkipToNextButtonClicked(View view) {
        mPlayer.skipToNext(mOperationCallback);
    }


    //display cover

    private void updateView() {

        final ImageView coverArtView = (ImageView) findViewById(R.id.cover_art);
        if (mMetadata != null && mMetadata.currentTrack != null) {
            final String durationStr = String.format(" (%dms)", mMetadata.currentTrack.durationMs);
            mMetadataText.setText(mMetadata.contextName + "\n" + mMetadata.currentTrack.name + " - " + mMetadata.currentTrack.artistName + durationStr);

            Picasso.with(this)
                    .load(mMetadata.currentTrack.albumCoverWebUrl)
                    .transform(new com.squareup.picasso.Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {

                            final Bitmap copy = source.copy(source.getConfig(), true);
                            source.recycle();
                            final Canvas canvas = new Canvas(copy);
                            canvas.drawColor(0xbb000000);
                            return copy;
                        }

                        @Override
                        public String key() {
                            return "darken";
                        }
                    })
                    .into(coverArtView);
        } else {
            mMetadataText.setText("<nothing is playing>");
            coverArtView.setBackground(null);
        }

    }

//UI event (Until here!!)

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent){
        mMetadata = mPlayer.getMetadata();
        // Trigger UI refresh
//        updateView();
        Log.d("ControllerActivity","playback error received:" + playerEvent.name());
        //add current
        mCurrentPlaybackState = mPlayer.getPlaybackState();
    }

    @Override
    public void onPlaybackError(Error error){
        Log.d("MusicController","Playback error received" + error.name());
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


    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MusicControllerActivity", "Received connection message: " + message);
    }

}
