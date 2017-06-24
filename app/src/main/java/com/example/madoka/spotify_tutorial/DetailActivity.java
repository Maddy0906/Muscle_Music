package com.example.madoka.spotify_tutorial;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;

public class DetailActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    //my client ID
    private static final String CLIENT_ID = "CLIENT_ID";
    //my redirect URI
    private static final String REDIRECT_URI = "http://mysite.com/callback/";

    private String TAG = DetailActivity.class.getSimpleName();
    private ListView lv;

    ArrayList<HashMap<String, String>> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        itemList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

        new GetContacts().execute();

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Toast.makeText(DetailActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpHandler sh = new HttpHandler();
            //Making a request to url and getting response
            String url = "https://api.spotify.com/v1/users/21vwg7tdxsk7dtmyslgh4qqhy/playlists/5eV6vRrrgempaVXnw1JDEo/tracks";

            SpotifyApi api = new SpotifyApi();
            api.setAccessToken("");
            SpotifyService spotify = api.getService();
//            OAuthToken token = null;
//            Header authorizationHeader = null;

            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Pesponse from url:" + jsonStr);
            if (jsonStr != null){

                try{
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray items = jsonObj.getJSONArray("items");
                    JSONArray artists = jsonObj.getJSONArray("artists");

                    // looping through All Contacts
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject itm = items.getJSONObject(i);
                        String href = itm.getString("href");
                        String id = itm.getString("id");
                        String name = itm.getString("name");
                        String popularity = itm.getString("popularity");
                        String preview_url = itm.getString("preview_url");
                        String track_number = itm.getString("track_number");
                        String type = itm.getString("type");
                        String uri = itm.getString("uri");

                        // Track node is JSON Object
                        JSONObject track = itm.getJSONObject("track");

                        JSONObject album = itm.getJSONObject("album");
                        String album_type = album.getString("album_type");

                        for (int j=0; j < artists.length(); i++ ){
                            JSONObject a = artists.getJSONObject(j);
                            String href2 = a.getString("href");
                            String id2 = a.getString("id");
                            String name2 = a.getString("name");
                            String type2 = a.getString("type");
                            String uri2 = a.getString("uri");

                            // Track node is JSON Object
                            JSONObject external_urls  = a.getJSONObject("external_urls");
                            String spotify2 = external_urls.getString("spotify");

                        }


                        // tmp hash map for single contact
                        HashMap<String, String> item = new HashMap<>();

                        // adding each child node to HashMap key => value
                        item.put("href", href);
                        item.put("id", id);
                        item.put("name", name);
                        item.put("type", type);
                        item.put("uri", uri);


                        //adding contact to contact list
                        itemList.add(item);
                    }
                }catch (final JSONException e){
                    Log.e(TAG, "Json parsing error:"+ e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error:" + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }else {
                Log.e(TAG, "Couldn't get json from server");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(DetailActivity.this, itemList,
                    R.layout.list_item, new String[]{"name","type"},
                    new int[]{R.id.name, R.id.type});
            lv.setAdapter(adapter);
        }
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
    public void onPlaybackEvent(PlayerEvent playerEvent) {

    }

    @Override
    public void onPlaybackError(Error error) {

    }

    @Override
    public void onLoggedIn() {

//        player.pause(null);
    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Error error) {

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }
}



