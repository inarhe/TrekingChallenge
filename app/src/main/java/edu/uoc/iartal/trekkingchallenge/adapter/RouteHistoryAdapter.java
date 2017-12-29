package edu.uoc.iartal.trekkingchallenge.adapter;

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
import edu.uoc.iartal.trekkingchallenge.model.Finished;

/**
 * Created by Ingrid Artal on 22/12/2017.
 */

public class RouteHistoryAdapter extends ArrayAdapter<Finished> {

    private Context mContext;
    private ArrayList<Finished> finishedRoutes;
    private Finished finished;
    private  ViewHolder holder;
    private View row;

    public RouteHistoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Finished> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.finishedRoutes = objects;
    }

    // Holda views of the ListView to improve its scrolling performance
    static class ViewHolder {
        TextView textViewName, textViewDate, textViewTime, textViewDistance;

        ViewHolder(View view){
            textViewName = (TextView) view.findViewById(R.id.tvRouteName);
            textViewDate = (TextView) view.findViewById(R.id.tvRouteDate);
            textViewTime = (TextView) view.findViewById(R.id.tvRouteTime);
            textViewDistance = (TextView) view.findViewById(R.id.tvRouteDistance);
        }
    }

    @Override
    public int getCount() {
        return finishedRoutes.size();
    }

    @Nullable
    @Override
    public Finished getItem(int position) {
        return finishedRoutes.get(position);
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
            row = LayoutInflater.from(mContext).inflate(R.layout.adapter_route_history, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        //DatabaseReference databaseChallengeResult = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);
        finished = this.getItem(position);

        holder.textViewName.setText(finished.getName());
        holder.textViewDate.setText(finished.getDate());
        holder.textViewTime.setText(String.valueOf(finished.getTime()) + " h");
        holder.textViewDistance.setText(String.valueOf(finished.getDistance()) + " km");

        return row;
    }
}
