package edu.uoc.iartal.trekkingchallenge.interfaces;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;


/**
 * Interface to notify when database information child is fetched. It allows works with data retrieved in a Firebase listener
 * from another class
 */
public interface OnGetChildListener {
    void onStart();
    void onSuccess(DataSnapshot data);
    void onChanged(DataSnapshot data);
    void onRemoved(DataSnapshot data);
    void onFailed(DatabaseError databaseError);
}
