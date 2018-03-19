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

import com.gcit.lms.entity.Genre;

public class GenreDAO  {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Genre> getAllGenres() {
		List<Map<String, Object>> list1 =  jdbcTemplate.queryForList("select * from tbl_genre");
		List<Genre> genreList = new ArrayList<Genre>();
		for (Map<String, Object> li : list1) {
			Genre genre = new Genre();
			genre.setGenreId((Integer) li.get("genre_id"));
			genre.setGenreName((String) li.get("genre_name"));
			genreList.add(genre);
		}
		
		return genreList;
	}
	
	public Genre getGenreById(Integer id) {
		List<Map<String, Object>> list1 =  jdbcTemplate.queryForList("select * from tbl_genre where genre_id = ?", new Object[] { id });
		Genre genre = new Genre();
		if (list1.isEmpty()) {
			return null;
		}
		else {
			genre.setGenreId((Integer) list1.get(0).get("genre_id"));
			genre.setGenreName((String) list1.get(0).get("genre_name"));
		}
		return genre;
	}

	public int saveGenreWithId(final String genreName) throws Exception {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
	            PreparedStatement ps = connection.prepareStatement("insert into tbl_genre (genre_name) values (?)", new String[] { "genre_id" });
	            ps.setString(1, genreName);
	            return ps;
	        }
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}
	
	public int deleteGenre(Integer genre_id) {
		return jdbcTemplate.update("delete from tbl_genre where genre_id = ?", new Object[] { genre_id });
	}
	
	public int updateGenre(Integer genre_id, String genre_name) throws Exception {
		int i = jdbcTemplate.update("update tbl_genre set genre_name = ? where genre_id = ?" , 
				new Object[] { genre_name, genre_id });
		return i;
	}
}
