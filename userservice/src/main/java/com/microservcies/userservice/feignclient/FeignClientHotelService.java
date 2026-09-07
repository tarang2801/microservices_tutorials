package com.microservcies.userservice.feignclient;

import com.microservcies.userservice.dto.Hotel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "HOTELSERVICE")
public interface FeignClientHotelService {

    @GetMapping("/hotelservice/hotels/{hotelId}")
    Hotel getHotel(@PathVariable String hotelId);
}
