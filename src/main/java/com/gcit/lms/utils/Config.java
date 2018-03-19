package com.gcit.lms.utils;


import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gcit.lms.dao.*;


@Configuration
//@EnableTransactionManagement
public class Config {

	Logger logger = LoggerFactory.getLogger(Config.class);

	public final String driver = "com.mysql.cj.jdbc.Driver";
	public final String url = "jdbc:mysql://localhost/library?useSSL=false";
	public final String username = "root";
	public final String password = "admin";
	
//	ConnectionUtil connUtil = new ConnectionUtil();

	@Bean
	public BasicDataSource dataSource() {
		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(driver);
		bds.setUrl(url);
		bds.setUsername(username);
		bds.setPassword(password);
		return bds;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}
	
	@Bean
	public AuthorDAO authorDAO()  {
		return new AuthorDAO();
	}
	
	@Bean
	public PublisherDAO pubDAO()  {
		return new PublisherDAO();
	}
	
	@Bean
	public BorrowerDAO borrowerDAO() {
		return new BorrowerDAO();
	}
	
	@Bean
	public BranchDAO branchDAO() {
		return new BranchDAO();
	}
	
	@Bean
	public GenreDAO genreDAO() {
		return new GenreDAO();
	}
	
	@Bean
	public CopyDAO copyDAO() {
		return new CopyDAO();
	}
	
	@Bean
	public LoanDAO loanDAO() {
		return new LoanDAO();
	}
	
	@Bean
	public BookDAO bookDAO() {
		return new BookDAO();
	}


}
