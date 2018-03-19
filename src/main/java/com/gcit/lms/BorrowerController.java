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

import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.entity.Borrower;
/**
 * Handles requests for the borrower routes.
 */
@RestController
public class BorrowerController {
	
	@Autowired
	BorrowerDAO borrowerDao;
	
	private static final Logger logger = LoggerFactory.getLogger(BorrowerController.class);

	/* Read all borrowers */
	@RequestMapping(value = "/borrowers", method = RequestMethod.GET)
	public List<Borrower> getAllBorrowers() {
		logger.info("Getting all borrowers"); 
		return borrowerDao.getAllBorrowers();
	}
	
	/* Read one borrower by id */
	@RequestMapping(value = "/borrowers/{cardNo}", method = RequestMethod.GET)
	public Borrower getBorrowerById(@PathVariable int cardNo, HttpServletResponse response) throws IOException {
		logger.info("Getting borrower with id: " + cardNo);
		Borrower borrower = borrowerDao.getBorrowerById(cardNo);
		if (borrower == null)
			response.sendError(404, "Invalid card number, borrower does not exist in the database.");
		return borrower;
	}
	
	/* Save one borrower and returns the auto-generated id */
	@RequestMapping(value = "/borrower", method = RequestMethod.POST , consumes = {"application/xml", "application/json"})
	public void saveBorrowerWithId(@RequestBody Borrower borrower, HttpServletResponse response) throws IOException {
		logger.info("Saving borrower with name: " + borrower.getName() + ", address: " + borrower.getAddress() + ", phone: " + borrower.getPhone());
		try {
			int i = borrowerDao.saveBorrowerWithId(borrower.getName(), borrower.getAddress(), borrower.getPhone());
			if (i != 0) {
				logger.info("Borrower saved with id: " + i);
				response.setHeader("location", "/borrowers/" + i);
				response.setStatus(201);
			} 
		} catch (Exception e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");
		}
	}
	
	/* Delete one borrower with the given id */
	@RequestMapping(value = "/borrowers/{cardNo}", method = RequestMethod.DELETE)
	public void deleteBorrower(@PathVariable int cardNo, HttpServletResponse response) throws IOException {
		logger.info("Deleting borrower with id " + cardNo);
		int i = borrowerDao.deleteBorrower(cardNo);
		logger.info(i + " borrower deleted with id " + cardNo);
		if ( i == 0)
			response.sendError(404, "Invalid card number, borrower does not exist in the database.");
		response.setStatus(204);
	}
	
	/* Update one borrower with the given id */
	@RequestMapping(value = "/borrowers/{cardNo}", method = RequestMethod.PUT)
	public void updateAuthor(@RequestBody Borrower borrower, @PathVariable int cardNo, HttpServletResponse response) throws IOException {
		logger.info("Updating borrower (id: " + cardNo + ") with new name: " + borrower.getName() + ", new address: " + borrower.getAddress() + ", new phone: " + borrower.getPhone());
		try {
			int i = borrowerDao.updateBorrower(cardNo, borrower.getName(), borrower.getAddress(), borrower.getPhone());
			logger.info(i + " borrower (id: " + cardNo + ") updated with new name: " + borrower.getName()
					+ ", new address: " + borrower.getAddress() + ", new phone: " + borrower.getPhone());
			if (i == 0)
				response.sendError(404, "Invalid card number, borrower does not exist in the database.");
			response.setHeader("location", "/borrowers/" + cardNo);
		} catch (Exception e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");
		}
	}
	
}
