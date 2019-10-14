package com.dorukdestan.interview.eestienergia.users;

import java.time.LocalDate;

class TestUtils {

	private TestUtils() {
	}

	static User createSampleUser() {
		return createSampleUser(null);
	}

	static User createSampleUser(String id) {
		return new User(id, "Destan", "Sarpkaya", "destan@kodgemisi.com", LocalDate.of(1988, 4, 7), "Karşıyaka İzmir");
	}
}
