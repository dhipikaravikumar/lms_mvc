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

import com.gcit.lms.entity.Branch;

public class BranchDAO  {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Branch> getAllBranches() {
		List<Map<String, Object>> list1 =  jdbcTemplate.queryForList("select * from tbl_library_branch");
		List<Branch> branchList = new ArrayList<Branch>();
		for (Map<String, Object> li : list1) {
			Branch branch = new Branch();
			branch.setBranchId((Integer) li.get("branchId"));
			branch.setBranchName((String) li.get("branchName"));
			branch.setBranchAddress((String) li.get("branchAddress")); 
			branchList.add(branch);
		}
		return branchList;
	}
	
	public Branch getBranchById(Integer id) {
		List<Map<String, Object>> list1 =  jdbcTemplate.queryForList("select * from tbl_library_branch where branchId = ?", new Object[] { id });
		Branch branch = new Branch();
		if (list1.isEmpty()) {
			return null;
		} else {
			branch.setBranchId((Integer) list1.get(0).get("branchId"));
			branch.setBranchName((String) list1.get(0).get("branchName"));
			branch.setBranchAddress((String) list1.get(0).get("branchAddress")); 
		}
		return branch;
	}

	public int saveBranchWithId(final String branchName, final String branchAddress) throws Exception {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
	            PreparedStatement ps = connection.prepareStatement("insert into tbl_library_branch (branchName, branchAddress) values (?,?)", new String[] { "branchId" });
	            ps.setString(1, branchName);
	            ps.setString(2, branchAddress);
	            return ps;
	        }
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}
	
	public int deleteBranch(Integer branchId) {
		return jdbcTemplate.update("delete from tbl_library_branch where branchId = ?", new Object[] { branchId });
	}
	
	public int updateBranch(Integer branchId, String branchName, String branchAddress) throws Exception {
		int i = jdbcTemplate.update("update tbl_library_branch set branchName = ?, branchAddress = ?  where branchId = ?" , 
				new Object[] { branchName, branchAddress, branchId });
		return i;
	}

}
