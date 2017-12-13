package edu.uoc.iartal.trekkingchallenge.objects;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.trip.ShowTripActivity;


public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> trips;
    private ArrayList<Boolean> isVisibleArray = new ArrayList<>();
    private ArrayList<String> tripMembers = new ArrayList<>();
    private DatabaseReference databaseTrip;
    private String idTrip;
    private Context context;

    // Object which represents a list item and save view references
    public static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTripName, textViewTripDate, textViewIsPublic;
        ImageView imageViewTrip;
        ImageButton buttonDelete;
        CardView cardView;

        // Link layout elements to variables
        public TripViewHolder(View view) {
            super(view);
            textViewTripName = (TextView) view.findViewById(R.id.cvTripName);
            textViewTripDate = (TextView) view.findViewById(R.id.cvTripDate);
            textViewIsPublic = (TextView) view.findViewById(R.id.cvisPublic);
            imageViewTrip = (ImageView) view.findViewById(R.id.cvTripPhoto);
            buttonDelete = (ImageButton) view.findViewById(R.id.icDelTripAdmin);
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

    /**
     * Modify content of each list item
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(TripViewHolder viewHolder, final int position) {
        viewHolder.textViewTripName.setText(trips.get(position).getName());
        viewHolder.textViewTripDate.setText(trips.get(position).getDate());
        viewHolder.imageViewTrip.setImageResource(R.drawable.ic_trip);

        if (trips.get(position).getPublic()) {
            viewHolder.textViewIsPublic.setText(R.string.publicTrip);
        } else {
            viewHolder.textViewIsPublic.setText(R.string.privateTrip);
        }

        // Show delete button only if current user is trip admin
        if (isVisibleArray.isEmpty()){
            viewHolder.buttonDelete.setVisibility(View.GONE);
        } else {
            if(isVisibleArray.get(position)){
                viewHolder.buttonDelete.setVisibility(View.VISIBLE);
            } else {
                viewHolder.buttonDelete.setVisibility(View.GONE);
            }
        }

        // When cardview is clicked starts show detail trip activity
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, ShowTripActivity.class);
                intent.putExtra("trip", trips.get(position));

                context.startActivity(intent);
            }
        });

        // When delete button is clicked, delete trip and all its members dependencies
        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get trip members
                getMembers(position, v);
                // Delete trip and its dependencies
                deleteTrip(position);
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
     * Updates trip list with search result
     * @param filterTrips
     */
    public void setFilter(List<Trip> filterTrips) {
        trips = new ArrayList<>();
        trips.addAll(filterTrips);
        notifyDataSetChanged();
    }

    /**
     * Get names of trip members
     * @param position
     * @param v
     */
    private void getMembers(int position, View v){
        tripMembers.clear();
        idTrip = trips.get(position).getId();
        final String name = trips.get(position).getName();
        context = v.getContext();
        databaseTrip = FirebaseDatabase.getInstance().getReference(FireBaseReferences.TRIP_REFERENCE);

        // Get names of trip members
        databaseTrip.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    Trip trip = tripSnapshot.getValue(Trip.class);
                    if (trip.getId().equals(idTrip)) {
                        for (String name : trip.getMembers().keySet()) {
                            tripMembers.add(name);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO-DO
            }
        });
    }

    /**
     * Delete selected trip and update trips list of each member
     * @param position
     */
    private void deleteTrip(final int position){
        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.tripAskDeleted));
        builder.setCancelable(true);

        builder.setPositiveButton(
                context.getResources().getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        databaseTrip.child(trips.get(position).getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
                                    for (String user:tripMembers){
                                        databaseUser.child(user).child(FireBaseReferences.USER_TRIPS_REFERENCE).child(idTrip).removeValue();
                                    }
                                    Toast.makeText(context , R.string.tripDeleted, Toast.LENGTH_SHORT).show();
                                    isVisibleArray.remove(position);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, R.string.tripNotDeleted, Toast.LENGTH_SHORT).show();
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
