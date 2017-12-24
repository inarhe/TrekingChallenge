package edu.uoc.iartal.trekkingchallenge.objects;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;

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
        viewHolder.textViewUser.setText("@" + messages.get(position).getUser());
        viewHolder.textViewBody.setText(messages.get(position).getText());
    }
}
