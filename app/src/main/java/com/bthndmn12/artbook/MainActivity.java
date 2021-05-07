package com.bthndmn12.artbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> artNames ;
    ArrayList<Integer> idS ;
    ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        artNames = new ArrayList<>();
        idS = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,artNames);
       listView.setAdapter(arrayAdapter);

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent= new Intent(MainActivity.this,ArtActivity.class);
               intent.putExtra("artId",idS.get(position));
               intent.putExtra("info","old");
               startActivity(intent);
           }
       });
        getData();
    }


    public void getData(){
        try {
            SQLiteDatabase database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            Cursor cursor = database.rawQuery("SELECT * FROM arts",null);
            int nameIX = cursor.getColumnIndex("artname");
            int idIX = cursor.getColumnIndex("id");
            while (cursor.moveToNext()){
                artNames.add(cursor.getString(nameIX));
                idS.add(cursor.getInt(idIX));
            }
            arrayAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_art,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_art){
            Intent intent = new Intent(getApplicationContext(),ArtActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);

    }
}