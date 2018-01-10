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
import edu.uoc.iartal.trekkingchallenge.model.Finished;


public class RouteHistoryAdapter extends ArrayAdapter<Finished> {

    private Context context;
    private ArrayList<Finished> finishedRoutes;

    public RouteHistoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Finished> objects) {
        super(context, resource, objects);
        this.context = context;
        this.finishedRoutes = objects;
    }

    // Link layout elements to variables only once
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

    /**
     * Show each item of user route results list in list view
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
            row = LayoutInflater.from(context).inflate(R.layout.adapter_route_history, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Finished finished = this.getItem(position);

        holder.textViewName.setText(finished.getName());
        holder.textViewDate.setText(finished.getDate());
        holder.textViewTime.setText(Integer.toString(finished.getHours()) + "h" + Integer.toString(finished.getMinutes()));
        holder.textViewDistance.setText(String.valueOf(finished.getDistance()) + " km");

        return row;
    }
}
