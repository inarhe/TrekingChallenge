package edu.uoc.iartal.trekkingchallenge.ObjectsDB;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;

/**
 * Created by Ingrid Artal on 12/11/2017.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder>{

    private List<Group> groups;

    public static class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
      //  CardView cardView;
        TextView textViewGroupName, textViewGroupDesc;
        ImageView imageViewGroup;

        public GroupViewHolder(View view) {
            super(view);
          //  cardView = (CardView) view.findViewById(R.id.cardViewGroup);
            view.setOnClickListener(this);
            textViewGroupName = (TextView) view.findViewById(R.id.cvGroupName);
            textViewGroupDesc = (TextView) view.findViewById(R.id.cvGroupDescription);
            imageViewGroup = (ImageView) view.findViewById(R.id.cvGroupPhoto);

        }

        @Override
        public void onClick(View v) {
            //start activity and pass data
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_group, viewGroup, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder viewHolder, int i) {
       // Group group = groups.get(position);
        viewHolder.textViewGroupName.setText(groups.get(i).getGroupName());
        viewHolder.textViewGroupDesc.setText(groups.get(i).getGroupDescription());
        viewHolder.imageViewGroup.setImageResource(R.drawable.ic_people);

    }

/*    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }*/


}
