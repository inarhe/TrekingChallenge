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

import edu.uoc.iartal.trekkingchallenge.route.ListRoutesActivity;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.route.ShowRouteActivity;

/**
 * Created by Ingrid Artal on 25/11/2017.
 */
public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private ArrayList<Route> routes = new ArrayList<>();
    ListRoutesActivity listRoutesActivity;
    Context context;
    private Uri downloadUri;

    //private List<String> selectedItemIdList;
    DatabaseReference databaseRoute;
    private StorageReference storageReference;

    // Object which represents a list item and save view references
    public static class RouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewRouteName, textViewDistance, textViewTime, textViewDifficult, textViewRegion;
        ImageView imageViewRoute, imageViewType;
       // CheckBox checkBox;
        ListRoutesActivity listRoutesActivity;
        CardView cardView;

        public RouteViewHolder(View itemView, ListRoutesActivity listRoutesActivity) {
            super(itemView);

            textViewRouteName = (TextView) itemView.findViewById(R.id.cvRouteName);
            textViewDistance = (TextView) itemView.findViewById(R.id.cvDistance);
            textViewTime = (TextView) itemView.findViewById(R.id.cvTime);
            textViewDifficult = (TextView) itemView.findViewById(R.id.cvDifficult);
            textViewRegion = (TextView) itemView.findViewById(R.id.cvRegion);
            imageViewRoute = (ImageView) itemView.findViewById(R.id.cvRoutePhoto);
            imageViewType = (ImageView) itemView.findViewById(R.id.cvIconType);
           // checkBox = (CheckBox) itemView.findViewById(R.id.checkListUser);
            this.listRoutesActivity = listRoutesActivity;
            cardView = (CardView)itemView.findViewById(R.id.cardViewRoute);
          //  cardView.setOnLongClickListener(listUsersActivity);
           // checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           // listRoutesActivity.prepareSelection(v, getAdapterPosition());
        }
    }

    public RouteAdapter(ArrayList<Route> routes, Context context) {
        this.routes = routes;
        this.context = context;
        listRoutesActivity = (ListRoutesActivity) context;
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }



    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        databaseRoute = FirebaseDatabase.getInstance().getReference(FireBaseReferences.ROUTE_REFERENCE);
        storageReference = FirebaseStorage.getInstance().getReference();

        // Inflates new list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_route, viewGroup, false);
        return new RouteViewHolder(view, listRoutesActivity);
    }

    @Override
    public void onBindViewHolder(final RouteViewHolder viewHolder, final int position) {
       // databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);

       // storageReference
        // Modify content of each list item
        viewHolder.textViewRouteName.setText(routes.get(position).getName());
        viewHolder.textViewDistance.setText(routes.get(position).getDistance() + " km");
        viewHolder.textViewTime.setText(routes.get(position).getTime());
        viewHolder.textViewDifficult.setText(routes.get(position).getDifficult());
        viewHolder.textViewRegion.setText(routes.get(position).getRegion());

        if (routes.get(position).getType().equals(R.string.circular)){
            viewHolder.imageViewType.setImageResource(R.drawable.ic_circular);
        } else {
            viewHolder.imageViewType.setImageResource(R.drawable.ic_goback);
        }



        String namePhoto = routes.get(position).getHeaderPhoto();

        Log.i("url",storageReference.child(FireBaseReferences.HEADERS_STORAGE + namePhoto).getDownloadUrl().toString());
        storageReference.child(FireBaseReferences.HEADERS_STORAGE + namePhoto).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context).load(uri).into(viewHolder.imageViewRoute);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "image not dowloaded", Toast.LENGTH_SHORT).show();
            }
        });


        // When an item is clicked starts show detail group activity
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, ShowRouteActivity.class);
                intent.putExtra("route", routes.get(position));

                context.startActivity(intent);
            }
        });


    }


    public void setFilter(List<Route> filterUsers) {
        routes = new ArrayList<>();
        routes.addAll(filterUsers);
        notifyDataSetChanged();
    }

}
