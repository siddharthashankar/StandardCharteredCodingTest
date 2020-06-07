package com.sid.standardcharteredcodingtest.requests;

import com.sid.standardcharteredcodingtest.requests.response.MovieResponse;
import com.sid.standardcharteredcodingtest.requests.response.MovieSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieAPI {
    //Search request
   @GET("/")
   Call<MovieSearchResponse> searchMovie(
           @Query("apikey") String apikey,
           @Query("s") String query,
           @Query("type") String type

   );

    //Recipe Request
   @GET("/")
    Call<MovieResponse> getMovieResponse(
            @Query("apikey") String key,
            @Query("i") String response_id

   );
}
