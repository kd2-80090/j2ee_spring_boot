package com.app.main.daos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.main.entity.Movie;
import com.app.main.entity.Review;

public interface ReviewDao extends JpaRepository<Review, Long> {

	List<Review> findAllByMovie(Movie movie);

}
