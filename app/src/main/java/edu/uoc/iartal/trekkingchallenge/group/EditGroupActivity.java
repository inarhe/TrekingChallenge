package edu.uoc.iartal.trekkingchallenge.group;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objects.Group;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class EditGroupActivity extends AppCompatActivity {
    private DatabaseReference databaseGroup;
    private Group group;
    private EditText editTextName, editTextDescription;
    private ProgressDialog progressDialog;
    private Boolean updateName, updateDesc;
    private String newName, newDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.editGroupToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.editGroupActivityName);

        // Get database references
        databaseGroup = FirebaseDatabase.getInstance().getReference(FireBaseReferences.GROUP_REFERENCE);

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Initialize variables
        progressDialog = new ProgressDialog(this);

        // Link layout elements with variables
        editTextName = (EditText) findViewById(R.id.etGroupName);
        editTextDescription = (EditText) findViewById(R.id.etGroupDesc);

        // Get data from item clicked on list groups activity
        Bundle bundle = getIntent().getExtras();
        group = bundle.getParcelable("group");

        // Show selected group information in the layout
        editTextName.setText(group.getName());
        editTextDescription.setText(group.getDescription());
    }

    /**
     * Executed when accept button is selected. Verify input parameters and update values
     * @param view
     */
    public void editGroup (View view) {
        //Initialize variables
        updateName = false;
        updateDesc = false;

        // Get input parameters
        newName = editTextName.getText().toString().trim();
        newDescription = editTextDescription.getText().toString().trim();

        // If some of the input parameters are incorrect, stops the function execution further
        if (TextUtils.isEmpty(newName)) {
            Toast.makeText(this, R.string.nameField, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(newDescription)) {
            Toast.makeText(this, R.string.descriptionField, Toast.LENGTH_LONG).show();
            return;
        }

        // Set progress dialog
        progressDialog.setMessage(getString(R.string.updating));
        progressDialog.show();

        // Check changes on group name parameter
        if (!(group.getName()).equals(newName)) {
            updateName = true;
        }

        // Check changes on group description parameter
        if ((!(group.getDescription()).equals(newDescription))) {
            updateDesc = true;
        }

        // Update database if its necessary
        if (updateName) {
            databaseGroup.child(group.getId()).child(FireBaseReferences.GROUP_NAME_REFERENCE).setValue(newName)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        if (updateDesc){
                            updateDescription();
                        }
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.editGroupOk, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.editGroupFail, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            if (updateDesc){
                updateDescription();
            }
            progressDialog.dismiss();
            finish();
        }
    }

    /**
     * Cancel edit group if cancel button is clicked
     * @param view
     */
    public void cancelEditGroup (View view) {
        finish();
    }

    /**
     * Update description group into database
     */
    private void updateDescription(){
        databaseGroup.child(group.getId()).child(FireBaseReferences.GROUP_DESCRIPTION_REFERENCE).setValue(newDescription)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), R.string.editGroupOk, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.editGroupFail, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
