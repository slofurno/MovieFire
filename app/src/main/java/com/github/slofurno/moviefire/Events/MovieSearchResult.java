package com.github.slofurno.moviefire.Events;

import com.github.slofurno.moviefire.Model.MovieDto;

import java.util.List;

public class MovieSearchResult {
    public List<MovieDto> result;

    public MovieSearchResult(List<MovieDto> result){
        this.result=result;
    }

}
