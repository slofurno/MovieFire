package com.github.slofurno.moviefire.Events;

import com.github.slofurno.moviefire.Model.MovieDto;

/**
 * Created by slofurno on 5/7/2015.
 */
public class SelectMovieEvent {
    public MovieDto movie;

    public SelectMovieEvent(MovieDto movie){
        this.movie=movie;
    }
}
