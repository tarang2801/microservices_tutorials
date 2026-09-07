package com.microservcies.hotel.hotelservice.service;

import com.microservcies.hotel.hotelservice.entity.Hotel;
import com.microservcies.hotel.hotelservice.exception.ResourceNotFoundException;
import com.microservcies.hotel.hotelservice.repository.HotelRepository;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    HotelRepository hotelRepository;

    @Override
    public Hotel createHotel(Hotel hotel) {

        String randomId = UUID.randomUUID().toString();
        hotel.setId(randomId);
        return hotelRepository.save(hotel);

    }

    @Override
    public List<Hotel> listAllHotels() {

        return hotelRepository.findAll();

    }

    @Override
    public Hotel getHotel(String hotelId) {

        return hotelRepository.findById(hotelId).orElseThrow(()->new ResourceNotFoundException("Hotel details not found for id "+hotelId));

    }
}
