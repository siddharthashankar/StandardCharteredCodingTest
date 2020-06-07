package com.sid.standardcharteredcodingtest.requests;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.sid.standardcharteredcodingtest.models.Movie;
import com.sid.standardcharteredcodingtest.requests.response.MovieResponse;
import com.sid.standardcharteredcodingtest.requests.response.MovieSearchResponse;
import com.sid.standardcharteredcodingtest.util.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Response;

public class MovieApiClient {
    private static final String TAG = "MovieApiClient";
    private static MovieApiClient mInstance = null;
    private MutableLiveData<List<Movie>> mMovies;
    private MutableLiveData<Movie> mMovie;
    private MutableLiveData<Boolean> mRecipeRequestTimeout = new MutableLiveData<>();
    private RetrieveMoviesRunnable mRetrieveMoviesRunnable;
    private RetrieveMovieRunnable mRetrieveMovieRunnable;

    public static MovieApiClient getInstance(){
        if (mInstance == null){
            mInstance = new MovieApiClient();
        }
        return mInstance;
    }

    private MovieApiClient(){
        mMovies = new MutableLiveData<>();
        mMovie = new MutableLiveData<>();
    }

    public LiveData<List<Movie>> getMovies(){
        return mMovies;
    }

    public LiveData<Movie> getMovie(){
        return mMovie;
    }

    public void searchMoviesApi(String query){
        if(mRetrieveMoviesRunnable != null){
            mRetrieveMoviesRunnable = null;
        }
        mRetrieveMoviesRunnable = new RetrieveMoviesRunnable(query);
        final Future handller = AppExecutor.getInstance().networkIO().submit(mRetrieveMoviesRunnable);

        AppExecutor.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                handller.cancel(true);
            }
        },3000, TimeUnit.MILLISECONDS);
    }

    public void searchMovieById(String movieId){
        if(mRetrieveMovieRunnable != null){
            mRetrieveMovieRunnable = null;
        }
        mRetrieveMovieRunnable = new RetrieveMovieRunnable(movieId);
        final Future handler = AppExecutor.getInstance().networkIO().submit(mRetrieveMovieRunnable);

        mRecipeRequestTimeout.setValue(false);
        AppExecutor.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                //let the user know its time out
                mRecipeRequestTimeout.postValue(true);
                handler.cancel(true);
            }
        },3000,TimeUnit.MILLISECONDS);
    }

    // Retrieve the movies value from webservice using retrofit
    private class RetrieveMoviesRunnable implements Runnable{
        private String query;
        private boolean cancelRequest;

        public RetrieveMoviesRunnable(String query){
            this.query = query;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            try{
                Response response = getMovies(query).execute();
                if(cancelRequest){
                    return;
                }
                if (response.code()== 200){
                    List<Movie> list = new ArrayList<>(((MovieSearchResponse)response.body()).getMovieList());
                    Log.d(TAG,"Response: "+response.code()+" , "+list.size());
                    mMovies.postValue(list);
                }else {
                    String error = response.errorBody().string();
                    Log.d(TAG,"Error: "+error);
                    mMovies.postValue(null);
                }
            }catch (IOException e){
                Log.d(TAG,"Error: "+e.getMessage());
                mMovies.postValue(null);
                e.printStackTrace();
            }
        }

        private Call<MovieSearchResponse> getMovies(String query){
            MovieAPI movieAPI = ServiceGenerator.getMovieAPI();
            Call<MovieSearchResponse> movieSearchResponse = movieAPI.searchMovie(
                    Constants.API_KEY,
                    query,
                    Constants.TYPE
            );
            return movieSearchResponse;
        }

        private void cancelRequest(){
            Log.d(TAG,"Cancel Request : Canceling the search request");
            cancelRequest = true;
        }

    }

    //retrive the recipe value based on Id from webservice using retrofit
    private class RetrieveMovieRunnable implements Runnable{
        private String movieId;
        private boolean cancelRequest;

        public RetrieveMovieRunnable(String movieId) {
            this.movieId = movieId;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getMovie(movieId).execute();
                if(cancelRequest){
                    return;
                }
                if(response.code() == 200){
                    Movie movie = new Movie();
                    Log.d(TAG,"response code "+response.code());
                  //  private TextView mMovieTitle, mYear, mRuntime, mRating, mSynopsisText, mScoreText, mReviewText, mDirectorName, mWriterName, mActorName;
                    String title = ((MovieResponse)response.body()).getTitle();
                    String year = ((MovieResponse)response.body()).getYear();
                    String runtime = ((MovieResponse)response.body()).getRuntime();
                    String rating = ((MovieResponse)response.body()).getImdbRating();
                    String synopsisText = ((MovieResponse)response.body()).getPlot();
                    String reviewText = ((MovieResponse)response.body()).getImdbVotes();
                    String directorName = ((MovieResponse)response.body()).getDirector();
                    String writerName = ((MovieResponse)response.body()).getWriter();
                    String actorName = ((MovieResponse)response.body()).getActors();
                    String imdbID = ((MovieResponse)response.body()).getImdbID();
                    String poster = ((MovieResponse)response.body()).getPoster();
                    movie.setTitle(title);
                    movie.setYear(year);
                    movie.setImdbID(imdbID);
                    movie.setPoster(poster);
                    movie.setRuntime(runtime);
                    movie.setImdbRating(rating);
                    movie.setPlot(synopsisText);
                    movie.setImdbVotes(reviewText);
                    movie.setDirector(directorName);
                    movie.setWriter(writerName);
                    movie.setActors(actorName);

                    mMovie.postValue(movie);
                }else {
                    String error = response.errorBody().string();
                    Log.d(TAG,"Error: "+error);
                    mMovie.postValue(null);
                }
            } catch (IOException e) {
                Log.d(TAG,"Error: "+e.getMessage());
                mMovie.postValue(null);
                e.printStackTrace();
            }

        }

        private Call<MovieResponse> getMovie(String movieId){
            MovieAPI recipeAPI = ServiceGenerator.getMovieAPI();
            Call<MovieResponse> movieResponse = recipeAPI.getMovieResponse(
                    Constants.API_KEY,
                    movieId
            );
            return movieResponse;
        }

        private void cancelRequest(){
            Log.d(TAG,"Cancel Request : Canceling the search request");
            cancelRequest = true;
        }
    }


}
