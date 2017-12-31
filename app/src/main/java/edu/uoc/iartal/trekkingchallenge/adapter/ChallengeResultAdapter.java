package edu.uoc.iartal.trekkingchallenge.adapter;

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
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.ChallengeResult;
import edu.uoc.iartal.trekkingchallenge.model.User;


public class ChallengeResultAdapter extends ArrayAdapter<ChallengeResult> {

    private Context context;
    private ArrayList<ChallengeResult> challengeResults;
    private DatabaseReference databaseChallResults, databaseHistory, databaseUser;
    private int challengeWin;
    private ViewHolder holder;
    private FirebaseController controller = new FirebaseController();

    public ChallengeResultAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ChallengeResult> objects) {
        super(context, resource, objects);
        this.context = context;
        this.challengeResults = objects;
    }

    // Link layout elements to variables only once
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

    /**
     * Show each item of user challenge results list in list ranking view
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        holder = null;

        if (row == null){
            row = LayoutInflater.from(context).inflate(R.layout.adapter_challenge_results, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        databaseChallResults = controller.getDatabaseReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);
        databaseHistory = controller.getDatabaseReference(FireBaseReferences.HISTORY_REFERENCE);
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        final ChallengeResult challengeResult = this.getItem(position);

        if (challengeResult.getPosition() != (position + 1)) {
            controller.editIntParameter(databaseChallResults, challengeResult.getId(), FireBaseReferences.CHALLENGERESULT_POSITION_REFERENCE, position + 1);
        }
        controller.readDataOnce(databaseUser, new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot userSnapshot : data.getChildren()){
                    final User user = userSnapshot.getValue(User.class);
                    if (user.getId().equals(challengeResult.getUser())) {
                        ArrayList<String> challengeResults = new ArrayList<>();
                        challengeResults.addAll(user.getChallengeResults().keySet());
                        holder.textViewUser.setText(user.getAlias());

                        controller.readDataOnce(databaseChallResults, new OnGetDataListener() {
                            @Override
                            public void onStart() {
                                // Nothing to do
                            }

                            @Override
                            public void onSuccess(DataSnapshot data) {
                                challengeWin = 0;
                                for (DataSnapshot result : data.getChildren()){
                                    ChallengeResult challengeResult = result.getValue(ChallengeResult.class);
                                    if (challengeResult.getUser().equals(user.getId())){
                                        if (challengeResult.getPosition() == 1){
                                            challengeWin ++;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailed(DatabaseError databaseError) {

                            }
                        });
                    }



                    databaseChallResults.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            challengeWin = 0;
                            for (DataSnapshot result : dataSnapshot.getChildren()){
                                ChallengeResult challengeResult = result.getValue(ChallengeResult.class);
                                if (challengeResult.getUser().equals(user.getId())){
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
            public void onFailed(DatabaseError databaseError) {

            }
        });

        holder.textViewPosition.setText(Integer.toString(position + 1));

        holder.textViewTime.setText(challengeResult.getTime().toString() + " h");
        holder.textViewDistance.setText(challengeResult.getDistance().toString() + " km");
        
        return row;
    }
}
