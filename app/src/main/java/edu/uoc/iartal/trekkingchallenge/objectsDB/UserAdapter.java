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
            cardView = (CardView)itemView.findViewById(R.id.cardViewGroup);
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

    //    if(!listUsersActivity.isInActionMode){
      //      viewHolder.checkBox.setVisibility(View.GONE);
       // } else {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.checkBox.setChecked(false);
       // }



        // When an item is clicked starts show detail group activity
      /*  viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {

                  //  User user = users.get(position);

                 //   addItemIdToSelectedList(user.getIdUser());
                } else {

                    User user = users.get(position);
                    addItemIdToSelectedList(user.getIdUser());
                }*/
                //String name = Integer.toString(R.string.listUsersActivity);
                //Log.i("LISTNAME", name);
                //User user = users.get(position);
                //databaseUser.child(user.getIdUser()).child("groups").child(name).setValue("true");

             /*   Query query = databaseGroup.orderByChild(FireBaseReferences.GROUPNAME_REFERENCE).equalTo(name);

                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Group group = dataSnapshot.getValue(Group.class);

                        databaseGroup.child(group.getIdGroup()).child(FireBaseReferences.MEMBERSGROUP_REFERENCE).child(user.getAlias()).setValue("true");

                        databaseUser.child(user.getIdUser()).child("groups").child(name).setValue("true");
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        //TO-DO
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        //TO-DO
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        //TO-DO
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //TO-DO
                    }
                });
              /*  Query query = databaseUser.orderByChild(FireBaseReferences.ALIAS_REFERENCE).equalTo(users.get(position).getAlias());

                // Query database to get user information
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        User user = dataSnapshot.getValue(User.class);
                        databaseGroup.child(groupKey).child(FireBaseReferences.MEMBERSGROUP_REFERENCE).child(user.getAlias()).setValue("true");

                        databaseUser.child(user.getIdUser()).child("groups").child(name).setValue("true");
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        //TO-DO
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        //TO-DO
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        //TO-DO
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //TO-DO
                    }
                });*/
              //  Context context = v.getContext();

                /*Intent intent = new Intent(context, ShowGroupActivity.class);
                intent.putExtra("groupName", users.get(position).getGroupName());
                intent.putExtra("groupDescription", users.get(position).getGroupDescription());
                intent.putExtra("members", users.get(position).getNumberOfMembers());
                intent.putExtra("groupKey", users.get(position).getIdGroup());

                context.startActivity(intent);*/

       //     }
       // });
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
