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
	private TextField textFieldNameLedger;
	@FXML
	private TextField textFieldYear;
	@FXML
	private Text textError;
	@FXML
	private ListView<String> listViewLedgers;
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
			if(!textFieldNameLedger.getText().isEmpty() && !textFieldYear.getText().isEmpty()) {
				String connStr = "jdbc:h2:~/db/ledgerdatabase;";
				conn = openConnection(connStr);
				String year = textFieldYear.getText().toString();
				String name = textFieldNameLedger.getText().toString();
				if(!ifLedgerExists(conn, year)) {
					if(addLedger(conn, name, year)) {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("NewLedgerFXML.fxml"));
			            stage = (Stage) anchorPaneEditor.getScene().getWindow();
			            stage.setScene(new Scene(loader.load()));
			            NewLedgerController newLedgerController = loader.<NewLedgerController>getController();
			            newLedgerController.initData(id_uzytkownik);
			            stage.setResizable(false);
			            stage.show();
					} else {
						textError.setText("Wyst¹pi³ b³¹d, nie stworzono ksiêgi");
					}
				} else {
					textError.setText("Ksiêga z podanym rokiem ju¿ istnieje!");
				}
				closeConnection(conn);
				
			} else {
				textError.setText("Pola nie s¹ wype³nione!");
			}
		} else if(event.getSource()==btnMonths) {
			if(listViewLedgers.getSelectionModel().getSelectedItem()!=null) {
				String year = listViewLedgers.getSelectionModel().getSelectedItem();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("NewMonthLedgerFXML.fxml"));
	            stage = (Stage) anchorPaneEditor.getScene().getWindow();
	            stage.setScene(new Scene(loader.load()));
	            NewMonthLedgerController newMonthLedgerController = loader.<NewMonthLedgerController>getController();
	            newMonthLedgerController.initData(id_uzytkownik, year);
	            stage.setResizable(false);
	            stage.show();
			} else {
				textError.setText("Nie wybrano ksiêgi!");
			}
		} else if(event.getSource()==btnMainMenu) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenuWindowFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            MainMenuWindowController mainMenuWindowController = loader.<MainMenuWindowController>getController();
            mainMenuWindowController.initData(id_uzytkownik);
            stage.setResizable(false);
            stage.show();
		}
	}
	
	private Boolean ifLedgerExists(Connection conn, String year) {
		Boolean exist = false;
		try {
			String query = String.format("select count(*) from rok where year(data) = '%s'", year);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				if(rs.getInt(1)==1) {
					exist = true;
				}
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d sprawdzenia czy ksiêga istnieje: " + ex);
		}
		return exist;
	}
	
	private Boolean addLedger(Connection conn, String name, String year) {
		Boolean result;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date d = new Date();
		try {
			d = dateFormat.parse(year + "-01-01");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			String query = String.format("insert into rok(id_uzytkownik,nazwa,data) values('%d','%s','%s')", this.id_uzytkownik, name, dateFormat.format(d)); 
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
	
	public void initData(int id_uzytkownik) {
		Connection conn;
		this.id_uzytkownik = id_uzytkownik;
		String connStr = "jdbc:h2:~/db/ledgerdatabase;";
		conn = openConnection(connStr);
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
			System.out.println("B³¹d pobierania danych o ksiêgach: " + ex);
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
