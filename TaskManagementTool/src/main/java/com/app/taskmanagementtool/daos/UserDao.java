package com.app.taskmanagementtool.daos;

import com.app.taskmanagementtool.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Long> {

}
