package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddEditGroup extends AppCompatActivity {

    private EditText et_group_name, et_group_description;
    private Boolean isEditMode;
    private FloatingActionButton fab;

    //string var

    private String id, name, description, addedTime, updatedTime;

    //action bar
    ActionBar actionBar;



    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_group);



        //initialiser dbHelper
        dbHelper = new DbHelper(this);

//action bar
        actionBar = getSupportActionBar();

        actionBar.setTitle("Ajouter un groupe");
        //back button activated
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //init view
        et_group_name = findViewById(R.id.et_group_name);
        et_group_description = findViewById(R.id.et_group_description);

        fab = findViewById(R.id.fab);

        //add event to save data
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false);

        if (isEditMode) {
            //set toolbar title
            actionBar.setTitle("Update Contact");

            //get the other value from intent
            id = intent.getStringExtra(Constants.COLUMN_GROUP_ID);
            name = intent.getStringExtra(Constants.COLUMN_GROUP_NAME);
            description = intent.getStringExtra(Constants.COLUMN_GROUP_DESCRIPTION);

            addedTime = intent.getStringExtra(Constants.COLUMN_GROUP_ADDED_TIME);
            updatedTime = intent.getStringExtra(Constants.COLUMN_GROUP_UPDATED_TIME);


            //set value in editText field
            et_group_name.setText(name);
            et_group_description.setText(description);


        } else {
            // add mode on
            actionBar.setTitle("Ajouter Groupe");
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

    }


    private void saveData() {
        //take user giver data in variable
        name = et_group_name.getText().toString();
        description = et_group_description.getText().toString();
        // get current time to save as added time
        String timeStamp = "" + System.currentTimeMillis();

        if (!name.isEmpty()) {
            //save data ,if user have only one data
//check edit or add mode to save data in sql
            if (isEditMode) {
                // edit mode
                dbHelper.updateGroup(
                        "" + id,
                        "" + name,
                        "" + description,
                        "" + addedTime,
                        "" + timeStamp // updated time will new time
                );

                Toast.makeText(getApplicationContext(), "Updated Successfully....", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, GroupeActivity.class);
                startActivity(intent);
                finish();
            } else {

                long id = dbHelper.insertGroup(
                        "" + name,
                        "" + description,
                        "" + timeStamp,
                        "" + timeStamp
                );
                Toast.makeText(getApplicationContext(), "Inserted Successfully.... " + id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, GroupeActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            // show toast message
            Toast.makeText(getApplicationContext(), "Nothing to save....", Toast.LENGTH_SHORT).show();
        }

    }


    //back button click
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}