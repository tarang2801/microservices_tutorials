package com.microservcies.hotel.hotelservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "micro_hotel")
public class Hotel {

    @Id
    private String id;
    private String name;
    private String location;
    private String about;


}
