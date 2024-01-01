package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    //view
    private TextView nameTv,phoneTv,emailTv,addedTimeTv,updatedTimeTv,noteTv,groupTv;
    private ImageView profileIv;
    private ImageView call;
    private ImageView msg;
    private ImageView mail;
    private String id,image,name,phone,email,note,addTime,updateTime,groupId;


    //database helper
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //init db
        dbHelper = new DbHelper(this);

        //get data from intent
        Intent intent = getIntent();
        id = intent.getStringExtra("contactId");

        //init view
        nameTv = findViewById(R.id.nameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        addedTimeTv = findViewById(R.id.addedTimeTv);
        updatedTimeTv = findViewById(R.id.updatedTimeTv);
        groupTv = findViewById(R.id.groupTv);
        noteTv = findViewById(R.id.noteTv);
        call=findViewById(R.id.call);
        msg=findViewById(R.id.msg);
        mail=findViewById(R.id.mail);
        profileIv = findViewById(R.id.profileIv);

        loadDataById();
        call.setOnClickListener(v -> {
            String phoneNumber = phoneTv.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            }
        });
        msg.setOnClickListener(v -> {
            String phoneNumber = phoneTv.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                // Si vous souhaitez ouvrir l'application SMS par défaut :
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null));
                startActivity(smsIntent);

                // Si vous souhaitez ouvrir WhatsApp (en supposant que l'application est installée) :
                //Intent whatsappIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber));
                //startActivity(whatsappIntent);
            }
        });
        mail.setOnClickListener(v -> {
            String emailAddress = emailTv.getText().toString().trim();
            if (!emailAddress.isEmpty()) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + emailAddress));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sujet de l'e-mail"); // Optionnel : définissez le sujet de l'e-mail
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Corps de l'e-mail"); // Optionnel : définissez le corps de l'e-mail
                startActivity(Intent.createChooser(emailIntent, "Envoyer un e-mail via:"));
            }
        });

    }

    private void loadDataById() {

        //get data from database
        //query for find data by id
        String selectQuery =  "SELECT * FROM "+Constants.TABLE_NAME + " WHERE " + Constants.C_ID + " =\"" + id + "\"";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()){
            do {
                //get data
                 name =  ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NAME));
                 image = ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_IMAGE));
                 phone = ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_PHONE));
                 email = ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_EMAIL));
                 note = ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NOTE));
                 addTime = ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_ADDED_TIME));
                 updateTime = ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_UPDATED_TIME));
                 groupId = ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_GROUP_ID));
                //convert time to dd/mm/yy hh:mm:aa format
                Calendar calendar = Calendar.getInstance(Locale.getDefault());

                calendar.setTimeInMillis(Long.parseLong(addTime));
                String timeAdd = ""+ DateFormat.format("dd/MM/yy hh:mm:aa",calendar);

                calendar.setTimeInMillis(Long.parseLong(updateTime));
                String timeUpdate = ""+ DateFormat.format("dd/MM/yy hh:mm:aa",calendar);
                String sans = "sans groupe";


                //set data
                nameTv.setText(name);
                phoneTv.setText(phone);
                emailTv.setText(email);
                noteTv.setText(note);
                addedTimeTv.setText(timeAdd);
                updatedTimeTv.setText(timeUpdate);

                GroupModel group=getGroupById(groupId);

                if(group==null){
                    groupTv.setText(sans);

                }else {

                    groupTv.setText(group.getName());
                }
                try {
                    if (image.equals("null")){
                        profileIv.setImageResource(R.drawable.baseline_account_circle_24);
                    } else {
                        Log.d("Debug", "Image value: " + image);
                        profileIv.setImageURI(Uri.parse(image));
                    }
                } catch (Exception e) {
                    Log.e("DetailActivity", "Error loading image: " + e.getMessage(), e);
                }


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
            Intent intent = new Intent(DetailActivity.this, AddEditContact.class);
            intent.putExtra("ID", id); // Passer l'ID du contact à l'activité d'édition
            intent.putExtra("NAME", name);
            intent.putExtra("PHONE", phone);
            intent.putExtra("EMAIL", email);
            intent.putExtra("NOTE", note);
            intent.putExtra("ADDEDTIME", addTime);
            intent.putExtra("UPDATEDTIME", updateTime);
            intent.putExtra("IMAGE", image);
            intent.putExtra(Constants.C_GROUP_ID, groupId);
            intent.putExtra("isEditMode",true);
            startActivity(intent);
            return true;
        } else if (id2 == R.id.deleteContact) {
            // Demandez une confirmation avant de supprimer le contact
            new AlertDialog.Builder(this)
                    .setMessage("Êtes-vous sûr de vouloir supprimer ce contact ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        // Supprimer le contact de la base de données
                        dbHelper.deleteContact(id);
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

    public GroupModel getGroupById(String groupId) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_GROUPS + " WHERE " + Constants.COLUMN_GROUP_ID + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{groupId});

        GroupModel groupModel = null;
        if (cursor != null && cursor.moveToFirst()) {
            groupModel = new GroupModel(
                    cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_GROUP_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_GROUP_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_GROUP_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_GROUP_ADDED_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_GROUP_UPDATED_TIME))
            );
        }

        // Close the cursor
        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return groupModel;
    }
}