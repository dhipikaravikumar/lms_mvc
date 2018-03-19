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

import com.gcit.lms.entity.Author;

public class AuthorDAO  {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Author> getAllAuthors() {
		List<Map<String, Object>> list1 =  jdbcTemplate.queryForList("select * from tbl_author");
		List<Author> authorList = new ArrayList<Author>();
		for (Map<String, Object> li : list1) {
			Author author = new Author();
			author.setAuthorId((Integer) li.get("authorId"));
			author.setAuthorName((String) li.get("authorName"));
			authorList.add(author);
		}
		
		return authorList;
	}
	
	public Author getAuthorById(Integer id) {
		List<Map<String, Object>> list1 =  jdbcTemplate.queryForList("select * from tbl_author where authorId = ?", new Object[] { id });
		Author author = new Author();
		if (list1.isEmpty()) {
			return null;
		}
		else {
			author.setAuthorId((Integer) list1.get(0).get("authorId"));
			author.setAuthorName((String) list1.get(0).get("authorName"));
		}
		return author;
	}

	public int saveAuthorWithId(final String authorName) throws Exception {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
	            PreparedStatement ps = connection.prepareStatement("insert into tbl_author (authorName) values (?)", new String[] { "authorId" });
	            ps.setString(1, authorName);
	            return ps;
	        }
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}
	
	public int deleteAuthor(Integer authorId) {
		return jdbcTemplate.update("delete from tbl_author where authorId = ?", new Object[] { authorId });
	}
	
	public int updateAuthor(Integer authorId, String authorName) throws Exception {
		int i = jdbcTemplate.update("update tbl_author set authorName = ? where authorid = ?" , new Object[] { authorName, authorId });
		return i;
	}
}
