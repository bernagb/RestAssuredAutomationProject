package stepDefinitions;

import static io.restassured.RestAssured.given;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Assert;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.common.mapper.TypeRef;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.GetList;
import utils.APIResources;
import utils.ScenarioContext;
import utils.Utils;


public class StepDefiniton extends Utils {
	RequestSpecification request;
	Response response;
	Response responseOrder;
	static String orderId;
	
	@Given("GetStatus API")
	public void get_status_api() throws FileNotFoundException {
	    
		request = given().spec(requestSpecification());
		
	}
	@When("user calls {string} with {string} http request")
	public void user_calls_get_status_api_with_get_http_request(String resource,String httpMethod) {
		
		APIResources apiResource = APIResources.valueOf(resource);
	  
		if(httpMethod.equalsIgnoreCase("POST"))
		{
			response = request.when().post(apiResource.getResource());
		}
		else if(httpMethod.equalsIgnoreCase("GET"))
		{
			response = request.when().get(apiResource.getResource());
		}
		else if(httpMethod.equalsIgnoreCase("DELETE"))
		{
			response = request.when().delete(apiResource.getResource());
		}
	}
	@Then("the API call got success with status code {int}")
	public void the_api_call_got_success_with_status_code(Integer statuscode) {
	    
		Assert.assertEquals(statuscode.intValue(), response.getStatusCode());
	}
	@Then("status in response body is OK")
	public void status_in_response_body_is_ok() {
	  
		String response1 = response.asString();
		JsonPath js= new JsonPath(response1);
		Assert.assertEquals(js.get("status"), "OK");
	}
	
	@Given("GetBooks API with type of {string}")
	public void get_books_api_with_type_of_non_fiction(String type) throws FileNotFoundException {
		
		request = given().spec(requestSpecification()).queryParam("type", type);
		
	}
	
	@Then("one book is selected from the list")
	public void one_book_is_selected_from_the_list() {
	    
		List<GetList>books = response.as(new TypeRef<List<GetList>>() {});
		int bookId = 0;
		for(int i=0; i<books.size() ;i++)
		{
			if(books.get(i).isAvailable()==true)
			{
				bookId = books.get(i).getId();
				ScenarioContext.getInstance().setBookId(bookId);
				break;
			}
		}
	}
	
	@Given("GetSingleBook API with bookID")
	public void get_single_book_api_with_book_id() throws FileNotFoundException {
		
		int bookId = ScenarioContext.getInstance().getBookId();
		request = given().spec(requestSpecification()).pathParam("bookId", bookId);
		
	}

	@Then("verify the book is in stock")
	public void verify_the_book_is_in_stock() {
	    
		String singleBook = response.asString();
		JsonPath js3= new JsonPath(singleBook);
		int stock = js3.getInt("current-stock");
		Assert.assertTrue(stock > 0);
	}
	
	@Given("SubmitOrder payload {string} and bookId")
	public void submit_order_payload_and_book_id(String customerName) throws FileNotFoundException {
		
		request = given().spec(requestSpecAuth()).header("Content-Type","application/json").
				body(orderPayload(customerName));
	}

	/*@Then("the call got success with status code {int}")
	public void the_call_got_success_with_status_code(Integer int1) {
	    
		Assert.assertEquals(response.getStatusCode(), 201);
	}*/
	@Then("created in response body is true")
	public void created_in_response_body_is_true() {
	    
		String order = response.asString();
		JsonPath js = new JsonPath(order);
		orderId = js.getString("orderId");
		Assert.assertEquals(true, js.getBoolean("created"));
	}
	@Then("verify order checking bookId and {string} using {string}")
	public void verify_order_checking_book_id_and_using_get_order_api(String customerName,String resource) throws FileNotFoundException {
		
		APIResources apiResource = APIResources.valueOf(resource);
		String responseSeeOrder = given().spec(requestSpecAuth()).pathParam("orderId", orderId).
				when().get(apiResource.getResource()).then().assertThat().statusCode(200).extract().response().asString();
						
		JsonPath js= new JsonPath(responseSeeOrder);
		int bookId = ScenarioContext.getInstance().getBookId();
		Assert.assertEquals(bookId, js.getInt("bookId"));
		Assert.assertEquals(customerName, js.getString("customerName"));
	}
	@Given("DeleteOrder API with bookID")
	public void delete_order_api_with_book_id() throws FileNotFoundException {
	   
		request = given().spec(requestSpecAuth()).pathParam("orderId", orderId);
	}
	/*@Then("the request got success with status code {int}")
	public void the_request_got_success_with_status_code(Integer int1) {
	    
		Assert.assertEquals(response.getStatusCode(), 204);
	}*/
	
}
