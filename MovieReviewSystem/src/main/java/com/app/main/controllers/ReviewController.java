package com.app.main.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.main.customexception.ResourceNotFoundException;
import com.app.main.dtos.ReviewDto;
import com.app.main.entity.Review;
import com.app.main.services.ReviewService;

@RestController
@RequestMapping("/review")
public class ReviewController {
	
	private static final Logger log = LogManager.getLogger(ReviewController.class);
		
	@Autowired
	private ReviewService reviewService;

	/**
	 * @methodInfo : Get all reviews
	 * @URL : http://localhost:8080/review/getAllReviews
	 * @method : GET Method
	 * @param pageNumber
	 * @param pageSize
	 * @return All reviews records
	 */
	@GetMapping("/getAllReviews")
	public ResponseEntity<?> getAllReviews(@RequestParam(defaultValue = "0",required = false) int pageNumber,
			@RequestParam(defaultValue = "5", required = false) int pageSize) {
		log.info("in get all reviews details " + pageNumber + " " + pageSize);
		
		List<ReviewDto> reviews = reviewService.findAllReviews(pageNumber,pageSize);
				
		if(reviews.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return new ResponseEntity<>(reviews,HttpStatus.OK);
	}
	
	/**
	 * @methodInfo : add review by user for a particular movie
	 * @URL : http://localhost:8080/review/addReview/userId/movieId
	 * @method : POST Method
	 * @return Created review details
	 */
	@PostMapping("/addReview/{userId}/{movieId}")
	public ResponseEntity<?> addReview(@PathVariable Long userId, @PathVariable Long movieId, @RequestBody Review review ) {
		
		ReviewDto reviewDetails = reviewService.addReviewByUser(review, userId, movieId);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(reviewDetails);
	}
	
	/**
	 * @methodInfo : get all movie review by movie id
	 * @URL : http://localhost:8080/review/getMovieReviews/movieId
	 * @method : GET Method
	 * @return All reviews details for particular movie
	 */
	@GetMapping("/getMovieReviews/{movieId}")
	public ResponseEntity<?> getMovieReviews(@PathVariable Long movieId) {
		
		try {
			List<ReviewDto> reviews = reviewService.getAllMovieReviewsById(movieId);
			
			if (reviews.isEmpty()) {
	            return new ResponseEntity<>("No reviews found for this movie", HttpStatus.OK);
	        }
			return ResponseEntity.status(HttpStatus.OK).body(reviews);
		}
		catch (ResourceNotFoundException e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	    }
	}
	
	
	/**
	 * @methodInfo : Get review data into pdf format
	 * @URL : http://localhost:8080/review/dataToPdf/movieId
	 * @method : GET Method
	 * @return : Review Data in Pdf
	 * @throws IOException 
	 */
	@GetMapping("/dataToPdf/{movieId}")
	public ResponseEntity<InputStreamResource> createPdf(@PathVariable Long movieId) {

	    log.info("Generating PDF for Movie ID: {}", movieId);
	    
	    ByteArrayInputStream pdf;
	    try {
	        pdf = reviewService.generatePdf(movieId);
	    } catch (Exception e) {
	        log.error("Error generating PDF for movieId: {}", movieId, e);
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(null);  // You can return an error message here if desired
	    }
	    
	    HttpHeaders httpHeaders = new HttpHeaders();
	    httpHeaders.add("Content-Disposition", "inline; filename=reviews.pdf");

	    return ResponseEntity
	            .ok()
	            .headers(httpHeaders)
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(new InputStreamResource(pdf));
	}

	
}
