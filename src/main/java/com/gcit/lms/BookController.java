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

import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Genre;
/**
 * Handles requests for the book routes.
 */
@RestController
public class BookController {
	
	@Autowired
	BookDAO bookDao;
	
	private static final Logger logger = LoggerFactory.getLogger(BookController.class);

	/* Read all books */
	@RequestMapping(value = "/books", method = RequestMethod.GET)
	public List<Book> getAllBooks() {
		logger.info("Getting all books"); 
		return bookDao.getAllBooks();
	}
	
	/* Read one book by id */
	@RequestMapping(value = "/books/{bookId}", method = RequestMethod.GET)
	public Book getBookById(@PathVariable int bookId, HttpServletResponse response) throws IOException {
		logger.info("Getting book with id: " + bookId);
		Book book = bookDao.getBookById(bookId);
		if (book == null) 
			response.sendError(404, "Invalid id, book does not exist in database.");
		return book;
	}
	
	/* Add one book with all its connections */
	@RequestMapping(value = "/book", method = RequestMethod.POST, consumes = {"application/xml", "application/json"})
	public void addBook(@RequestBody Book book, HttpServletResponse response) throws IOException {
		try {
			logger.info("Adding book with title: " + book.getTitle() + ", pubId: " + book.getPublisher().getPubId());
			Integer bookId = bookDao.saveBookWithId(book.getTitle(), book.getPublisher().getPubId());
			logger.info("Book added with id: " + bookId);
			/* Connect the book with each genre */
			for (Genre g : book.getGenres() ) {
				int i = bookDao.connectGenre(bookId, g.getGenreId());
				logger.info(i + " book connected with genreId: " + g.getGenreId());
			}
			/* Connect the book with each author */
			for (Author a : book.getAuthors() ) {
				int i = bookDao.connectAuthor(bookId, a.getAuthorId());
				logger.info(i + " book connected with authorId: " + a.getAuthorId());
			}
			response.setHeader("location","/books/"+ book.getBookId());
			response.setStatus(201);
		} catch (Exception e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");
		}
	}
	
	/* Delete one book with the given id */
	@RequestMapping(value = "/books/{bookId}", method = RequestMethod.DELETE)
	public void deleteBook(@PathVariable int bookId, HttpServletResponse response) throws IOException {
		logger.info("Deleting book with id " + bookId);
		int i = bookDao.deleteBook(bookId);
		logger.info(i + " book deleted with id " + bookId);
		if (i == 0)
			response.sendError(404, "Invalid id, book does not exist in database.");
		response.setStatus(204);
	}
	
	/* Update one book title with the given id */
	@RequestMapping(value = "/books/{bookId}", method = RequestMethod.PUT, consumes = {"application/xml", "application/json"})
	public void updateBook(@RequestBody Book book, @PathVariable int bookId, HttpServletResponse response) throws IOException {
		logger.info("Updating book (id: " + bookId + ") with new title: " + book.getTitle());
		try {
			int i = bookDao.updateBook(bookId, book.getTitle());
			logger.info(i + " book (id: " + bookId + ") updated with new title: " + book.getTitle());
			if (i == 0)
				response.sendError(404, "Invalid id, book does not exist in database.");
			response.setHeader("location", "/books/" + bookId);
		} catch (Exception e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");
		}
	}
	
}
