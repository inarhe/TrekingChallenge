package edu.uoc.iartal.trekkingchallenge.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
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
import edu.uoc.iartal.trekkingchallenge.model.Rating;
import edu.uoc.iartal.trekkingchallenge.model.User;


public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private ArrayList<Rating> ratings = new ArrayList<>();

    // Object which represents a list item and save view references
    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUser, textViewTitle, textViewOpinion;
        RatingBar rbUserRate;

        // Link layout elements to variables
        public RatingViewHolder(View itemView) {
            super(itemView);
            textViewUser = (TextView) itemView.findViewById(R.id.cvRatingUser);
            textViewTitle = (TextView) itemView.findViewById(R.id.cvRatingTitle);
            textViewOpinion = (TextView) itemView.findViewById(R.id.cvRatingOpinion);
            rbUserRate = (RatingBar) itemView.findViewById(R.id.rbUserRate);
        }
    }

    public RatingAdapter(ArrayList<Rating> ratings) {
        this.ratings = ratings;
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    @Override
    public RatingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Inflates new list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_rating, viewGroup, false);
        return new RatingViewHolder(view);
    }

    /**
     * Modify content of each list item
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(final RatingViewHolder viewHolder, final int position) {
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        Query query = databaseUser.orderByChild(FireBaseReferences.USER_ID_REFERENCE).equalTo(ratings.get(position).getUser());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    viewHolder.textViewUser.setText("@" + user.getAlias());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        viewHolder.textViewTitle.setText(ratings.get(position).getTitle());
        viewHolder.textViewOpinion.setText(ratings.get(position).getOpinion());
        viewHolder.rbUserRate.setRating(ratings.get(position).getValue());
    }
}
