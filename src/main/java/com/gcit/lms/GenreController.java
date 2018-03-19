package com.gcit.lms;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.entity.Genre;
/**
 * Handles requests for the genre routes.
 */
@RestController
public class GenreController {
	
	@Autowired
	GenreDAO genreDao;
	
	private static final Logger logger = LoggerFactory.getLogger(GenreController.class);

	/* Read all genres */
	@RequestMapping(value = "/genres", method = RequestMethod.GET)
	public List<Genre> getAllGenres() {
		logger.info("Getting all genres"); 
		return genreDao.getAllGenres();
	}
	
	/* Read one genre by id */
	@RequestMapping(value = "/genres/{genreId}", method = RequestMethod.GET )
	public Genre getGenreById(@PathVariable int genreId , HttpServletResponse response) throws IOException {
		logger.info("Getting genre with id: " + genreId);
		Genre genre =  genreDao.getGenreById(genreId);
		if ( genre == null) 
			response.sendError(404, "Invalid id, genre does not exist in the database.");
		return genre;
	}
	
	/* Save one genre and returns the auto-generated id */
	@RequestMapping(value = "/genre", method = RequestMethod.POST)
	public void saveGenreWithId(@RequestBody Genre genre, HttpServletResponse response) throws IOException {
		logger.info("Saving genre with name: " + genre.getGenreName());
		try {
			int i = genreDao.saveGenreWithId(genre.getGenreName());
			if (i != 0) {
				logger.info("Genre saved with id: " + i);
				response.setHeader("location", "/genres/" + i);
				response.setStatus(201);
			} 
		} catch (Exception e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");		}
	}
	
	/* Delete one genre with the given id */
	@RequestMapping(value = "/genres/{genreId}", method = RequestMethod.DELETE)
	public void deleteGenre(@PathVariable int genreId, HttpServletResponse response) throws IOException {
		logger.info("Deleting genre with id " + genreId);
		int i = genreDao.deleteGenre(genreId);
		logger.info(i + " genre deleted with id " + genreId);
		if ( i == 0 ) {
			response.sendError(404, "Invalid id, genre does not exist in the database.");
		}
		response.setStatus(204);
	}
	
	/* Update one genre with the given id */
	@RequestMapping(value = "/genres/{genreId}", method = RequestMethod.PUT)
	public void updateGenre(@RequestBody Genre genre, @PathVariable int genreId, HttpServletResponse response) throws IOException {
		logger.info("Updating genre (id: " + genreId + ") with new name: " + genre.getGenreName());
		try {
			int i = genreDao.updateGenre(genreId, genre.getGenreName());
			logger.info(i + " genre (id: " + genreId + ") updated with new name: " + genre.getGenreName());
			if (i == 0) {
				response.sendError(404, "Invalid id, genre does not exist in the database.");
			}
			response.setHeader("location", "/genres/" + genreId);
		} catch (Exception e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");		
		}
	}
	
}
