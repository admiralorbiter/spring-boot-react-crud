package com.dorukdestan.interview.eestienergia.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
class UserValidator implements Validator {

	private final UsersService usersService;

	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		final User user = (User) target;

		if (usersService.exists(user)) {
			errors.rejectValue("firstName", "full name must be unique", "full name must be unique");
			errors.rejectValue("lastName", "full name must be unique", "full name must be unique");
		}
	}
}
