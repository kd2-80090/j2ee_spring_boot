package com.app.main.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.app.main.customexception.ResourceNotFoundException;
import com.app.main.daos.UserDao;
import com.app.main.dtos.UserDto;
import com.app.main.entity.User;

@Service
public class UserServiceImpl implements UserService{
	
	private static final Logger log = LogManager.getLogger();
	
	@Autowired 
	private UserDao userDao;

	@Override
	public List<UserDto> findAllUsers(int pageNumber,int pageSize) {
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		
		List<User> users =  userDao.findAll(pageable).getContent();
		
		log.info("Retrieved users: " + users);
		
		List<UserDto> listOfUserDto = new ArrayList<UserDto>();
		
		for (User user : users) {

			UserDto userDto = new UserDto();
			
			userDto.setId(user.getId());
			userDto.setEmail(user.getEmail());
			userDto.setName(user.getName());
			userDto.setMobileNo(user.getMobileNo());
			userDto.setStatus(user.getStatus());
			log.info("User Dto : " + userDto);
		
			listOfUserDto.add(userDto);
		}
		log.info("User Dto List to return: " + listOfUserDto);
		return listOfUserDto;
	}

	@Override
	public UserDto findUserById(Long id) {
		
		User user = userDao.findById(id).orElseThrow(()-> new ResourceNotFoundException("User Not Found"));
				
		UserDto userDto = new UserDto();
		
		userDto.setId(user.getId());
		userDto.setEmail(user.getEmail());
		userDto.setName(user.getName());
		userDto.setMobileNo(user.getMobileNo());
		userDto.setStatus(user.getStatus());
		log.info("Retrieved user dto: " + userDto);

		return userDto;
	}

	@Override
	public UserDto addNewUser(User addUserDetails) {

		addUserDetails.setStatus("active");
		User persistentUser = userDao.save(addUserDetails);
		
		UserDto userDto = new UserDto();
		
		userDto.setId(persistentUser.getId());
		userDto.setEmail(persistentUser.getEmail());
		userDto.setName(persistentUser.getName());
		userDto.setMobileNo(persistentUser.getMobileNo());
		userDto.setStatus(persistentUser.getStatus());
		log.info("Added user dto: " + userDto);
		
		return userDto;
	}

	@Override
	public UserDto updateUserDetails(User updatedUser, Long id) {

		User userDetails = userDao.findById(id).orElseThrow(()-> new ResourceNotFoundException("User Not Found"));
		
		userDetails.setName(updatedUser.getName());
		userDetails.setEmail(updatedUser.getEmail());
		userDetails.setMobileNo(updatedUser.getMobileNo());
		
		User persistentUser =  userDao.save(userDetails);
		
		UserDto userDto = new UserDto();
		
		userDto.setId(persistentUser.getId());
		userDto.setEmail(persistentUser.getEmail());
		userDto.setName(persistentUser.getName());
		userDto.setMobileNo(persistentUser.getMobileNo());
		userDto.setStatus(persistentUser.getStatus());

		log.info("Updated user dto details: " + userDto);
		return userDto;
	}

	@Override
	public String deleteUserDetails(Long id) {

		User user = userDao.findById(id).orElseThrow(()-> new ResourceNotFoundException("User Not Found"));
		
		if ("inactive".equals(user.getStatus())) {
	        return "User is already inactive";
	    }
		user.setStatus("inactive");
		userDao.save(user);
		
		return "Success : User Deleted Successfully";
	}
}
