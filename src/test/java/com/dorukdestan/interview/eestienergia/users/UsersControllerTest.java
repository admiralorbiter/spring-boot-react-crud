package com.dorukdestan.interview.eestienergia.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersControllerTest {

	@LocalServerPort
	private int port;

	private String url;

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private UsersService usersService;// Although this field is unused in some tests it really mocks the service for UsersController

	@BeforeEach
	void initialize() {
		url = "http://localhost:" + port + "/users";
	}

	@Test
	void create() {
		ResponseEntity<User> response = restTemplate.postForEntity(url, TestUtils.createSampleUser(), User.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}

	@Test
	void createWithEmptyFields() {
		ResponseEntity<User> response = restTemplate.postForEntity(url, new User(), User.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void cannotCreateWithSameFullName() {
		Mockito.when(usersService.exists(ArgumentMatchers.any())).thenReturn(true);

		ResponseEntity<User> response = restTemplate.postForEntity(url, TestUtils.createSampleUser(), User.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

}