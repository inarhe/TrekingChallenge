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

package edu.uoc.iartal.trekkingchallenge.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import edu.uoc.iartal.trekkingchallenge.R;


public class AccessActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.accessToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.accessActivity));
    }

    /**
     * Open register activity when register button is clicked
     * @param view
     */
    public void registerActivity (View view){
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        finish();
    }

    /**
     * Open login activity when login button is clicked
     * @param view
     */
    public void loginActivity (View view){
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
