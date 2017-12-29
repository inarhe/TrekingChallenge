package edu.uoc.iartal.trekkingchallenge.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.challenge.EditChallengeActivity;
import edu.uoc.iartal.trekkingchallenge.challenge.ShowChallengeActivity;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.model.Challenge;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    private List<Challenge> challenges;
    private ArrayList<Boolean> isVisibleArray = new ArrayList<>();
    private ArrayList<String> challengeMembers = new ArrayList<>();
    private Context context;

    // Object which represents a list item and save view references
    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        TextView textViewChallengeName, textViewChallengeDate, textViewIsPublic;
        ImageView imageViewChallenge;
        ImageButton buttonDelete, buttonEdit;
        CardView cardView;

        // Link layout elements to variables
        public ChallengeViewHolder(View view) {
            super(view);
            textViewChallengeName = (TextView) view.findViewById(R.id.cvChallengeName);
            textViewChallengeDate = (TextView) view.findViewById(R.id.cvChallengeDate);
            textViewIsPublic = (TextView) view.findViewById(R.id.cvIsPublic);
            imageViewChallenge = (ImageView) view.findViewById(R.id.cvChallengePhoto);
            buttonDelete = (ImageButton) view.findViewById(R.id.icDelChallengeAdmin);
            buttonEdit = (ImageButton) view.findViewById(R.id.icEditChallengeAdmin);
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

    /**
     * Modify content of each list item
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(ChallengeViewHolder viewHolder, final int position) {
        viewHolder.textViewChallengeName.setText(challenges.get(position).getName());
        viewHolder.textViewChallengeDate.setText(challenges.get(position).getLimitDate());
        viewHolder.imageViewChallenge.setImageResource(R.drawable.ic_challenge);

        if (challenges.get(position).getPublic()) {
            viewHolder.textViewIsPublic.setText(R.string.publicChallenge);
        } else {
            viewHolder.textViewIsPublic.setText(R.string.privateChallenge);
        }

        // Show delete button only if current user is challenge admin
        if (isVisibleArray.isEmpty()){
            viewHolder.buttonDelete.setVisibility(View.GONE);
            viewHolder.buttonEdit.setVisibility(View.GONE);
        } else {
            if(isVisibleArray.get(position)){
                viewHolder.buttonDelete.setVisibility(View.VISIBLE);
                viewHolder.buttonEdit.setVisibility(View.VISIBLE);
            } else {
                viewHolder.buttonDelete.setVisibility(View.GONE);
                viewHolder.buttonEdit.setVisibility(View.GONE);
            }
        }

        // When cardview is clicked starts show detail challenge activity
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, ShowChallengeActivity.class);
                intent.putExtra("challenge", challenges.get(position));

                context.startActivity(intent);
            }
        });

        // When delete button is clicked, delete challenge and all its members dependencies
        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get challenge members
                getMembers(position);
                context = v.getContext();

                // Delete challenge and its dependencies
                deleteChallenge(position);
            }
        });

        // When edit button is clicked, starts edit detail challenge activity
        viewHolder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, EditChallengeActivity.class);
                intent.putExtra("challenge", challenges.get(position));

                context.startActivity(intent);
            }
        });
    }

    /**
     * Define visibility of delete button for each element
     * @param visible
     */
    public void setVisibility(Boolean visible){
        if (visible){
            isVisibleArray.add(true);
        } else {
            isVisibleArray.add(false);
        }
    }

    /**
     * Updates challenge list with search result
     * @param filterChallenges
     */
    public void setFilter(List<Challenge> filterChallenges) {
        challenges = new ArrayList<>();
        challenges.addAll(filterChallenges);
        notifyDataSetChanged();
    }

    /**
     * Get names of challenge members
     * @param position
     */
    private void getMembers(int position){
        challengeMembers.clear();
        Challenge challenge = challenges.get(position);
        challengeMembers.addAll(challenge.getMembers().keySet());
    }

    /**
     * Delete selected challenge and update challenge list of each member
     * @param position
     */
    private void deleteChallenge(final int position){
        // Create alert dialog to ask user confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.challengeAskDeleted));
        builder.setCancelable(true);

        builder.setPositiveButton(
                context.getResources().getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        DatabaseReference databaseChallenge = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGE_REFERENCE);
                        databaseChallenge.child(challenges.get(position).getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
                                    for (String user:challengeMembers){
                                        databaseUser.child(user).child(FireBaseReferences.USER_CHALLENGES_REFERENCE).child(challenges.get(position).getId()).removeValue();
                                    }
                                    Toast.makeText(context , R.string.challengeDeleted, Toast.LENGTH_SHORT).show();
                                    challenges.remove(position);
                                    isVisibleArray.remove(position);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, R.string.challengeNotDeleted, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

        builder.setNegativeButton(
                context.getResources().getString(R.string.cancelButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
