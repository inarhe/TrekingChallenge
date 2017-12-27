package edu.uoc.iartal.trekkingchallenge.objects;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;

/**
 * Created by Ingrid Artal on 22/12/2017.
 */

public class TripHistoryAdapter extends ArrayAdapter<TripDone> {

    private Context mContext;
    private ArrayList<TripDone> tripsDone;
    private String currentUserName;
    private TripDone tripDone;
    private ViewHolder holder;
    private View row;

    public TripHistoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<TripDone> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.tripsDone = objects;
    }

    // Holda views of the ListView to improve its scrolling performance
    static class ViewHolder {
        TextView textViewName, textViewDate, textViewRoute, textViewTime, textViewDistance;

        ViewHolder(View view){
            textViewName = (TextView) view.findViewById(R.id.tvTripName);
            textViewDate = (TextView) view.findViewById(R.id.tvTripDate);
            textViewRoute = (TextView) view.findViewById(R.id.tvTripRoute);
            textViewTime = (TextView) view.findViewById(R.id.tvTripTime);
            textViewDistance = (TextView) view.findViewById(R.id.tvTripDistance);
        }
    }

    @Override
    public int getCount() {
        return tripsDone.size();
    }

    @Nullable
    @Override
    public TripDone getItem(int position) {
        return tripsDone.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        row = convertView;
        holder = null;

        if (row == null){
            row = LayoutInflater.from(mContext).inflate(R.layout.adapter_trip_history, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        //DatabaseReference databaseChallengeResult = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);
        tripDone = this.getItem(position);

        holder.textViewName.setText(tripDone.getTripName());
        holder.textViewDate.setText(tripDone.getDate());
        holder.textViewRoute.setText(tripDone.getRouteName());
        holder.textViewTime.setText(String.valueOf(tripDone.getTime()) + " h");
        holder.textViewDistance.setText(String.valueOf(tripDone.getDistance()) + " km");

        return row;
    }
}
