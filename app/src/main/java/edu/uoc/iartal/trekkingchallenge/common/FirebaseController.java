package edu.uoc.iartal.trekkingchallenge.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.group.AddGroupActivity;
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
 * Created by Ingrid Artal on 27/12/2017.
 */

public class FirebaseController {


    public void createUserAndHistory (final String alias, final String name, final String mail, final String password, final Context context, final ProgressDialog progressDialog){

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

                                History history = new History(idHistory, 0.0, 0.0, 0, 0, idUser);
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
                            Log.e("error",task.getException().getMessage());
                            progressDialog.dismiss();
                            Toast.makeText(context,R.string.failedRegister,Toast.LENGTH_SHORT).show();
                            ((Activity)context).finish();
                        }
                    }
                });
    }

    public FirebaseUser getActiveUserSession(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void loginDatabase(String email, String password, final ProgressDialog progressDialog, final Context context){
        // Execute firebase sign in function
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // If logging is successful start main activity
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

    public String getCurrentUserEmail(){
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
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
     *
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
     *
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
     *
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

    public void executeTask (DatabaseReference database, String child, String reference, String value, final OnCompleteTaskListener listener){
        listener.onStart();
        database.child(child).child(reference).setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void signOutDatabase(Context context){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(context, R.string.userSignOut, Toast.LENGTH_SHORT).show();
        ((Activity)context).finish();
    }

    public AuthCredential getUserCredentials(String mail, String password) {
        return EmailAuthProvider.getCredential(mail, password);
    }

    public String getFirebaseNewKey (DatabaseReference databaseReference){
        try{
            String id = databaseReference.push().getKey();
            return id;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void creatGroup(final DatabaseReference databaseGroup, final Group group, final String userAdmin, final Context context){
        final DatabaseReference databaseUser = getDatabaseReference(FireBaseReferences.USER_REFERENCE);

            databaseGroup.child(group.getId()).setValue(group).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        databaseGroup.child(group.getId()).child(FireBaseReferences.MEMBERS_REFERENCE).child(userAdmin).setValue("true");
                        databaseUser.child(userAdmin).child(FireBaseReferences.USER_GROUPS_REFERENCE).child(group.getId()).setValue("true")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(context, R.string.groupSaved, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, R.string.failedAddGroup,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(context, R.string.failedAddGroup, Toast.LENGTH_SHORT).show();
                        Log.e("error",task.getException().getMessage());
                    }
                }
            });
    }

    /**
     * Update joins, when a user wants to join or leave a group, trip or challenge
     * @param action
     * @param databaseReference
     * @param reference
     * @param id
     * @param user
     * @param numberOfMembers
     */
    public void updateJoins (String action, DatabaseReference databaseReference, String reference, String id, User user, int numberOfMembers){
        DatabaseReference databaseUser = getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        if (action.equals("join")) {
            databaseReference.child(id).child(FireBaseReferences.MEMBERS_REFERENCE).child(user.getId()).setValue("true");
            databaseUser.child(user.getId()).child(reference).child(id).setValue("true");
            databaseReference.child(id).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(numberOfMembers+1);
        } else {
            databaseReference.child(id).child(FireBaseReferences.MEMBERS_REFERENCE).child(user.getId()).removeValue();
            databaseUser.child(user.getId()).child(reference).child(id).removeValue();
            databaseReference.child(id).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(numberOfMembers-1);
        }
    }




    public void deleteFirebaseUser (final FirebaseUser firebaseUser, final User user, final Context context){
        AuthCredential credential = getUserCredentials(user.getMail(), user.getPassword());
        //removeUserDependencies(user);
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        firebaseUser.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {



                                            Toast.makeText(context, context.getResources().getString(R.string.userAccountDeleted), Toast.LENGTH_SHORT).show();
                                            ((Activity)context).finish();
                                        }
                                    }
                                });
                    }
                });
    }

    public void removeUserDependencies(User user){
        String history;
        ArrayList<String> userGroups = new ArrayList<>();
        ArrayList<String> userTrips = new ArrayList<>();
        ArrayList<String> userChallenges = new ArrayList<>();
        ArrayList<String> userChallengeResults = new ArrayList<>();
        ArrayList<String> userTripsDone = new ArrayList<>();
        ArrayList<String> userFinished = new ArrayList<>();
        ArrayList<String> userRatings = new ArrayList<>();
        ArrayList<String> userMessages = new ArrayList<>();


        history = user.getHistory();
        removeUserHistory (history);

        userGroups.addAll(user.getGroups().keySet());
        if (userGroups.size() != 0){
            removeGroupMember (user.getId(), userGroups);
        }

        userTrips.addAll(user.getTrips().keySet());
        if (userTrips.size() != 0){
            removeTripMember (user.getId(), userTrips);
        }

        userChallenges.addAll(user.getChallenges().keySet());
        if (userChallenges.size() != 0){
            removeChallengeMember (user.getId(), userChallenges);
        }

        userChallengeResults.addAll(user.getChallengeResults().keySet());
        if (userChallengeResults.size() != 0){
            removeChallengeResult (userChallengeResults);
        }

        userTripsDone.addAll(user.getTripsDone().keySet());
        if (userTripsDone.size() != 0){
            removeTripsDone (userTripsDone);
        }

        userFinished.addAll(user.getFinished().keySet());
        if (userFinished.size() != 0){
            removeFinished (userFinished);
        }


        userRatings.addAll(user.getRatings().keySet());
        if (userRatings.size() != 0){
            removeRating (userRatings);
        }


        userMessages.addAll(user.getMessages().keySet());
        if (userMessages.size() != 0){
            removeMessage (userMessages);
        }

        removeUser(user);


    }

    public void removeUserHistory(String history){
        final DatabaseReference databaseHistory = getDatabaseReference(FireBaseReferences.HISTORY_REFERENCE);
        databaseHistory.child(history).removeValue();
    }

    public void removeGroupMember (final String userId, final ArrayList<String> userGroups){
        final DatabaseReference databaseGroup = getDatabaseReference(FireBaseReferences.GROUP_REFERENCE);
        databaseGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Group group = data.getValue(Group.class);
                    if (userGroups.contains(group.getId())){
                        userGroups.remove(group.getId());
                        if (group.getMembers().size() == 1){
                            databaseGroup.child(group.getId()).removeValue();
                        } else {
                            updateMembers(databaseGroup, userId, group.getId(), group.getNumberOfMembers());

                            if (group.getUserAdmin().equals(userId) && group.getMembers().size() != 0) {
                                ArrayList<String> members = new ArrayList<>();
                                members.addAll(group.getMembers().keySet());
                                changeGroupAdmin(group.getId(), members, databaseGroup);
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeTripMember (final String userId, final ArrayList<String> userTrips){
        final DatabaseReference databaseTrip = getDatabaseReference(FireBaseReferences.TRIP_REFERENCE);
        databaseTrip.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Trip trip = data.getValue(Trip.class);
                    if (userTrips.contains(trip.getId())){
                        userTrips.remove(trip.getId());
                        if (trip.getMembers().size() == 1){
                            databaseTrip.child(trip.getId()).removeValue();
                        } else {
                            updateMembers(databaseTrip, userId, trip.getId(), trip.getNumberOfMembers());

                            if (trip.getUserAdmin().equals(userId) && trip.getMembers().size() != 0) {
                                ArrayList<String> members = new ArrayList<>();
                                members.addAll(trip.getMembers().keySet());
                                changeGroupAdmin(trip.getId(), members, databaseTrip);
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeChallengeMember (final String userId, final ArrayList<String> userChallenges){
        final DatabaseReference databaseChallenge = getDatabaseReference(FireBaseReferences.CHALLENGE_REFERENCE);

        databaseChallenge.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Challenge challenge = data.getValue(Challenge.class);

                    if (userChallenges.contains(challenge.getId())){
                        userChallenges.remove(challenge.getId());
                        if (challenge.getMembers().size() == 1){
                            databaseChallenge.child(challenge.getId()).removeValue();
                        } else {
                            updateMembers(databaseChallenge, userId, challenge.getId(), challenge.getNumberOfMembers());

                            if (challenge.getUserAdmin().equals(userId) && challenge.getMembers().size() != 0) {
                                ArrayList<String> members = new ArrayList<>();
                                members.addAll(challenge.getMembers().keySet());
                                changeGroupAdmin(challenge.getId(), members, databaseChallenge);
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeChallengeResult (final ArrayList<String> userChallengeResults){
        final DatabaseReference databaseChallenge = getDatabaseReference(FireBaseReferences.CHALLENGE_REFERENCE);
        final DatabaseReference databaseResult = getDatabaseReference(FireBaseReferences.CHALLENGERESULT_REFERENCE);

        databaseResult.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    ChallengeResult challengeResult = data.getValue(ChallengeResult.class);
                    if (userChallengeResults.contains(challengeResult.getId())){
                        userChallengeResults.remove(challengeResult.getId());
                        databaseChallenge.child(challengeResult.getChallenge()).child(FireBaseReferences.CHALLENGE_RESULTS_REFERENCE).child(challengeResult.getId()).removeValue();
                        databaseResult.child(challengeResult.getId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeTripsDone (final ArrayList<String> userTripsDone){
        final DatabaseReference databaseTrip = getDatabaseReference(FireBaseReferences.TRIP_REFERENCE);
        final DatabaseReference databaseTripsDone = getDatabaseReference(FireBaseReferences.TRIPSDONE_REFERENCE);

        databaseTripsDone.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    TripDone tripDone = data.getValue(TripDone.class);
                    if (userTripsDone.contains(tripDone.getId())){
                        userTripsDone.remove(tripDone.getId());
                        databaseTrip.child(tripDone.getTrip()).child(FireBaseReferences.TRIP_DONE_REFERENCE).child(tripDone.getId()).removeValue();
                        databaseTripsDone.child(tripDone.getId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeFinished (final ArrayList<String> userFinished){
        final DatabaseReference databaseRoute = getDatabaseReference(FireBaseReferences.ROUTE_REFERENCE);
        final DatabaseReference databaseFinished = getDatabaseReference(FireBaseReferences.FINISHED_REFERENCE);

        databaseFinished.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Finished finished = data.getValue(Finished.class);
                    if (userFinished.contains(finished.getId())){
                        userFinished.remove(finished.getId());
                        databaseRoute.child(finished.getRoute()).child(FireBaseReferences.ROUTE_FINISHED_REFERENCE).child(finished.getId()).removeValue();
                        databaseFinished.child(finished.getId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeRating (final ArrayList<String> userRatings){
        final DatabaseReference databaseRating = getDatabaseReference(FireBaseReferences.RATING_REFERENCE);

        databaseRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Rating rating = data.getValue(Rating.class);
                    if (userRatings.contains(rating.getId())){
                        databaseRating.child(rating.getId()).child(FireBaseReferences.OBJECT_USER_REFERENCE).setValue("anonymous");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeMessage (final ArrayList<String> userMessages){
        final DatabaseReference databaseMessage = getDatabaseReference(FireBaseReferences.MESSAGE_REFERENCE);
        final DatabaseReference databaseTrip = getDatabaseReference(FireBaseReferences.TRIP_REFERENCE);
        final DatabaseReference databaseGroup = getDatabaseReference(FireBaseReferences.GROUP_REFERENCE);

        databaseMessage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Message message = data.getValue(Message.class);
                    if (userMessages.contains(message.getId())){
                        if (message.getTrip().equals("none")){
                            databaseGroup.child(message.getGroup()).child(FireBaseReferences.MESSAGES_REFERENCE).child(message.getId()).removeValue();
                        } else {
                            databaseTrip.child(message.getTrip()).child(FireBaseReferences.MESSAGES_REFERENCE).child(message.getId()).removeValue();
                        }
                        userMessages.remove(message.getId());
                        databaseMessage.child(message.getId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeUser(User user){
        DatabaseReference databaseUser = getDatabaseReference(FireBaseReferences.USER_REFERENCE);
        databaseUser.child(user.getId()).removeValue();

    }

    public void updateMembers(DatabaseReference databaseReference, String idUser, String idObject, int numberOfMembers){
        databaseReference.child(idObject).child(FireBaseReferences.MEMBERS_REFERENCE).child(idUser).removeValue();
        databaseReference.child(idObject).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(numberOfMembers - 1);
    }

    public void changeGroupAdmin(String idObject, ArrayList <String> members, DatabaseReference databaseReference){
        String admin = members.get(0);
        databaseReference.child(idObject).child(FireBaseReferences.USER_ADMIN_REFERENCE).setValue(admin);
    }


}
