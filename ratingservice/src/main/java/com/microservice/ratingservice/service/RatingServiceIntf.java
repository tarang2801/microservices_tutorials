package com.microservice.ratingservice.service;

import com.microservice.ratingservice.entity.Rating;

import java.util.List;

public interface RatingServiceIntf {

    Rating createRating(Rating rating);
    List<Rating> getRatings();

    List<Rating> getRatingsByHotelId(String hotelId);

    List<Rating> getRatingsbyUserId(String userId);



}
