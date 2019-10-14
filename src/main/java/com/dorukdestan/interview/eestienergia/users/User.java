package com.dorukdestan.interview.eestienergia.users;

import com.dorukdestan.interview.eestienergia.datevalidation.Before;
import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class User implements Comparable<User> {

	private String id;

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@Email
	private String email;

	@Before
	private LocalDate birthDate;

	@NotBlank
	private String address;

	@JsonGetter
	String getFullName() {
		return firstName + " " + lastName;
	}

	@Override
	public int compareTo(User user) {

		if (user == null) {
			return -1;
		}

		return this.getFullName().compareTo(user.getFullName());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User) o;
		return getFullName().equals(user.getFullName());
	}

	@Override
	public int hashCode() {
		return getFullName().hashCode();
	}

}
