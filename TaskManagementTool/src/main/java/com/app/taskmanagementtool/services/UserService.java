package com.app.taskmanagementtool.services;

import com.app.taskmanagementtool.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();
}
