package com.microservcies.userservice.controller;

import com.microservcies.userservice.entity.User;
import com.microservcies.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){

        List<User> users = userService.getAllUsers();

        return new ResponseEntity<>(users, HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){

        User newUser = userService.saveUser(user);


        return ResponseEntity.status(HttpStatus.OK).body(newUser);

    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId){

        User user = userService.getUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

}
