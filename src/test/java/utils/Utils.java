package utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import utils.ScenarioContext;
import io.restassured.specification.RequestSpecification;
import pojo.OrderBook;

public class Utils {
	
	public static RequestSpecification req;
	public static RequestSpecification req2;
	
	public RequestSpecification requestSpecification() throws FileNotFoundException {
		
		if(req == null)
		{
		
		PrintStream log = new PrintStream(new FileOutputStream("APIlogging.txt"));
		
		req = new RequestSpecBuilder().setBaseUri("https://simple-books-api.glitch.me").addFilter(RequestLoggingFilter.logRequestTo(log))
				.addFilter(ResponseLoggingFilter.logResponseTo(log)).build();
		
		return req;
		}
		return req;
	}
	public RequestSpecification requestSpecAuth() throws FileNotFoundException {
		String token = "e6819e36de35b0ad219d45801bc6fc2c41e2479d72a5b719dc4f5f53fc545343";
		if(req2 == null)
		{
		
		PrintStream log = new PrintStream(new FileOutputStream("APIlogging.txt"));
		
		req2 = new RequestSpecBuilder().setBaseUri("https://simple-books-api.glitch.me").addHeader("Authorization","Bearer " +token).
				addFilter(RequestLoggingFilter.logRequestTo(log))
				.addFilter(ResponseLoggingFilter.logResponseTo(log)).build();
		
		return req2;
		}
		return req2;
	}
	
	
	public OrderBook orderPayload(String customerName) {
		OrderBook b = new OrderBook();
		int bookId = ScenarioContext.getInstance().getBookId();
		b.setBookId(bookId);
		b.setCustomerName(customerName);
		return b;
	}
	

}
