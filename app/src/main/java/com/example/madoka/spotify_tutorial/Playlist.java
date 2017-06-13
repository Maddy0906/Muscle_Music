package com.example.madoka.spotify_tutorial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

public class Playlist extends AppCompatActivity implements RecyclerViewAdapter.ItemListener {
    RecyclerView recyclerView;
    ArrayList arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        arrayList = new ArrayList();
        arrayList.add(new DataModel("Item 1", R.drawable.workout01));
        arrayList.add(new DataModel("Item 2", R.drawable.workout02));
        arrayList.add(new DataModel("Item 3", R.drawable.workout03));
        arrayList.add(new DataModel("Item 4", R.drawable.workout04));
        arrayList.add(new DataModel("Item 5", R.drawable.workout05));
        arrayList.add(new DataModel("Item 6", R.drawable.workout06));
        arrayList.add(new DataModel("Item 7", R.drawable.workout07));
        arrayList.add(new DataModel("Item 8", R.drawable.workout08));
        arrayList.add(new DataModel("Item 9", R.drawable.workout09));
        arrayList.add(new DataModel("Item 10", R.drawable.workout10));

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, arrayList, this);
        recyclerView.setAdapter(adapter);


        /**
         AutoFitGridLayoutManager that auto fits the cells by the column width defined.
         **/

        /*AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, 500);
        recyclerView.setLayoutManager(layoutManager);*/


        /**
         Simple GridLayoutManager that spans two columns
         **/
        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
    }

    @Override
    public void onItemClick(DataModel item) {

        Toast.makeText(getApplicationContext(), item.text + " is clicked", Toast.LENGTH_SHORT).show();
    }
}
