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

import com.gcit.lms.entity.Publisher;

public class PublisherDAO  {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Publisher> getAllPublishers() {
		List<Map<String, Object>> list1 =  jdbcTemplate.queryForList("select * from tbl_publisher");
		List<Publisher> publisherList = new ArrayList<Publisher>();
		for (Map<String, Object> li : list1) {
			Publisher publisher = new Publisher();
			publisher.setPubId((Integer) li.get("publisherId"));
			publisher.setPubName((String) li.get("publisherName"));
			publisher.setPubAddress((String) li.get("publisherAddress")); 
			publisher.setPubPhone((String) li.get("publisherPhone")); 
			publisherList.add(publisher);
		}
		return publisherList;
	}
	
	public Publisher getPublisherById(Integer id) {
		List<Map<String, Object>> list1 =  jdbcTemplate.queryForList("select * from tbl_publisher where publisherId = ?", new Object[] { id });
		Publisher publisher = new Publisher();
		if (list1.isEmpty()) {
			return null;
		} else {
			publisher.setPubId((Integer) list1.get(0).get("publisherId"));
			publisher.setPubName((String) list1.get(0).get("publisherName"));
			publisher.setPubAddress((String) list1.get(0).get("publisherAddress")); 
			publisher.setPubPhone((String) list1.get(0).get("publisherPhone"));
		}
		return publisher;
	}

	public int savePublisherWithId(final String publisherName, final String publisherAddress, final String publisherPhone) throws Exception {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
	            PreparedStatement ps = connection.prepareStatement("insert into tbl_publisher (publisherName, publisherAddress, publisherPhone) values (?,?,?)", new String[] { "publisherId" });
	            ps.setString(1, publisherName);
	            ps.setString(2, publisherAddress);
	            ps.setString(3, publisherPhone);
	            return ps;
	        }
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}
	
	public int deletePublisher(Integer pubId) {
		return jdbcTemplate.update("delete from tbl_publisher where publisherId = ?", new Object[] { pubId });
	}
	
	public int updatePublisher(Integer pubId, String publisherName, String publisherAddress, String publisherPhone) throws Exception {
		int i = jdbcTemplate.update("update tbl_publisher set publisherName = ?, publisherAddress = ? , publisherPhone = ? where publisherId = ?" , 
				new Object[] { publisherName, publisherAddress, publisherPhone, pubId });
		return i;
	}

}
