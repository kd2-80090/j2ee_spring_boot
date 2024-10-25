package com.app.main.helper;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.app.main.customexception.ResourceNotFoundException;
import com.app.main.daos.MovieDao;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.app.main.entity.Movie;

@Component
public class ExcelHelper {

    @Autowired
    private MovieDao movieDao;
	
	public static boolean checkExcelFormat(MultipartFile file) {
		
		String contentType = file.getContentType();
		
		if(contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
			return true;
		}
		else {
			return false;
		}
	}
	
//    @SuppressWarnings("resource")
    public  List<Movie> convertExcelToListOfMovie(InputStream is) {

        List<Movie> list = new ArrayList<Movie>();

        try {

            XSSFWorkbook workbook = new XSSFWorkbook(is);

            System.out.println("Available sheets:");
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                System.out.println(workbook.getSheetName(i));
            }

            XSSFSheet sheet = workbook.getSheet("Sheet1");

//            if (sheet == null) {
//                throw new IllegalArgumentException("Sheet 'Sheet1' not found in the provided Excel file.");
//            }

            int rowNumber = 0;

            Iterator<Row> iterator = sheet.iterator();

            while (iterator.hasNext()) {

                Row row = iterator.next();

                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cells = row.iterator();
                boolean isRowValid = true;
                
                int cid = 0;

                String movieName = row.getCell(0).getStringCellValue();
                Optional<Movie> existingMovie = movieDao.findByMovieName(movieName);

                if (existingMovie.isPresent()) {
                    System.out.println("Duplicate movie found: '" + movieName + "' - skipping.");
                    isRowValid = false;
                }

                Movie movie = new Movie();

                while (cells.hasNext()) {

                    Cell cell = cells.next();

                    switch (cid) {
                        case 0:
                        	if (cell == null || cell.getCellType() != CellType.STRING) {
                        		System.out.println("Invalid or missing movie name at row " + rowNumber);
                        		isRowValid = false;
                        	} else {
                                movie.setMovieName(cell.getStringCellValue());
                            }
                            //	movie.setMovieName(cell.getStringCellValue());
                            break;
                        case 1:
                        	Cell releaseDateCell = row.getCell(1);
                            LocalDate releaseDate = null;
                            
                            if (releaseDateCell == null) {
                                System.out.println("Missing release date at row " + rowNumber);
                                isRowValid = false;
                            }
                            if (releaseDateCell != null) {
                                if (releaseDateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(releaseDateCell)) {
                                    releaseDate = releaseDateCell.getLocalDateTimeCellValue().toLocalDate();
                                } 
                            else if (releaseDateCell.getCellType() == CellType.STRING) {

                            	String dateStr = releaseDateCell.getStringCellValue().trim();
                            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                releaseDate = LocalDate.parse(dateStr, formatter);
                          
                            	} 
                            }
                            movie.setReleaseDate(releaseDate);
                            break;
                        default:
                            break;
                    }

                    cid++;
//                    movie.setStatus("active");
                }
                
                if (isRowValid) {
                    movie.setStatus("active");
                    list.add(movie);  
                }
//                list.add(movie);
            }

            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
	
	
}
