package com.app.taskmanagementtool.services;

import com.app.taskmanagementtool.daos.UserDao;
import com.app.taskmanagementtool.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDao userDao;

    @Override
    public List<User> getAllUsers() {

        return userDao.findAll();
    }
}
