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

package edu.uoc.iartal.trekkingchallenge.trip;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.CommonFunctionality;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ListTripsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CommonFunctionality common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_trips);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.listTripToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.listTripsActivity));

        // Initialize variables
        FirebaseController controller = new FirebaseController();
        common = new CommonFunctionality();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Create the adapter that will return a fragment for each of the two primary sections of the activity
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager that will host the section contents
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pagerTrips);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the TabLayout that will contains all necessary tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Set up listeners for tab selected and the content it must show
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        // Set actionbar drawer layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // Set navigation view listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position){
                case 0:
                    return new AllTripsFragment();
                case 1:
                    return new MyTripsFragment();
            }
            return null;
        }

        /**
         * Get pages amount
         * @return number of pages
         */
        @Override
        public int getCount() {
            // Show 2 total pages
            return 2;
        }

        /**
         * Define tab name according to its position
         * @param position
         * @return tab name
         */
        @Override
        public CharSequence getPageTitle(int position){
            switch (position){
                case 0:
                    return getString(R.string.tabAllTrips);
                case 1:
                    return getString(R.string.tabMyTrips);
            }
            return null;
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    /**
     * Handle navigation drawer view item clicked
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeItem:
                common.startHomeNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.mapItem:
                common.startMapNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.routeItem:
                common.startRouteNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.tripItem:
                common.startTripNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.challengeItem:
                common.startChallengeNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.userItem:
                common.startUserNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.groupItem:
                common.startGroupNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.logoutItem:
                common.startLogOutNavigationDrawer(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            default:
                return true;
        }
    }
}
