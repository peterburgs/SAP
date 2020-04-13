package com.example.sap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        ListView myListView=findViewById(R.id.myListView);

        final ArrayList<String> myFamily=new ArrayList<String>();

        myFamily.add("Peter Burgs");
        myFamily.add("John Wick");
        myFamily.add("Donald Trumpet");
        myFamily.add("Back Obama");

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, myFamily);
        myListView.setAdapter(arrayAdapter);


        //Show the name of the item clicked
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "You clicked: "+myFamily.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }
}