package edu.uoc.iartal.trekkingchallenge.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.model.Route;

/**
 * Created by Ingrid Artal on 25/11/2017.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

   // private ArrayList<Route> routes = new ArrayList<>();
    private Route route;
    DatabaseReference databaseRoute;
    Context context;
    private StorageReference storageReference;


    // Object which represents a list item and save view references
    public static class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewGallery;

        public PhotoViewHolder(View itemView) {
            super(itemView);

            imageViewGallery = (ImageView) itemView.findViewById(R.id.ivPhoto);
        }

        @Override
        public void onClick(View v) {

            // listUsersActivity.prepareSelection(v, getAdapterPosition());
        }
    }

    public PhotoAdapter(Route route, Context context) {
        this.route = route;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return 1;
    }


    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Inflates new list item
        databaseRoute = FirebaseDatabase.getInstance().getReference(FireBaseReferences.ROUTE_REFERENCE);
        storageReference = FirebaseStorage.getInstance().getReference();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_view_photo_gallery, viewGroup, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder viewHolder, final int position) {
        String nameGallery = route.getIdRoute();
        Log.i("url",storageReference.child(FireBaseReferences.GALLERY_STORAGE + nameGallery).getDownloadUrl().toString() );
        storageReference.child(FireBaseReferences.GALLERY_STORAGE + nameGallery).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context).load(uri).into(viewHolder.imageViewGallery);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "image not dowloaded", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


