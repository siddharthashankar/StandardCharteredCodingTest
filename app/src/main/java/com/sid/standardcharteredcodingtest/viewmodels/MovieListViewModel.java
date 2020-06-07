package com.sid.standardcharteredcodingtest.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sid.standardcharteredcodingtest.models.Movie;
import com.sid.standardcharteredcodingtest.repositories.MovieRepository;

import java.util.List;

public class MovieListViewModel extends ViewModel {
    private MovieRepository mMovieRepository;
    private boolean mIsViewingMovies;

    public MovieListViewModel(){
        mMovieRepository = MovieRepository.getInstance();
        mIsViewingMovies = false;
    }

    public LiveData<List<Movie>> getMovies(){
        return mMovieRepository.getMovies();
    }

    public void searchMovieApi(String query){
        mMovieRepository.searchMovieApi(query);
        mIsViewingMovies = true;
    }

    public boolean isViewingMovies() {
        return mIsViewingMovies;
    }

    public void setIsViewingMovies(boolean mIsViewingMovies) {
        this.mIsViewingMovies = mIsViewingMovies;
    }
}
