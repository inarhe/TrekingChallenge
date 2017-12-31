package edu.uoc.iartal.trekkingchallenge.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnCompleteTaskListener;
import edu.uoc.iartal.trekkingchallenge.model.Trip;
import edu.uoc.iartal.trekkingchallenge.trip.EditTripActivity;
import edu.uoc.iartal.trekkingchallenge.trip.ShowTripActivity;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> trips;
    private ArrayList<Boolean> isVisibleArray = new ArrayList<>();
    private ArrayList<String> tripMembers = new ArrayList<>();
    private Context context;
    private FirebaseController controller = new FirebaseController();

    // Object which represents a list item and save view references
    public static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTripName, textViewTripDate, textViewIsPublic;
        ImageView imageViewTrip;
        ImageButton buttonDelete, buttonEdit;
        CardView cardView;

        // Link layout elements to variables
        public TripViewHolder(View view) {
            super(view);
            textViewTripName = (TextView) view.findViewById(R.id.cvTripName);
            textViewTripDate = (TextView) view.findViewById(R.id.cvTripDate);
            textViewIsPublic = (TextView) view.findViewById(R.id.cvIsPublic);
            imageViewTrip = (ImageView) view.findViewById(R.id.cvTripPhoto);
            buttonDelete = (ImageButton) view.findViewById(R.id.icDelTripAdmin);
            buttonEdit = (ImageButton) view.findViewById(R.id.icEditTripAdmin);
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

        // Show delete and edit buttons only if current user is trip admin
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
                getMembers(position);

                context = v.getContext();

                // Delete trip and its dependencies
                deleteTrip(position);
            }
        });

        // When edit button is clicked, starts edit detail trip activity
        viewHolder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context = v.getContext();

                Intent intent = new Intent(context, EditTripActivity.class);
                intent.putExtra("trip", trips.get(position));

                context.startActivity(intent);
            }
        });
    }

    /**
     * Define visibility of delete and edit buttons for each element
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
     */
    private void getMembers(int position){
        tripMembers.clear();
        Trip trip = trips.get(position);
        tripMembers.addAll(trip.getMembers().keySet());
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
                        DatabaseReference databaseTrip = controller.getDatabaseReference(FireBaseReferences.TRIP_REFERENCE);

                        controller.executeRemoveTask(databaseTrip, trips.get(position).getId(), new OnCompleteTaskListener() {
                            @Override
                            public void onStart() {
                                //Nothing to do
                            }

                            @Override
                            public void onSuccess() {
                                DatabaseReference databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);
                                for (String member : tripMembers) {
                                    controller.removeValue(databaseUser, member, FireBaseReferences.USER_TRIPS_REFERENCE, trips.get(position).getId());
                                }
                                Toast.makeText(context, R.string.tripDeleted, Toast.LENGTH_SHORT).show();
                                trips.remove(position);
                                isVisibleArray.remove(position);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFailed() {
                                Toast.makeText(context, R.string.tripNotDeleted, Toast.LENGTH_SHORT).show();
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