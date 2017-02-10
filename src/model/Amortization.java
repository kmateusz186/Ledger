package model;

public class Amortization {
	private String allAmount;
	private String yearAmount;
	private String monthAmount;
	
	public Amortization(String allAmount, String yearAmount, String monthAmount) {
		super();
		this.allAmount = allAmount;
		this.yearAmount = yearAmount;
		this.monthAmount = monthAmount;
	}
	
	public String getAllAmount() {
		return allAmount;
	}
	public void setAllAmount(String allAmount) {
		this.allAmount = allAmount;
	}
	public String getYearAmount() {
		return yearAmount;
	}
	public void setYearAmount(String yearAmount) {
		this.yearAmount = yearAmount;
	}
	public String getMonthAmount() {
		return monthAmount;
	}
	public void setMonthAmount(String monthAmount) {
		this.monthAmount = monthAmount;
	}
}
