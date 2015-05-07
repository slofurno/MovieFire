package com.github.slofurno.moviefire.Events;

import com.github.slofurno.moviefire.Model.MovieDto;

import java.util.List;

/**
 * Created by slofurno on 5/5/2015.
 */
public class NextMovieResult {
    public List<MovieDto> result;

    public NextMovieResult(List<MovieDto> result){
        this.result=result;
    }

}

