package com.microservcies.userservice.entity;

import com.microservcies.userservice.dto.Rating;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Table(name = "micro_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {


    @Id
    @Column(name = "ID")
    private String userId;

    @Column(name = "FIRSTNAME", length = 15)
    private String firstName;

    @Column(name = "LASTNAME", length = 15)
    private String lastName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ABOUT", length = 100)
    private String about;

    @Transient
    private List<Rating> ratings;

}
