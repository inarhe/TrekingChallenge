package edu.uoc.iartal.trekkingchallenge.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.group.EditGroupActivity;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.group.ShowGroupActivity;
import edu.uoc.iartal.trekkingchallenge.model.Group;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private List<Group> groups;
    private ArrayList<Boolean> isVisibleArray = new ArrayList<>();
    private ArrayList<String> groupMembers = new ArrayList<>();
    private Context context;

    // Object which represents a list item and save view references
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGroupName, textViewIsPublic;
        ImageView imageViewGroup;
        ImageButton buttonDelete, buttonEdit;
        CardView cardView;

        // Link layout elements to variables
        public GroupViewHolder(View view) {
            super(view);
            textViewGroupName = (TextView) view.findViewById(R.id.cvGroupName);
            textViewIsPublic = (TextView) view.findViewById(R.id.cvIsPublic);
            imageViewGroup = (ImageView) view.findViewById(R.id.cvGroupPhoto);
            buttonDelete = (ImageButton) view.findViewById(R.id.icDelGroupAdmin);
            buttonEdit = (ImageButton) view.findViewById(R.id.icEditGroupAdmin);
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
        if (position < groups.size()){
            viewHolder.textViewGroupName.setText(groups.get(position).getName());
            viewHolder.imageViewGroup.setImageResource(R.drawable.ic_people);

            if (groups.get(position).getIsPublic()) {
                viewHolder.textViewIsPublic.setText(R.string.publicGroup);
            } else {
                viewHolder.textViewIsPublic.setText(R.string.privateGroup);
            }

            // Show delete button only if current user is group admin
            if (isVisibleArray.isEmpty()){
                viewHolder.buttonDelete.setVisibility(View.GONE);
                viewHolder.buttonEdit.setVisibility(View.GONE);
            } else {
                if(isVisibleArray.get(position)){
                    viewHolder.buttonDelete.setVisibility(View.VISIBLE);
                    viewHolder.buttonEdit.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.buttonDelete.setVisibility(View.GONE);
                    viewHolder.buttonEdit.setVisibility(View.GONE);
                }
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
                // Get group members
                getMembers(position);
                context = v.getContext();

                // Delete group and its dependencies
                deleteGroup(position);
            }
        });

        // When edit button is clicked, starts edit detail group activity
        viewHolder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, EditGroupActivity.class);
                intent.putExtra("group", groups.get(position));

                context.startActivity(intent);
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

    public void removeVisibility(){
        isVisibleArray.clear();
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

    /**
     * Get names of group members
     * @param position
     */
    private void getMembers(int position){
        groupMembers.clear();
        Group group = groups.get(position);
        groupMembers.addAll(group.getMembers().keySet());
    }

    /**
     * Delete selected group and update groups list of each member
     * @param position
     */
    private void deleteGroup(final int position){
        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.groupAskDeleted));
        builder.setCancelable(true);

        builder.setPositiveButton(
                context.getResources().getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        DatabaseReference databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);
                        databaseGroup.child(groups.get(position).getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
                                    for (String user:groupMembers){
                                        databaseUser.child(user).child(FireBaseReferences.USER_GROUPS_REFERENCE).child(groups.get(position).getId()).removeValue();
                                    }
                                    Toast.makeText(context , R.string.groupDeleted, Toast.LENGTH_SHORT).show();
                                    groups.remove(position);
                                    isVisibleArray.remove(position);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, R.string.groupNotDeleted, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

        builder.setNegativeButton(
                context.getResources().getString(R.string.cancelButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
