package model;

public class YearTable {
	private int id;
	private String year;
	
	public YearTable(int id, String year) {
		super();
		this.setId(id);
		this.setYear(year);
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
