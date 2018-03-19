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

import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.entity.Publisher;
/**
 * Handles requests for the publisher routes.
 */
@RestController
public class PublisherController {
	
	@Autowired
	PublisherDAO publisherDao;
	
	private static final Logger logger = LoggerFactory.getLogger(PublisherController.class);

	/* Read all publishers */
	@RequestMapping(value = "/publishers", method = RequestMethod.GET)
	public List<Publisher> getAllPublishers() {
		logger.info("Getting all publishers"); 
		return publisherDao.getAllPublishers();
		
	}
	
	/* Read one publisher by id */
	@RequestMapping(value = "/publishers/{pubId}", method = RequestMethod.GET)
	public Publisher getPublisherById(@PathVariable int pubId , HttpServletResponse response) throws IOException {
		logger.info("Getting publisher with id: " + pubId);
		Publisher pub = publisherDao.getPublisherById(pubId);
		if (pub == null) 
			response.sendError(404, "Invalid publisher id, publisher does not exist in the database.");
		return pub;
	}
	
	/* Save one publisher and returns the auto-generated id */
	@RequestMapping(value = "/publisher", method = RequestMethod.POST)
	public void savePublisherWithId(@RequestBody Publisher publisher, HttpServletResponse response) throws IOException {
		logger.info("Saving publisher with name: " + publisher.getPubName());
		try {
			int i = publisherDao.savePublisherWithId(publisher.getPubName(), publisher.getPubAddress(),
					publisher.getPubPhone());
			if (i != 0) {
				logger.info("Publisher saved with id: " + i);
				response.setHeader("location", "/publishers/" + i);
				response.setStatus(201);
			} 
		} catch (Exception e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");
		}
		
	}
	
	/* Delete one publisher with the given id */
	@RequestMapping(value = "/publishers/{pubId}", method = RequestMethod.DELETE)
	public void deletePublisher(@PathVariable int pubId, HttpServletResponse response) throws IOException {
		logger.info("Deleting publisher with id " + pubId);
		int i = publisherDao.deletePublisher(pubId);
		logger.info(i + " publisher deleted with id " + pubId);
		if ( i == 0 ) {
			response.sendError(404, "Invalid publisher id, publisher does not exist in the database.");
		}
		response.setStatus(204);
	}
	
	/* Update one publisher with the given id */
	@RequestMapping(value = "/publishers/{pubId}", method = RequestMethod.PUT)
	public void updatePublisher(@RequestBody Publisher publisher, @PathVariable int pubId, HttpServletResponse response) throws IOException {
		logger.info("Updating publisher (id: " + pubId + ") with new name: " + publisher.getPubName() + ", new address: " + publisher.getPubAddress() + ", new phone: " + publisher.getPubPhone());
		try {
			int i = publisherDao.updatePublisher(pubId, publisher.getPubName(), publisher.getPubAddress(),
					publisher.getPubPhone());
			logger.info(i + " publisher (id: " + pubId + ") updated with new name: " + publisher.getPubName()
					+ ", new address: " + publisher.getPubAddress() + ", new phone: " + publisher.getPubPhone());
			if (i == 0) {
				response.sendError(404, "Invalid publisher id, publisher does not exist in the database.");
			}
			response.setHeader("location", "/publishers/" + pubId);
		} catch (Exception e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");
		}
	}
	
}
