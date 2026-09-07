package com.microservcies.userservice.feignclient;

import com.microservcies.userservice.dto.Rating;
import com.microservcies.userservice.entity.User;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "RATINGSERVICE")
public interface FeignClientRatingService {

    @GetMapping("/ratingservice/rating/user/{userId}")
    List<Rating> getRatings(@PathVariable String userId);

    @PostMapping("/ratingservice/rating")
    List<Rating> createRating(Rating rating);


}
