package com.gcit.lms.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gcit.lms.entity.Copy;

public class CopyDAO  {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Copy> getAllCopies() {
		List<Map<String, Object>> list1 =  jdbcTemplate.queryForList("select * from tbl_book_copies");
		List<Copy> copyList = new ArrayList<Copy>();
		for (Map<String, Object> li : list1) {
			Copy copy = new Copy();
			copy.setBookId((Integer) li.get("bookId"));
			copy.setBranchId((Integer) li.get("branchId"));
			copy.setNoCopies((Integer) li.get("noOfCopies"));
			copyList.add(copy);
		}
		return copyList;
	}
	
	public Copy getCopyById(Integer branchId, Integer bookId) {
		List<Map<String, Object>> list1 =  jdbcTemplate.queryForList("select * from tbl_book_copies where branchId = ? && bookId = ?", new Object[] { branchId, bookId });
		Copy copy = new Copy();
		if (list1.isEmpty()) {
			return null;
		} else {
			copy.setBookId((Integer) list1.get(0).get("bookId"));
			copy.setBranchId((Integer) list1.get(0).get("branchId"));
			copy.setNoCopies((Integer) list1.get(0).get("noOfCopies"));; 
		}
		return copy;
	}
	
	public int updateCopy(Integer branchId, Integer bookId, Integer noOfCopies) throws Exception {
		int i = jdbcTemplate.update("update tbl_book_copies set noOfCopies = ? where branchId = ? && bookId = ?" , 
				new Object[] { noOfCopies, branchId, bookId });
		return i;
	}

}
