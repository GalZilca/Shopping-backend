package collab.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import collab.logic.UnvalidException;
import collab.logic.UsersService;
import collab.rest.boundaries.NewUserForm;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;

@RestController
public class UserController {
	
	
	private UsersService usersService;

	
	
	@Autowired
	public UserController(UsersService usersService) {
		this.usersService = usersService;
	}

	
	
						// URL Example for GET method -http://localhost:8080/collab/users/login/2020a.alik/new@us.er
	@RequestMapping(
			path = "/collab/users/login/{domain}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary getLoginUser(
			@PathVariable("domain") String domain,
			@PathVariable("userEmail") String userEmail) {
		
		return this.usersService.login(domain, userEmail);
	}

	
	
						//	URL for POST method - http://localhost:8080/collab/users	
	@RequestMapping(
			path = "/collab/users", 
			method = RequestMethod.POST, 
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createUser(@RequestBody NewUserForm newUser) {

		UserBoundary userBoundary = new UserBoundary(new UserId("", newUser.getEmail()), newUser.getRole(),
				newUser.getUsername(), newUser.getAvatar());
		return this.usersService.create(userBoundary);
	}

	
	
								// URL Example for PUT method - http://localhost:8080/collab/users/2020a.alik/new@us.er
	@RequestMapping(
			path = "/collab/users/{domain}/{userEmail}", 
			method = RequestMethod.PUT, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUser(
			@PathVariable("domain") String domain,
			@PathVariable("userEmail") String userEmail,
			@RequestBody UserBoundary updateUser) {
		
		updateUser.setUserId(new UserId(domain, userEmail));
		this.usersService.update(updateUser);
	}

	
	
	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public Map<String, String> handleUserNotFoundException(NotFoundException e) {
		
		String message = e.getMessage();
		if (message == null) {
			message = "user could not be found";
		}

		Map<String, String> errorMessage = new HashMap<>();
		errorMessage.put("error", message);

		return errorMessage;
	}
	
	
	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public Map<String, String> handleUserUnvalidException(UnvalidException e) {
		
		String message = e.getMessage();
		if (message == null) {
			message = "Unvalid user";
		}

		Map<String, String> errorMessage = new HashMap<>();
		errorMessage.put("error", message);

		return errorMessage;
	}
	
	
	
}