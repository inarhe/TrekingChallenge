package edu.uoc.iartal.trekkingchallenge.common;

import android.net.Uri;

/**
 * Interface to notify when photo route information is fetched. It allows works with data retrieved in a Firebase listener
 * from another class
 */
public interface OnGetPhotoListener {
    void onSuccess(Uri uri);
    void onFailed(Exception e);
}
