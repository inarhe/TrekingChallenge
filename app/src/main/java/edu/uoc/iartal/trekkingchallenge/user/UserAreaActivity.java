package edu.uoc.iartal.trekkingchallenge.user;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;
import edu.uoc.iartal.trekkingchallenge.R;

public class UserAreaActivity extends AppCompatActivity {

    private static final int ACTIVITY_CODE = 1;
    private TextView textViewUserName, textViewUserMail, textViewIdUser;
    private FirebaseAuth firebaseAuth;
    private String idUser, userName, userMail, userPassword, userKey, currentMail;
    private DatabaseReference databaseUser;
    private Intent intent;
    private ProgressDialog progressDialog;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        Log.i("create","ok");

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.userToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.userAreaActivity));

        // Get Firebase authentication instance
       // firebaseAuth =

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);

        // Link layout elements with variables
        textViewIdUser = (TextView) findViewById(R.id.tvIdUser);
        textViewUserName = (TextView) findViewById(R.id.tvUserName);
        textViewUserMail = (TextView) findViewById(R.id.tvUserMail);

        // Load user information
        loadUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_area_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_editProfile:
               // startActivityForResult(intent, ACTIVITY_CODE);
                loadUser();
                intent = new Intent(UserAreaActivity.this, EditProfileActivity.class);
                intent.putExtra("user", user);
               // startActivity(intent);
                startActivityForResult(intent, ACTIVITY_CODE);
               // finish();
                return true;
            case R.id.action_deleteProfile:
                deleteUserAccount();
                return true;
            case R.id.action_userHistory:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    public void deleteUserAccount(){
        // Delete user account when delete button is clicked
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.deleteUserConfirmation));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(userMail, userPassword);

                        if (user != null){
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            user.delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getApplicationContext(), getString(R.string.userAccountDeleted), Toast.LENGTH_LONG).show();
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        } else {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }
                    }
                });

        builder.setNegativeButton(
                getString(R.string.cancelButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();

    }

    /**
     * Logout when sign out button is clicked
     * @param view
     */
    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
    }



       @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      //  textViewIdUser.setText(adat.getStringExtra("idUser"));
      //  textViewUserName.setText(data.getStringExtra("userName"));
      //  textViewUserMail.setText(data.getStringExtra("userMail"));
         //  user = data.getParcelableExtra("user");
         //  loadUser();
           if (data.getStringExtra("userMail") != null){
               textViewUserMail.setText(data.getStringExtra("userMail"));
           }


      //  intent.putExtra("idUser", data.getStringExtra("idUser"));
     //   intent.putExtra("userName", data.getStringExtra("userName"));
     //   intent.putExtra("userMail", data.getStringExtra("userMail"));
        //NECESSITAT DE PASSAR CONTRASENYA TAMBÉ PER SI CANVIA QUE S'ACTUALITZI AL EDITAR
    }

    /**
     * Load data of current user
     */
    private void loadUser(){

        currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);


        // Show message on progress dialog
        progressDialog.setMessage(getString(R.string.loadingData));
        progressDialog.show();

        // Query database to get user information
        Query query = databaseUser.orderByChild(FireBaseReferences.USERMAIL_REFERENCE).equalTo(currentMail);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                user = dataSnapshot.getValue(User.class);

                textViewIdUser.setText(user.getIdUser());
                textViewUserName.setText(user.getUserName());
                textViewUserMail.setText(user.getUserMail());

                Log.i("mailadded",user.getUserMail() );


                /*idUser = dataSnapshot.getValue(User.class).getIdUser();
                userName = dataSnapshot.getValue(User.class).getUserName();
                userMail = dataSnapshot.getValue(User.class).getUserMail();
                userPassword = dataSnapshot.getValue(User.class).getUserPassword();
                userKey = dataSnapshot.getValue(User.class).getIdUser();

                textViewIdUser.setText(idUser);
                textViewUserName.setText(userName);
                textViewUserMail.setText(userMail);

                intent.putExtra("idUser", idUser);
                intent.putExtra("userName", userName);
                intent.putExtra("userMail", userMail);
                intent.putExtra("userPassword", userPassword);
                intent.putExtra("userKey", userKey);*/

                progressDialog.dismiss();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //TO-DO
                user = dataSnapshot.getValue(User.class);

                textViewIdUser.setText(user.getIdUser());
                textViewUserName.setText(user.getUserName());
                textViewUserMail.setText(user.getUserMail());

                Log.i("mailchanged",user.getUserMail() );
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

            }
        });
    }
}
