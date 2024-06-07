Feature: Simple Books API validations
@Regression 
Scenario: Verify if API functionality is working
		Given GetStatus API
		When user calls "GetStatusAPI" with "GET" http request
		Then the API call got success with status code 200
    And status in response body is OK
@Regression @OrderBook   
Scenario Outline: Get the list of the books filtering with type and select a book
    Given GetBooks API with type of "<type>"
    When user calls "GetBooksAPI" with "GET" http request
    Then the API call got success with status code 200
    And one book is selected from the list
  
Examples: 
      | type        | 
      | fiction     |
      | non-fiction |
      
@Regression   
Scenario: Verify if book is in stock
   	Given GetSingleBook API with bookID
   	When user calls "GetSingleBookAPI" with "GET" http request
   	Then the API call got success with status code 200
   	And verify the book is in stock 
   	
@Regression @OrderBook	
Scenario: Verify if order book functionalty is working
    Given SubmitOrder payload "<customerName>" and bookId
    When user calls "SubmitOrderAPI" with "POST" http request
    Then the API call got success with status code 201
    And created in response body is true
    And verify order checking bookId and "<customerName>" using "GetOrderAPI"
    
     Examples: 
      | customerName | 
      | Ursula Queen |
      | Lily Dorsay  |
 
@Regression    
Scenario: Verify if DeleteOrder API functionality is working
   	Given DeleteOrder API with bookID
   	When user calls "DeleteOrderAPI" with "DELETE" http request
   	Then the API call got success with status code 204
