import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;


public class BasicTests {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		RestAssured.baseURI= "https://simple-books-api.glitch.me";
		//check the status of the API
		//status code 200
		//body --> status = OK
		String response = given().log().all().when().get("/status").then().log().all().assertThat().statusCode(200).body("status", equalTo("OK")).
		extract().response().asString();
		
		JsonPath js= new JsonPath(response);
		String status = js.getString("status");
		System.out.println(status);
		
		//get list of fiction type of books
		String response1 = given().log().all().queryParam("type", "non-fiction").when().get("/books")
		.then().log().all().statusCode(200).extract().response().asString();
		
		System.out.println(response1);
		JsonPath js1= new JsonPath(response1);
		int size = js1.getInt("size()");
		int bookId = 0;
		for(int i=0; i< size ;i++)
		{
			if(js1.getBoolean("["+i+"].available") == true)
			{
				bookId = js1.getInt("["+i+"].id");
				break;
			}
		}
		
		
		
		//get a single book
		//use the book ID above
		String response2 = given().log().all().pathParam("bookId", bookId).when().get("/books/{bookId}").then().log().all().statusCode(200).
				extract().response().asString();
		
		System.out.println(response2);
		
		//get authentication token
		/*String token = given().log().all().body("{\r\n"
				+ "   \"clientName\": \"bgb\",\r\n"
				+ "   \"clientEmail\": \"bgb@example.com\"\r\n"
				+ "}").when().post("/api-clients/").then().log().all().extract().response().asString();
		
		System.out.println(token);*/
		String token = "c24570ff18d7e23906f4fe677a513e8640598fe88047e93fc8662784270c2db0";
		
		//order book
		String response3 = given().log().all().header("Authorization","Bearer " +token).header("Content-Type","application/json").
		body("{\r\n"
				+ "  \"bookId\": "+bookId+",\r\n"
				+ "  \"customerName\": \"John\"\r\n"
				+ "}").when().post("/orders").then().log().all().statusCode(201).extract().response().asString();
		
		JsonPath js2= new JsonPath(response3);
		String orderId = js2.getString("orderId");
		
		//see order
				String response4 = given().log().all().header("Authorization","Bearer " +token).pathParam("orderId", orderId).
				when().get("/orders/{orderId}").then().log().all().statusCode(200).extract().response().asString();
				
				System.out.println(response4);
	
		
		//update order name - patch
		given().log().all().header("Authorization","Bearer " +token).pathParam("orderId", orderId)
				.body("{\r\n"
						+ "  \"customerName\": \"Lily Johanson\"\r\n"
						+ "}").when().patch("/orders/{orderId}").then().log().all().statusCode(204);
		

		
		
		
		
	}

}
