package edu.uoc.iartal.trekkingchallenge.adapter;

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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetPhotoListener;
import edu.uoc.iartal.trekkingchallenge.model.Finished;
import edu.uoc.iartal.trekkingchallenge.model.Route;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.route.ListRoutesActivity;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.route.ShowRouteActivity;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private ArrayList<Route> routes = new ArrayList<>();
    private ListRoutesActivity listRoutesActivity;
    private Context context;
    private User currentUser;
    private DatabaseReference databaseFinished, databaseUser;
    private StorageReference storageReference;
    private FirebaseController controller = new FirebaseController();

    // Object which represents a list item and save view references
    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRouteName, textViewDistance, textViewTime, textViewDifficult, textViewRegion, textViewDate;
        ImageView imageViewRoute, imageViewType;
        ListRoutesActivity listRoutesActivity;
        CardView cardView;
        RatingBar rbAverage;

        // Link layout elements to variables
        public RouteViewHolder(View itemView, ListRoutesActivity listRoutesActivity) {
            super(itemView);
            textViewRouteName = (TextView) itemView.findViewById(R.id.cvRouteName);
            textViewDistance = (TextView) itemView.findViewById(R.id.cvDistance);
            textViewTime = (TextView) itemView.findViewById(R.id.cvTime);
            textViewDifficult = (TextView) itemView.findViewById(R.id.cvDifficult);
            textViewRegion = (TextView) itemView.findViewById(R.id.cvRegion);
            imageViewRoute = (ImageView) itemView.findViewById(R.id.cvRoutePhoto);
            imageViewType = (ImageView) itemView.findViewById(R.id.cvIconType);
            this.listRoutesActivity = listRoutesActivity;
            cardView = (CardView)itemView.findViewById(R.id.cardViewRoute);
            rbAverage = (RatingBar) itemView.findViewById(R.id.rbAverage);
            textViewDate = (TextView) itemView.findViewById(R.id.cvDoneDate);

        }
    }

    public RouteAdapter(ArrayList<Route> routes, Context context) {
        this.routes = routes;
        this.context = context;

        listRoutesActivity = (ListRoutesActivity) context;

        // Get database and storage references
        databaseFinished = controller.getDatabaseReference(FireBaseReferences.FINISHED_REFERENCE);
        storageReference = controller.getStorageReference();
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        // Get current user
        getCurrentUser();
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Inflates new list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_route, viewGroup, false);
        return new RouteViewHolder(view, listRoutesActivity);
    }

    /**
     * Modify content of each list item
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(final RouteViewHolder viewHolder, final int position) {
        viewHolder.textViewRouteName.setText(routes.get(position).getName());
        viewHolder.textViewDistance.setText(routes.get(position).getDistance());
        viewHolder.textViewTime.setText(routes.get(position).getTime());
        viewHolder.textViewDifficult.setText(routes.get(position).getDifficult());
        viewHolder.textViewRegion.setText(routes.get(position).getRegion());
        viewHolder.rbAverage.setRating(routes.get(position).getSumRatings() / routes.get(position).getNumRatings());

        // Show icon according to route type
        if (routes.get(position).getType().equals(context.getResources().getString(R.string.circular))){
            viewHolder.imageViewType.setImageResource(R.drawable.ic_circular);
        } else {
            viewHolder.imageViewType.setImageResource(R.drawable.ic_goback);
        }

        // Get header route photo
        getHeaderPhoto(routes.get(position), viewHolder);

        // Check if user has done the route
        checkIfUserHasDone(routes.get(position), viewHolder);




        // When cardview is clicked starts show detail route activity
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, ShowRouteActivity.class);
                intent.putExtra("route", routes.get(position));

                context.startActivity(intent);
            }
        });
    }

    /**
     * Updates route list with search result
     * @param filterRoutes
     */
    public void setFilter(List<Route> filterRoutes) {
        routes = new ArrayList<>();
        routes.addAll(filterRoutes);
        notifyDataSetChanged();
    }

    /**
     * Check if current user has done the route
     * @param route
     * @param viewHolder
     */
    private void checkIfUserHasDone(Route route, final RouteViewHolder viewHolder){

        final ArrayList<String> finishedList = new ArrayList<>();
        finishedList.addAll(route.getFinished().keySet());

        controller.readDataOnce(databaseFinished, new OnGetDataListener() {
            @Override
            public void onStart() {
                //Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot finishedSnapshot : data.getChildren()){
                    if (finishedList.contains(finishedSnapshot.getValue(Finished.class).getId())){
                        String finisher = finishedSnapshot.getValue(Finished.class).getUser();
                        if (finisher.equals(currentUser.getId())){
                            viewHolder.textViewDate.setText(finishedSnapshot.getValue(Finished.class).getDate());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("RouteAdp fin error", databaseError.getMessage());
            }
        });
    }

    /**
     * Get current user information and know who is doing the action
     */
    private void getCurrentUser(){
        // Execute controller method to get database current user object. Use OnGetDataListener interface to know
        // when database data is retrieved
        controller.readDataOnce(databaseUser, new OnGetDataListener() {
            @Override
            public void onStart() {
                //Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                String currentMail = controller.getCurrentUserEmail();

                for (DataSnapshot userSnapshot : data.getChildren()){
                    User user = userSnapshot.getValue(User.class);

                    if (user.getMail().equals(currentMail)){
                        currentUser = user;
                        break;
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("RouteAdp usr error", databaseError.getMessage());
            }
        });
    }

    /**
     * Get header photo name, download it and set into show route
     */
    private void getHeaderPhoto(Route route, final RouteViewHolder viewHolder){
        String namePhoto = route.getHeaderPhoto();

        controller.readPhoto(storageReference, FireBaseReferences.HEADERS_STORAGE + namePhoto, new OnGetPhotoListener() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(viewHolder.imageViewRoute);
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.imageNotDonwloaded, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
