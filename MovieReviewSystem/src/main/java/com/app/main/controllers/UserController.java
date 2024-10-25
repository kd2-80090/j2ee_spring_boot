package com.app.main.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.main.customexception.ResourceNotFoundException;
import com.app.main.dtos.UserDto;
import com.app.main.entity.User;
import com.app.main.services.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	private static final Logger log = LogManager.getLogger();
	
	@Autowired
	private UserService userService;

	/**
	 * @description : get all user using pagination
	 * @URL : http://localhost:8080/user/getAllUsers
	 * @method : GET method
	 * @param pageNumber
	 * @param pageSize
	 * @return : all users on pageNumber and passing pageSize 
	 */
	@GetMapping("/getAllUsers")
	public ResponseEntity<?> getAllUsers(@RequestParam (defaultValue = "0",required = false) int pageNumber,
			@RequestParam (defaultValue = "5", required = false) int pageSize) {
		
		List<UserDto> listOfUserDto =  userService.findAllUsers(pageNumber,pageSize);
		log.info("User List : " + listOfUserDto);
		
		if(listOfUserDto.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return new ResponseEntity<>(listOfUserDto,HttpStatus.OK);
	}
	
	/**
	 * @description : get user details by id
	 * @URL : http://localhost:8080/user/getUserById/id
	 * @method : GET method
	 * @return : user details
	 */
	@GetMapping("/getUserById/{id}")
	public ResponseEntity<?> getUser(@PathVariable Long id) {
		
		try {
			UserDto user = userService.findUserById(id);
			return new ResponseEntity<>(user,HttpStatus.OK);
		}
		catch (ResourceNotFoundException e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	    }
	}
	
	/**
	 * @description : add user details
	 * @URL : http://localhost:8080/user/addUser
	 * @method : POST method
	 * @return : added user details
	 */
	@PostMapping("/addUser")
	public ResponseEntity<?> getUser(@RequestBody User addUserDetails) {
				
		UserDto user =  userService.addNewUser(addUserDetails);
		
		return new ResponseEntity<>(user,HttpStatus.CREATED);
	}
	
	/**
	 * @description : update user details
	 * @URL : http://localhost:8080/user/updateUser/id
	 * @method : PUT method
	 * @return : updated user details
	 */
	@PutMapping("/updateUser/{id}")
	public ResponseEntity<?> updateUser(@RequestBody User updatedUser, @PathVariable Long id) {
		
		try {
			UserDto user =  userService.updateUserDetails(updatedUser,id);
			return new ResponseEntity<>(user,HttpStatus.OK);
		}
		catch (ResourceNotFoundException e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	    }
	}
		
	/**
	 * @description : delete user details
	 * @URL : http://localhost:8080/user/deleteUser/id
	 * @method : DELETE method
	 * @return : string message with user deleted or not
	 */
	@DeleteMapping("/deleteUser/{id}")
	public ResponseEntity<?> deleteUser( @PathVariable Long id) {			
			
		try {
			String message = userService.deleteUserDetails(id);
			return new ResponseEntity<>(message, HttpStatus.OK);
		}
		catch (ResourceNotFoundException e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	    }
	}
	
}
