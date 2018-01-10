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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.model.TripDone;


public class TripHistoryAdapter extends ArrayAdapter<TripDone> {

    private Context context;
    private ArrayList<TripDone> tripsDone;

    public TripHistoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<TripDone> objects) {
        super(context, resource, objects);
        this.context = context;
        this.tripsDone = objects;
    }

    // Link layout elements to variables only once
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

    /**
     * Show each item of user trip results list in list view
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null){
            row = LayoutInflater.from(context).inflate(R.layout.adapter_trip_history, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        TripDone tripDone = this.getItem(position);

        holder.textViewName.setText(tripDone.getTripName());
        holder.textViewDate.setText(tripDone.getDate());
        holder.textViewRoute.setText(tripDone.getRouteName());
        holder.textViewTime.setText(Integer.toString(tripDone.getHours()) + "h" + Integer.toString(tripDone.getMinutes()));
        holder.textViewDistance.setText(String.valueOf(tripDone.getDistance()) + " km");

        return row;
    }
}
