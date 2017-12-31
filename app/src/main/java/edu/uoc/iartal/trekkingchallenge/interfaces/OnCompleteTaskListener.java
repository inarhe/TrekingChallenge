package edu.uoc.iartal.trekkingchallenge.interfaces;


/**
 * Interface to notify when database task is completed. It allows works with data retrieved in a Firebase listener
 * from another class
 */
public interface OnCompleteTaskListener {
    void onStart();
    void onSuccess();
    void onFailed();
}
