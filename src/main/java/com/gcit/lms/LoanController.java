package com.gcit.lms;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.entity.*;
import com.gcit.lms.dao.*;
/**
 * Handles requests for the loan routes.
 */
@RestController
public class LoanController {
	
	@Autowired
	LoanDAO loanDao;
	
	@Autowired
	CopyDAO copyDao;
	
	private static final Logger logger = LoggerFactory.getLogger(LoanController.class);

	/* Read all loans */
	@RequestMapping(value = "/loans", method = RequestMethod.GET)
	public List<Loan> getAllLoans() {
		logger.info("Getting all loans"); 
		return loanDao.getAllLoans();  
		
	}
	
	/* Get one loan */ 
	@RequestMapping(value = "/loans/borrowers/{cardNo}/branches/{branchId}/books/{bookId}", method = RequestMethod.GET)
	public Loan getLoan(@PathVariable int cardNo, @PathVariable int branchId, @PathVariable int bookId, HttpServletResponse response ) throws IOException {
		//Timestamp di = new Timestamp(Long.parseLong(dateIn));
		logger.info("Getting loan with cardNo: " + cardNo + ", branchId: " + branchId + ", bookId: " + bookId );
		Loan loan =  loanDao.getLoan(cardNo, branchId, bookId);
		if (loan == null) 
			response.sendError(404, "Invalid  keys. Loan entry does not exist in the database.");
		return loan;
	}
	
	/* Read all loans of by one borrower from cardNo */
	@RequestMapping(value = "/loans/borrowers/{cardNo}", method = RequestMethod.GET)
	public List<Loan> getPendingLoansByCardNo(@PathVariable int cardNo , HttpServletResponse response) throws IOException {
		logger.info("Getting all pending loans with cardNo: " + cardNo);
		List<Loan> loans = loanDao.getPendingLoansByCardNo(cardNo);
		if (loans.isEmpty()) 
			response.sendError(404, "Invalid card number, either borrower does not have any pending loans or borrower does not exist in the database.");
		return loans;
	}
	
	/* Borrow book with bookId and branchId */
	@RequestMapping(value = "/loans/borrowers/{cardNo}/branches/{branchId}/books/{bookId}", method = RequestMethod.POST)
	public void borrowBook(@PathVariable int cardNo, @PathVariable int branchId, @PathVariable int bookId, HttpServletResponse response) throws IOException {
		if (loanDao.checkLoanStatus(cardNo, branchId, bookId)) {
			logger.info("Borrowing book using cardNo: " + cardNo + ", branch: " +  branchId + ", book: " + bookId);
			try {
				int i = loanDao.borrowBook(cardNo, branchId, bookId);
				logger.info(i + " borrowed borrowed successfully.");
				Copy copy = copyDao.getCopyById(branchId, bookId);
				int j = copyDao.updateCopy(branchId, bookId, (copy.getNoCopies()-1));
				logger.info(j + " row update with " + (copy.getNoCopies()-1) + " copies (bookId: " + bookId + ", branchId: " + branchId + ")");
				response.setHeader("location","/loans/borrowers/"+ cardNo);
			} catch (Exception e) {
				logger.info("In catch block");
				response.sendError(400, "Invalid request caused by invalid path parameters.");
			}
		}
		else {
			logger.info("Book already borrowed, please return before borrowing again.");
			response.sendError(400, "Book already borrowed, please return before borrowing again.");
		}
	}
	
	/* Return book with bookId and branchId */
	@RequestMapping(value = "/loans/borrowers/{cardNo}/branches/{branchId}/books/{bookId}/checkIn", method = RequestMethod.PUT)
	public void returnBook(@PathVariable int cardNo, @PathVariable int branchId, @PathVariable int bookId, HttpServletResponse response) throws IOException {
		logger.info("Returning book of cardNo: " + cardNo + ", branch: " +  branchId + ", book: " + bookId);
		try {
			int i = loanDao.returnBook(cardNo, branchId, bookId);
			logger.info(i + " book returned successfully.");
			Copy copy = copyDao.getCopyById(branchId, bookId);
			i = copyDao.updateCopy(branchId, bookId, (copy.getNoCopies()+1));
			logger.info(i + " row update with " + (copy.getNoCopies()+1) + " copies (bookId: " + bookId + ", branchId: " + branchId + ")");
			response.setHeader("location","/loans/borrowers/"+ cardNo);
		} catch (Exception e) {
			logger.info("In catch block");
			response.sendError(400, "Invalid request caused by invalid path parameters.");
		}
	}
	
	/* Update one loan with new due date */
	@RequestMapping(value = "/loans/borrowers/{cardNo}/branches/{branchId}/books/{bookId}", method = RequestMethod.PUT)
	public void updateLoan(@RequestBody @Valid Loan loan, @PathVariable int cardNo, @PathVariable int branchId, @PathVariable int bookId , HttpServletResponse response) {
		logger.info("Updating loan with new due date: " + loan.getDueDate());
		
		int i = loanDao.updateLoan(cardNo, branchId, bookId, loan.getDateIn(), loan.getDueDate());
		logger.info(i + " loan updated with new due date: " + loan.getDueDate());
		response.setHeader("location","/loans/borrowers/"+ cardNo + "/branches/" + branchId + "/books/" + bookId );
	}
}
