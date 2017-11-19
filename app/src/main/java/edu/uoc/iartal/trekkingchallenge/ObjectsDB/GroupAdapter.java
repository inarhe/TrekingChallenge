package edu.uoc.iartal.trekkingchallenge.ObjectsDB;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.instantapps.ActivityCompat;

import java.util.List;

import edu.uoc.iartal.trekkingchallenge.AccessActivity;
import edu.uoc.iartal.trekkingchallenge.LoginActivity;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.ShowGroupActivity;

import static java.security.AccessController.getContext;

/**
 * Created by Ingrid Artal on 12/11/2017.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groups;

    public static class GroupViewHolder extends RecyclerView.ViewHolder{//} implements View.OnClickListener {
        CardView cardView;
        TextView textViewGroupName, textViewGroupDesc;
        ImageView imageViewGroup;

        public GroupViewHolder(View view) {
            super(view);
          //  cardView = (CardView) view.findViewById(R.id.cardViewGroup);
            //view.setOnClickListener(this);
            textViewGroupName = (TextView) view.findViewById(R.id.cvGroupName);
            textViewGroupDesc = (TextView) view.findViewById(R.id.cvGroupDescription);
            imageViewGroup = (ImageView) view.findViewById(R.id.cvGroupPhoto);
           //cardView = (CardView) view.findViewById(R.id.cardViewGroup);



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
    public void onBindViewHolder(GroupViewHolder viewHolder, final int position) {
       // Group group = groups.get(position);
        viewHolder.textViewGroupName.setText(groups.get(position).getGroupName());
        viewHolder.textViewGroupDesc.setText(groups.get(position).getGroupDescription());
        viewHolder.imageViewGroup.setImageResource(R.drawable.ic_people);
       /* viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ShowGroupActivity.class);
                intent.putExtra("groupName", groups.get(position).getGroupName());
               // Log.i("EXTRANAME", "onClick: " + groups.get(position).getGroupName());
                intent.putExtra("groupDescription", groups.get(position).getGroupDescription());
               // Log.i("EXTRADESC", "onClick: " + groups.get(position).getGroupDescription());
                intent.putExtra("members", groups.get(position).getMembers().length());
              //  Log.i("EXTRAMEMB", "onClick: " + groups.get(position).getMembers().length());*/
                context.startActivity(intent);
            }
        });

    }

/*    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }*/


}
