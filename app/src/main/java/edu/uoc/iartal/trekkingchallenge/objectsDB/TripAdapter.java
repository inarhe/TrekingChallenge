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
import edu.uoc.iartal.trekkingchallenge.ShowTripActivity;
import edu.uoc.iartal.trekkingchallenge.group.ShowGroupActivity;

/**
 * Created by Ingrid Artal on 12/11/2017.
 */
public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    List<Trip> trips;
    Boolean isVisible = false;
    ArrayList<Boolean> isVisibleArray = new ArrayList<>();


    // Object which represents a list item and save view references
    public static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTripName, textViewTripDate, textViewIsPublic;
        ImageView imageViewTrip;
        ImageButton imageButton;
        CardView cardView;


        public TripViewHolder(View view) {
            super(view);
            textViewTripName = (TextView) view.findViewById(R.id.cvTripName);
            textViewTripDate = (TextView) view.findViewById(R.id.cvTripDate);
            textViewIsPublic = (TextView) view.findViewById(R.id.cvisPublic);
            imageViewTrip = (ImageView) view.findViewById(R.id.cvTripPhoto);
            imageButton = (ImageButton) view.findViewById(R.id.icDelTripAdmin);
            cardView = (CardView) view.findViewById(R.id.cardViewTrip);

        }
    }

    public TripAdapter(List<Trip> trips) {
        this.trips = trips;
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Inflates new list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_trip, viewGroup, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TripViewHolder viewHolder, final int position) {
       // Modify content of each list item
        viewHolder.textViewTripName.setText(trips.get(position).getIdTrip());
        viewHolder.textViewTripDate.setText(trips.get(position).getDate());
      //  viewHolder.textViewTripDesc.setText(trips.get(position).getTripDescription());
        viewHolder.imageViewTrip.setImageResource(R.drawable.ic_trip);
        if (trips.get(position).getPublic()) {
            viewHolder.textViewIsPublic.setText(R.string.publicTrip);
        } else {
            viewHolder.textViewIsPublic.setText(R.string.privateTrip);
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

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, ShowTripActivity.class);
                intent.putExtra("trip", trips.get(position));

                context.startActivity(intent);
            }
        });

       /* viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
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
        });*/

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

    public void setFilter(List<Trip> filterTrips) {
        trips = new ArrayList<>();
        trips.addAll(filterTrips);
        notifyDataSetChanged();
    }



}
