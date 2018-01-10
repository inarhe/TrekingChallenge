/* Copyright 2018 Ingrid Artal Hermoso

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package edu.uoc.iartal.trekkingchallenge.route;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.model.Route;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class DetailRouteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_route);

        // Initialize variables
        FirebaseController controller = new FirebaseController();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
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

        // Link layout elements with variables
        TextView textViewDetails = (TextView) findViewById(R.id.textArea_information);
        TextView textViewMeteo = (TextView) findViewById(R.id.tvMeteo);
        TextView textViewStartPlace = (TextView) findViewById(R.id.tvStartPlace);

        // Show selected route information in the layout
        textViewDetails.setMovementMethod(new ScrollingMovementMethod());
        textViewDetails.setText(route.getDescription());
        textViewMeteo.setText(route.getMeteo());
        textViewStartPlace.setText(route.getStartPlace());
    }
}
