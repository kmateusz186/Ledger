package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class NewMonthLedgerController implements Initializable {
	
	private HashMap<String, Integer> monthsMap;
	private int id_uzytkownik;
	private String year;
	@FXML
	private Button btnLogOut;
	@FXML
	private Button btnMainMenu;
	@FXML
	private Button btnNewMonth;
	@FXML
	private Button btnNewDocuments;
	@FXML
	private AnchorPane anchorPaneEditor;
	@FXML
	private ChoiceBox<String> choiceBoxMonths;
	@FXML
	private Text textError;
	@FXML
	private Text textNewMonth;
	@FXML
	private ListView<String> listViewMonths;
	@FXML
	public void handleButtonAction(ActionEvent event) throws IOException {
		Parent root;
		Stage stage = null;
		Connection conn;
		if(event.getSource()==btnLogOut) {
			stage = (Stage) anchorPaneEditor.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("WelcomeWindowFXML.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		} else if(event.getSource()==btnNewMonth) {
			if(choiceBoxMonths.getValue()!=null) {
				String connStr = "jdbc:h2:~/db/ledgerdatabase;";
				conn = openConnection(connStr);
				String month = choiceBoxMonths.getValue().toString();
				int month_number = monthsMap.get(month);
				if(addNewMonth(conn, month_number)) {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("NewMonthLedgerFXML.fxml"));
		            stage = (Stage) anchorPaneEditor.getScene().getWindow();
		            stage.setScene(new Scene(loader.load()));
		            NewMonthLedgerController newMonthLedgerController = loader.<NewMonthLedgerController>getController();
		            newMonthLedgerController.initData(id_uzytkownik, year);
		            stage.setResizable(false);
		            stage.show();
				} else {
					textError.setText("Wyst¹pi³ b³¹d, nie dodano miesi¹ca");
				}
				closeConnection(conn);
			} else {
				textError.setText("Pola nie s¹ wype³nione!");
			}
		} else if(event.getSource()==btnNewDocuments) {
			if(listViewMonths.getSelectionModel().getSelectedItem()!=null) {
				String month = listViewMonths.getSelectionModel().getSelectedItem();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("NewDocumentsFXML.fxml"));
	            stage = (Stage) anchorPaneEditor.getScene().getWindow();
	            stage.setScene(new Scene(loader.load()));
	            NewDocumentsController newDocumentsController = loader.<NewDocumentsController>getController();
	            newDocumentsController.initData(id_uzytkownik, year, month);
	            stage.setResizable(false);
	            stage.show();
			} else {
				textError.setText("Nie wybrano miesi¹ca!");
			}
		} else if(event.getSource()==btnMainMenu) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("NewLedgerFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            NewLedgerController newLedgerController = loader.<NewLedgerController>getController();
            newLedgerController.initData(id_uzytkownik);
            stage.setResizable(false);
            stage.show();
		}
	}
	
	private Boolean addNewMonth(Connection conn, int month) {
		Boolean result;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date d = new Date();
		try {
			d = dateFormat.parse(year + "-" + month + "-01");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			String query = String.format("insert into miesiac(id_rok,data) values('%d','%s')", getIdLedger(conn, year), dateFormat.format(d)); 
			Statement stm = conn.createStatement();
			int count = stm.executeUpdate(query);
			System.out.println("Liczba dodanych rekordów " + count);
			result = true;
		} catch (SQLException e) {
			System.out.println("B³¹d przy przetwarzaniu danych: " + e);
			result = false;
		}
		return result;
	}
	
	public void initData(int id_uzytkownik, String year) {
		this.id_uzytkownik = id_uzytkownik;
		this.year = year;
		Connection conn;
		choiceBoxMonths.setItems(FXCollections.observableArrayList("styczeñ", "luty", "marzec", "kwiecieñ", "maj", "czerwiec", "lipiec", "sierpieñ", "wrzesieñ", "paŸdziernik", "listopad", "grudzieñ"));
		String connStr = "jdbc:h2:~/db/ledgerdatabase;";
		conn = openConnection(connStr);
		listViewMonths.setItems(FXCollections.observableArrayList(getMonths(conn)));
		closeConnection(conn);
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
		textNewMonth.setText("Tworzenie nowego miesi¹ca ksiêgi na rok " + year);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
	
	private int getIdLedger(Connection conn, String year) {
		int id = 0;
		try {
			String query = String.format("select id_rok from rok where year(data) = '%s'", year);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				id = rs.getInt(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania id ksiêgi: " + ex);
		}
		return id;
	}
	
	private ArrayList<String> getMonths(Connection conn) {
		ArrayList<String> months = new ArrayList<>();
		try {
			String query = String.format("select monthname(miesiac.data) from miesiac, rok where miesiac.id_rok = rok.id_rok and year(rok.data) = '%s'", year);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			String month_name;
			while(rs.next()) {
				switch (rs.getString(1)) {
					case "January":
						month_name = "styczeñ";
						break;
					case "February":
						month_name = "luty";
						break;
					case "March":
						month_name = "marzec";
						break;
					case "April":
						month_name = "kwiecieñ";
						break;
					case "May":
						month_name = "maj";
						break;
					case "June":
						month_name = "czerwiec";
						break;
					case "July":
						month_name = "lipiec";
						break;
					case "August":
						month_name = "sierpieñ";
						break;
					case "September":
						month_name = "wrzesieñ";
						break;
					case "October":
						month_name = "paŸdziernik";
						break;
					case "November":
						month_name = "listopad";
						break;
					case "December":
						month_name = "grudzieñ";
						break;
					default: month_name = "invalid";
						break;
				}
				months.add(month_name);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych o miesiacach: " + ex);
		}
		return months;
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
