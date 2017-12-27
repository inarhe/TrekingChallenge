package edu.uoc.iartal.trekkingchallenge.objects;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.user.ListUsersActivity;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<User> users = new ArrayList<>();
    private ListUsersActivity listUsersActivity;
    private Context context;

    // Object which represents a list item and save view references
    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewUserAlias, textViewUserName;
        ImageView imageViewUser;
        CheckBox checkBox;
        ListUsersActivity listUsersActivity;
        CardView cardView;

        // Link layout elements to variables
        public UserViewHolder(View itemView, ListUsersActivity listUsersActivity) {
            super(itemView);
            textViewUserAlias = (TextView) itemView.findViewById(R.id.cvUserAlias);
            textViewUserName = (TextView) itemView.findViewById(R.id.cvUserName);
            imageViewUser = (ImageView) itemView.findViewById(R.id.cvUserPhoto);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkListUser);
            this.listUsersActivity = listUsersActivity;
            cardView = (CardView)itemView.findViewById(R.id.cardViewUser);
            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listUsersActivity.prepareSelection(v, getAdapterPosition());
        }
    }

    public UserAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
        listUsersActivity = (ListUsersActivity) context;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }



    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Inflates new list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_user, viewGroup, false);
        return new UserViewHolder(view, listUsersActivity);
    }

    /**
     * Modify content of each list item
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(final UserViewHolder viewHolder, final int position) {
        // Modify content of each list item
        viewHolder.textViewUserAlias.setText(users.get(position).getAlias());
        viewHolder.textViewUserName.setText(users.get(position).getName());
        viewHolder.imageViewUser.setImageResource(R.drawable.ic_person);
        viewHolder.checkBox.setVisibility(View.VISIBLE);
        viewHolder.checkBox.setChecked(false);
    }

    /**
     * Updates users list with search result
     * @param filterUsers
     */
    public void setFilter(List<User> filterUsers) {
        users = new ArrayList<>();
        users.addAll(filterUsers);
        notifyDataSetChanged();
    }

}
