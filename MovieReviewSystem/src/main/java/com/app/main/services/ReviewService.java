package com.app.main.services;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.app.main.dtos.ReviewDto;
import com.app.main.entity.Review;

public interface ReviewService {

	ReviewDto addReviewByUser(Review review,Long userId, Long movieId);

	List<ReviewDto> getAllMovieReviewsById(Long movieId);

	List<ReviewDto> findAllReviews(int pageNumber, int pageSize);

	ByteArrayInputStream generatePdf(Long movieId);

}
