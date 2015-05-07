package com.github.slofurno.moviefire.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slofurno on 5/5/2015.
 */
public class MovieDto{
    public List<Integer> Writers = new ArrayList<>();
    public List<Integer>Directors = new ArrayList<>();
    public List<Integer>Actors = new ArrayList<>();
    public int Id;
    public String Name;
    public String Year;
    public float Rating;
    public int Votes;

    MovieDto(){}

    public MovieDto(List<Integer>Writers, List<Integer>Directors, List<Integer>Actors){
        this.Writers=Writers;
        this.Directors = Directors;
        this.Actors = Actors;

    }

    public String toString(){
        return this.Name + " " + this.Year;
    }

    public List<Integer>getWriters(){
        return Writers;
    }

    public List<Integer>getDirectors(){
        return Directors;
    }

    public List<Integer>getActors(){
        return Actors;
    }

}
