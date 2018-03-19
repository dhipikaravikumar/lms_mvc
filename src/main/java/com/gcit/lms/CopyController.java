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

import com.gcit.lms.dao.CopyDAO;
import com.gcit.lms.entity.Copy;
/**
 * Handles requests for the copy routes.
 */
@RestController
public class CopyController {
	
	@Autowired
	CopyDAO copyDao;
	
	private static final Logger logger = LoggerFactory.getLogger(CopyController.class);

	/* Read all copies */
	@RequestMapping(value = "/copies", method = RequestMethod.GET)
	public List<Copy> getAllCopys() {
		logger.info("Getting all copies"); 
		return copyDao.getAllCopies();
	}
	
	/* Read one copy by branchId and bookId */
	@RequestMapping(value = "/copies/{branchId}/{bookId}", method = RequestMethod.GET)
	
	public Copy getCopyById(@PathVariable int branchId, @PathVariable int bookId, HttpServletResponse response) throws IOException {
		logger.info("Getting copy with branchId: " + branchId + ", bookId: " + bookId);
		Copy copy = copyDao.getCopyById(branchId, bookId);
		if (copy == null) {
			response.sendError(404, "Invalid branch id and/or book id, copy entry does not exist in the database.");
		}
		return copy;
	}

	/* Update one copy with the given branchId and bookId */
	@RequestMapping(value = "/copies/{branchId}/{bookId}", method = RequestMethod.PUT)
	public void updateCopy(@RequestBody Copy copy, @PathVariable int branchId, @PathVariable int bookId, HttpServletResponse response) throws IOException {
		logger.info("Updating book (id: " + bookId + ") at branch (id: " + branchId + ") with " + copy.getNoCopies() + " copies");
		try {
			int i = copyDao.updateCopy(branchId, bookId, copy.getNoCopies());
			logger.info(i + " row with " + copy.getNoCopies() + " copies (bookId: " + bookId + ", branchId: " + branchId
					+ ") updated.");
			if (i == 0) {
				response.sendError(404, "Invalid branch id and/or book id, book/branch does not exist in the database.");
			}
			response.setHeader("location", "/copies/" + branchId + "/" + bookId);
		} catch (Exception e) {
			response.sendError(400, "Invalid request caused by invalid path parameters.");
		}
		
	}
	
}
