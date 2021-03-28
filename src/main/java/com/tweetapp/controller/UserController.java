package com.tweetapp.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.model.ChangePasswordTo;
import com.tweetapp.model.CreateUserTo;
import com.tweetapp.model.BaseUserTo;
import com.tweetapp.service.UserService;

@RestController
@RequestMapping("/users/v1")
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<String> signUp(@Valid @RequestBody CreateUserTo createUserTo) {
		String response = userService.signUp(createUserTo);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@Valid @RequestBody BaseUserTo userTo) {
		String response = userService.login(userTo);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@PostMapping("/change-password")
	public ResponseEntity<String> get(@Valid @RequestBody ChangePasswordTo changePasswordTo) {
		String response = userService.changePassword(changePasswordTo);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

}