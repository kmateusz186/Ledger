package model;

public class Document {
	private String number;
	private String date;
	private Double netAmount;
	private Double grossAmount;
	private Double vatAmount;
	private String description;
	private int documentNumberLedger;
	private String nameContractor;
	private String addressContractor;
	
	public Document(String number, String date, String nameContractor) {
		super();
		this.number = number;
		this.date = date;
		this.nameContractor = nameContractor;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Double getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(Double netAmount) {
		this.netAmount = netAmount;
	}
	public Double getGrossAmount() {
		return grossAmount;
	}
	public void setGrossAmount(Double grossAmount) {
		this.grossAmount = grossAmount;
	}
	public Double getVatAmount() {
		return vatAmount;
	}
	public void setVatAmount(Double vatAmount) {
		this.vatAmount = vatAmount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getDocumentNumberLedger() {
		return documentNumberLedger;
	}
	public void setDocumentNumberLedger(int documentNumberLedger) {
		this.documentNumberLedger = documentNumberLedger;
	}
	public String getNameContractor() {
		return nameContractor;
	}
	public void setNameContractor(String nameContractor) {
		this.nameContractor = nameContractor;
	}
	public String getAddressContractor() {
		return addressContractor;
	}
	public void setAddressContractor(String addressContractor) {
		this.addressContractor = addressContractor;
	}
	
	
}
