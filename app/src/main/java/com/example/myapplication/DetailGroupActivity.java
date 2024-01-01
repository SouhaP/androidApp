package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class DetailGroupActivity extends AppCompatActivity {









    //view
    private TextView nameTv,descriptionTv,addedTimeTv,updatedTimeTv;

    private String id,name,description,addTime,updateTime;


    //database helper
    private DbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_group);
        //init db
        dbHelper = new DbHelper(this);

        //get data from intent
        Intent intent = getIntent();
        id = intent.getStringExtra("contactId");

        //init view
        nameTv = findViewById(R.id.tv_group_name);
        descriptionTv = findViewById(R.id.tv_group_description);
        addedTimeTv = findViewById(R.id.tv_group_addTime);
        updatedTimeTv = findViewById(R.id.tv_group_updateTime);

        loadDataById();

    }

    private void loadDataById() {

        //get data from database
        //query for find data by id
        String selectQuery =  "SELECT * FROM "+Constants.TABLE_GROUPS + " WHERE " + Constants.COLUMN_GROUP_ID+ " =\"" + id + "\"";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()){
            do {
                //get data
                name =  ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_GROUP_NAME));
                description = ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_GROUP_DESCRIPTION));

                addTime = ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_GROUP_ADDED_TIME));
                updateTime = ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_GROUP_UPDATED_TIME));

                //convert time to dd/mm/yy hh:mm:aa format
                Calendar calendar = Calendar.getInstance(Locale.getDefault());

                calendar.setTimeInMillis(Long.parseLong(addTime));
                String timeAdd = ""+ DateFormat.format("dd/MM/yy hh:mm:aa",calendar);

                calendar.setTimeInMillis(Long.parseLong(updateTime));
                String timeUpdate = ""+ DateFormat.format("dd/MM/yy hh:mm:aa",calendar);

                //set data
                nameTv.setText(name);
                descriptionTv.setText(description);

                addedTimeTv.setText(timeAdd);
                updatedTimeTv.setText(timeUpdate);

                // if (image.equals("null")){
                //    profileIv.setImageResource(R.drawable.baseline_account_circle_24);
                //}else {
                //    Log.d("Debug", "Image value: " + image);
                //     profileIv.setImageURI(Uri.parse(image));
                //  }

            }while (cursor.moveToNext());
        }

        db.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.edit_delete_top_menu,menu);


        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id2 = item.getItemId();

        if (id2 == R.id.editContact) {
            // Ouvrez l'activité pour l'édition du contact
            Intent intent = new Intent(DetailGroupActivity.this, AddEditGroup.class);
            intent.putExtra(Constants.COLUMN_GROUP_ID, id); // Passer l'ID du contact à l'activité d'édition
            intent.putExtra(Constants.COLUMN_GROUP_NAME, name);
            intent.putExtra(Constants.COLUMN_GROUP_DESCRIPTION, description);
            intent.putExtra(Constants.COLUMN_GROUP_ADDED_TIME, addTime);
            intent.putExtra(Constants.COLUMN_GROUP_UPDATED_TIME, updateTime);

            intent.putExtra("isEditMode",true);
            startActivity(intent);
            return true;
        } else if (id2 == R.id.deleteContact) {
            // Demandez une confirmation avant de supprimer le contact
            new AlertDialog.Builder(this)
                    .setMessage("Êtes-vous sûr de vouloir supprimer ce groupe ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        // Supprimer le contact de la base de données
                        dbHelper.deleteGroup(id);
                        // Retourner à l'activité précédente (ou fermer si c'est l'activité principale)
                        finish();
                    })
                    .setNegativeButton("Non", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // run app
    // we have error in profileIv,dbHelper initialization
    // we successfully read our db and show details
}