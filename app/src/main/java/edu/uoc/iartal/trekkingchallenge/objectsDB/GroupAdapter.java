package edu.uoc.iartal.trekkingchallenge.objectsDB;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.group.ShowGroupActivity;

/**
 * Created by Ingrid Artal on 12/11/2017.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    List<Group> groups;
    Boolean isVisible = false;
    ArrayList<Boolean> isVisibleArray = new ArrayList<>();
    ArrayList<String> keys = new ArrayList<>();


    // Object which represents a list item and save view references
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGroupName, textViewGroupDesc, textViewIsPublic;
        ImageView imageViewGroup;
        ImageButton buttonDelete;
        CardView cardView;


        public GroupViewHolder(View view) {
            super(view);
            textViewGroupName = (TextView) view.findViewById(R.id.cvUserAlias);
            textViewGroupDesc = (TextView) view.findViewById(R.id.cvUserName);
            textViewIsPublic = (TextView) view.findViewById(R.id.cvisPublic);
            imageViewGroup = (ImageView) view.findViewById(R.id.cvUserPhoto);
            buttonDelete = (ImageButton) view.findViewById(R.id.icDelGroupAdmin);
            cardView = (CardView) view.findViewById(R.id.cardViewGroup);

        }
    }

    public GroupAdapter(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Inflates new list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_group, viewGroup, false);

        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder viewHolder, final int position) {
       // Modify content of each list item
        viewHolder.textViewGroupName.setText(groups.get(position).getIdGroup());
        viewHolder.textViewGroupDesc.setText(groups.get(position).getGroupDescription());
        viewHolder.imageViewGroup.setImageResource(R.drawable.ic_people);
        if (groups.get(position).getIsPublic()) {
            viewHolder.textViewIsPublic.setText(R.string.publicGroup);
        } else {
            viewHolder.textViewIsPublic.setText(R.string.privateGroup);
        }

        if (isVisibleArray.isEmpty()){
            viewHolder.buttonDelete.setVisibility(View.GONE);
        } else {
            if(isVisibleArray.get(position)){
                viewHolder.buttonDelete.setVisibility(View.VISIBLE);
            } else {
                viewHolder.buttonDelete.setVisibility(View.GONE);
            }
        }


        // When an item is clicked starts show detail group activity

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, ShowGroupActivity.class);
                intent.putExtra("groupName", groups.get(position).getGroupName());
                intent.putExtra("groupDescription", groups.get(position).getGroupDescription());
                intent.putExtra("members", groups.get(position).getNumberOfMembers());
                intent.putExtra("groupKey", groups.get(position).getIdGroup());

                context.startActivity(intent);
            }
        });

        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keys.removeAll(keys);
                final String id = groups.get(position).getIdGroup();
                final String name = groups.get(position).getGroupName();
                final Context context = v.getContext();
                DatabaseReference databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);

                databaseGroup.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                            Group group = groupSnapshot.getValue(Group.class);

                            if (group.getIdGroup().equals(id)) {
                                for (String key : group.getMembers().keySet()) {
                                    keys.add(key);
                                }

                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //TO-DO
                    }
                });

                databaseGroup.child(groups.get(position).getGroupName()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context , R.string.groupDeleted, Toast.LENGTH_SHORT).show();
                            DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
                            for (String user:keys){
                                databaseUser.child(user).child("groups").child(name).removeValue();
                            }

                        } else {

                            Toast.makeText(context, R.string.groupNotDeleted, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void setVisibility(Boolean visible){
        if (visible){
            isVisibleArray.add(true);
        } else {
            isVisibleArray.add(false);
        }
     //   Log.i("VISIBLE",isVisible.toString());

     //   this.notifyDataSetChanged();
    }

    public void setFilter(List<Group> filterGroups) {
        groups = new ArrayList<>();
        groups.addAll(filterGroups);
        notifyDataSetChanged();
    }



}
