package com.app.main.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.app.main.dtos.MovieDto;

public interface MovieService {

	List<MovieDto> findAllMovies(int pageNumber, int pageSize);

	MovieDto addNewMovie(MovieDto newMovie);

	MovieDto updateMovieDetails(MovieDto updatedMovie, Long id);

	String deleteMovieDetails(Long id);

	MovieDto getMovieDetails(Long id);
	
	ByteArrayInputStream getActualData() throws IOException;

	ByteArrayInputStream generatePdf();
	
	void save(MultipartFile file);
}
