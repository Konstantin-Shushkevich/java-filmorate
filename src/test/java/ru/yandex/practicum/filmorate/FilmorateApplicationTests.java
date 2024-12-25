package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.extractor.FilmExtractor;
import ru.yandex.practicum.filmorate.extractor.UserExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.repository.user.JdbcUserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({JdbcFilmRepository.class, JdbcUserRepository.class, UserExtractor.class, FilmExtractor.class})
class FilmorateApplicationTests {

	private final JdbcFilmRepository jdbcFilmRepository;
	private final JdbcUserRepository jdbcUserRepository;
	private final JdbcTemplate jdbcTemplate;

	private final User user1 = new User();
	private final User user2 = new User();
	private final Film film1 = new Film();
	private final Film film2 = new Film();

	RatingMpa mpa1 = new RatingMpa();
	RatingMpa mpa2 = new RatingMpa();
	Genre genre1 = new Genre();
	Genre genre2 = new Genre();
	Genre genre3 = new Genre();


	List<User> usersTest = List.of(user1, user2);
	List<Film> filmsTest = List.of(film1, film2);

	@BeforeEach
	public void setData() {
		user1.setEmail("user1@test.ru");
		user1.setLogin("user1");
		user1.setName("user1");
		user1.setBirthday(LocalDate.of(2000, 1, 1));

		user2.setEmail("user2@test.ru");
		user2.setLogin("user2");
		user2.setName("user2");
		user2.setBirthday(LocalDate.of(1990, 1, 1));

		mpa1.setId(1);
		mpa1.setName("G");

		mpa2.setId(5);
		mpa2.setName("C-17");

		genre1.setId(1);
		genre1.setName("Комедия");

		genre2.setId(2);
		genre2.setName("Драма");

		genre3.setId(6);
		genre3.setName("Боевик");

		film1.setName("film1");
		film1.setDescription("testing test1");
		film1.setReleaseDate(LocalDate.of(1995, 6, 1));
		film1.setDuration(100);
		film1.setGenres(Set.of(genre1, genre2));
		film1.setMpa(mpa1);

		film2.setName("film2");
		film2.setDescription("testing test2");
		film2.setReleaseDate(LocalDate.of(2005, 6, 1));
		film2.setDuration(100);
		film2.setGenres(Set.of(genre3));
		film2.setMpa(mpa2);

		jdbcUserRepository.saveUser(user1);
		jdbcUserRepository.saveUser(user2);
		jdbcFilmRepository.saveFilm(film1);
		jdbcFilmRepository.saveFilm(film2);
	}

	@AfterEach
	public void refreshTableIds() {
		jdbcTemplate.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
		jdbcTemplate.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
	}

	@Test
	public void testFindUserById() {
		Optional<User> userOptional = jdbcUserRepository.findById(1);

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
				);
	}

	@Test
	public void testGetAllUsersAndFindByIds() {
		List<User> users = jdbcUserRepository.getAll().stream().toList();
		List<User> usersGotByIds = jdbcUserRepository.findByIds(List.of(1, 2));

		assertThat(users).containsExactlyInAnyOrderElementsOf(usersTest);
		assertThat(usersGotByIds).containsExactlyInAnyOrderElementsOf(usersTest);
	}

	@Test
	public void testUpdateUser() {
		User userUpdated = new User();
		userUpdated.setId(1);
		userUpdated.setEmail("user1upd@test.ru");
		userUpdated.setLogin("user1Upd");
		userUpdated.setName("user1Upd");
		userUpdated.setBirthday(LocalDate.of(1999, 1, 1));

		jdbcUserRepository.updateUser(userUpdated);
		User userUpdatedFromDb = jdbcUserRepository.findById(1).get();
		assertThat(userUpdatedFromDb).isEqualTo(userUpdated);
	}

	@Test
	public void testDeleteUser() {
		jdbcUserRepository.deleteUser(1);
		jdbcUserRepository.deleteUser(2);
		assertTrue(jdbcUserRepository.getAll().isEmpty());
	}

	@Test
	public void addAndDeleteFriend() {
		jdbcUserRepository.addFriend(1, 2, false);

		Optional<User> userExtractedOptional1 = jdbcUserRepository.findById(1);
		Optional<User> userExtractedOptional2 = jdbcUserRepository.findById(2);

		assertThat(userExtractedOptional1)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("friends", Set.of(2))
				);
		assertThat(userExtractedOptional2)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("friends", Set.of())
				);

		jdbcUserRepository.addFriend(2, 1, true);

		userExtractedOptional1 = jdbcUserRepository.findById(1);
		userExtractedOptional2 = jdbcUserRepository.findById(2);

		assertThat(userExtractedOptional1)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("friends", Set.of(2))
				);

		assertThat(userExtractedOptional2)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("friends", Set.of(1))
				);

		jdbcUserRepository.deleteFriend(1, 2, true);

		userExtractedOptional1 = jdbcUserRepository.findById(1);
		userExtractedOptional2 = jdbcUserRepository.findById(2);

		assertThat(userExtractedOptional1)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("friends", Set.of(2))
				);

		assertThat(userExtractedOptional2)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("friends", Set.of())
				);

		jdbcUserRepository.deleteFriend(1, 2, false);

		userExtractedOptional1 = jdbcUserRepository.findById(1);
		userExtractedOptional2 = jdbcUserRepository.findById(2);

		assertThat(userExtractedOptional1)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("friends", Set.of())
				);

		assertThat(userExtractedOptional2)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("friends", Set.of())
				);
	}

	@Test
	public void testFindFilmById() {
		Optional<Film> filmOptional = jdbcFilmRepository.findById(1);

		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film ->
						assertThat(film).hasFieldOrPropertyWithValue("id", 1)
				);
	}

	@Test
	public void testGetAllFilms() {
		List<Film> films = jdbcFilmRepository.getAll().stream().toList();
		System.out.println(films);
		System.out.println("!!!!");
		System.out.println(filmsTest);

		assertThat(films).containsExactlyInAnyOrderElementsOf(filmsTest);
	}

	@Test
	public void testUpdateFilm() {
		Film filmUpdated = new Film();
		filmUpdated.setId(1);
		filmUpdated.setName("filmUpd");
		filmUpdated.setDescription("testing test upd");
		filmUpdated.setReleaseDate(LocalDate.of(1985, 6, 1));
		filmUpdated.setDuration(120);
		filmUpdated.setGenres(Set.of(genre3));
		filmUpdated.setMpa(mpa2);

		jdbcFilmRepository.updateFilm(filmUpdated);
		Film filmUpdatedFromRep = jdbcFilmRepository.findById(1).get();
		assertThat(filmUpdatedFromRep).isEqualTo(filmUpdated);
	}

	@Test
	public void testDeleteFilm() {
		jdbcFilmRepository.deleteFilm(1);
		jdbcFilmRepository.deleteFilm(2);
		assertTrue(jdbcFilmRepository.getAll().isEmpty());
	}

	@Test
	public void testLikEndDisLikeFilms() {
		jdbcFilmRepository.likeFilm(1, 2);
		Film filmExecuted = jdbcFilmRepository.findById(1).get();
		Set<Integer> likes = filmExecuted.getLikes();
		assertEquals(likes, Set.of(2));

		jdbcFilmRepository.disLikeFilm(1, 2);
		filmExecuted = jdbcFilmRepository.findById(1).get();
		likes = filmExecuted.getLikes();
		assertTrue(likes.isEmpty());
	}
}
