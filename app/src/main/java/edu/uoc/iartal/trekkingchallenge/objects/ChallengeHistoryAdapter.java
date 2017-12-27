package edu.uoc.iartal.trekkingchallenge.objects;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;

/**
 * Created by Ingrid Artal on 22/12/2017.
 */

public class ChallengeHistoryAdapter extends ArrayAdapter<ChallengeResult> {

    private Context mContext;
    private ArrayList<ChallengeResult> challengeResults;
    private String currentUserName;
    private  ChallengeResult challengeResult;
    private  ViewHolder holder;
    private View row;

    public ChallengeHistoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ChallengeResult> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.challengeResults = objects;
    }

    // Holda views of the ListView to improve its scrolling performance
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        row = convertView;
        holder = null;

        if (row == null){
            row = LayoutInflater.from(mContext).inflate(R.layout.adapter_challenge_history, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        //DatabaseReference databaseChallengeResult = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);
        challengeResult = this.getItem(position);

        holder.textViewName.setText(challengeResult.getName());
        holder.textViewDate.setText(challengeResult.getDate());
        holder.textViewPosition.setText(Integer.toString(challengeResult.getPosition()));
        holder.textViewTime.setText(String.valueOf(challengeResult.getTime()) + " h");
        holder.textViewDistance.setText(String.valueOf(challengeResult.getDistance()) + " km");

        return row;
    }
}
