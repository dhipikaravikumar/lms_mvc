package com.gcit.lms.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gcit.lms.entity.*;

public class LoanDAO {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Loan> getAllLoans() {
		List<Map<String, Object>> list1 = jdbcTemplate.queryForList("select * from tbl_book_loans");
		List<Loan> loanList = new ArrayList<Loan>();
		for (Map<String, Object> li : list1) {
			Loan loan = new Loan();
			loan.setCardNo((Integer) li.get("cardNo"));
			loan.setBranchId((Integer) li.get("branchId"));
			loan.setBookId((Integer) li.get("bookId"));
			loan.setDateOut((Timestamp) li.get("dateOut"));
			loan.setDueDate((Timestamp) li.get("dueDate"));
			loan.setDateIn((Timestamp) li.get("dateIn"));

			/* Find the borrower with the cardNo of the loan */
			List<Map<String, Object>> borrowerList = jdbcTemplate
					.queryForList("select * from tbl_borrower where cardNo = ?", new Object[] { loan.getCardNo() });
			loan.setBorrowerName((String) borrowerList.get(0).get("name"));

			/* Find the book title with the bookId of the loan */
			List<Map<String, Object>> bookList = jdbcTemplate.queryForList("select * from tbl_book where bookId = ?",
					new Object[] { loan.getBookId() });
			loan.setBookName((String) bookList.get(0).get("title"));

			/* Find the branch with the branchId of the loan */
			List<Map<String, Object>> branchList = jdbcTemplate.queryForList(
					"select * from tbl_library_branch where branchId = ?", new Object[] { loan.getBranchId() });
			loan.setBranchName((String) branchList.get(0).get("branchName"));

			loanList.add(loan);
		}
		return loanList;
	}

	public Loan getLoan(Integer cardNo, Integer branchId, Integer bookId) {
		List<Map<String, Object>> list1 = jdbcTemplate.queryForList(
				"select * from tbl_book_loans where cardNo = ? && branchId = ? && bookId = ? ",
				new Object[] { cardNo, branchId, bookId});
		if (list1.isEmpty())
			return null;
		Loan loan = new Loan();
		loan.setCardNo(cardNo);
		loan.setBranchId(branchId);
		loan.setBookId(bookId);
		loan.setDateOut((Timestamp) list1.get(0).get("dateOut"));
		loan.setDueDate((Timestamp) list1.get(0).get("dueDate"));
		
		/* Find the borrower name with the cardNo of the loan */
		List<Map<String, Object>> borrowerList = jdbcTemplate
				.queryForList("select * from tbl_borrower where cardNo = ?", new Object[] { loan.getCardNo() });
		loan.setBorrowerName((String) borrowerList.get(0).get("name"));

		/* Find the book title with the bookId of the loan */
		List<Map<String, Object>> bookList = jdbcTemplate.queryForList("select * from tbl_book where bookId = ?",
				new Object[] { loan.getBookId() });
		loan.setBookName((String) bookList.get(0).get("title"));

		/* Find the branch name with the branchId of the loan */
		List<Map<String, Object>> branchList = jdbcTemplate.queryForList(
				"select * from tbl_library_branch where branchId = ?", new Object[] { loan.getBranchId() });
		loan.setBranchName((String) branchList.get(0).get("branchName"));
		return loan;
	}

	public List<Loan> getPendingLoansByCardNo(Integer id) {
		List<Map<String, Object>> list1 = jdbcTemplate.queryForList(
				"select * from tbl_book_loans where cardNo = ?",
				new Object[] { id });
		List<Loan> loanList = new ArrayList<Loan>();
		for (Map<String, Object> li : list1) {
			Loan loan = new Loan();
			loan.setCardNo((Integer) li.get("cardNo"));
			loan.setBranchId((Integer) li.get("branchId"));
			loan.setBookId((Integer) li.get("bookId"));
			loan.setDateOut((Timestamp) li.get("dateOut"));
			loan.setDueDate((Timestamp) li.get("dueDate"));
			loan.setDateIn((Timestamp) li.get("dateIn"));

			/* Find the borrower name with the cardNo of the loan */
			List<Map<String, Object>> borrowerList = jdbcTemplate
					.queryForList("select * from tbl_borrower where cardNo = ?", new Object[] { loan.getCardNo() });
			loan.setBorrowerName((String) borrowerList.get(0).get("name"));

			/* Find the book title with the bookId of the loan */
			List<Map<String, Object>> bookList = jdbcTemplate.queryForList("select * from tbl_book where bookId = ?",
					new Object[] { loan.getBookId() });
			loan.setBookName((String) bookList.get(0).get("title"));

			/* Find the branch name with the branchId of the loan */
			List<Map<String, Object>> branchList = jdbcTemplate.queryForList(
					"select * from tbl_library_branch where branchId = ?", new Object[] { loan.getBranchId() });
			loan.setBranchName((String) branchList.get(0).get("branchName"));

			loanList.add(loan);
		}
		return loanList;
	}
	
	public boolean checkLoanStatus (Integer cardNo, Integer branchId, Integer bookId) {
		List<Map<String, Object>> list1 = jdbcTemplate.queryForList("select * from tbl_book_loans where bookId = ? && branchId = ? && cardNo = ?", new Object[] { bookId, branchId, cardNo });
		return list1.isEmpty();
	}
	
	public int borrowBook(Integer cardNo, Integer branchId, Integer bookId) throws Exception {
		Date date = new Date();
        
		Timestamp dateOut = new Timestamp(date.getTime());
		Timestamp dueDate = new Timestamp(date.getTime() + 604800000);
		
		int i = jdbcTemplate.update(
				"insert into tbl_book_loans (bookId, branchId, cardNo, dateOut, dueDate) values (?,?,?,?,?)",
				new Object[] { bookId, branchId, cardNo, dateOut, dueDate });
		return i;
	}
	
	public int returnBook(Integer cardNo, Integer branchId, Integer bookId) throws Exception {
		Date date = new Date();
		Timestamp dateIn = new Timestamp(date.getTime());
		
		int i = jdbcTemplate.update(
				"update tbl_book_loans set dateIn = ? where cardNo = ? && branchId = ? && bookId = ?",
				new Object[] { dateIn, cardNo, branchId, bookId });
		return i;
	}

	public int updateLoan(Integer cardNo, Integer branchId, Integer bookId, Timestamp dateIn, Timestamp dueDate) {
		int i = jdbcTemplate.update("update tbl_book_loans set dueDate = ?, dateIn = ?  where bookId = ? && branchId = ? && cardNo = ?",
				new Object[] { dueDate, dateIn, bookId, branchId, cardNo });
		return i;
	}
}
