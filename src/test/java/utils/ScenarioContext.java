package utils;

public class ScenarioContext {
	
	private static ScenarioContext instance;
    private int bookId;
    

    private ScenarioContext() {}

    public static ScenarioContext getInstance() {
        if (instance == null) {
            instance = new ScenarioContext();
        }
        return instance;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
  

}
