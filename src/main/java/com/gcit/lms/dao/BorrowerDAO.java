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

import com.gcit.lms.entity.Borrower;

public class BorrowerDAO  {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Borrower> getAllBorrowers() {
		List<Map<String, Object>> list1 =  jdbcTemplate.queryForList("select * from tbl_borrower");
		List<Borrower> borrowerList = new ArrayList<Borrower>();
		for (Map<String, Object> li : list1) {
			Borrower borrower = new Borrower();
			borrower.setCardNo((Integer) li.get("cardNo"));
			borrower.setName((String) li.get("name"));
			borrower.setAddress((String) li.get("address"));
			borrower.setPhone((String) li.get("phone"));
			borrowerList.add(borrower);
		}
		
		return borrowerList;
	}
	
	public Borrower getBorrowerById(Integer id) {
		List<Map<String, Object>> list1 =  jdbcTemplate.queryForList("select * from tbl_borrower where cardNo = ?", new Object[] { id });
		Borrower borrower = new Borrower();
		if (list1.isEmpty()) {
			return null;
		}
		else {
			borrower.setCardNo((Integer) list1.get(0).get("cardNo"));
			borrower.setName((String) list1.get(0).get("name"));
			borrower.setAddress((String) list1.get(0).get("address"));
			borrower.setPhone((String) list1.get(0).get("phone"));
		}
		return borrower;
	}

	public int saveBorrowerWithId(final String name, final String address, final String phone ) throws Exception {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
	            PreparedStatement ps = connection.prepareStatement("insert into tbl_borrower (name, address, phone) values (?,?,?)", new String[] { "cardNo" });
	            ps.setString(1, name);
	            ps.setString(2, address);
	            ps.setString(3, phone);
	            return ps;
	        }
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}
	
	public int deleteBorrower(Integer cardNo) {
		return jdbcTemplate.update("delete from tbl_borrower where cardNo = ?", new Object[] { cardNo });
	}
	
	public int updateBorrower(Integer cardNo, String name, String address, String phone) throws Exception {
		int i = jdbcTemplate.update("update tbl_borrower set name = ?, address = ?, phone = ? where cardNo = ?" , 
				new Object[] { name, address, phone, cardNo });
		return i;
	}

}
