package edu.uoc.iartal.trekkingchallenge.objectsDB;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.user.ListUsersActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ingrid Artal on 25/11/2017.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<User> users = new ArrayList<>();
    ListUsersActivity listUsersActivity;
    Context context;

    private List<String> selectedItemIdList;
    DatabaseReference databaseUser, databaseGroup;

    // Object which represents a list item and save view references
    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewUserAlias, textViewUserName;
        ImageView imageViewUser;
        CheckBox checkBox;
        ListUsersActivity listUsersActivity;
        CardView cardView;

        public UserViewHolder(View itemView, ListUsersActivity listUsersActivity) {
            super(itemView);

            textViewUserAlias = (TextView) itemView.findViewById(R.id.cvUserAlias);
            textViewUserName = (TextView) itemView.findViewById(R.id.cvUserName);
            imageViewUser = (ImageView) itemView.findViewById(R.id.cvUserPhoto);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkListUser);
            this.listUsersActivity = listUsersActivity;
            cardView = (CardView)itemView.findViewById(R.id.cardViewUser);
          //  cardView.setOnLongClickListener(listUsersActivity);
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

    @Override
    public void onBindViewHolder(final UserViewHolder viewHolder, final int position) {
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);
        // Modify content of each list item
        viewHolder.textViewUserAlias.setText(users.get(position).getIdUser());
        viewHolder.textViewUserName.setText(users.get(position).getUserName());
        viewHolder.imageViewUser.setImageResource(R.drawable.ic_person);

        viewHolder.checkBox.setVisibility(View.VISIBLE);
        viewHolder.checkBox.setChecked(false);
    }

    private void addItemIdToSelectedList(String itemId) {
        if (!selectedItemIdList.contains(itemId)){
            selectedItemIdList.add(itemId);
        } else {
            Iterator<String> strId = selectedItemIdList.iterator();
            while (strId.hasNext()){
                String listItemId = strId.next();
                if(listItemId == itemId){
                    strId.remove();
                }
            }

        }
    }

    public void setFilter(List<User> filterUsers) {
        users = new ArrayList<>();
        users.addAll(filterUsers);
        notifyDataSetChanged();
    }

}
