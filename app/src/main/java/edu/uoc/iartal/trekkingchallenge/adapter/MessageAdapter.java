package edu.uoc.iartal.trekkingchallenge.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import edu.uoc.iartal.trekkingchallenge.model.Message;
import edu.uoc.iartal.trekkingchallenge.model.User;

/**
 * Created by Ingrid Artal on 21/12/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private ArrayList<Message> messages = new ArrayList<>();

    // Object which represents a list item and save view references
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUser, textViewBody;

        // Link layout elements to variables
        public MessageViewHolder(View itemView) {
            super(itemView);
            textViewUser = (TextView) itemView.findViewById(R.id.cvMessageUser);
            textViewBody = (TextView) itemView.findViewById(R.id.cvMessageText);
        }
    }

    public MessageAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Inflates new list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_message, viewGroup, false);
        return new MessageAdapter.MessageViewHolder(view);
    }

    /**
     * Modify content of each list item
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(final MessageAdapter.MessageViewHolder viewHolder, final int position) {
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        Query query = databaseUser.orderByChild(FireBaseReferences.USER_ID_REFERENCE).equalTo(messages.get(position).getUser());

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


        viewHolder.textViewBody.setText(messages.get(position).getText());
    }
}
