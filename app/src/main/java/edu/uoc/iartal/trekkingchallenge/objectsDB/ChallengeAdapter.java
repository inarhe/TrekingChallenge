package edu.uoc.iartal.trekkingchallenge.objectsDB;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.challenge.ShowChallengeActivity;
import edu.uoc.iartal.trekkingchallenge.trip.ShowTripActivity;

/**
 * Created by Ingrid Artal on 12/11/2017.
 */
public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    List<Challenge> challenges;
    Boolean isVisible = false;
    ArrayList<Boolean> isVisibleArray = new ArrayList<>();


    // Object which represents a list item and save view references
    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        TextView textViewChallengeName, textViewChallengeDate, textViewIsPublic;
        ImageView imageViewChallenge;
        ImageButton imageButton;
        CardView cardView;


        public ChallengeViewHolder(View view) {
            super(view);
            textViewChallengeName = (TextView) view.findViewById(R.id.cvChallengeName);
            textViewChallengeDate = (TextView) view.findViewById(R.id.cvChallengeDate);
            textViewIsPublic = (TextView) view.findViewById(R.id.cvisPublic);
            imageViewChallenge = (ImageView) view.findViewById(R.id.cvChallengePhoto);
            imageButton = (ImageButton) view.findViewById(R.id.icDelChallengeAdmin);
            cardView = (CardView) view.findViewById(R.id.cardViewChallenge);

        }
    }

    public ChallengeAdapter(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    @Override
    public ChallengeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Inflates new list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_challenge, viewGroup, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChallengeViewHolder viewHolder, final int position) {
       // Modify content of each list item
        viewHolder.textViewChallengeName.setText(challenges.get(position).getIdChallenge());
        viewHolder.textViewChallengeDate.setText(challenges.get(position).getLimitDate());
      //  viewHolder.textViewTripDesc.setText(trips.get(position).getTripDescription());
        viewHolder.imageViewChallenge.setImageResource(R.drawable.ic_challenge);
        if (challenges.get(position).getPublic()) {
            viewHolder.textViewIsPublic.setText(R.string.publicChallenge);
        } else {
            viewHolder.textViewIsPublic.setText(R.string.privateChallenge);
        }

        if (isVisibleArray.isEmpty()){
            viewHolder.imageButton.setVisibility(View.GONE);
        } else {
            if(isVisibleArray.get(position)){
                viewHolder.imageButton.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imageButton.setVisibility(View.GONE);
            }
        }


        // When an item is clicked starts show detail group activity

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, ShowChallengeActivity.class);
                intent.putExtra("challenge", challenges.get(position));

                context.startActivity(intent);
            }
        });

       /* viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keys.removeAll(keys);
                final String id = groups.get(position).getId();
                final String name = groups.get(position).getName();
                final Context context = v.getContext();
                DatabaseReference databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);

                databaseGroup.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                            Group group = groupSnapshot.getValue(Group.class);

                            if (group.getId().equals(id)) {
                                for (String key : group.getMembers().keySet()) {
                                    keys.add(key);
                                }

                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //TO-DO
                    }
                });

                databaseGroup.child(groups.get(position).getName()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context , R.string.groupDeleted, Toast.LENGTH_SHORT).show();
                            DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
                            for (String user:keys){
                                databaseUser.child(user).child("groups").child(name).removeValue();
                            }

                        } else {

                            Toast.makeText(context, R.string.groupNotDeleted, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });*/

    }

    public void setVisibility(Boolean visible){
        if (visible){
            isVisibleArray.add(true);
        } else {
            isVisibleArray.add(false);
        }
     //   Log.i("VISIBLE",isVisible.toString());

     //   this.notifyDataSetChanged();
    }

    public void setFilter(List<Challenge> filterChallenges) {
        challenges = new ArrayList<>();
        challenges.addAll(filterChallenges);
        notifyDataSetChanged();
    }



}
