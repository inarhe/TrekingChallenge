package edu.uoc.iartal.trekkingchallenge.objectsDB;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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
public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    List<Trip> trips;
    Boolean isVisible = false;
    ArrayList<Boolean> isVisibleArray = new ArrayList<>();


    // Object which represents a list item and save view references
    public static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTripName, textViewTripDesc, textViewIsPublic;
        ImageView imageViewTrip;
        ImageButton imageButton;


        public TripViewHolder(View view) {
            super(view);
            textViewTripName = (TextView) view.findViewById(R.id.cvTripName);
          //  textViewTripDesc = (TextView) view.findViewById(R.id.cvTripDesc);
            textViewIsPublic = (TextView) view.findViewById(R.id.cvisPublic);
            imageViewTrip = (ImageView) view.findViewById(R.id.cvTripPhoto);
            imageButton = (ImageButton) view.findViewById(R.id.icDelTripAdmin);

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
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, ShowGroupActivity.class);
                intent.putExtra("tripName", trips.get(position).getTripName());
                intent.putExtra("tripDescription", trips.get(position).getTripDescription());
                intent.putExtra("members", trips.get(position).getNumberOfMembers());
                intent.putExtra("tripKey", trips.get(position).getIdTrip());

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

    public void setFilter(List<Trip> filterTrips) {
        trips = new ArrayList<>();
        trips.addAll(filterTrips);
        notifyDataSetChanged();
    }



}
