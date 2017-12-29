package edu.uoc.iartal.trekkingchallenge.common;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;


/**
 * Interface to notify when database information is fetched. It allows works with data retrieved in a Firebase listener
 * from another class
 */
public interface OnCompleteTaskListener {
    void onStart();
    void onSuccess();
    void onFailed();
}
