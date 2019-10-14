package com.dorukdestan.interview.eestienergia.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
class UsersController {

	private final UserValidator userValidator;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.addValidators(userValidator);
	}

	private final UsersService usersService;

	@GetMapping
	ResponseEntity<List<User>> list(@RequestParam("search") Optional<String> searchParams) {

		final List<User> users;
		if (searchParams.isPresent()) {
			users = usersService.findByNameLike(searchParams.get());
		}
		else {
			users = usersService.findAll();
		}

		return ResponseEntity.ok(users);
	}

	@PostMapping
	ResponseEntity<User> create(@RequestBody @Validated User user) {
		final User persistedUser = usersService.create(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(persistedUser);
	}

	@PutMapping("/{id}")
	ResponseEntity<User> update(@PathVariable String id, @RequestBody @Validated User user) {

		if (!id.equals(user.getId())) {
			throw new IllegalArgumentException("Attempt to update different user than the one specified in path variable!");
			// We could just make the endpoint like "PUT /users" or using current endpoint we could make user.setId(id) instead of throwing exception
			// It's a matter of style and sometimes matter of business expectation.
		}

		final User persistedUser = usersService.update(id, user);
		return ResponseEntity.ok(persistedUser);
	}

	@DeleteMapping("/{id}")
	ResponseEntity<User> delete(@PathVariable String id) {
		usersService.delete(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
