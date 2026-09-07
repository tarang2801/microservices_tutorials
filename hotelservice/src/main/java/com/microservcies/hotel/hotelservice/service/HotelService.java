package com.microservcies.hotel.hotelservice.service;

import com.microservcies.hotel.hotelservice.entity.Hotel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HotelService {

    Hotel createHotel(Hotel hotel);

    List<Hotel> listAllHotels();

    Hotel getHotel(String hotelId);


}
