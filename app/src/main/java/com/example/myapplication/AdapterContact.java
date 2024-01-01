package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterContact extends RecyclerView.Adapter<AdapterContact.ContactViewHolder> {


    private ArrayList<ModelContact> contactsList;
    private Context context;
    DbHelper dbHelper;
    public AdapterContact(Context context, ArrayList<ModelContact> contactsList) {
        this.context = context;
        this.contactsList = contactsList;
        dbHelper = new DbHelper(context);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater translate item layout to view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
       // contactsList=[('image1','name1',...),('image2','name2',....)],
        ModelContact contact = contactsList.get(position);

        //get data
        //we need only All data
        String id = contact.getId();
        String image = contact.getImage();
        String name = contact.getName();
        String phone= contact.getPhone();
        String email = contact.getEmail();
        String note = contact.getNote();
        String addedTime = contact.getAddedTime();
        String updatedTime = contact.getUpdatedTime();
        String groupId= contact.getGroupId();
       GroupModel group=getGroupById(groupId);

        //set data in view
        holder.contactName.setText(name);
        if(group!=null) {
            holder.groupName.setText(group.getName());
        }else{
            holder.groupName.setText("sans groupe");

        }
        if (image.equals("null")|| image.trim().isEmpty()){
            holder.contactImage.setImageResource(R.drawable.baseline_account_circle_24);
        }else {
            holder.contactImage.setImageURI(Uri.parse(image));
        }

        // Ici, vous pouvez également mettre à jour l'image du contact si nécessaire.
        // Pour cela, vous pouvez utiliser une bibliothèque comme Picasso ou Glide.
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView contactName;
        ImageView contactImage;
        TextView groupName;

        RelativeLayout relativeLayout;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            contactImage = itemView.findViewById(R.id.contact_image);
            groupName = itemView.findViewById(R.id.group_name);

            // Ajoutez un listener pour l'item de la liste
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Lorsque l'item est cliqué, naviguez vers l'activité de détail
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("contactId", contactsList.get(getAdapterPosition()).getId());
            context.startActivity(intent);
        }
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
}