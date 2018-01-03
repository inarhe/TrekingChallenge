package edu.uoc.iartal.trekkingchallenge.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnCompleteTaskListener;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetChildListener;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetPhotoListener;
import edu.uoc.iartal.trekkingchallenge.model.Challenge;
import edu.uoc.iartal.trekkingchallenge.model.ChallengeResult;
import edu.uoc.iartal.trekkingchallenge.model.Finished;
import edu.uoc.iartal.trekkingchallenge.model.Group;
import edu.uoc.iartal.trekkingchallenge.model.History;
import edu.uoc.iartal.trekkingchallenge.model.Message;
import edu.uoc.iartal.trekkingchallenge.model.Rating;
import edu.uoc.iartal.trekkingchallenge.model.Trip;
import edu.uoc.iartal.trekkingchallenge.model.TripDone;
import edu.uoc.iartal.trekkingchallenge.model.User;


/**
 * Controller class that gets data from database and send it to activities
 */
public class FirebaseController {

    //*** FIREBASE AND DATABASE MANAGE FUNCTIONS ***//

    /**
     * Get current user, who has active session
     * @return
     */
    public FirebaseUser getActiveUserSession(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Get current user mail, who has active session
     * @return
     */
    public String getCurrentUserEmail(){
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    /**
     * Login user to Firebase server and allows access to database
     * @param email
     * @param password
     * @param progressDialog
     * @param context
     */
    public void loginDatabase(String email, String password, final ProgressDialog progressDialog, final Context context){
        // Execute firebase sign in function
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(context, R.string.loginSuccess,Toast.LENGTH_SHORT).show();
                            ((Activity)context).finish();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(context, R.string.failedLogin,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Sign Out. Close user session
     * @param context
     */
    public void signOutDatabase(Context context){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(context, R.string.userSignOut, Toast.LENGTH_SHORT).show();
        ((Activity)context).finish();
    }

    /**
     * Get a specific firebase database instance, according with a node reference
     * @param reference
     * @return
     */
    public DatabaseReference getDatabaseReference (String reference){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(reference);

        return databaseReference;
    }

    /**
     * Get firebase storage reference
     * @return
     */
    public StorageReference getStorageReference(){
        return FirebaseStorage.getInstance().getReference();
    }

    /**
     * Get new object reference
     * @param databaseReference
     * @return
     */
    public String getFirebaseNewKey (DatabaseReference databaseReference){
        try{
            String id = databaseReference.push().getKey();
            return id;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    //*** FUNCTIONS THAT ALLOW ADD NEW DATABASE OBJECTS ***//

    /**
     * Regiter user in Firebase server, add new user to database, add new user history and create dependencies between user and history
     * @param alias
     * @param name
     * @param mail
     * @param password
     * @param context
     * @param progressDialog
     */
    public void addNewUser(final String alias, final String name, final String mail, final String password, final Context context, final ProgressDialog progressDialog){

        final DatabaseReference databaseUser = getDatabaseReference(FireBaseReferences.USER_REFERENCE);
        final DatabaseReference databaseHistory = getDatabaseReference(FireBaseReferences.HISTORY_REFERENCE);

        // Execute firebase user registration function
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If user is successfully registered and logged in, create user object and start main activity
                        if(task.isSuccessful()){
                            try {
                                String idHistory = databaseHistory.push().getKey();
                                String idUser = databaseUser.push().getKey();

                                History history = new History(idHistory, 0.0, 0, 0, 0, 0, idUser);
                                databaseHistory.child(idHistory).setValue(history);

                                User user = new User(idUser, alias, name, mail, password, idHistory);
                                databaseUser.child(idUser).setValue(user);

                                progressDialog.dismiss();
                                Toast.makeText(context, R.string.successfulRegister,Toast.LENGTH_SHORT).show();
                                ((Activity)context).finish();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("Register error",task.getException().getMessage());
                            progressDialog.dismiss();
                            Toast.makeText(context,R.string.failedRegister,Toast.LENGTH_SHORT).show();
                            ((Activity)context).finish();
                        }
                    }
                });
    }

    /**
     * Add group to firebase database and update group list in user and group database nodes
     * @param databaseGroup
     * @param group
     * @param userAdmin
     * @param context
     */
    public void addNewGroup(final DatabaseReference databaseGroup, final Group group, final String userAdmin, final Context context){
        final DatabaseReference databaseUser = getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        databaseGroup.child(group.getId()).setValue(group).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    databaseGroup.child(group.getId()).child(FireBaseReferences.MEMBERS_REFERENCE).child(userAdmin).setValue("true");
                    databaseUser.child(userAdmin).child(FireBaseReferences.USER_GROUPS_REFERENCE).child(group.getId()).setValue("true");
                    Toast.makeText(context, R.string.groupSaved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.failedAddGroup, Toast.LENGTH_SHORT).show();
                    Log.e("New group error",task.getException().getMessage());
                }
            }
        });
    }

