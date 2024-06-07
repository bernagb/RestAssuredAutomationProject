

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
	RequestSpecification requestList;
	Response responseList;
	RequestSpecification requestbook;
	Response responsebook;
	RequestSpecification requestorder;
	Response responseOrder;
	String token = "c24570ff18d7e23906f4fe677a513e8640598fe88047e93fc8662784270c2db0";
	String orderId;
	
	@Given("GetStatus API")
	public void get_status_api() throws FileNotFoundException {
	    
		request = given().spec(requestSpecification());
		
	}
	@When("user calls {string} with GET http request")
	public void user_calls_get_status_api_with_get_http_request(String resource) {
		
		APIResources apiResource = APIResources.valueOf(resource);
	  
		response = request.when().get(apiResource.getResource()).then().extract().response();
	}
	@Then("the API call got success with status code {int}")
	public void the_api_call_got_success_with_status_code(Integer int1) {
	    
		Assert.assertEquals(response.getStatusCode(), 200);
	}
	@Then("status in response body is OK")
	public void status_in_response_body_is_ok() {
	  
		String response1 = response.asString();
		JsonPath js= new JsonPath(response1);
		Assert.assertEquals(js.get("status"), "OK");
	}
	
	@Given("GetBooks API with type of {string}")
	public void get_books_api_with_type_of_non_fiction(String type) throws FileNotFoundException {
		
		requestList = given().spec(requestSpecification()).queryParam("type", type);
		
	}
	@When("user calls GetBooksAPI with GET http request")
	public void user_calls_get_books_api_with_get_http_request() {
		
		responseList = requestList.when().get("/books").then().extract().response();
		List<GetList>books = responseList.as(new TypeRef<List<GetList>>() {});
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
	@Then("the GetBooksAPI call got success with status code {int}")
	public void the_get_books_api_call_got_success_with_status_code(Integer int1) {
	    
		Assert.assertEquals(responseList.getStatusCode(), 200);
	}
	
	@Given("GetSingleBook API with bookID")
	public void get_single_book_api_with_book_id() throws FileNotFoundException {
		
		int bookId = ScenarioContext.getInstance().getBookId();
		requestbook = given().spec(requestSpecification()).pathParam("bookId", bookId);
		
	}
	/*@When("user calls GetSingleBookAPI with GET http request")
	public void user_calls_get_single_book_api_with_get_http_request() {
	    
		
		responsebook = requestbook.when().get("/books/{bookId}").then().extract().response();
	}*/
	@Then("the GetSingleBookAPI call got success with status code {int}")
	public void the_get_single_book_api_call_got_success_with_status_code(Integer int1) {
	    
		Assert.assertEquals(responsebook.getStatusCode(), 200);
	}
	@Then("verify the book is in stock")
	public void verify_the_book_is_in_stock() {
	    
		String singleBook = responsebook.asString();
		JsonPath js3= new JsonPath(singleBook);
		int stock = js3.getInt("current-stock");
		Assert.assertTrue(stock > 0);
	}
	
	@Given("SubmitOrder payload {string} and bookId")
	public void submit_order_payload_and_book_id(String customerName) throws FileNotFoundException {
		
		requestorder = given().spec(requestSpecification()).header("Authorization","Bearer " +token).header("Content-Type","application/json").
				body(orderPayload(customerName));
	}
	@When("user calls SubmitOrderAPI with POST http request")
	public void user_calls_submit_order_api_with_post_http_request() {
	    
		responseOrder = requestorder.when().post("/orders").then().extract().response();
	}
	@Then("the call got success with status code {int}")
	public void the_call_got_success_with_status_code(Integer int1) {
	    
		Assert.assertEquals(responseOrder.getStatusCode(), 201);
	}
	@Then("created in response body is true")
	public void created_in_response_body_is_true() {
	    
		String order = responseOrder.asString();
		JsonPath js = new JsonPath(order);
		orderId = js.getString("orderId");
		Assert.assertEquals(true, js.getBoolean("created"));
	}
	@Then("verify order checking bookId and {string} using GetOrderAPI")
	public void verify_order_checking_book_id_and_using_get_order_api(String customerName) throws FileNotFoundException {
	    
		String responseSeeOrder = given().spec(requestSpecification()).header("Authorization","Bearer " +token).pathParam("orderId", orderId).
				when().get("/orders/{orderId}").then().assertThat().statusCode(200).extract().response().asString();
						
		JsonPath js= new JsonPath(responseSeeOrder);
		int bookId = ScenarioContext.getInstance().getBookId();
		Assert.assertEquals(bookId, js.getInt("bookId"));
		Assert.assertEquals(customerName, js.getString("customerName"));
	}

	
}
