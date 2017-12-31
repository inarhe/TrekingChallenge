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
import edu.uoc.iartal.trekkingchallenge.model.ChallengeResult;


public class ChallengeHistoryAdapter extends ArrayAdapter<ChallengeResult> {

    private Context context;
    private ArrayList<ChallengeResult> challengeResults;

    public ChallengeHistoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ChallengeResult> objects) {
        super(context, resource, objects);
        this.context = context;
        this.challengeResults = objects;
    }

    // Link layout elements to variables only once
    static class ViewHolder {
        TextView textViewName, textViewDate, textViewPosition, textViewTime, textViewDistance;

        ViewHolder(View view){
            textViewName = (TextView) view.findViewById(R.id.tvChallengeName);
            textViewDate = (TextView) view.findViewById(R.id.tvChallengeDate);
            textViewPosition = (TextView) view.findViewById(R.id.tvChallengePosition);
            textViewTime = (TextView) view.findViewById(R.id.tvChallengeTime);
            textViewDistance = (TextView) view.findViewById(R.id.tvChallengeDistance);
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

    /**
     * Show each item of user challenge results list in list view
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
            row = LayoutInflater.from(context).inflate(R.layout.adapter_challenge_history, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ChallengeResult challengeResult = this.getItem(position);

        holder.textViewName.setText(challengeResult.getName());
        holder.textViewDate.setText(challengeResult.getDate());
        holder.textViewPosition.setText(Integer.toString(challengeResult.getPosition()));
        holder.textViewTime.setText(String.valueOf(challengeResult.getTime()) + " h");
        holder.textViewDistance.setText(String.valueOf(challengeResult.getDistance()) + " km");

        return row;
    }
}
