package edu.uoc.iartal.trekkingchallenge.group;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ListGroupsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_groups);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.listGroupToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.listGroupsActivity));

        // If user isn't logged, start login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Create the adapter that will return a fragment for each of the two primary sections of the activity
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager that will host the section contents.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pagerGroups);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the TabLayout that will contains all necessary tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Set up listeners for tab selected and the content it must show
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
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
                    return new AllGroupsFragment();
                case 1:
                    return new MyGroupsFragment();
            }
            return null;
        }

        /**
         * Get pages amount
         * @return number of pages
         */
        @Override
        public int getCount() {
            // Show 2 total pages.
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
                    return "TOTS";
                case 1:
                    return "ELS MEUS";
            }
            return null;
        }
    }
}
