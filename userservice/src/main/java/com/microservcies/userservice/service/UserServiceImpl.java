package com.microservcies.userservice.service;

import com.microservcies.userservice.dto.Hotel;
import com.microservcies.userservice.dto.Rating;
import com.microservcies.userservice.entity.User;
import com.microservcies.userservice.feignclient.FeignClientHotelService;
import com.microservcies.userservice.feignclient.FeignClientRatingService;
import com.microservcies.userservice.repository.UserRepository;

import com.microservcies.userservice.exception.ResourceNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FeignClientHotelService hotelService;

    @Autowired
    private FeignClientRatingService ratingService;

    @Override
    public User saveUser(User user) {
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);
        User addedUser = userRepository.save(user);
        user.getRatings().get(0).setUserId(addedUser.getUserId());
        ratingService.createRating(user.getRatings().get(0));

        return addedUser;

    }

    int retryIndex = 1;
    @Override
//    @Retry(name = "ratingHotelService", fallbackMethod = "ratingHotelBreakerFallback")
//    @CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod = "ratingHotelBreakerFallback")
    @RateLimiter(name = "ratingHotelRateLimiter")
    public User getUser(String userId) {

        log.info("retry count "+retryIndex);
         User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found"+userId));

        // implementation using rest template
        ResponseEntity<List<Rating>> ratingsResponseEntity = restTemplate.exchange("http://RATINGSERVICE/ratingservice/rating/user/"+userId, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<Rating>>() {
         });

        List<Rating> ratings = ratingsResponseEntity.getBody().stream().map(rating -> {

            // implementation using rest template
            ResponseEntity<Hotel> hotelResponseEntity = restTemplate.exchange("http://HOTELSERVICE/hotelservice/hotels/" + rating.getHotelId(), HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<Hotel>() {
            });

            rating.setHotel(hotelResponseEntity.getBody());
            return rating;
        }).collect(Collectors.toList());

        user.setRatings(ratings);


        return user;
    }

    public User ratingHotelBreakerFallback(String userId, Exception e){
        log.info("Inside fallback method");
        User user = User.builder()
                .firstName("Dummu")
                .lastName("User")
                .about("Downstream api failed to this is a dummy user")
                .build();
        return user;
    }

    @Override
    public List<User> getAllUsers() {

        List<User> users = userRepository.findAll();

        List<User> usersWithRating = users.stream().map(user -> {

            // implementation using feign client
            List<Rating> ratings = ratingService.getRatings(user.getUserId());

            List<Rating> ratingsWithHotel = ratings.stream().map(rating -> {

                // implementation using feign client
                Hotel hotel = hotelService.getHotel(rating.getHotelId());

                rating.setHotel(hotel);
                return rating;
            }).collect(Collectors.toList());

            user.setRatings(ratingsWithHotel);
                    return user;
        }).collect(Collectors.toList());
        return  usersWithRating;
    }


}
