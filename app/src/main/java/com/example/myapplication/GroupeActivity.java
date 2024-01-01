package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GroupeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DbHelper dbHelper;
    private GroupAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupe);

        dbHelper = new DbHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);


//initialization
        FloatingActionButton fab = findViewById(R.id.fab);
        //add listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //move to new activity to add contact
                Intent intent=new Intent(GroupeActivity.this,AddEditGroup.class);
                intent.putExtra("isEditMode",false);
                startActivity(intent);
            }
        });


        loadData();

    }

    private void loadData() {
        adapter = new GroupAdapter(this,dbHelper.getAllGroupData());
        recyclerView.setAdapter(adapter);

    }
    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // refresh data
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_top_menu,menu);
        getMenuInflater().inflate(R.menu.group_menu, menu);

        //get search item from menu
        MenuItem item = menu.findItem(R.id.searchContact);
        //search area
        SearchView searchView = (SearchView) item.getActionView();
        //set max value for width

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchContact(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchContact(newText);
                return true;
            }
        });


        return true;

    }

    private void searchContact(String query) {
        adapter = new GroupAdapter(this,dbHelper.getSearchGroup(query));
        recyclerView.setAdapter(adapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.deleteAllContact) {
            // Demander une confirmation à l'utilisateur avant de supprimer tous les contacts
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete all groups?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Supprimer tous les contacts
                            dbHelper.deleteAllGroup();
                            // Recharger les données dans le RecyclerView
                            loadData();
                        }
                    })
                    .setNegativeButton("No", null);
            builder.create().show();
            return true;
        }


        //  if (id == R.id.action_main_activity) {
        // Ouvrez MainActivity
        ///    Intent intent = new Intent(this, MainActivity.class);
        //     startActivity(intent);
        //      return true;
        //} else
        if (id == R.id.action_other_package) {
            // Ouvrez l'activité (ou autre chose) du package com.example.appcontacts
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.example.appcontacts", "com.example.appcontacts.MainActivity"); // Remplacez YourActivityName par le nom de votre activité dans le package
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}