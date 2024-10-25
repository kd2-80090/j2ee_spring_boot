package com.app.main.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

	private Long id;
	
	private String content;
	
	private LocalDateTime reviewDate;
	
	private String movieName;
	
	private String userName;
	
	private Long userId;
	
	private Long movieId;
	
}
