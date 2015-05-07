package com.github.slofurno.moviefire.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.github.slofurno.moviefire.Events.MovieSearchResult;
import com.github.slofurno.moviefire.Events.SelectMovieEvent;
import com.github.slofurno.moviefire.Events.SearchMovieEvent;
import com.github.slofurno.moviefire.Events.NextMovieResult;
import com.github.slofurno.moviefire.Model.CastDto;
import com.github.slofurno.moviefire.Model.MovieDto;
import com.github.slofurno.moviefire.Model.Tuple;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FireService extends Service {

    Bus bus;
    FirebaseApi movies = new FirebaseApi("https://firemovies.firebaseio.com/movies");
    FirebaseApi movielookup = new FirebaseApi("https://firemovies.firebaseio.com/movielookup");
    FirebaseApi cast = new FirebaseApi("https://firemovies.firebaseio.com/cast");

    @Override
    public void onCreate() {
        bus = OttoBus.getInstance().getBus();
        bus.register(this);
    }

    @Subscribe
    public void searchMovie(final SearchMovieEvent event){
        Thread thread = new Thread() {
            public void run() {
                try {
                    List<MovieDto> results = movies.Search(event.term, MovieDto.class);
                    bus.post(new MovieSearchResult(results));
                }
                catch (MalformedURLException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();


    }

    @Subscribe
    public void recommendMovies(final SelectMovieEvent event){

        Thread thread = new Thread() {
            public void run() {
                try {
                    MovieDto movie = event.movie;
                    List<String>castids = new ArrayList<>();


                    for(int id : movie.Actors){
                        castids.add(Integer.toString(id));
                    }

                    for(int id : movie.Directors){
                        castids.add(Integer.toString(id));
                    }

                    for(int id : movie.Writers){
                        castids.add(Integer.toString(id));
                    }

                    List<CastDto> moviecast = cast.Get(castids, CastDto.class);
                    List<MovieDto> results = determineRecommendations(moviecast, movie.Id);
                    bus.post(new NextMovieResult(results));

                } catch (MalformedURLException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    public List<MovieDto> determineRecommendations(List<CastDto> cast, Integer skipid) throws MalformedURLException, InterruptedException, ExecutionException{

        HashMap<Integer, Integer> movies = new HashMap<>();

        for(CastDto actor:cast){
            int weight = actor.Movies.size();

            for(Integer movieid : actor.Movies){
                Integer count = movies.get(movieid);
                if (count==null){
                    count=0;
                }
                //Integer count = movies.getOrDefault(movieid, 0);
                count+=weight;
                movies.put(movieid, count);
            }
        }

        movies.put(skipid, 0);
        List<Tuple<Integer,Integer>>pairs = new ArrayList<>();

        for(Integer key: movies.keySet()){
            pairs.add(new Tuple<>(key, movies.get(key)));
        }

        for(int i = 1; i<pairs.size();i++ ){
            int j = i;
            while (j>0 && pairs.get(j-1).Item2 > pairs.get(j).Item2){
                Tuple<Integer, Integer> temp = pairs.get(j-1);
                pairs.set(j-1, pairs.get(j));
                pairs.set(j, temp);
                j--;
            }
        }

        int start = Math.max(pairs.size()-10, 0);
        List<String>movieids = new ArrayList<>();

        for(int i = pairs.size()-1; i>=start;i-- ){
            movieids.add(Integer.toString(pairs.get(i).Item1));
        }
        return movielookup.Get(movieids, MovieDto.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