    /**
     * Add trip to firebase database and update trip list in user and trip database nodes
     * @param databaseTrip
     * @param trip
     * @param userAdmin
     * @param context
     */
    public void addNewTrip(final DatabaseReference databaseTrip, final Trip trip, final String userAdmin, final Context context){
        final DatabaseReference databaseUser = getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        databaseTrip.child(trip.getId()).setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    databaseTrip.child(trip.getId()).child(FireBaseReferences.MEMBERS_REFERENCE).child(userAdmin).setValue("true");
                    databaseUser.child(userAdmin).child(FireBaseReferences.USER_TRIPS_REFERENCE).child(trip.getId()).setValue("true");
                    Toast.makeText(context, R.string.tripSaved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.failedAddTrip, Toast.LENGTH_SHORT).show();
                    Log.e("New trip error",task.getException().getMessage());
                }
            }
        });
    }

    /**
     * Add challenge to firebase database and update challenge list in user and challenge database nodes
     * @param databaseChallenge
     * @param challenge
     * @param userAdmin
     * @param context
     */
    public void addNewChallenge(final DatabaseReference databaseChallenge, final Challenge challenge, final String userAdmin, final Context context){
        final DatabaseReference databaseUser = getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        databaseChallenge.child(challenge.getId()).setValue(challenge).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    databaseChallenge.child(challenge.getId()).child(FireBaseReferences.MEMBERS_REFERENCE).child(userAdmin).setValue("true");
                    databaseUser.child(userAdmin).child(FireBaseReferences.USER_CHALLENGES_REFERENCE).child(challenge.getId()).setValue("true");
                    Toast.makeText(context, R.string.challengeSaved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.failedAddChallenge, Toast.LENGTH_SHORT).show();
                    Log.e("New challenge error",task.getException().getMessage());
                }
            }
        });
    }

    /**
     * Add message to firebase database and update message list in user and trip/group database nodes
     * @param databaseMessage
     * @param message
     * @param idUser
     * @param idGroup
     * @param idTrip
     * @param context
     */
    public void addNewMessage(final DatabaseReference databaseMessage, final Message message, final String idUser, final String idGroup, final String idTrip, final Context context){
        final DatabaseReference databaseUser = getDatabaseReference(FireBaseReferences.USER_REFERENCE);
        final DatabaseReference databaseGroup = getDatabaseReference(FireBaseReferences.GROUP_REFERENCE);
        final DatabaseReference databaseTrip = getDatabaseReference(FireBaseReferences.TRIP_REFERENCE);

        databaseMessage.child(message.getId()).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    databaseUser.child(idUser).child(FireBaseReferences.MESSAGES_REFERENCE).child(message.getId()).setValue("true");

                    if (idGroup.equals(context.getResources().getString(R.string.none))){
                        databaseTrip.child(idTrip).child(FireBaseReferences.MESSAGES_REFERENCE).child(message.getId()).setValue("true");
                    } else {
                        databaseGroup.child(idGroup).child(FireBaseReferences.MESSAGES_REFERENCE).child(message.getId()).setValue("true");
                    }
                    Toast.makeText(context, R.string.messagePublished, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.messageNotPublished, Toast.LENGTH_SHORT).show();
                    Log.e("New message error",task.getException().getMessage());
                }
            }
        });
    }

    /**
     * Add trip result to firebase database and update result list in user and challenge database nodes
     * @param databaseTripDone
     * @param tripDone
     * @param idUser
     * @param idTrip
     * @param context
     */
    public void addNewTripResult(DatabaseReference databaseTripDone, final TripDone tripDone, final String idUser, final String idTrip, final Context context){
        final DatabaseReference databaseTrip = getDatabaseReference(FireBaseReferences.TRIP_REFERENCE);
        final DatabaseReference databaseUser = getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        databaseTripDone.child(tripDone.getId()).setValue(tripDone).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    databaseUser.child(idUser).child(FireBaseReferences.USER_TRIPSDONE_REFERENCE).child(tripDone.getId()).setValue("true");
                    databaseTrip.child(idTrip).child(FireBaseReferences.TRIP_DONE_REFERENCE).child(tripDone.getId()).setValue("true");
                    Toast.makeText(context, R.string.finishedSaved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.finishedFailed, Toast.LENGTH_SHORT).show();
                    Log.e("New tripResult error",task.getException().getMessage());
                }
            }
        });
    }

    /**
     * Add challenge result to firebase database and update result list in user and challenge database nodes
     * @param databaseChallengeResult
     * @param challengeResult
     * @param idUser
     * @param idChallenge
     * @param context
     */
    public void addNewChallengeResult(DatabaseReference databaseChallengeResult, final ChallengeResult challengeResult, final String idUser, final String idChallenge, final Context context){
        final DatabaseReference databaseChallenge = getDatabaseReference(FireBaseReferences.CHALLENGE_REFERENCE);
        final DatabaseReference databaseUser = getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        databaseChallengeResult.child(challengeResult.getId()).setValue(challengeResult).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    databaseUser.child(idUser).child(FireBaseReferences.USER_RESULT_REFERENCE).child(challengeResult.getId()).setValue("true");
                    databaseChallenge.child(idChallenge).child(FireBaseReferences.CHALLENGE_FINISHED_REFERENCE).child(challengeResult.getId()).setValue("true");
                    Toast.makeText(context, R.string.finishedSaved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.finishedFailed, Toast.LENGTH_SHORT).show();
                    Log.e("New challResult error",task.getException().getMessage());
                }
            }
        });
    }

    /**
     * Add route result to firebase database and update result list in user and route database nodes
     * @param databaseFinished
     * @param finished
     * @param idUser
     * @param idRoute
     * @param context
     */
    public void addNewRouteResult(DatabaseReference databaseFinished, final Finished finished, final String idUser, final String idRoute, final Context context){
        final DatabaseReference databaseRoute = getDatabaseReference(FireBaseReferences.ROUTE_REFERENCE);
        final DatabaseReference databaseUser = getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        databaseFinished.child(finished.getId()).setValue(finished).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    databaseUser.child(idUser).child(FireBaseReferences.USER_FINISHED_REFERENCE).child(finished.getId()).setValue("true");
                    databaseRoute.child(idRoute).child(FireBaseReferences.ROUTE_FINISHED_REFERENCE).child(finished.getId()).setValue("true");
                    Toast.makeText(context, R.string.finishedSaved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.finishedFailed, Toast.LENGTH_SHORT).show();
                    Log.e("New routeResult error",task.getException().getMessage());
                }
            }
        });
    }

    /**
     * Add rating to firebase database and update rating list in user and route database nodes
     * @param databaseRating
     * @param rating
     * @param context
     */
    public void addNewRating(DatabaseReference databaseRating, final Rating rating, final String idUser, final String idRoute, final Context context){
        final DatabaseReference databaseRoute = getDatabaseReference(FireBaseReferences.ROUTE_REFERENCE);
        final DatabaseReference databaseUser = getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        databaseRating.child(rating.getId()).setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    databaseUser.child(idUser).child(FireBaseReferences.USER_RATINGS_REFERENCE).child(rating.getId()).setValue("true");
                    databaseRoute.child(idRoute).child(FireBaseReferences.ROUTE_RATINGS_REFERENCE).child(rating.getId()).setValue("true");
                    Toast.makeText(context, R.string.rateSaved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.rateNotSaved, Toast.LENGTH_SHORT).show();
                    Log.e("New rating error",task.getException().getMessage());
                }
            }
        });
    }


    //*** FUNCTIONS THAT ALLOW GET SNAPSHOT DATA FROM SPECIFIC DATABASE NODE ***//

    /**
     * Read a specific database node and read again every time it changes. Active listener when all data is fetched
     * @param database
     * @param listener
     * @return
     */
    public void readData(DatabaseReference database, final OnGetDataListener listener){
        listener.onStart();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    /**
     * Read a specific database node only once. Active listener when all data is fetched
     * @param database
     * @param listener
     * @return
     */
    public void readDataOnce(DatabaseReference database, final OnGetDataListener listener){
        listener.onStart();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    /**
     * Read a specific storage url. Active listener when uri is fetched
     * @param storage
     * @param reference
     * @param listener
     */
    public void readPhoto(StorageReference storage, String reference, final OnGetPhotoListener listener){
        storage.child(reference).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                listener.onSuccess(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailed(e);
            }
        });
    }

    /**
     * Read a specific database node and read again every time one of its children change. Active listener when data is fetched
     * @param database
     * @param listener
     * @return
     */
    public void readChild(DatabaseReference database, final OnGetChildListener listener){
        listener.onStart();
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                listener.onChanged(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                listener.onRemoved(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    /**
     * Execute remove database object task and wait for its result
     * @param database
     * @param child
     * @param listener
     */
    public void executeRemoveTask(DatabaseReference database, String child, final OnCompleteTaskListener listener){
        listener.onStart();
        database.child(child).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    listener.onSuccess();
                } else {
                    listener.onFailed();
                }
            }
        });
    }

    //*** FUNCTIONS THAT UPDATE SPECIFIC DATABASE OBJECTS ***//

    /**
     * Remove parameter from database object
     * @param database
     * @param child
     * @param reference
     * @param value
     */
    public void removeValue (DatabaseReference database, String child, String reference, String value){
        database.child(child).child(reference).child(value).removeValue();
    }

    /**
     * Remove object from database
     * @param database
     * @param child
     */
    public void removeObject (DatabaseReference database, String child){
        database.child(child).removeValue();
    }


    /**
     * Update string parameter of database object
     * @param database
     * @param child
     * @param reference
     * @param value
     */
    public void updateStringParameter(DatabaseReference database, String child, String reference, String value){
        database.child(child).child(reference).setValue(value);
    }

    /**
     * Update int parameter of database object
     * @param database
     * @param child
     * @param reference
     * @param value
     */
    public void updateIntParameter(DatabaseReference database, String child, String reference, int value){
        database.child(child).child(reference).setValue(value);
    }

    /**
     * Update float parameter of database object
     * @param database
     * @param child
     * @param reference
     * @param value
     */
    public void updateFloatParameter(DatabaseReference database, String child, String reference, float value){
        database.child(child).child(reference).setValue(value);
    }

    /**
     * Update double parameter of database object
     * @param database
     * @param child
     * @param reference
     * @param value
     */
    public void updateDoubleParameter(DatabaseReference database, String child, String reference, double value){
        database.child(child).child(reference).setValue(value);
    }



    public AuthCredential getUserCredentials(String mail, String password) {
        return EmailAuthProvider.getCredential(mail, password);
    }







    /**
     * Update joins, when a user wants to join or leave a group, trip or challenge
     * @param action
     * @param databaseReference
     * @param reference
     * @param childId
     * @param user
     * @param numberOfMembers
     */
    public void updateJoins (String action, DatabaseReference databaseReference, String reference, String childId, User user, int numberOfMembers){
        DatabaseReference databaseUser = getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        if (action.equals("join")) {
            databaseReference.child(childId).child(FireBaseReferences.MEMBERS_REFERENCE).child(user.getId()).setValue("true");
            databaseUser.child(user.getId()).child(reference).child(childId).setValue("true");
            databaseReference.child(childId).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(numberOfMembers+1);
        } else {
            databaseReference.child(childId).child(FireBaseReferences.MEMBERS_REFERENCE).child(user.getId()).removeValue();
            databaseUser.child(user.getId()).child(reference).child(childId).removeValue();
            databaseReference.child(childId).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(numberOfMembers-1);
        }
    }
}
