package com.app.main.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.main.customexception.ResourceNotFoundException;
import com.app.main.daos.MovieDao;
import com.app.main.dtos.MovieDto;
import com.app.main.entity.Movie;
import com.app.main.helper.ExcelHelper;
import com.app.main.helper.MovieHelper;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class MovieServiceImpl implements MovieService {
	
	public static final Logger log = LogManager.getLogger(MovieServiceImpl.class);

	@Autowired
	private MovieDao movieDao;

	@Autowired
	private ExcelHelper  helper;

	@Override
	public List<MovieDto> findAllMovies(int pageNumber, int pageSize) {

		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		
		List<Movie> moviesList = movieDao.findAll(pageable).getContent();
		log.info("List of movies : " + moviesList);

//		List<MovieDto> movies = new ArrayList<MovieDto>();

//		for (Movie movie : moviesList) {
//			
//			MovieDto movieDto = new MovieDto();
//			
//			movieDto.setId(movie.getId());
//			movieDto.setMovieName(movie.getMovieName());
//			movieDto.setReleaseDate(movie.getReleaseDate());
//			
//			movies.add(movieDto);
//		}
		List<MovieDto> movies = moviesList.stream().map(movie -> {
			MovieDto movieDto = new MovieDto();
			movieDto.setId(movie.getId());
			movieDto.setMovieName(movie.getMovieName());
			movieDto.setReleaseDate(movie.getReleaseDate());
			return movieDto;
		}).collect(Collectors.toList());

		return movies;
	}

	@Override
	public MovieDto addNewMovie(MovieDto newMovie) {

		Optional<Movie> existingMovie = movieDao.findByMovieName(newMovie.getMovieName());

		if (existingMovie.isPresent()) {
			throw new ResourceNotFoundException("Movie with the name '" + newMovie.getMovieName() + "' already exists.");
		}

		Movie movie = new Movie();

		movie.setMovieName(newMovie.getMovieName());
		movie.setReleaseDate(newMovie.getReleaseDate());
		movie.setStatus("active");

		Movie persistentMovie = movieDao.save(movie);
		log.info("Added movie = " + persistentMovie);
		
		MovieDto movieDto = new MovieDto();

		movieDto.setId(persistentMovie.getId());
		movieDto.setMovieName(persistentMovie.getMovieName());
		movieDto.setReleaseDate(persistentMovie.getReleaseDate());

		log.info("Dto movie final details after getting added:" + movieDto);
		return movieDto;
	}

	@Override
	public MovieDto updateMovieDetails(MovieDto updatedMovie, Long id) {

		Movie movieDetails = movieDao.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

		if (updatedMovie.getMovieName() != null) {
	        movieDetails.setMovieName(updatedMovie.getMovieName());
	    }
	    if (updatedMovie.getReleaseDate() != null) {
	        movieDetails.setReleaseDate(updatedMovie.getReleaseDate());
	    }

		Movie persistentMovie = movieDao.save(movieDetails);

		MovieDto movieDto = new MovieDto();

		movieDto.setId(persistentMovie.getId());
		movieDto.setMovieName(persistentMovie.getMovieName());
		movieDto.setReleaseDate(persistentMovie.getReleaseDate());

		log.info("Dto movie final after updating the movie details :" + movieDto);
		return movieDto;
	}

	@Override
	public String deleteMovieDetails(Long id) {

		Movie movie = movieDao.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movie Not Found"));

	    if ("inactive".equals(movie.getStatus())) {
	        return "Movie is already inactive";
	    }

	    movie.setStatus("inactive");
	    movieDao.save(movie);

	    log.info("Movie with id " + id + " has been marked as inactive.");
	    return "Success: Movie Deleted Successfully";
	}

	@Override
	public MovieDto getMovieDetails(Long id) {

		Movie movie = movieDao.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movie Not Found"));

		MovieDto movieDto = new MovieDto();

		movieDto.setId(movie.getId());
		movieDto.setMovieName(movie.getMovieName());
		movieDto.setReleaseDate(movie.getReleaseDate());

		log.info("Dto movie final details after fetching successfully from the body:" + movieDto);
		return movieDto;
	}

	@Override
	public ByteArrayInputStream getActualData() throws IOException {

		List<Movie> allMovies = movieDao.findAll();

		ByteArrayInputStream byteArrayInputStream = MovieHelper.dataToExcel(allMovies);

		return byteArrayInputStream;
	}

	@Override
	public ByteArrayInputStream generatePdf() {
	    String title = "Movie Ticket";
	    Long id = (long) 6;
	    Movie movie = movieDao.findById(id).orElseThrow();

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Document document = new Document();

	    try {
	        PdfWriter.getInstance(document, out);
	        document.open();

	        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 25);
	        Paragraph titlePara = new Paragraph(title, titleFont);
	        titlePara.setAlignment(Element.ALIGN_CENTER);
	        document.add(titlePara);
	        
	        Paragraph movieParagraph = new Paragraph(
	            "______________________________________________________________________________\n" + "\n"
	        );
	        document.add(movieParagraph);

	        PdfPTable table = new PdfPTable(3);
	        table.setWidthPercentage(100);
	        table.setSpacingBefore(20f);

	        String posterPath = "src/main/resources/singham.jpeg";
	        Image posterImage = Image.getInstance(posterPath);
	        posterImage.scaleToFit(140, 175);

	        PdfPCell posterCell = new PdfPCell(posterImage);
	        posterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        posterCell.setBorder(PdfPCell.RIGHT);
	        table.addCell(posterCell);

	        String qrCodePath = "src/main/resources/qrcode.png";
	        Image qrCodeImage = Image.getInstance(qrCodePath);
	        qrCodeImage.scaleToFit(75, 75);

	        PdfPCell qrCell = new PdfPCell(qrCodeImage);
	        qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        qrCell.setBorder(PdfPCell.NO_BORDER);
	        table.addCell(qrCell);

	        Font movieFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

	        String movieDetails = "********************************" + "\n" +
	                              "BOOKING ID: WXCYT4AC" + "\n" +
	                              "Movie Title: " + movie.getMovieName() + "\n" +
	                              "Date: " + LocalDate.now() + "\n" +
	                              "Time: 09:30 PM" + "\n" +
	                              "Screen: Screen1" + "\n" +
	                              "Seats: A5-A10" + "\n" +
	                              "********************************";

	        PdfPCell detailsCell = new PdfPCell(new Paragraph(movieDetails, movieFont));
	        detailsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        detailsCell.setBorder(PdfPCell.NO_BORDER);
	        table.addCell(detailsCell);

	        document.add(table);

	        PdfPTable footerTable = new PdfPTable(1);
	        footerTable.setWidthPercentage(80);
	        

	        PdfPCell confirmationCell = new PdfPCell(new Paragraph("\nA confirmation is sent on e-mail/SMS/WhatsApp within 15 mins of Booking."));
	        confirmationCell.setBorder(PdfPCell.NO_BORDER); 
	        confirmationCell.setHorizontalAlignment(Element.ALIGN_CENTER); 
	        footerTable.addCell(confirmationCell);
	        
	        confirmationCell.setPaddingRight(20);

	        document.add(footerTable);

	        Paragraph movieParagraph1 = new Paragraph(
	            "______________________________________________________________________________" + "\n"
	        );
	        document.add(movieParagraph1);

	    } catch (DocumentException | IOException e) {
	        e.printStackTrace();
	    } finally {
	        document.close();
	    }

	    return new ByteArrayInputStream(out.toByteArray());
	}

	@Override
	public void save(MultipartFile file) {

		try {
			List<Movie> movies = helper.convertExcelToListOfMovie(file.getInputStream());
			
			this.movieDao.saveAll(movies);
			
		} catch (IOException e) {

			e.printStackTrace();
		}
		
	}

	
	

}
