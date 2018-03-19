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

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.entity.Author;
/**
 * Handles requests for the author routes.
 */
@RestController
public class AuthorController {
	
	@Autowired
	AuthorDAO authorDao;
	
	private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);

	/* Read all authors */
	@RequestMapping(value = "/authors", method = RequestMethod.GET)
	public List<Author> getAllAuthors() {
		logger.info("Getting all authors"); 
		return authorDao.getAllAuthors();
	}
	
	/* Read one author by id */
	@RequestMapping(value = "/authors/{authorId}", method = RequestMethod.GET)
	public Author getAuthorById(@PathVariable int authorId, HttpServletResponse response) throws IOException {
		logger.info("Getting author with id: " + authorId);
		
		Author author =  authorDao.getAuthorById(authorId);
		if (author == null) 
			response.sendError(404, "Invalid id, author does not exist in the database.");
		return author;
	}
	
	/* Save one author and returns the auto-generated id */
	@RequestMapping(value = "/author", method = RequestMethod.POST, consumes = {"application/xml", "application/json"})
	public void saveAuthorWithId(@RequestBody Author author, HttpServletResponse response) throws IOException {
		logger.info("Saving author with name: " + author.getAuthorName());
		try {
			int i = authorDao.saveAuthorWithId(author.getAuthorName());
			if (i != 0) {
				logger.info("Author saved with id: " + i);
				response.setHeader("location", "/authors/" + i);
				response.setStatus(201);
			} 
		} catch (Exception e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");
		}
	}
	
	/* Delete one author with the given id */
	@RequestMapping(value = "/authors/{authorId}", method = RequestMethod.DELETE)
	public void deleteAuthor(@PathVariable int authorId , HttpServletResponse response) throws IOException {
		logger.info("Deleting author with id " + authorId);
		int i = authorDao.deleteAuthor(authorId);
		logger.info(i + " author deleted with id " + authorId);
		if (i == 0) 
			response.sendError(404, "Invalid id, author does not exist in database.");
		response.setStatus(204);
	}
	
	/* Update one author with the given id */
	@RequestMapping(value = "/authors/{authorId}", method = RequestMethod.PUT, consumes = {"application/xml", "application/json"})
	public void updateAuthor(@RequestBody Author author, @PathVariable int authorId, HttpServletResponse response) throws IOException {
		logger.info("Updating author (id: " + authorId + ") with new name: " + author.getAuthorName());
		try {
			int i = authorDao.updateAuthor(authorId, author.getAuthorName());
			logger.info(i + " author (id: " + authorId + ") updated with new name: " + author.getAuthorName());
			if (i == 0)
				response.sendError(404, "Invalid id, author does not exist in database.");
			response.setHeader("location", "/authors/" + authorId);
		} catch (Exception e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");		}
	}
	
}
