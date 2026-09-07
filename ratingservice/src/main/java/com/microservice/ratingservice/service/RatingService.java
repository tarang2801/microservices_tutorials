package com.microservice.ratingservice.service;

import com.microservice.ratingservice.entity.Rating;
import com.microservice.ratingservice.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService implements RatingServiceIntf{

    @Autowired
    RatingRepository ratingRepository;

    @Override
    public Rating createRating(Rating rating) {

        return ratingRepository.save(rating);

    }

    @Override
    public List<Rating> getRatings() {

        return ratingRepository.findAll();
    }

    @Override
    public List<Rating> getRatingsByHotelId(String hotelId) {
        return ratingRepository.findByHotelId(hotelId);
    }

    @Override
    public List<Rating> getRatingsbyUserId(String userId) {
        return ratingRepository.findByUserId(userId);
    }


}
