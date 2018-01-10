/* Copyright 2018 Ingrid Artal Hermoso

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package edu.uoc.iartal.trekkingchallenge.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnCompleteTaskListener;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Group;
import edu.uoc.iartal.trekkingchallenge.model.Message;
import edu.uoc.iartal.trekkingchallenge.model.Trip;
import edu.uoc.iartal.trekkingchallenge.trip.EditTripActivity;
import edu.uoc.iartal.trekkingchallenge.trip.ShowTripActivity;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> trips;
    private ArrayList<Boolean> isVisibleArray = new ArrayList<>();
    private ArrayList<String> tripMembers = new ArrayList<>();
    private ArrayList<String> tripMessages = new ArrayList<>();
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

                // Get trip messages
                getMessages(position);

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
     * Get id of trip messages
     * @param position
     */
    private void getMessages(int position){
        tripMessages.clear();
        Trip trip = trips.get(position);
        tripMessages.addAll(trip.getMessages().keySet());
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

                        // Remove trip from database
                        controller.executeRemoveTask(databaseTrip, trips.get(position).getId(), new OnCompleteTaskListener() {
                            @Override
                            public void onStart() {
                                //Nothing to do
                            }

                            @Override
                            public void onSuccess() {
                                deleteTripFromMembers(position);
                                deleteMessages(position);


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

    /**
     * Delete dependencies of trip members
     * @param position
     */
    private void deleteTripFromMembers(int position){
        DatabaseReference databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);
        for (String member : tripMembers) {
            controller.removeValue(databaseUser, member, FireBaseReferences.USER_TRIPS_REFERENCE, trips.get(position).getId());
        }
    }

    /**
     * Delete trip messages and their dependencies
     * @param position
     */
    private void deleteMessages(int position){
        final DatabaseReference databaseMessage = controller.getDatabaseReference(FireBaseReferences.MESSAGE_REFERENCE);
        final DatabaseReference databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        if (tripMessages.size() != 0){
            controller.readDataOnce(databaseMessage, new OnGetDataListener() {
                @Override
                public void onStart() {
                    //Nothing to do
                }

                @Override
                public void onSuccess(DataSnapshot data) {
                    for (DataSnapshot messageSnapshot : data.getChildren()){
                        Message message = messageSnapshot.getValue(Message.class);
                        if (tripMessages.contains(message.getId())){
                            String sender = message.getUser();
                            controller.removeObject(databaseMessage, message.getId());
                            controller.removeValue(databaseUser, sender, FireBaseReferences.MESSAGES_REFERENCE, message.getId());
                        }
                    }
                }

                @Override
                public void onFailed(DatabaseError databaseError) {
                    Log.e("DelTripMssg error", databaseError.getMessage());
                }
            });

        }
    }
}