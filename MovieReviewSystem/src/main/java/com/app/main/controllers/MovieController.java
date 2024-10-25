package com.app.main.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.main.customexception.ResourceNotFoundException;
import com.app.main.dtos.MovieDto;
import com.app.main.helper.ExcelHelper;
import com.app.main.services.MovieService;

@RestController
@RequestMapping("/movie")
public class MovieController {
	
	private static final Logger log = LogManager.getLogger(MovieController.class);
	
	@Autowired
	private MovieService movieService;
	
	/**
	 * @methodInfo : Get all movies
	 * @URL : http://localhost:8080/movie/getAllMovies
	 * @method : GET Method
	 * @param pageNumber
	 * @param pageSize
	 * @return All movie records
	 */
	@GetMapping("/getAllMovies")
	public ResponseEntity<List<MovieDto>> getAllMovies(@RequestParam(defaultValue = "0",required = false) int pageNumber,
			@RequestParam(defaultValue = "5", required = false) int pageSize) {
				
		log.info("in get all movies details " + pageNumber + " " + pageSize);
		
		List<MovieDto> movies = movieService.findAllMovies(pageNumber,pageSize);
				
		if(movies.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return new ResponseEntity<List<MovieDto>>( movies,HttpStatus.OK);
	}
	
	/**
	 * @methodInfo : Add movie detail
	 * @URL : http://localhost:8080/movie/addMovie
	 * @method : POST Method
	 * @return added movie details
	 * @throws IOException 
	 */
	@PostMapping("/addMovie")
	public ResponseEntity<?> addMovie(@RequestBody MovieDto newMovie) {
				
		log.info("New Movie Details to be added in database : " + newMovie);

		try {
			MovieDto movie = movieService.addNewMovie(newMovie);
			return new ResponseEntity<>(movie, HttpStatus.CREATED);
		} catch (ResourceNotFoundException ex) {
			// Handle the case where a duplicate movie is detected
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
		}

	}
	
	/**
	 * @methodInfo : Update movie details
	 * @URL : http://localhost:8080/movie/updateMovie/id
	 * @method : PUT Method
	 * @return : Updated Movie Details
	 */
	@PutMapping("/updateMovie/{id}")
	public ResponseEntity<?> updateMovie(@RequestBody MovieDto updatedMovie, @PathVariable Long id) {
		
		try {
	        MovieDto movieDetails = movieService.updateMovieDetails(updatedMovie, id);
	        return new ResponseEntity<>(movieDetails, HttpStatus.OK);
	    } catch (ResourceNotFoundException e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	    }
	}
	
	/**
	 * @methodInfo : Delete movie details
	 * @URL : http://localhost:8080/movie/deleteMovie/id
	 * @method : DELETE Method
	 * @return : Message that shows movie is deleted
	 */
	@DeleteMapping("/deleteMovie/{id}")
	public ResponseEntity<?> deleteMovie( @PathVariable Long id) {
		
		try {
	        String response = movieService.deleteMovieDetails(id);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (ResourceNotFoundException e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	    }
	}
	
	/**
	 * @methodInfo : Get movie details by id
	 * @URL : http://localhost:8080/movie/getMovieById/id
	 * @method : GET Method
	 * @return : Movie details fetched by particular id 
	 */
	@GetMapping("/getMovieById/{id}")
	public ResponseEntity<?> getMovieById( @PathVariable Long id) {
		
		try {
	        MovieDto movieDetails = movieService.getMovieDetails(id);
	        return new ResponseEntity<>(movieDetails, HttpStatus.OK);
	    } catch (ResourceNotFoundException e) {
	        return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
	    }
	}
	
	/**
	 * @methodInfo : Get movie data into excel sheet
	 * @URL : http://localhost:8080/movie/getDataToExcel
	 * @method : GET Method
	 * @return : Movie Data in Excel Sheet
	 */
	@GetMapping("/getDataToExcel")
	public ResponseEntity<Resource> download() throws IOException {
		
		String fileName = "movies_data.xlsx";
		
		ByteArrayInputStream actualData= movieService.getActualData();
		
		InputStreamResource file = new InputStreamResource(actualData);
		
		ResponseEntity<Resource> body= ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,"attachment; fileName =" +fileName)
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
				.body(file);
		
		return body;
	}
	
	/**
	 * @methodInfo : Get movie data into pdf format
	 * @URL : http://localhost:8080/movie/dataToPdf
	 * @method : GET Method
	 * @return : Movie Data in Pdf
	 */
	@GetMapping("/dataToPdf")
	public ResponseEntity<InputStreamResource> createPdf() {

	    ByteArrayInputStream pdf = movieService.generatePdf();

	    HttpHeaders httpHeaders = new HttpHeaders();
	    httpHeaders.add("Content-Disposition", "inline; filename=movies.pdf");  

	    return ResponseEntity
	            .ok()
	            .headers(httpHeaders)
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(new InputStreamResource(pdf));
	}

	/**
	 * @methodInfo : Upload movie data from excel format to store it into database
	 * @URL : http://localhost:8080/movie/upload/excelToDatabase
	 * @method : POST Method
	 * @return : Message that movie excel file date is uploaded and saved successfully
	 */
	@PostMapping("/upload/excelToDatabase")
	public ResponseEntity<?> uploadExcelDate(@RequestParam("file") MultipartFile file) {
		
		if(ExcelHelper.checkExcelFormat(file)) {
		
			this.movieService.save(file);
			
			return ResponseEntity.ok(Map.of("Message","File is uploaded and data is saved to database"));
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload excel file only");
	}
	
}
