package edu.uoc.iartal.trekkingchallenge.objectsDB;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.route.ListRoutesActivity;
import edu.uoc.iartal.trekkingchallenge.route.ShowRouteActivity;

/**
 * Created by Ingrid Artal on 25/11/2017.
 */
public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private ArrayList<Rating> ratings = new ArrayList<>();
    ListRoutesActivity listRoutesActivity;
    Context context;

    //private List<String> selectedItemIdList;
    private DatabaseReference databaseRating;


    // Object which represents a list item and save view references
    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUser, textViewTitle, textViewOpinion;

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
      //  databaseRating = FirebaseDatabase.getInstance().getReference(FireBaseReferences.RATING_REFERENCE);

        // Inflates new list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_rating, viewGroup, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RatingViewHolder viewHolder, final int position) {
        // Modify content of each list item
        viewHolder.textViewUser.setText("@" + ratings.get(position).getUser());
        viewHolder.textViewTitle.setText(ratings.get(position).getTitle());
        viewHolder.textViewOpinion.setText(ratings.get(position).getOpinion());

    }


}
