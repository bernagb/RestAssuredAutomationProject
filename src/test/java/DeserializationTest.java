import static io.restassured.RestAssured.given;

import java.util.List;

import org.junit.Assert;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import pojo.GetList;
import pojo.OrderBook;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class DeserializationTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		RestAssured.baseURI= "https://simple-books-api.glitch.me";
		//check the status of the API
				//status code 200
				//body --> status = OK
		RequestSpecification req = new RequestSpecBuilder().setBaseUri("https://simple-books-api.glitch.me").build();
		
		RequestSpecification request = given().spec(req);
		Response response = request.when().get("/status").then().log().all().assertThat().
				statusCode(200).body("status", equalTo("OK")).extract().response();
	
			
		
		//get list of books
		RequestSpecification requestList = given().spec(req).queryParam("type", "non-fiction");
		//List<GetList> books = requestList.when().get("/books").then().assertThat().statusCode(200).extract().as(new TypeRef<List<GetList>>() {});
		Response responseList = requestList.when().get("/books").then().extract().response();
		List<GetList>books = responseList.as(new TypeRef<List<GetList>>() {});
		/*List<GetList> books = given()
                .log().all()
                .queryParam("type", "non-fiction")
                .when()
                .get("/books")
                .then()
                .log().all().assertThat()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<GetList>>() {});*/
		
		int bookId = 0;
		for(int i=0; i<books.size() ;i++)
		{
			if(books.get(i).isAvailable()==true)
			{
				bookId = books.get(i).getId();
				break;
			}
		}
		
		//get the book info to check whether it is in stock
		/*RequestSpecification requestbook = given().spec(req).pathParam("bookId", bookId);
		Response responsebook = requestbook.when().get("/books/{bookId}").then().log().all().assertThat()
				.statusCode(200).extract().response();*/
		String response4 = given().log().all().pathParam("bookId", bookId).when().get("/books/{bookId}").then().log().all().assertThat()
				.statusCode(200).extract().response().asString();
		JsonPath js3= new JsonPath(response4);
		int stock = js3.getInt("current-stock");
		Assert.assertTrue(stock > 0);
		
		// order book
		String customerName = "Ursula Queen";
		OrderBook b = new OrderBook();
		b.setBookId(bookId);
		b.setCustomerName(customerName);
		String token = "c24570ff18d7e23906f4fe677a513e8640598fe88047e93fc8662784270c2db0";
		
		String response2 = given().log().all().header("Authorization","Bearer " +token).header("Content-Type","application/json").
				body(b).when().post("/orders").then().log().all().assertThat().statusCode(201).body("created", equalTo(true)).
				extract().response().asString();
				
			JsonPath js1= new JsonPath(response2);
			String orderId = js1.getString("orderId");

				
		//get order, check if bookId and customerName are correct
				
		String response3 = given().log().all().header("Authorization","Bearer " +token).pathParam("orderId", orderId).
				when().get("/orders/{orderId}").then().log().all().assertThat().statusCode(200).extract().response().asString();
						
		JsonPath js2= new JsonPath(response3);
		Assert.assertEquals(bookId, js2.getInt("bookId"));
		Assert.assertEquals(customerName, js2.getString("customerName"));
		
		//delete order
		given().log().all().header("Authorization","Bearer " +token).pathParam("orderId", orderId).when().delete("/orders/{orderId}")
		.then().assertThat().statusCode(204);

	}

}
