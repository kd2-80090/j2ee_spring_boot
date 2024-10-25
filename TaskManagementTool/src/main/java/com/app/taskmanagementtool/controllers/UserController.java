package com.app.taskmanagementtool.controllers;

import com.app.taskmanagementtool.entities.User;
import com.app.taskmanagementtool.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     *
     */
    @GetMapping("/getAllUsers")
    public List<User> getAllUserList() {

        return userService.getAllUsers();
    }

}
