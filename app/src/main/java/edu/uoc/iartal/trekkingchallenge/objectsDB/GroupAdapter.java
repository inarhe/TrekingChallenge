package edu.uoc.iartal.trekkingchallenge.objectsDB;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.group.ShowGroupActivity;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private List<Group> groups;
    private ArrayList<Boolean> isVisibleArray = new ArrayList<>();
    private ArrayList<String> groupMembers = new ArrayList<>();

    // Object which represents a list item and save view references
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGroupName, textViewGroupDesc, textViewIsPublic;
        ImageView imageViewGroup;
        ImageButton buttonDelete;
        CardView cardView;

        // Link layout elements to variables
        public GroupViewHolder(View view) {
            super(view);
            textViewGroupName = (TextView) view.findViewById(R.id.cvGroupName);
            textViewGroupDesc = (TextView) view.findViewById(R.id.cvGroupDesc);
            textViewIsPublic = (TextView) view.findViewById(R.id.cvisPublic);
            imageViewGroup = (ImageView) view.findViewById(R.id.cvGroupPhoto);
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

    /**
     * Modify content of each list item
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(GroupViewHolder viewHolder, final int position) {
        viewHolder.textViewGroupName.setText(groups.get(position).getName());
        viewHolder.textViewGroupDesc.setText(groups.get(position).getDescription());
        viewHolder.imageViewGroup.setImageResource(R.drawable.ic_people);

        if (groups.get(position).getIsPublic()) {
            viewHolder.textViewIsPublic.setText(R.string.publicGroup);
        } else {
            viewHolder.textViewIsPublic.setText(R.string.privateGroup);
        }

        // Show delete button only if current user is group admin
        if (isVisibleArray.isEmpty()){
            viewHolder.buttonDelete.setVisibility(View.GONE);
        } else {
            if(isVisibleArray.get(position)){
                viewHolder.buttonDelete.setVisibility(View.VISIBLE);
            } else {
                viewHolder.buttonDelete.setVisibility(View.GONE);
            }
        }

        // When cardview is clicked starts show detail group activity
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, ShowGroupActivity.class);
                intent.putExtra("group", groups.get(position));

                context.startActivity(intent);
            }
        });

        // When delete button is clicked, delete group and all its members dependencies
        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize variables
                groupMembers.clear();
                final String id = groups.get(position).getId();
                final String name = groups.get(position).getName();
                final Context context = v.getContext();
                DatabaseReference databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);

                // Get names of group members
                databaseGroup.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                            Group group = groupSnapshot.getValue(Group.class);
                            if (group.getId().equals(id)) {
                                for (String name : group.getMembers().keySet()) {
                                    groupMembers.add(name);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //TO-DO
                    }
                });

                // Delete selected group and update members groups
                databaseGroup.child(groups.get(position).getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
                            for (String user:groupMembers){
                                databaseUser.child(user).child(FireBaseReferences.USER_GROUPS_REFERENCE).child(id).removeValue();
                            }
                            Toast.makeText(context , R.string.groupDeleted, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, R.string.groupNotDeleted, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * Define visibility of delete button for each element
     * @param visible
     */
    public void setVisibility(Boolean visible){
        if (visible){
            isVisibleArray.add(true);
        } else {
            isVisibleArray.add(false);
        }
    }

    /**
     * Updates group list with search result
     * @param filterGroups
     */
    public void setFilter(List<Group> filterGroups) {
        groups = new ArrayList<>();
        groups.addAll(filterGroups);
        notifyDataSetChanged();
    }



}
