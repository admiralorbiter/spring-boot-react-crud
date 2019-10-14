package com.dorukdestan.interview.eestienergia.users;

import com.dorukdestan.interview.eestienergia.ResourceNotFoundException;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.internal.util.reflection.FieldSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsersServiceTest {

	@Autowired
	private UsersService usersService;

	@BeforeEach
	void resetInternalState() {
		setInternalState(new HashMap<>());
	}

	@Test
	@DisplayName("User object is assigned an id when created")
	void create() {
		final User user = new User();
		usersService.create(user);

		assertNotNull(user.getId());
	}

	@Test
	@DisplayName("Create throws on null parameter")
	void createWithNull() {
		Assertions.assertThrows(NullPointerException.class, () -> {
			usersService.create(null);
		});

	}

	@Test
	void findAll() {
		assertEquals(0, usersService.findAll().size());

		final Map<String, User> testState = new HashMap<>();
		testState.put("a", TestUtils.createSampleUser());
		testState.put("b", new User(null, "John", "Doe", "john@example.com", LocalDate.of(2000, 1, 30), "Ataşehir İstanbul"));
		setInternalState(testState);

		assertEquals(2, usersService.findAll().size());
	}

	@Test
	@DisplayName("Update throws ResourceNotFoundException on non-existing user")
	void updateThrowsResourceNotFoundException() {
		final User user = TestUtils.createSampleUser();
		user.setId("a");
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			usersService.update("a", user);
		});
	}

	@Test
	@DisplayName("Update changes the existing user")
	void update() {
		final String id = "a";
		final LocalDate now = LocalDate.now();
		final Map<String, User> testState = new HashMap<>();
		testState.put(id, TestUtils.createSampleUser(id));
		setInternalState(testState);

		final User toBeUpdatedUser = TestUtils.createSampleUser(id);
		toBeUpdatedUser.setFirstName("John");
		toBeUpdatedUser.setLastName("Doe");
		toBeUpdatedUser.setAddress("istanbul");
		toBeUpdatedUser.setBirthDate(now);

		usersService.update(id, toBeUpdatedUser);

		final Map<String, User> testState2 = (Map<String, User>) ReflectionUtils.readFieldValue(UsersService.class, "userMap", usersService).orElseThrow(IllegalStateException::new);
		final User updatedUser = testState2.get(id);

		assertNotNull(updatedUser);
		assertEquals("John", updatedUser.getFirstName());
		assertEquals("Doe", updatedUser.getLastName());
		assertEquals("istanbul", updatedUser.getAddress());
		assertEquals(now, updatedUser.getBirthDate());
	}

	@Test
	@DisplayName("Delete throws ResourceNotFoundException on non-existing user")
	void deleteThrowsResourceNotFoundException() {
		final String id = "a";

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			usersService.update(id, TestUtils.createSampleUser(id));
		});
	}

	@Test
	@DisplayName("Delete removes the user object from the map")
	void delete() {
		final String id = "a";
		final Map<String, User> testState = new HashMap<>();
		testState.put(id, TestUtils.createSampleUser(id));
		setInternalState(testState);

		final Map<String, User> testStateBeforeDeletion = (Map<String, User>) ReflectionUtils.readFieldValue(UsersService.class, "userMap", usersService).orElseThrow(IllegalStateException::new);
		assertEquals(1, testStateBeforeDeletion.size());

		usersService.delete(id);
		final Map<String, User> testStateAfterDeletion = (Map<String, User>) ReflectionUtils.readFieldValue(UsersService.class, "userMap", usersService).orElseThrow(IllegalStateException::new);

		assertEquals(0, testStateAfterDeletion.size());
	}

	@Test
	@DisplayName("Exists returns false when cannot find")
	void existsReturnsFalse() {
		assertFalse(usersService.exists(TestUtils.createSampleUser()));
	}

	@Test
	@DisplayName("Exists returns true when finds")
	void existsReturnsTrue() {
		final String id = "a";
		final User existingUser = TestUtils.createSampleUser(id);
		final Map<String, User> testState = new HashMap<>();
		testState.put(id, existingUser);
		setInternalState(testState);

		final User user = new User();
		user.setFirstName(existingUser.getFirstName());
		user.setLastName(existingUser.getLastName());
		assertTrue(usersService.exists(user));
	}

	private void setInternalState(@NonNull Map<String, User> map) {
		try {
			FieldSetter.setField(usersService, UsersService.class.getDeclaredField("userMap"), map);
		}
		catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

}