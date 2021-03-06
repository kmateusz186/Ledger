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
import java.util.ResourceBundle;

import database.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class NewLedgerController implements Initializable {

	private int id_uzytkownik;
	@FXML
	private Button btnLogOut;
	@FXML
	private Button btnMainMenu;
	@FXML
	private Button btnNewLedger;
	@FXML
	private Button btnMonths;
	@FXML
	private AnchorPane anchorPaneEditor;
	@FXML
	private TextField textFieldYear;
	@FXML
	private Text textError;
	@FXML
	private ListView<String> listViewLedgers;
	
	private static final String CONN_STR = "jdbc:h2:"+ System.getProperty("user.dir") + "/db/ledgerdatabase;";
	
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
		} else if(event.getSource()==btnNewLedger) {
			if(!textFieldYear.getText().isEmpty()) {
				conn = openConnection(CONN_STR);
				String year = textFieldYear.getText().toString();
				if(!ifLedgerExists(conn, year)) {
					if(addLedger(conn, year)) {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("NewLedgerFXML.fxml"));
			            stage = (Stage) anchorPaneEditor.getScene().getWindow();
			            Scene scene = new Scene(loader.load());
						scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			            stage.setScene(scene);
			            NewLedgerController newLedgerController = loader.<NewLedgerController>getController();
			            newLedgerController.initData(id_uzytkownik);
			            stage.setResizable(false);
			            stage.show();
					} else {
						textError.setText("Wyst�pi� b��d, nie stworzono ksi�gi");
					}
				} else {
					textError.setText("Ksi�ga z podanym rokiem ju� istnieje!");
				}
				closeConnection(conn);
				
			} else {
				textError.setText("Pola nie s� wype�nione!");
			}
		} else if(event.getSource()==btnMonths) {
			if(listViewLedgers.getSelectionModel().getSelectedItem()!=null) {
				String year = listViewLedgers.getSelectionModel().getSelectedItem();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("NewMonthLedgerFXML.fxml"));
	            stage = (Stage) anchorPaneEditor.getScene().getWindow();
	            Scene scene = new Scene(loader.load());
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	            stage.setScene(scene);
	            NewMonthLedgerController newMonthLedgerController = loader.<NewMonthLedgerController>getController();
	            newMonthLedgerController.initData(id_uzytkownik, year);
	            stage.setResizable(false);
	            stage.show();
			} else {
				textError.setText("Nie wybrano ksi�gi!");
			}
		} else if(event.getSource()==btnMainMenu) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenuWindowFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            Scene scene = new Scene(loader.load());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            MainMenuWindowController mainMenuWindowController = loader.<MainMenuWindowController>getController();
            mainMenuWindowController.initData(id_uzytkownik);
            stage.setResizable(false);
            stage.show();
		}
	}
	
	private Boolean ifLedgerExists(Connection conn, String year) {
		Boolean exist = false;
		try {
			String query = String.format("select count(*) from rok, uzytkownik "
					+ "where uzytkownik.id_uzytkownik = rok.id_uzytkownik "
					+ "and uzytkownik.id_uzytkownik = '%d' "
					+ "and year(data) = '%s'", id_uzytkownik, year);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				if(rs.getInt(1)==1) {
					exist = true;
				}
			}
		} catch(SQLException ex) {
			System.out.println("B��d sprawdzenia czy ksi�ga istnieje: " + ex);
		}
		return exist;
	}
	
	private Boolean addLedger(Connection conn, String year) {
		Boolean result;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date d = new Date();
		try {
			d = dateFormat.parse(year + "-01-01");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			String query = String.format("insert into rok(id_uzytkownik,data) values('%d','%s')", this.id_uzytkownik, dateFormat.format(d)); 
			Statement stm = conn.createStatement();
			int count = stm.executeUpdate(query);
			System.out.println("Liczba dodanych rekord�w " + count);
			result = true;
		} catch (SQLException e) {
			System.out.println("B��d przy przetwarzaniu danych: " + e);
			result = false;
		}
		return result;
	}
	
	public void initData(int id_uzytkownik) {
		Connection conn;
		this.id_uzytkownik = id_uzytkownik;
		conn = openConnection(CONN_STR);
		listViewLedgers.setItems(FXCollections.observableArrayList(getLedgers(conn)));
		closeConnection(conn);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		int maxLength = 4;
		textFieldYear.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				if (textFieldYear.getText().length() > maxLength) {
	                String s = textFieldYear.getText().substring(0, maxLength);
	                textFieldYear.setText(s);
	            }
				if (!newValue.matches("\\d*")) {
	                textFieldYear.setText(newValue.replaceAll("[^\\d]", ""));
	            }	
			} 
		});	
	}
	
	private ArrayList<String> getLedgers(Connection conn) {
		ArrayList<String> ledgers = new ArrayList<>();
		try {
			String query = String.format("select year(data) from rok where id_uzytkownik = '%d'", this.id_uzytkownik);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				ledgers.add(rs.getString(1));
			}
		} catch(SQLException ex) {
			System.out.println("B��d pobierania danych o ksi�gach: " + ex);
		}
		return ledgers;
	}
	
	private Connection openConnection(String connStr) {
		String user = "test";
		String pass = "test";
		Connection conn = DBConnection.getConnection(connStr, user, pass);
		if (conn == null) {
			System.exit(-1);
		}
		System.out.println("Po��czenie z serwerem H2 otwarte!");
		return conn;
	}
	
	private Boolean closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("B��d przy zamykaniu po��czenia: " + e);
			System.exit(-1);
		}
		System.out.println("Po��cznie zosta�o zamkni�te"); 
		return true;
	}
}
