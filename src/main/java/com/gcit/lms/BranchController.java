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

import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.entity.Branch;
/**
 * Handles requests for the branch routes.
 */
@RestController
public class BranchController {
	
	@Autowired
	BranchDAO branchDao;
	
	private static final Logger logger = LoggerFactory.getLogger(BranchController.class);

	/* Read all branches */
	@RequestMapping(value = "/branches", method = RequestMethod.GET)
	public List<Branch> getAllBranches() {
		logger.info("Getting all branches"); 
		return branchDao.getAllBranches();
		
	}
	
	/* Read one branch by id */
	@RequestMapping(value = "/branches/{branchId}", method = RequestMethod.GET)
	public Branch getBranchById(@PathVariable int branchId, HttpServletResponse response) throws IOException {
		logger.info("Getting branch with id: " + branchId);
		Branch branch = branchDao.getBranchById(branchId);
		if (branch == null) {
			response.sendError(404, "Invalid branch id, branch does not exist in the database.");
		}
		return branch;
	}
	
	/* Save one branch and returns the auto-generated id */
	@RequestMapping(value = "/branch", method = RequestMethod.POST)
	public void saveBranchWithId(@RequestBody Branch branch, HttpServletResponse response) throws IOException {
		logger.info("Saving branch with name: " + branch.getBranchName());
		try {
			int i = branchDao.saveBranchWithId(branch.getBranchName(), branch.getBranchAddress());
			if (i != 0) {
				logger.info("Branch saved with id: " + i);
				response.setHeader("location", "/branches/" + i);
				response.setStatus(201);
			} 
		} catch (Exception e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");		}
	}
	
	/* Delete one branch with the given id */
	@RequestMapping(value = "/branches/{branchId}", method = RequestMethod.DELETE)
	public void deleteBranch(@PathVariable int branchId, HttpServletResponse response) throws IOException {
		logger.info("Deleting branch with id " + branchId);
		int i = branchDao.deleteBranch(branchId);
		logger.info(i + " branch deleted with id " + branchId);
		if (i == 0) {
			response.sendError(404, "Invalid branch id, branch does not exist in the database.");
		}
		response.setStatus(204);
	}
	
	/* Update one branch with the given id */
	@RequestMapping(value = "/branches/{branchId}", method = RequestMethod.PUT)
	public void updateBranch(@RequestBody Branch branch, @PathVariable int branchId, HttpServletResponse response) throws IOException {
		logger.info("Updating branch (id: " + branchId + ") with new name: " + branch.getBranchName() + ", new address: " + branch.getBranchAddress() );
		try {
			int i = branchDao.updateBranch(branchId, branch.getBranchName(), branch.getBranchAddress());
			logger.info(i + " branch (id: " + branchId + ") updated with new name: " + branch.getBranchName()
					+ ", new address: " + branch.getBranchAddress());
			if (i == 0) {
				response.sendError(404, "Invalid branch id, branch does not exist in the database.");
			}
			response.setHeader("location", "/branches/" + branchId);
		} catch (Exception e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");		}
	}
	
//	/* Update book at branch with new number of copies */
//	@RequestMapping(value = "/branches/{branchId}/{bookId}", method = RequestMethod.PUT)
//	public void updateBranchBookCopies(@RequestBody @Valid Copy copy, @PathVariable int branchId, @PathVariable int bookId, HttpServletResponse response) {
//		logger.info("Updating copies of book (id: " + bookId + ") from branch (id: " + branchId + ") with new number of copies: " + copy.getNoCopies() );
//		int i = copyDao.updateCopy(branchId, bookId, copy.getNoCopies());
//		logger.info(i + " copy row with book (id: " + bookId + ") from branch (id: " + branchId + ") updated with new number of copies: " + copy.getNoCopies() );
//		response.setHeader("location","/copies/" + branchId + "/" + bookId);
//	}
	
}
