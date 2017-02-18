package event;

public class Date {

	private int month;
	private int day;
	private int year;
	
	public Date(int time) {
		day = time%30;
		year = time/300;
		month = (time%300)/30;
	}
	
	public String getDateString() {
		return "Year " + year + ", Month " + month + ", Day " + day;
	}
}
