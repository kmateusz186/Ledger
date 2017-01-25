package application;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import components.ExcelCreation;
import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class NewDocumentsController {
	
	private int id_uzytkownik;
	private String year;
	private String month;
	private HashMap<String, Integer> monthsMap;
	@FXML
	private Button btnLogOut;
	@FXML
	private Button btnMainMenu;
	@FXML
	private Button btnNewDocument;
	@FXML
	private Button btnCreateExcel;
	@FXML
	private Text textNewDocument;
	@FXML
	private ChoiceBox<String> choiceBoxDocumentTypes;
	@FXML
	private TextField textFieldNumberDocument;
	@FXML
	private DatePicker datePickerDateDocument;
	@FXML
	private TextField textFieldNetAmount;
	@FXML
	private TextField textFieldGrossAmount;
	@FXML
	private TextField textFieldVatAmount;
	@FXML
	private TextArea textAreaDescription;
	@FXML
	private TextField textFieldNameContractor;
	@FXML
	private TextField textFieldAddressContractor;
	@FXML
	private ListView<String> listViewDocuments;
	@FXML 
	private Text textError;
	@FXML
	private AnchorPane anchorPaneEditor;
	@FXML
	public void handleButtonAction(ActionEvent event) throws IOException {
		Parent root;
		Stage stage = null;
		Connection conn;
		LocalDate ld;
		Calendar c;
		String numberDocument;
		String documentType;
		Date date;
		String grossAmount;
		String description;
		String nameContractor;
		String addressContractor;
		String netAmount = "";
		String vatAmount = "";
		if(event.getSource()==btnLogOut) {
			stage = (Stage) anchorPaneEditor.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("WelcomeWindowFXML.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
			
		} else if(event.getSource()==btnNewDocument) {
			String connStr = "jdbc:h2:~/db/ledgerdatabase;";
			conn = openConnection(connStr);
			if(ifVatUser(conn)) {
				if(!textFieldNumberDocument.getText().isEmpty() && choiceBoxDocumentTypes.getValue()!=null && datePickerDateDocument.getValue()!=null && !textFieldNetAmount.getText().isEmpty() && !textFieldVatAmount.getText().isEmpty() && !textFieldGrossAmount.getText().isEmpty() && !textAreaDescription.getText().isEmpty() && !textFieldNameContractor.getText().isEmpty() && !textFieldAddressContractor.getText().isEmpty()) {
					numberDocument = textFieldNumberDocument.getText().toString();
					documentType = choiceBoxDocumentTypes.getValue().toString();
					ld = datePickerDateDocument.getValue();
					c =  Calendar.getInstance();
					c.set(ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth());
					date = c.getTime();
					netAmount = textFieldNetAmount.getText().toString();
					vatAmount = textFieldVatAmount.getText().toString();
					grossAmount = textFieldGrossAmount.getText().toString();
					description = textAreaDescription.getText().toString();
					nameContractor = textFieldNameContractor.getText().toString();
					addressContractor = textFieldAddressContractor.getText().toString();
					
					if(addNewDocument(conn, numberDocument, documentType, date, netAmount, vatAmount, grossAmount, description, nameContractor, addressContractor, true)) {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("NewDocumentsFXML.fxml"));
			            stage = (Stage) anchorPaneEditor.getScene().getWindow();
			            stage.setScene(new Scene(loader.load()));
			            NewDocumentsController newDocumentsController = loader.<NewDocumentsController>getController();
			            newDocumentsController.initData(id_uzytkownik, year, month);
			            stage.setResizable(false);
			            stage.show();
					} else {
						textError.setText("Wyst¹pi³ b³¹d, nie dodano dokumentu");
					}
				} else {
					textError.setText("Pola nie s¹ wype³nione!");
				}
			} else {
				if(!textFieldNumberDocument.getText().isEmpty() && choiceBoxDocumentTypes.getValue()!=null && datePickerDateDocument.getValue()!=null && !textFieldGrossAmount.getText().isEmpty() && !textAreaDescription.getText().isEmpty() && !textFieldNameContractor.getText().isEmpty() && !textFieldAddressContractor.getText().isEmpty()) {
					numberDocument = textFieldNumberDocument.getText().toString();
					documentType = choiceBoxDocumentTypes.getValue().toString();
					ld = datePickerDateDocument.getValue();
					c =  Calendar.getInstance();
					c.set(ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth());
					date = c.getTime();
					grossAmount = textFieldGrossAmount.getText().toString();
					description = textAreaDescription.getText().toString();
					nameContractor = textFieldNameContractor.getText().toString();
					addressContractor = textFieldAddressContractor.getText().toString();
					
					if(addNewDocument(conn, numberDocument, documentType, date, netAmount, vatAmount, grossAmount, description, nameContractor, addressContractor, false)) {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("NewDocumentsFXML.fxml"));
			            stage = (Stage) anchorPaneEditor.getScene().getWindow();
			            stage.setScene(new Scene(loader.load()));
			            NewDocumentsController newDocumentsController = loader.<NewDocumentsController>getController();
			            newDocumentsController.initData(id_uzytkownik, year, month);
			            stage.setResizable(false);
			            stage.show();
					} else {
						textError.setText("Wyst¹pi³ b³¹d, nie dodano dokumentu");
					}
				} else {
					textError.setText("Pola nie s¹ wype³nione!");
				}
			}
			closeConnection(conn);
		} else if(event.getSource()==btnCreateExcel) {
			String connStr = "jdbc:h2:~/db/ledgerdatabase;";
			conn = openConnection(connStr);
			if(ifVatUser(conn)) {
				if(createExcel(conn, true)) {
					
				} else {
					textError.setText("Wyst¹pi³ b³¹d, nie utworzono pliku");
				}
			} else {	
				if(createExcel(conn, false)) {
					
				} else {
					textError.setText("Wyst¹pi³ b³¹d, nie utworzono pliku");
				}
			}
			
		} else if(event.getSource()==btnMainMenu) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("NewMonthLedgerFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            NewMonthLedgerController newMonthLedgerController = loader.<NewMonthLedgerController>getController();
            newMonthLedgerController.initData(id_uzytkownik, year);
            stage.setResizable(false);
            stage.show(); 
		}
	}
	
	private Boolean createExcel(Connection conn, Boolean vat) {
		Boolean value = false;
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<String> documentTypes = new ArrayList<>();
		int monthNumber = monthsMap.get(month);
		Double income_sum = 0.00;
		Double expense_sum = 0.00;
		if(vat) {
			try {
				String query = String.format("select typ_dokumentu.nazwa, dokument_ksiegowy.lp, dokument_ksiegowy.numer, "
						+ "dokument_ksiegowy.data, dokument_ksiegowy.nazwa_kontrahenta, dokument_ksiegowy.adres_kontrahenta, "
						+ "dokument_ksiegowy.opis, dokument_ksiegowy.kwota_brutto, dokument_ksiegowy.kwota_netto, dokument_ksiegowy.kwota_vat "
				+ "from dokument_ksiegowy, miesiac, uzytkownik, rok, typ_dokumentu " 
				+ "where dokument_ksiegowy.id_uzytkownik = uzytkownik.id_uzytkownik "
				+ "and dokument_ksiegowy.id_miesiac = miesiac.id_miesiac "
				+ "and miesiac.id_rok = rok.id_rok "
				+ "and dokument_ksiegowy.id_typ_dokumentu = typ_dokumentu.id_typ_dokumentu "
				+ "and uzytkownik.id_uzytkownik = '%d' "
				+ "and month(miesiac.data) = '%d' "
				+ "and year(rok.data) = '%s'", id_uzytkownik, monthNumber, year);
				Statement stm = conn.createStatement();
				ResultSet rs = stm.executeQuery(query);
				int i = 1;
				while(rs.next()) {
					map.put("lp" + i, "" + rs.getInt(2));
					map.put("date" + i, rs.getString(4));
					map.put("number" + i, rs.getString(3));
					map.put("nameC" + i, rs.getString(5));
					map.put("addressC" + i, rs.getString(6));
					map.put("description" + i, rs.getString(7));
					if(rs.getString(1) == "przychody") {
						map.put("income" + i, rs.getString(9).replaceAll(".", ","));
						map.put("incomeR" + i, "");
						map.put("incomeS" + i, rs.getString(9).replaceAll(".", ","));
						map.put("buyG" + i, "");
						map.put("expenseIn" + i, "");
						map.put("rewards" + i, "");
						map.put("expenseR" + i, "");
						map.put("expenseS" + i, "0,00");
						map.put("expenseResearchD" + i, "");
						map.put("expenseResearch" + i, "");
						map.put("comment" + i, "");
					} else if(rs.getString(1) == "zakupy/wydatki") {
						map.put("rewards" + i, rs.getString(9).replaceAll(".", ","));
						map.put("income" + i, "");
						map.put("incomeR" + i, "");
						map.put("incomeS" + i, "0,00");
						map.put("buyG" + i, "");
						map.put("expenseIn" + i, "");
						map.put("expenseR" + i, "");
						map.put("expenseS" + i, rs.getString(9).replaceAll(".", ","));
						map.put("expenseResearchD" + i, "");
						map.put("expenseResearch" + i, "");
						map.put("comment" + i, "");
					}
				i++;	 
				}
			} catch(SQLException ex) {
				System.out.println("B³¹d pobierania danych o dokumentach: " + ex);
			}
			 ExcelCreation create = new ExcelCreation(System.getenv("userprofile") + "/Desktop/AJO/template3.xlsx", 0, System.getenv("userprofile") + "/Desktop/AJO/out.xlsx", "Arkusz 1", map, "Arkusz 4");
		     create.doExcel();
		     value = true;
		} else {
			try {
				String query = String.format("select typ_dokumentu.nazwa, dokument_ksiegowy.lp, dokument_ksiegowy.numer, "
						+ "dokument_ksiegowy.data, dokument_ksiegowy.nazwa_kontrahenta, dokument_ksiegowy.adres_kontrahenta, "
						+ "dokument_ksiegowy.opis, dokument_ksiegowy.kwota_brutto "
				+ "from dokument_ksiegowy, miesiac, uzytkownik, rok, typ_dokumentu " 
				+ "where dokument_ksiegowy.id_uzytkownik = uzytkownik.id_uzytkownik "
				+ "and dokument_ksiegowy.id_miesiac = miesiac.id_miesiac "
				+ "and miesiac.id_rok = rok.id_rok "
				+ "and dokument_ksiegowy.id_typ_dokumentu = typ_dokumentu.id_typ_dokumentu "
				+ "and uzytkownik.id_uzytkownik = '%d' "
				+ "and month(miesiac.data) = '%d' "
				+ "and year(rok.data) = '%s'", id_uzytkownik, monthNumber, year);
				Statement stm = conn.createStatement();
				ResultSet rs = stm.executeQuery(query);
				int i = 1;
				while(rs.next()) {
					map.put("lp" + i, "" + rs.getInt(2));
					map.put("date" + i, rs.getString(4));
					map.put("number" + i, rs.getString(3));
					map.put("nameC" + i, rs.getString(5));
					map.put("addressC" + i, rs.getString(6));
					map.put("description" + i, rs.getString(7));
					
					if(rs.getString(1).equals("przychody")) {
						map.put("income" + i, rs.getString(8).replace('.', ','));
						map.put("incomeR" + i, "");
						map.put("incomeS" + i, rs.getString(8).replace('.', ','));
						map.put("buyG" + i, "");
						map.put("expenseIn" + i, "");
						map.put("rewards" + i, "");
						map.put("expenseR" + i, "");
						map.put("expenseS" + i, "0,00");
						map.put("expenseResearchD" + i, "");
						map.put("expenseResearch" + i, "");
						map.put("comment" + i, "");
						
						income_sum += rs.getDouble(8);
						
					} else if(rs.getString(1).equals("zakupy/wydatki")) {
						map.put("rewards" + i, rs.getString(8).replaceAll(".", ","));
						map.put("income" + i, "");
						map.put("incomeR" + i, "");
						map.put("incomeS" + i, "0,00");
						map.put("buyG" + i, "");
						map.put("expenseIn" + i, "");
						map.put("expenseR" + i, "");
						map.put("expenseS" + i, rs.getString(8).replaceAll(".", ","));
						map.put("expenseResearchD" + i, "");
						map.put("expenseResearch" + i, "");
						map.put("comment" + i, "");
						
						expense_sum += rs.getDouble(8);
					}
				i++;	 
				}
			} catch(SQLException ex) {
				System.out.println("B³¹d pobierania danych o dokumentach: " + ex);
			}
			map.put("sheetI", String.valueOf(income_sum).replace('.', ','));
			map.put("sheetIR", "0,00");
			map.put("sheetIS", String.valueOf(income_sum).replace('.', ','));
			
			map.put("sheetRewards", String.valueOf(expense_sum).replace('.', ','));
			map.put("sheetExpense", "0,00");
			map.put("sheetExpenseS", String.valueOf(expense_sum).replace('.', ','));
			
			ExcelCreation create = new ExcelCreation(System.getenv("userprofile") + "/Desktop/AJO/template3.xlsx", 0, System.getenv("userprofile") + "/Desktop/AJO/out.xlsx", month + " " + year, map, month + " " + year);
		    create.doExcel();
		    value = true;
		}
		
		return value;
	}
	
	private Boolean ifVatUser(Connection conn) {
		Boolean vat = false;
		try {
			String query = String.format("select vat from uzytkownik where id_uzytkownik = '%d'", id_uzytkownik);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				if(rs.getString(1)=="tak") {
					vat = true;
				} else {
					vat = false;
				}
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d sprawdzenia czy u¿ytkownik prowadzi rejestr vat: " + ex);
		}
		return vat;
	}
	
	private Boolean addNewDocument(Connection conn, String numberDocument, String documentType, Date date, String netAmount, String vatAmount, String grossAmount, String description, String nameContractor, String addressContractor, Boolean vat) {
		Boolean result;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println(dateFormat.format(date));
		int idDocumentType = getIdDocumentType(conn, documentType);
		int idMonth = getIdMonth(conn);
		int orderNumber = getDocumentOrderNumber(conn) + 1;
		BigDecimal netA;
		BigDecimal grossA;
		BigDecimal vatA;
		String strGrossAmount = grossAmount.replaceAll(",",".");
		System.out.println(strGrossAmount);
		grossA = new BigDecimal(strGrossAmount);
		System.out.println(grossA);
		if (vat) {
			String strNetAmount=netAmount.replaceAll(",",".");
			netA = new BigDecimal(strNetAmount);
			String strVatAmount = vatAmount.replaceAll(",",".");
			vatA = new BigDecimal(strVatAmount);
			try {
				String query = String.format("insert into dokument_ksiegowy(id_uzytkownik, id_typ_dokumentu, id_miesiac, numer, data, "
						+ "kwota_netto, kwota_brutto, opis, kwota_vat, lp, nazwa_kontrahenta, adres_kontrahenta) "
						+ "values('%d','%d','%d','%s','%s','%f','%f','%s','%f','%d','%s','%s')", 
						id_uzytkownik, idDocumentType, idMonth, numberDocument, dateFormat.format(date), netA, grossA, description, vatA, orderNumber, nameContractor, addressContractor); 
				Statement stm = conn.createStatement();
				int count = stm.executeUpdate(query);
				System.out.println("Liczba dodanych rekordów " + count);
				result = true;
			} catch (SQLException e) {
				System.out.println("B³¹d przy przetwarzaniu danych: " + e);
				result = false;
			}
		} else {
			try {
				String query = String.format("insert into dokument_ksiegowy(id_uzytkownik, id_typ_dokumentu, id_miesiac, numer, data, "
						+ "kwota_brutto, opis, lp, nazwa_kontrahenta, adres_kontrahenta) "
						+ "values('%d','%d','%d','%s','%s','%s','%s','%d','%s','%s')", 
						id_uzytkownik, idDocumentType, idMonth, numberDocument, dateFormat.format(date), grossA, description, orderNumber, nameContractor, addressContractor); 
				Statement stm = conn.createStatement();
				int count = stm.executeUpdate(query);
				System.out.println("Liczba dodanych rekordów " + count);
				result = true;
			} catch (SQLException e) {
				System.out.println("B³¹d przy przetwarzaniu danych: " + e);
				result = false;
			}
		}
		
		return result;
	}
	
	private int getIdDocumentType(Connection conn, String nazwa) {
		int id = 0;
		try {
			String query = String.format("select id_typ_dokumentu from typ_dokumentu where nazwa = '%s'", nazwa);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				id = rs.getInt(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania id typu_dokumentu: " + ex);
		}
		return id;
	}
	
	private int getDocumentOrderNumber(Connection conn) {
		int id = 0;
		int monthNumber = monthsMap.get(month);
		try {
			String query = String.format("select count(*) from dokument_ksiegowy, miesiac, uzytkownik, rok " 
					+ "where dokument_ksiegowy.id_uzytkownik = uzytkownik.id_uzytkownik "
					+ "and dokument_ksiegowy.id_miesiac = miesiac.id_miesiac "
					+ "and miesiac.id_rok = rok.id_rok "
					+ "and uzytkownik.id_uzytkownik = '%d' "
					+ "and month(miesiac.data) = '%d' "
					+ "and year(rok.data) = '%s'", id_uzytkownik, monthNumber, year);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				id = rs.getInt(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania id miesiaca: " + ex);
		}
		return id;
	}
	
	private int getIdMonth(Connection conn) {
		int id = 0;
		int monthNumber = monthsMap.get(month);
		try {
			String query = String.format("select miesiac.id_miesiac from miesiac, rok, uzytkownik where miesiac.id_rok = rok.id_rok "
					+ "and rok.id_uzytkownik = uzytkownik.id_uzytkownik "
					+ "and uzytkownik.id_uzytkownik = '%d' "
					+ "and year(rok.data) = '%s' "
					+ "and month(miesiac.data) = '%d'", id_uzytkownik, year, monthNumber);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				id = rs.getInt(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania id miesiaca: " + ex);
		}
		return id;
	}
	
	public void initData(int id_uzytkownik, String year, String month) {
		this.id_uzytkownik = id_uzytkownik;
		this.year = year;
		this.month = month;
		monthsMap = new LinkedHashMap<>();
		monthsMap.put("styczeñ", 1);
		monthsMap.put("luty", 2);
		monthsMap.put("marzec", 3);
		monthsMap.put("kwiecieñ", 4);
		monthsMap.put("maj", 5);
		monthsMap.put("czerwiec", 6);
		monthsMap.put("lipiec", 7);
		monthsMap.put("sierpieñ", 8);
		monthsMap.put("wrzesieñ", 9);
		monthsMap.put("paŸdziernik", 10);
		monthsMap.put("listopad", 11);
		monthsMap.put("grudzieñ", 12);
		int monthNumber = monthsMap.get(month);
		Connection conn;
		textNewDocument.setText("Dodawanie dokumentów na " + month + " " + year);
		String connStr = "jdbc:h2:~/db/ledgerdatabase;";
		conn = openConnection(connStr);
		choiceBoxDocumentTypes.setItems(FXCollections.observableArrayList(getDocumentTypes(conn)));
		listViewDocuments.setItems(FXCollections.observableArrayList(getDocuments(conn, monthNumber)));
		closeConnection(conn);
		setAmountFields();
		textFieldNetAmount.setDisable(true);
		textFieldVatAmount.setDisable(true);
	}
	
	private void setAmountFields() {
		DecimalFormat format = new DecimalFormat( "#.00" );

		textFieldNetAmount.setTextFormatter( new TextFormatter<>(c ->
		{
		    if ( c.getControlNewText().isEmpty() )
		    {
		        return c;
		    }

		    ParsePosition parsePosition = new ParsePosition( 0 );
		    Object object = format.parse( c.getControlNewText(), parsePosition );

		    if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
		    {
		        return null;
		    }
		    else
		    {
		        return c;
		    }
		}));
		textFieldGrossAmount.setTextFormatter( new TextFormatter<>(c ->
		{
		    if ( c.getControlNewText().isEmpty() )
		    {
		        return c;
		    }

		    ParsePosition parsePosition = new ParsePosition( 0 );
		    Object object = format.parse( c.getControlNewText(), parsePosition );

		    if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
		    {
		        return null;
		    }
		    else
		    {
		        return c;
		    }
		}));
		textFieldVatAmount.setTextFormatter( new TextFormatter<>(c ->
		{
		    if ( c.getControlNewText().isEmpty() )
		    {
		        return c;
		    }

		    ParsePosition parsePosition = new ParsePosition( 0 );
		    Object object = format.parse( c.getControlNewText(), parsePosition );

		    if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
		    {
		        return null;
		    }
		    else
		    {
		        return c;
		    }
		}));
	}
	
	private ArrayList<String> getDocuments(Connection conn, int month) {
		ArrayList<String> documents = new ArrayList<>();
		try {
			String query = String.format("select dokument_ksiegowy.numer, dokument_ksiegowy.data, dokument_ksiegowy.nazwa_kontrahenta from dokument_ksiegowy, miesiac, uzytkownik, rok " 
			+ "where dokument_ksiegowy.id_uzytkownik = uzytkownik.id_uzytkownik "
			+ "and dokument_ksiegowy.id_miesiac = miesiac.id_miesiac "
			+ "and miesiac.id_rok = rok.id_rok "
			+ "and uzytkownik.id_uzytkownik = '%d' "
			+ "and month(miesiac.data) = '%d' "
			+ "and year(rok.data) = '%s'", id_uzytkownik, month, year);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				documents.add(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3));
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych o dokumentach: " + ex);
		}
		return documents;
	}
	
	private ArrayList<String> getDocumentTypes(Connection conn) {
		ArrayList<String> documentTypes = new ArrayList<>();
		try {
			String query = "select nazwa from typ_dokumentu";
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				documentTypes.add(rs.getString(1));
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych o typach dokumentów: " + ex);
		}
		return documentTypes;
	}
	
	private Connection openConnection(String connStr) {
		String user = "test";
		String pass = "test";
		Connection conn = DBConnection.getConnection(connStr, user, pass);
		if (conn == null) {
			System.exit(-1);
		}
		System.out.println("Po³¹czenie z serwerem H2 otwarte!");
		return conn;
	}
	
	private Boolean closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("B³¹d przy zamykaniu po³¹czenia: " + e);
			System.exit(-1);
		}
		System.out.println("Po³¹cznie zosta³o zamkniête"); 
		return true;
	}
}
