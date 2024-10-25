package com.app.main.daos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.main.entity.Movie;

public interface MovieDao extends JpaRepository<Movie, Long> {

	@Query("SELECT m FROM Movie m WHERE YEAR(m.releaseDate) = :year")
    List<Movie> findAllMoviesByYear(@Param("year") int year);

    Optional<Movie> findByMovieName(String movieName);
}
