package edu.uoc.iartal.trekkingchallenge.route;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.objects.Route;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class DetailRouteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_route);

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.detailRouteToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.titleDetail));

        // Get data from show route activity
        Bundle bundle = getIntent().getExtras();
        Route route = bundle.getParcelable("route");

        // Link layout elements with variables and set values
        TextView textViewDetails = (TextView) findViewById(R.id.textArea_information);
        textViewDetails.setMovementMethod(new ScrollingMovementMethod());
        textViewDetails.setText(route.getDescription());
        TextView textViewMeteo = (TextView) findViewById(R.id.tvMeteo);
        textViewMeteo.setText(route.getMeteo());
        TextView textViewStartPlace = (TextView) findViewById(R.id.tvStartPlace);
        textViewStartPlace.setText(route.getStartPlace());
    }
}
