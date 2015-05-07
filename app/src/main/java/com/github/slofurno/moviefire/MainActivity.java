package com.github.slofurno.moviefire;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.github.slofurno.moviefire.Events.MovieSearchResult;
import com.github.slofurno.moviefire.Events.SearchMovieEvent;
import com.github.slofurno.moviefire.Events.NextMovieResult;
import com.github.slofurno.moviefire.Events.SelectMovieEvent;
import com.github.slofurno.moviefire.Model.MovieDto;
import com.github.slofurno.moviefire.Service.OttoBus;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    Bus bus;
    private ArrayAdapter<String> recommendationAdapter;
    private List<String> recomendations;

    private ArrayAdapter<MovieDto> movieAdapter;
    private List<MovieDto> movies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movies=new ArrayList<>();
        movieAdapter = new ArrayAdapter<MovieDto>(this, android.R.layout.simple_list_item_1, movies);

        final ListView searchresultView = (ListView) findViewById(R.id.searchList);
        searchresultView.setAdapter(movieAdapter);

        searchresultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                MovieDto movie = (MovieDto) searchresultView.getItemAtPosition(position);
                bus.post(new SelectMovieEvent(movie));

            }

        });

        recomendations=new ArrayList<>();
        recommendationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recomendations);

        ListView listView = (ListView) findViewById(R.id.suggestionList);
        listView.setAdapter(recommendationAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onResume() {
        bus = OttoBus.getInstance().getBus();
        bus.register(this);
        super.onResume();
    }

    @Override protected void onPause() {
        OttoBus.getInstance().getBus().unregister(this);
        super.onPause();
    }

    @Subscribe
    public void displaySearchResults(final MovieSearchResult event){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                movies.clear();
                recomendations.clear();
                for (MovieDto movie : event.result) {
                    movies.add(movie);
                }
                movieAdapter.notifyDataSetChanged();
                recommendationAdapter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe
    public void displayRecommendations(final NextMovieResult event){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                movies.clear();
                recomendations.clear();

                for (MovieDto movie : event.result) {
                    recomendations.add(movie.Name);
                }
                movieAdapter.notifyDataSetChanged();
                recommendationAdapter.notifyDataSetChanged();
            }
        });
    }


    public void sendMessage(View view) {

        EditText message = (EditText)findViewById(R.id.nextmessage);
        bus.post(new SearchMovieEvent( message.getText().toString()));
    }

    public void nextMovie(View view) {


    }
}
