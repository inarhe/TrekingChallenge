package edu.uoc.iartal.trekkingchallenge.objects;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;

/**
 * Created by Ingrid Artal on 22/12/2017.
 */

public class ChallengeResultAdapter extends ArrayAdapter<ChallengeResult> {

    private Context mContext;
    private ArrayList<ChallengeResult> challengeResults;
    private DatabaseReference databaseChallResults;
    private int challengeWin;

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

        databaseChallResults = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);
        final DatabaseReference databaseHistory = FirebaseDatabase.getInstance().getReference(FireBaseReferences.HISTORY_REFERENCE);
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);

        final ChallengeResult challengeResult = this.getItem(position);

        if (challengeResult.getPosition() != (position + 1)){
            databaseChallResults.child(challengeResult.getId()).child(FireBaseReferences.CHALLENGERESULT_POSITION_REFERENCE).setValue(position + 1);

            Query query = databaseUser.orderByChild(FireBaseReferences.USER_ID_REFERENCE).equalTo(challengeResult.getUser());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                        final User user = userSnapshot.getValue(User.class);

                        ArrayList<String> challengeResults = new ArrayList<>();
                        challengeResults.addAll(user.getResults().keySet());

                        databaseChallResults.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                challengeWin = 0;
                                for (DataSnapshot result : dataSnapshot.getChildren()){
                                    ChallengeResult challengeResult = result.getValue(ChallengeResult.class);
                                    if (challengeResult.getUser().equals(user.getIdUser())){
                                        if (challengeResult.getPosition() == 1){
                                            challengeWin ++;
                                        }
                                    }
                                }
                                databaseHistory.child(user.getHistory()).child(FireBaseReferences.HISTORY_WINS_REFERENCE).setValue(challengeWin);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        holder.textViewPosition.setText(Integer.toString(position + 1));
        holder.textViewUser.setText(challengeResult.getUser());
        holder.textViewTime.setText(challengeResult.getTime().toString() + " h");
        holder.textViewDistance.setText(challengeResult.getDistance().toString() + " km");

        return row;
    }
}
