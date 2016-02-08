package com.android.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.popularmovies.R;
import com.android.popularmovies.fragments.MovieDetailFragment;
import com.android.popularmovies.model.Movie;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putParcelable("Movie", getIntent().getData());

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }



//        Bundle bundle = getIntent().getExtras();
//
//        if(bundle!= null) {
//
//            mMovie = bundle.getParcelable("Movie");
//        }
//
//        if(savedInstanceState !=null){
//            mMovie  = savedInstanceState.getParcelable("Movie");
//
//        }

//        if (savedInstanceState != null || bundle != null) {
//
//        }else {
////            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.container, new MovieDetailFragment())
//                        .commit();
////            }

//        if(savedInstanceState == null && bundle == null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.container, mContent)
//                    .commit();
//        }else {

//            if (savedInstanceState == null) {
//                MovieDetailFragment df = (MovieDetailFragment) getSupportFragmentManager().findFragmentByTag("DETAILTAG");
//                if (df == null) {
//
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .add(R.id.container, MovieDetailFragment.newInstance(mMovie))
//                            .commit();
//                }
//
//            }
//        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        if (mMovie  != null)
//            outState.putParcelable("Movie", mMovie );
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
