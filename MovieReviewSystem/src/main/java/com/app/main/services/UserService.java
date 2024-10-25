package com.app.main.services;

import java.util.List;

import com.app.main.dtos.UserDto;
import com.app.main.entity.User;

public interface UserService {

	List<UserDto> findAllUsers(int pageNumber, int pageSize);

	UserDto findUserById(Long id);

	UserDto addNewUser(User addUserDetails);

	UserDto updateUserDetails(User updatedUser, Long id);

	String deleteUserDetails(Long id);
}
