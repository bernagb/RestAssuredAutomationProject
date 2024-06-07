package utils;

public enum APIResources {
	
	GetStatusAPI("/status"),
	GetBooksAPI("/books"),
	GetSingleBookAPI("/books/{bookId}"),
	SubmitOrderAPI("/orders"),
	DeleteOrderAPI("/orders/{orderId}"),
	GetOrderAPI("/orders/{orderId}");
	
	private String resource;
	APIResources(String resource) {
		this.resource = resource;
	}
	public String getResource() {
		return resource;
	}
	

}
