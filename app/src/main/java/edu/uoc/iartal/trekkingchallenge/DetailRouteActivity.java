package edu.uoc.iartal.trekkingchallenge;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import edu.uoc.iartal.trekkingchallenge.objectsDB.Route;

public class DetailRouteActivity extends AppCompatActivity {

    private TextView textViewDetails, textViewMeteo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_route);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detailRouteToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.infoDetail));

        Bundle bundle = getIntent().getExtras();
        Route route = bundle.getParcelable("route");
        //String details = bundle.getString("details");
       // String meteo = bundle.getString("meteo");

        textViewDetails = (TextView) findViewById(R.id.textArea_information);
        textViewDetails.setText(route.getDescription());
        textViewMeteo = (TextView) findViewById(R.id.tvMeteo);
      //  textViewMeteo.setMovementMethod(LinkMovementMethod.getInstance());
        textViewMeteo.setText(route.getMeteo());

        Intent result = new Intent(this, ShowRouteActivity.class);
        result.putExtra("route", route);
        setResult(RESULT_OK, result);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
        //    case R.id.home:
          //      NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
