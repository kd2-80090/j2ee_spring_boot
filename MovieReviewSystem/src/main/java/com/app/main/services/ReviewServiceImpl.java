package com.app.main.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.app.main.controllers.MovieController;
import com.app.main.customexception.ResourceNotFoundException;
import com.app.main.daos.MovieDao;
import com.app.main.daos.ReviewDao;
import com.app.main.daos.UserDao;
import com.app.main.dtos.ReviewDto;
import com.app.main.entity.Movie;
import com.app.main.entity.Review;
import com.app.main.entity.User;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import java.awt.Color;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ReviewServiceImpl implements ReviewService {

	private static final Logger log = LogManager.getLogger(MovieController.class);

	@Autowired
	private ReviewDao reviewDao;

	@Autowired
	private MovieDao movieDao;

	@Autowired
	private UserDao userDao;

	@Override
	public ReviewDto addReviewByUser(Review review, Long userId, Long movieId) {

		review.setReviewDate(LocalDateTime.now());
		Movie movie = movieDao.findById(movieId)
				.orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + movieId));

		review.setMovie(movie);
		log.info("Movie Details : " + movie);

		User user = userDao.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
		review.setUser(user);

		log.info("UserDetails : " + user);

		review.setStatus("active");
		Review addedReview = reviewDao.save(review);

		ReviewDto reviewDetails = new ReviewDto();

		reviewDetails.setId(addedReview.getId());
		reviewDetails.setContent(addedReview.getContent());
		reviewDetails.setReviewDate(addedReview.getReviewDate());

		reviewDetails.setUserName(user.getName());
		reviewDetails.setMovieName(movie.getMovieName());

		reviewDetails.setUserId(user.getId());
		reviewDetails.setMovieId(movie.getId());

		log.info("Review added: " + reviewDetails);

		return reviewDetails;
	}

	@Override
	public List<ReviewDto> getAllMovieReviewsById(Long movieId) {

		Movie movie = movieDao.findById(movieId).orElseThrow(()-> new ResourceNotFoundException("Movie Not Found"));

		List<Review> listOfReviews = reviewDao.findAllByMovie(movie);

		List<ReviewDto> totalMovieReviews = new ArrayList<ReviewDto>();

		for (Review addedReview : listOfReviews) {

			ReviewDto reviewDetails = new ReviewDto();

			reviewDetails.setId(addedReview.getId());
			reviewDetails.setContent(addedReview.getContent());
			reviewDetails.setReviewDate(addedReview.getReviewDate());

			reviewDetails.setUserName(addedReview.getUser().getName());
			reviewDetails.setMovieName(addedReview.getMovie().getMovieName());

			reviewDetails.setUserId(addedReview.getUser().getId());
			reviewDetails.setMovieId(addedReview.getMovie().getId());

			totalMovieReviews.add(reviewDetails);
		}

		return totalMovieReviews;
	}

	@Override
	public List<ReviewDto> findAllReviews(int pageNumber, int pageSize) {

		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		List<Review> listOfReviews = reviewDao.findAll(pageable).getContent();

		List<ReviewDto> totalReviews = new ArrayList<ReviewDto>();

		for (Review addedReview : listOfReviews) {

			ReviewDto reviewDetails = new ReviewDto();

			reviewDetails.setId(addedReview.getId());
			reviewDetails.setContent(addedReview.getContent());
			reviewDetails.setReviewDate(addedReview.getReviewDate());

			reviewDetails.setUserName(addedReview.getUser().getName());
			reviewDetails.setMovieName(addedReview.getMovie().getMovieName());

			reviewDetails.setUserId(addedReview.getUser().getId());
			reviewDetails.setMovieId(addedReview.getMovie().getId());

			totalReviews.add(reviewDetails);
		}

		return totalReviews;
	}

	public ByteArrayInputStream generatePdf(Long movieId) {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Document document = new Document(PageSize.A4, 36, 36, 36, 36);

	    try {
	        PdfWriter.getInstance(document, out);
	        document.open();

	        PdfPTable table = new PdfPTable(2);
	        table.setWidthPercentage(100); 

	        Image img = Image.getInstance("src/main/resources/imdb.jpg");
	        img.scaleToFit(100, 100); 
	        PdfPCell logoCell = new PdfPCell(img);
	        logoCell.setBorder(PdfPCell.NO_BORDER); 
	        logoCell.setHorizontalAlignment(Element.ALIGN_LEFT); 
	        table.addCell(logoCell);

	        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLUE); // Set to Blue color
	        Paragraph title = new Paragraph("Movie Review Report", titleFont);
	        title.setAlignment(Paragraph.ALIGN_CENTER);
	        PdfPCell titleCell = new PdfPCell(title);
	        titleCell.setBorder(PdfPCell.NO_BORDER); 
	        titleCell.setHorizontalAlignment(Element.ALIGN_MIDDLE); 
	        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE); 
	        table.addCell(titleCell);

	        document.add(table); 

	        LineSeparator separator = new LineSeparator();
	        document.add(new Chunk(separator));
	        document.add(new Paragraph("\n"));

	        Movie movie = movieDao.findById(movieId)
	                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + movieId));
	        List<Review> reviews = reviewDao.findAllByMovie(movie);

	        Font movieFont = new Font(Font.HELVETICA, 14, Font.BOLDITALIC, Color.RED); // Set to Green color
	        Font reviewFont = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL, Color.BLACK); // Set to Black color

	        Paragraph movieTitle = new Paragraph("Movie: " + movie.getMovieName(), movieFont);
	        Paragraph releaseDate = new Paragraph("Release Date: " + movie.getReleaseDate(), reviewFont);

	        document.add(movieTitle);
	        document.add(releaseDate);
	        document.add(new Paragraph("\n"));

	        int i = 1;
	        for (Review review : reviews) {
	            Paragraph reviewContent = new Paragraph("[" + i + "] : " + review.getContent(), reviewFont);
	            i++;
	            document.add(reviewContent);
	            document.add(new Paragraph("\n"));
	        }

	        document.close();
	    } catch (Exception e) {
	        System.err.println("Error generating PDF: " + e.getMessage());
	    } finally {
	        try {
	            out.close(); 
	        } catch (IOException e) {
	            System.err.println("Error closing output stream: " + e.getMessage());
	        }
	    }

	    return new ByteArrayInputStream(out.toByteArray());
	}

}
