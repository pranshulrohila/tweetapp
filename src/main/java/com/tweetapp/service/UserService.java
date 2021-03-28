package com.tweetapp.service;

import java.util.Calendar;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tweetapp.entity.UserJpa;
import com.tweetapp.exception.RecordNotFoundException;
import com.tweetapp.model.BaseUserTo;
import com.tweetapp.model.ChangePasswordTo;
import com.tweetapp.model.CreateUserTo;
import com.tweetapp.repository.UserRepository;

@Service
@Transactional
public class UserService {

	private final String BAD_CREDS = "Bad Credentials";
	private final String SUCCESS = "Successful Login";
	private final String DUPLICATE_USER = "Duplicate User";
	private final String USER_ADDED = "User Created Successfully";
	private final String PASSWORD_CHANGE_SUCCESS = "Password Changed Successfully";
	private final String NEW_AND_CONFIRM_PASSWORD_MISMATCH = "New Password and Confirm New Password did not match.";
	private final String PASSWORD_AND_CONFIRM_PASSWORD_MISMATCH = "Password and Confirm Password did not match.";
	private final String OLD_PASSWORD_WRONG = "Old Password is incorrect.";
	private final String SAME_NEW_PASSWORD_AND_OLD_PASSWORD = "New Password cannot be same as Old Password.";

	private final Date CURRENT_DATE = Calendar.getInstance().getTime();

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public String login(BaseUserTo userTo) {
		UserJpa userJpa = userRepository.findByUsername(userTo.getName());
		return userJpa != null && bCryptPasswordEncoder.matches(userTo.getPassword(), userJpa.getPassword()) ? SUCCESS
				: BAD_CREDS;
	}

	public String signUp(CreateUserTo createUserTo) {
		if (!isPasswordAndConfmPasswordSame(createUserTo.getConfirmPassword(), createUserTo.getPassword())) {
			return PASSWORD_AND_CONFIRM_PASSWORD_MISMATCH;
		}
		UserJpa existingUser = userRepository.findByUsername(createUserTo.getName());
		if (existingUser == null) {
			existingUser = new UserJpa();
			populateUserJpaFromCreateUserTo(createUserTo, existingUser);
			userRepository.save(existingUser);
			return USER_ADDED;
		}
		return DUPLICATE_USER;
	}

	public String changePassword(ChangePasswordTo changePasswordTo) {
		if (!isPasswordAndConfmPasswordSame(changePasswordTo.getNewPassword(),
				changePasswordTo.getConfirmNewPassword())) {
			return NEW_AND_CONFIRM_PASSWORD_MISMATCH;
		}
		if (changePasswordTo.getPassword().equals(changePasswordTo.getNewPassword())) {
			return SAME_NEW_PASSWORD_AND_OLD_PASSWORD;
		}
		UserJpa existingUser = userRepository.findByUsername(changePasswordTo.getName());
		if (existingUser == null) {
			throw new RecordNotFoundException("Invalid user name : " + changePasswordTo.getName());
		} else if (bCryptPasswordEncoder.matches(changePasswordTo.getPassword(), existingUser.getPassword())) {
			existingUser.setLastUpdatedDate(CURRENT_DATE);
			existingUser.setPassword(bCryptPasswordEncoder.encode(changePasswordTo.getNewPassword()));
			userRepository.save(existingUser);
			return PASSWORD_CHANGE_SUCCESS;
		}
		return OLD_PASSWORD_WRONG;
	}

	private void populateUserJpaFromCreateUserTo(CreateUserTo createUserTo, UserJpa userJpa) {
		userJpa.setName(createUserTo.getName());
		userJpa.setPassword(bCryptPasswordEncoder.encode(createUserTo.getPassword()));
		userJpa.setLastUpdatedDate(CURRENT_DATE);
		userJpa.setCreatedAtDate(CURRENT_DATE);
	}

	private boolean isPasswordAndConfmPasswordSame(String password, String confirmPassword) {
		return password.equals(confirmPassword);
	}

	public UserJpa findByUsername(String userName) {
		return userRepository.findByUsername(userName);
	}

}
