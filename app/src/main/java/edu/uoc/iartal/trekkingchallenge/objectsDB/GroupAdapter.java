package edu.uoc.iartal.trekkingchallenge.objectsDB;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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


    // Object which represents a list item and save view references
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGroupName, textViewGroupDesc, textViewIsPublic;
        ImageView imageViewGroup;
        ImageButton imageButton;


        public GroupViewHolder(View view) {
            super(view);
            textViewGroupName = (TextView) view.findViewById(R.id.cvUserAlias);
            textViewGroupDesc = (TextView) view.findViewById(R.id.cvUserName);
            textViewIsPublic = (TextView) view.findViewById(R.id.cvisPublic);
            imageViewGroup = (ImageView) view.findViewById(R.id.cvUserPhoto);
            imageButton = (ImageButton) view.findViewById(R.id.icDelGroupAdmin);

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
            viewHolder.imageButton.setVisibility(View.GONE);
        } else {
            if(isVisibleArray.get(position)){
                viewHolder.imageButton.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imageButton.setVisibility(View.GONE);
            }
        }


        // When an item is clicked starts show detail group activity
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
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
