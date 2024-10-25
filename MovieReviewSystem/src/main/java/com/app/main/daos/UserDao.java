package com.app.main.daos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.main.entity.User;

public interface UserDao extends JpaRepository<User, Long> {

}
