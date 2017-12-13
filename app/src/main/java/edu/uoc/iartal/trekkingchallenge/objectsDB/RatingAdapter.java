package edu.uoc.iartal.trekkingchallenge.objectsDB;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;


public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private ArrayList<Rating> ratings = new ArrayList<>();

    // Object which represents a list item and save view references
    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUser, textViewTitle, textViewOpinion;

        // Link layout elements to variables
        public RatingViewHolder(View itemView) {
            super(itemView);
            textViewUser = (TextView) itemView.findViewById(R.id.cvRatingUser);
            textViewTitle = (TextView) itemView.findViewById(R.id.cvRatingTitle);
            textViewOpinion = (TextView) itemView.findViewById(R.id.cvRatingOpinion);
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
        viewHolder.textViewUser.setText("@" + ratings.get(position).getUser());
        viewHolder.textViewTitle.setText(ratings.get(position).getTitle());
        viewHolder.textViewOpinion.setText(ratings.get(position).getOpinion());
    }
}
