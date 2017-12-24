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

public class ChallengeResultAdapter extends ArrayAdapter<ChallengeResult> {

    private Context mContext;
    private ArrayList<ChallengeResult> challengeResults;

    public ChallengeResultAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ChallengeResult> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.challengeResults = objects;
    }

    // Holda views of the ListView to improve its scrolling performance
    static class ViewHolder {
        TextView textViewPosition, textViewUser, textViewTime, textViewDistance;

        ViewHolder(View view){
            textViewPosition = (TextView) view.findViewById(R.id.tvPosition);
            textViewUser = (TextView) view.findViewById(R.id.tvUser);
            textViewTime = (TextView) view.findViewById(R.id.tvTime);
            textViewDistance = (TextView) view.findViewById(R.id.tvDistance);
        }
    }

    @Override
    public int getCount() {
        return challengeResults.size();
    }

    @Nullable
    @Override
    public ChallengeResult getItem(int position) {
        return challengeResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get challenge result information
      /*  String user = getItem(position).getUser();
        String time = getItem(position).getTime().toString();
        String distance = getItem(position).getDistance().toString();*/

        // Create the challenge result object with the information
       // ChallengeResult challengeResult = new Cha
       // convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_show_challenge, parent, false);
      //  convertView = inflater.inflate(mResource, parent, false);

        View row = convertView;
        ViewHolder holder = null;

        if (row == null){
            row = LayoutInflater.from(mContext).inflate(R.layout.adapter_challenge_results, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
       /* TextView textViewUser = (TextView) convertView.findViewById(R.id.tvUser);
        TextView textViewTime = (TextView) convertView.findViewById(R.id.tvTime);
        TextView textViewDistance = (TextView) convertView.findViewById(R.id.tvDistance);*/

        ChallengeResult challengeResult = this.getItem(position);

        holder.textViewPosition.setText(Integer.toString(position + 1));
        holder.textViewUser.setText(challengeResult.getUser());
        holder.textViewTime.setText(challengeResult.getTime().toString() + " h");
        holder.textViewDistance.setText(challengeResult.getDistance().toString() + " km");

        return row;
    }
}
