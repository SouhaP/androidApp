package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AddEditContact extends AppCompatActivity {
    //declaration des var
    private ImageView profileIv;
    private EditText nameEt,phoneEt,emailEt,noteEt;
    private Boolean isEditMode;
    private FloatingActionButton fab;
    private static final int REQUEST_CODE = 2;
    //string var
    private Spinner groupSpinner;
    private List<GroupModel> groupList = new ArrayList<>();
    private GroupModel selectedGroup;
    private String id,image,name,phone,email,note,addedTime,updatedTime;

    //action bar
    ActionBar actionBar;

    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private static final int REQUEST_CODE_GALLERY = 101;
    private static final int REQUEST_CODE_CAMERA = 102;

    private Uri imageUri;

    private DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);

        //initialiser dbHelper
        dbHelper = new DbHelper(this);

//action bar
        actionBar=getSupportActionBar();

        actionBar.setTitle("Ajouter un Contact");
        //back button activated
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

    //init view
        profileIv = findViewById(R.id.profileIv);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        emailEt = findViewById(R.id.emailEt);
        noteEt = findViewById(R.id.noteEt);
        fab = findViewById(R.id.fab);

    //add event to save data
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode",false);
        groupSpinner = findViewById(R.id.groupSpinner);
        groupList = dbHelper.getAllGroupData();
        // Remplissez la liste de groupes ici. C'est un exemple; vous devrez peut-être obtenir les groupes de votre base de données ou d'une autre source.
        groupList.add(new GroupModel("0", "sans groupe", "Default groupe", "time1", "time2"));
     //   groupList.add(new GroupModel("2", "Friends", "Friends circle", "time1", "time2"));
        // ... Ajoutez d'autres groupes selon vos besoins

        ArrayAdapter<GroupModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter);

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroup = groupList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGroup = null;
            }
        });



        if (isEditMode){
            //set toolbar title
            actionBar.setTitle("Update Contact");

            //get the other value from intent
            id = intent.getStringExtra("ID");
            name = intent.getStringExtra("NAME");
            phone = intent.getStringExtra("PHONE");
            email = intent.getStringExtra("EMAIL");
            note = intent.getStringExtra("NOTE");
            addedTime = intent.getStringExtra("ADDEDTIME");
            updatedTime = intent.getStringExtra("UPDATEDTIME");
            image = intent.getStringExtra("IMAGE");

            //set value in editText field
            nameEt.setText(name);
            phoneEt.setText(phone);
            emailEt.setText(email);
            noteEt.setText(note);

            imageUri = Uri.parse(image);
            String groupId = intent.getStringExtra(Constants.C_GROUP_ID); // Supposons que vous avez un ID de groupe dans votre intent
            for (int i =0; i < groupList.size(); i++) {

                if (groupList.get(i).getId().equals(groupId)) {
                    groupSpinner.setSelection(i);
                    break;
                }
            }
            if (image == null || TextUtils.isEmpty(image)) {
                profileIv.setImageResource(R.drawable.baseline_account_circle_24);
            } else {
                Uri uri = Uri.parse(image);
                profileIv.setImageURI(uri);
            }

        }else {
            // add mode on
            actionBar.setTitle("Add Contact");
        }

fab.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        saveData();
    }
});






      profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    openGalleryAndCamera();
                } else {
                    requestPermissions();
                }
            }
        });
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            // Handle gallery result
            Toast.makeText(this, "Image selected from gallery", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            // Handle camera result
            Toast.makeText(this, "Image captured from camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void modifyAndDisplayImage(Uri uri) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);

            // Afficher l'image redimensionnée avec Picasso
            Picasso.get().load(uri).into(profileIv);

            // Note: Vous pouvez également sauvegarder l'image modifiée si nécessaire
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


private void saveData(){
    //take user giver data in variable
    name = nameEt.getText().toString();
    phone = phoneEt.getText().toString();
    email = emailEt.getText().toString();
    note = noteEt.getText().toString();
    // get current time to save as added time
    String timeStamp = ""+System.currentTimeMillis();

    if (!name.isEmpty() || !phone.isEmpty() || !email.isEmpty() || !note.isEmpty()){
        //save data ,if user have only one data
//check edit or add mode to save data in sql
        if (isEditMode){
            // edit mode
            String groupId = String.valueOf(((GroupModel) groupSpinner.getSelectedItem()).getId());

            dbHelper.updateContact(
                    ""+id,
                    ""+imageUri,
                    ""+name,
                    ""+phone,
                    ""+email,
                    ""+note,
                    ""+addedTime,
                    ""+timeStamp, // updated time will new time
                    ""+groupId
            );

            Toast.makeText(getApplicationContext(), "Updated Successfully....", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            String groupId = String.valueOf(((GroupModel) groupSpinner.getSelectedItem()).getId());
            long id = dbHelper.insertContact(
                    "" + imageUri,
                    "" + name,
                    "" + phone,
                    "" + email,
                    "" + note,
                    "" + timeStamp,
                    "" + timeStamp,
                    ""+groupId
            );
            Toast.makeText(getApplicationContext(), "Inserted Successfully.... " + groupId, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }else {
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
    private boolean checkPermissions() {
        boolean isGalleryPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean isCameraPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        Log.d("Permissions", "Gallery Permission: " + isGalleryPermissionGranted);
        Log.d("Permissions", "Camera Permission: " + isCameraPermissionGranted);

        return isGalleryPermissionGranted && isCameraPermissionGranted;
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS);
    }

    private void openGalleryAndCamera() {
        // Open gallery or camera here
        Toast.makeText(this, "Opening Gallery and Camera...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openGalleryAndCamera();
            } else {
                Toast.makeText(this, "Permissions denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }









}
