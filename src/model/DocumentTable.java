package model;

public class DocumentTable {
	private int id;
	private String number;
	private String grossAmount;
	private String nameContractor;
	private String date;
	private String documentType;
	private String description;
	private String netAmount;
	private String addressContractor;
	private String vatAmount;
	
	public DocumentTable(int id, String number, String grossAmount, String nameContractor, String date) {
		super();
		this.id = id;
		this.number = number;
		this.grossAmount = grossAmount;
		this.nameContractor = nameContractor;
		this.date = date;
	}
	
	public DocumentTable(int id, String number, String grossAmount, String nameContractor, String date, String documentType) {
		super();
		this.id = id;
		this.number = number;
		this.grossAmount = grossAmount;
		this.nameContractor = nameContractor;
		this.date = date;
		this.documentType = documentType;
	}
	
	public DocumentTable(int id, String number, String grossAmount, String nameContractor, String date, String netAmount, String description, String addressContractor) {
		super();
		this.id = id;
		this.number = number;
		this.grossAmount = grossAmount;
		this.nameContractor = nameContractor;
		this.date = date;
		this.netAmount = netAmount;
		this.description = description;
		this.addressContractor = addressContractor;
	}
	
	public DocumentTable(int id, String number, String grossAmount, String nameContractor, String date, String netAmount, String description, String addressContractor, String vatAmount, String documentType) {
		super();
		this.id = id;
		this.number = number;
		this.grossAmount = grossAmount;
		this.nameContractor = nameContractor;
		this.date = date;
		this.netAmount = netAmount;
		this.description = description;
		this.addressContractor = addressContractor;
		this.setVatAmount(vatAmount);
		this.documentType = documentType;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getGrossAmount() {
		return grossAmount;
	}

	public void setGrossAmount(String grossAmount) {
		this.grossAmount = grossAmount;
	}

	public String getNameContractor() {
		return nameContractor;
	}

	public void setNameContractor(String nameContractor) {
		this.nameContractor = nameContractor;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(String netAmount) {
		this.netAmount = netAmount;
	}

	public String getAddressContractor() {
		return addressContractor;
	}

	public void setAddressContractor(String addressContractor) {
		this.addressContractor = addressContractor;
	}

	public String getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(String vatAmount) {
		this.vatAmount = vatAmount;
	}
	
}
