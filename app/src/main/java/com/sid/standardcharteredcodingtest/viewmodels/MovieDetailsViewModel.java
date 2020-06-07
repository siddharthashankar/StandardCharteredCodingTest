package com.sid.standardcharteredcodingtest.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sid.standardcharteredcodingtest.models.Movie;
import com.sid.standardcharteredcodingtest.repositories.MovieRepository;

public class MovieDetailsViewModel extends ViewModel {
    private MovieRepository mMovieRepository;
    private String mMovieId;
    private boolean mDidRetrieveMovie;

    public MovieDetailsViewModel(){
        mMovieRepository = MovieRepository.getInstance();
        mDidRetrieveMovie = false;
    }

    public LiveData<Movie> getMovie(){
        return mMovieRepository.getMovie();
    }

    public void searchMovieById(String movieId){
        mMovieId = movieId;
        mMovieRepository.searchMovieById(movieId);
    }

    public String getMovieId(){
        return mMovieId;
    }

    public void setmDidRetrieveMovie(boolean didRetrieveRecipe){
        mDidRetrieveMovie = didRetrieveRecipe;
    }
    public Boolean didRetrieveMovie(){
        return mDidRetrieveMovie;
    }
}
