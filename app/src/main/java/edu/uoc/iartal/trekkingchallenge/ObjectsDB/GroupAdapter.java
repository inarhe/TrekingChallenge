package edu.uoc.iartal.trekkingchallenge.ObjectsDB;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.uoc.iartal.trekkingchallenge.Group;
import edu.uoc.iartal.trekkingchallenge.R;

/**
 * Created by Ingrid Artal on 12/11/2017.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder>{

    List<Group> groups;

    public GroupAdapter(List<Group> groups) {
        this.groups = groups;
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textViewGroupName;
        ImageView imageViewGroup;

        public GroupViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            textViewGroupName = (TextView) itemView.findViewById(R.id.groupName);
            imageViewGroup = (ImageView) itemView.findViewById(R.id.groupPhoto);

        }
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_group, parent, false);
        GroupViewHolder holder = new GroupViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.textViewGroupName.setText(group.getGroupName());
        holder.imageViewGroup.setImageResource(R.drawable.ic_landscape);
       // holder.imageViewGroup
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
