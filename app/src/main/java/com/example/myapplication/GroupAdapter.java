package com.example.myapplication;

// GroupAdapter.java

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private Context context;
    private List<GroupModel> groupList;

    public GroupAdapter(Context context, List<GroupModel> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        GroupModel group = groupList.get(position);
        holder.groupName.setText(group.getName());
        holder.groupDescription.setText(group.getDescription());
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        TextView groupName;
        TextView groupDescription;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            groupDescription = itemView.findViewById(R.id.group_description);
            // Ajoutez un listener pour l'item de la liste
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            // Lorsque l'item est cliqué, naviguez vers l'activité de détail
            Intent intent = new Intent(context, DetailGroupActivity.class);
            intent.putExtra("contactId", groupList.get(getAdapterPosition()).getId());
            context.startActivity(intent);
        }
    }

}
