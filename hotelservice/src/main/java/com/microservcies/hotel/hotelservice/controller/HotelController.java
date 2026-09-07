package com.microservcies.hotel.hotelservice.controller;

import com.microservcies.hotel.hotelservice.entity.Hotel;
import com.microservcies.hotel.hotelservice.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    HotelService hotelService;

    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotel(){
        List<Hotel> hotels = hotelService.listAllHotels();

        return ResponseEntity.status(HttpStatus.OK).body(hotels);

    }


    @PostMapping("/createHotel")
    public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel){

        Hotel newHotel = hotelService.createHotel(hotel);

        return  ResponseEntity.status(HttpStatus.OK).body(newHotel);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<Hotel> getHotel(@PathVariable String hotelId){

        Hotel hotel = hotelService.getHotel(hotelId);
        return ResponseEntity.status(HttpStatus.OK).body(hotel);
    }

}
