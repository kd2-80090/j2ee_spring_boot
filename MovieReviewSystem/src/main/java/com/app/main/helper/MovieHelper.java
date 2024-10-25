package com.app.main.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.app.main.entity.Movie;

public class MovieHelper {

	public static String[] HEADERS= {
		
		"id",
		"movieName",
		"releaseDate",
		"status"
	};
	
	public static String SHEET_NAME = "Movies_Date";
	
	public static ByteArrayInputStream dataToExcel(List<Movie> movieList) throws IOException {
		
		
		//create work book
		Workbook workBook = new XSSFWorkbook();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			
			//create sheet
			Sheet sheet= workBook.createSheet(SHEET_NAME);
			
			//create row : header row
			Row row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(0);
			for(int i=0;i<HEADERS.length;i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(HEADERS[i]);
			}
			
			//value rows
			int rowIndex =1;
			
			for (Movie movie : movieList) {
			
				Row dataRow = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowIndex);
			
				rowIndex++;
				dataRow.createCell(0).setCellValue(movie.getId());
				dataRow.createCell(1).setCellValue(movie.getMovieName());
				
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				
				dataRow.createCell(2).setCellValue(movie.getReleaseDate().format(dateFormatter));
				dataRow.createCell(3).setCellValue(movie.getStatus());
			}
			
			workBook.write(out);
			
			return new ByteArrayInputStream(out.toByteArray());
			
		} catch (IOException e) {
			e.printStackTrace();
			
			System.out.println("Failed to import data to excel");
			return null;
		} finally {
			
			workBook.close();
			out.close();
		}
		
		
	}
}
