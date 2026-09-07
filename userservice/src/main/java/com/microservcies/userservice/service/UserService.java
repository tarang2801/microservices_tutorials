package com.microservcies.userservice.service;

import com.microservcies.userservice.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    User saveUser(User user);

    User getUser(String id);

    List<User> getAllUsers();

}
