package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.gcit.lms.entity.*;

public class BookDAO {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Book> getAllBooks() {
		List<Map<String, Object>> list1 =  jdbcTemplate.queryForList("select * from tbl_book");
		List<Book> bookList = new ArrayList<Book>();
		for (Map<String, Object> li : list1) {
			/* MAKE A NEW BOOK AND INITIALIZE WITH DATA */
			Book book = new Book();
			book.setBookId((Integer)li.get("bookId"));
			book.setTitle((String)li.get("title"));
		
			/* GET ALL GENRES WITH BOOKID AND SET TO THE BOOK OBJECT */
			List<Map<String, Object>> list2 = jdbcTemplate.queryForList("select tbl_genre.genre_id, tbl_genre.genre_name from tbl_book_genres join tbl_genre on tbl_genre.genre_id = tbl_book_genres.genre_id where tbl_book_genres.bookId = ?", 
					new Object[] { li.get("bookId") });
			List<Genre> genreList = new ArrayList<Genre>();
			for (Map<String, Object> li2 : list2) {
				Genre genre = new Genre();
				genre.setGenreId((Integer)li2.get("genre_id"));
				genre.setGenreName((String)li2.get("genre_name"));
				genreList.add(genre);
			}
			book.setGenres(genreList);
			
			/* GET ALL AUTHORS WITH BOOKID AND SET TO THE BOOK OBJECT */
			List<Map<String, Object>> list3 = jdbcTemplate.queryForList("select tbl_author.authorId, tbl_author.authorName from tbl_book_authors join tbl_author on tbl_author.authorId = tbl_book_authors.authorId where tbl_book_authors.bookId = ?", 
					new Object[] { li.get("bookId") });
			List<Author> authorList = new ArrayList<Author>();
			for (Map<String, Object> li3 : list3) {
				Author author = new Author();
				author.setAuthorId((Integer)li3.get("authorId"));
				author.setAuthorName((String)li3.get("authorName"));
				authorList.add(author);
			}
			book.setAuthors(authorList);
			
			/* GET PUBLISHER OF BOOK */
			List<Map<String, Object>> list4 = jdbcTemplate.queryForList("select tbl_publisher.publisherId, tbl_publisher.publisherName, tbl_publisher.publisherAddress, tbl_publisher.publisherPhone from tbl_publisher join tbl_book on tbl_book.pubId = tbl_publisher.publisherId where tbl_book.bookId = ?", 
					new Object[] { li.get("bookId") });
			if (!list4.isEmpty()) {
				Publisher publisher = new Publisher();
				publisher.setPubId((Integer) list4.get(0).get("publisherId"));
				publisher.setPubName((String) list4.get(0).get("publisherName"));
				publisher.setPubAddress((String) list4.get(0).get("publisherAddress"));
				publisher.setPubPhone((String) list4.get(0).get("publisherPhone"));
				book.setPublisher(publisher);
			}
			/* ADD THE INITIALIZED BOOK TO THE LIST */
			bookList.add(book);
		}
		return bookList;
	}

	public Book getBookById(Integer id) {
		List<Map<String, Object>> list1 =  jdbcTemplate.queryForList("select * from tbl_book where bookId = ?", new Object[] {id});
		/* Returns null if list is empty */
		if (list1.isEmpty())
			return null;
		
		/* MAKE A NEW BOOK AND INITIALIZE WITH DATA */
		Book book = new Book();
		book.setBookId((Integer) list1.get(0).get("bookId"));
		book.setTitle((String) list1.get(0).get("title"));

		/* GET ALL GENRES WITH BOOKID AND SET TO THE BOOK OBJECT */
		List<Map<String, Object>> list2 = jdbcTemplate.queryForList(
				"select tbl_genre.genre_id, tbl_genre.genre_name from tbl_book_genres join tbl_genre on tbl_genre.genre_id = tbl_book_genres.genre_id where tbl_book_genres.bookId = ?",
				new Object[] { list1.get(0).get("bookId") });
		List<Genre> genreList = new ArrayList<Genre>();
		for (Map<String, Object> li2 : list2) {
			Genre genre = new Genre();
			genre.setGenreId((Integer) li2.get("genre_id"));
			genre.setGenreName((String) li2.get("genre_name"));
			genreList.add(genre);
		}
		book.setGenres(genreList);

		/* GET ALL AUTHORS WITH BOOKID AND SET TO THE BOOK OBJECT */
		List<Map<String, Object>> list3 = jdbcTemplate.queryForList(
				"select tbl_author.authorId, tbl_author.authorName from tbl_book_authors join tbl_author on tbl_author.authorId = tbl_book_authors.authorId where tbl_book_authors.bookId = ?",
				new Object[] { list1.get(0).get("bookId") });
		List<Author> authorList = new ArrayList<Author>();
		for (Map<String, Object> li3 : list3) {
			Author author = new Author();
			author.setAuthorId((Integer) li3.get("authorId"));
			author.setAuthorName((String) li3.get("authorName"));
			authorList.add(author);
		}
		book.setAuthors(authorList);

		/* GET PUBLISHER OF BOOK */
		List<Map<String, Object>> list4 = jdbcTemplate.queryForList(
				"select tbl_publisher.publisherId, tbl_publisher.publisherName, tbl_publisher.publisherAddress, tbl_publisher.publisherPhone from tbl_publisher join tbl_book on tbl_book.pubId = tbl_publisher.publisherId where tbl_book.bookId = ?",
				new Object[] { list1.get(0).get("bookId") });
		if (!list4.isEmpty()) {
			Publisher publisher = new Publisher();
			publisher.setPubId((Integer) list4.get(0).get("publisherId"));
			publisher.setPubName((String) list4.get(0).get("publisherName"));
			publisher.setPubAddress((String) list4.get(0).get("publisherAddress"));
			publisher.setPubPhone((String) list4.get(0).get("publisherPhone"));
			book.setPublisher(publisher);
		}
		return book;
	 }
	
	public int saveBookWithId(final String title, final Integer pubId) throws Exception {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
	            PreparedStatement ps = connection.prepareStatement("insert into tbl_book (title, pubId) values (?,?)", new String[] { "bookId" });
	            ps.setString(1, title);
	            ps.setInt(2, pubId);
	            return ps;
	        }
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}
	
	public int connectGenre(Integer bookId, Integer genreId) throws Exception {
		int i = jdbcTemplate.update("insert into tbl_book_genres (bookId, genre_id) values (?,?) ", new Object[]{bookId, genreId});
		return i;
	}
	
	public int connectAuthor(Integer bookId, Integer authorId) throws Exception {
		int i = jdbcTemplate.update("insert into tbl_book_authors (bookId, authorId) values (?,?)", new Object[]{bookId, authorId});
		return i;
	}
	
	public int deleteBook(int bookId) {
		return jdbcTemplate.update("delete from tbl_book where bookId = ?", new Object[] { bookId });
	}
	
	public int updateBook(Integer bookId, String title) throws Exception {
		int i = jdbcTemplate.update("update tbl_book set title = ? where bookid = ?" , new Object[] { title, bookId });
		return i;
	}
}
