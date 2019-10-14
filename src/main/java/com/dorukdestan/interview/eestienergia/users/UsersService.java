package com.dorukdestan.interview.eestienergia.users;

import com.dorukdestan.interview.eestienergia.ResourceNotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
class UsersService {

	private final Map<String, User> userMap = Collections.synchronizedMap(new HashMap<>());

	@Bean
	@Profile("initial-data")
	CommandLineRunner initialDataGenerator() {
		return args -> {
			this.create(new User(null, "Destan", "Sarpkaya", "destan@kodgemisi.com", LocalDate.of(1988, 4, 7), "Karşıyaka İzmir"));
			this.create(new User(null, "John", "Doe", "john@example.com", LocalDate.of(2000, 1, 30), "Ataşehir İstanbul"));
		};
	}

	User create(@NonNull User user) {

		Assert.isNull(user.getId(), "Existing users cannot be created! [id is not null]");

		final String id = UUID.randomUUID().toString();
		user.setId(id);
		this.userMap.put(id, user);
		return user;
	}

	List<User> findAll() {
		final List<User> users = new ArrayList<>(this.userMap.values());
		Collections.sort(users);
		return users;
	}

	/**
	 * TODO This method currently returns all the users however when a database is used we can return the result of a {@code like} query.
	 *
	 * @param fullName
	 * @return List of matched users or an empty list if none matched.
	 */
	List<User> findByNameLike(String fullName) {
		return this.findAll();
	}

	User update(@NonNull String id, @NonNull User user) {

		// Ensure such a user exists because `userMap.put(id, user)` would swallow such a mistake
		if (!this.userMap.containsKey(id)) {
			log.warn("Attempt to update non-existing user {}", user);
			throw new ResourceNotFoundException();
		}

		user.setId(id); // this is ridiculous when using a map but if we had used a db we wouldn't had to set the id, it will be an update query.
		this.userMap.put(id, user);
		return user;
	}

	void delete(@NonNull String id) {
		if (this.userMap.remove(id) == null) {
			log.warn("Attempt to delete non-existing user with id {}", id);
			throw new ResourceNotFoundException();
		}
	}

	boolean exists(User user) {
		return userMap
				.values()
				.stream()
				.anyMatch(persistedUser -> persistedUser.getFullName().equals(user.getFullName()) && !persistedUser.getId().equals(user.getId()));
	}
}
