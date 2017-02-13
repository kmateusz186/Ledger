package application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.YearTable;

public class LedgersVatController {

	private int id_uzytkownik;
	@FXML
	private Button btnLogOut;
	@FXML
	private Button btnShowChosenLedger;
	@FXML
	private Button btnShowChosenLedgerVat;
	@FXML
	private Button btnMainMenu;
	@FXML
	private Text textError;
	@FXML
	private Text textErrorVat;
	@FXML
	private TableView<YearTable> tableViewLedgers;
	@FXML
	private TableView<YearTable> tableViewLedgersVat;
	@FXML
	private TableColumn tcYear;
	@FXML
	private TableColumn tcYearVat;
	@FXML
	private AnchorPane anchorPaneEditor;
	@FXML
	public void handleButtonAction(ActionEvent event) throws IOException {
		Parent root;
		Stage stage = null;
		if(event.getSource()==btnLogOut) {
			stage = (Stage) anchorPaneEditor.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("WelcomeWindowFXML.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		} else if(event.getSource()==btnShowChosenLedger) {
			if(tableViewLedgers.getSelectionModel().getSelectedItem()!=null) {
				int id_ledger = tableViewLedgers.getSelectionModel().getSelectedItem().getId();
				Connection conn;
				String connStr = "jdbc:h2:~/db/ledgerdatabase;";
				conn = openConnection(connStr);
				String ledgerName = getLedgerName(conn, id_ledger);
				closeConnection(conn);
				if(!ledgerName.equals("")) {
					if (Desktop.isDesktopSupported()) {
					    try {
					        File myFile = new File(System.getProperty("user.dir") + "/CreatedFiles/" + ledgerName);
					        Desktop.getDesktop().open(myFile);
					    } catch (IOException ex) {
					    	textError.setText("B³¹d odczytu pliku");
					    }
					}
				} else {
					textError.setText("B³¹d odczytu z bazy danych");
				}
			} else {
				textError.setText("Nie wybrano ksiêgi!");
			} 
		} else if(event.getSource()==btnShowChosenLedgerVat) {
			if(tableViewLedgersVat.getSelectionModel().getSelectedItem()!=null) {
				int id_ledger = tableViewLedgersVat.getSelectionModel().getSelectedItem().getId();
				Connection conn;
				String connStr = "jdbc:h2:~/db/ledgerdatabase;";
				conn = openConnection(connStr);
				String ledgerName = getLedgerName(conn, id_ledger);
				closeConnection(conn);
				if(!ledgerName.equals("")) {
					String[] parts = ledgerName.split("_");
					ledgerName = "VAT_" + parts[1] + "_" + parts[2];
					if (Desktop.isDesktopSupported()) {
					    try {
					        File myFile = new File(System.getProperty("user.dir") + "/CreatedFiles/" + ledgerName);
					        Desktop.getDesktop().open(myFile);
					    } catch (IOException ex) {
					    	textError.setText("B³¹d odczytu pliku");
					    }
					}
				} else {
					textErrorVat.setText("B³¹d odczytu z bazy danych");
				} 
			} else {
				textErrorVat.setText("Nie wybrano rejestru VAT!");
			} 
		} else if(event.getSource()==btnMainMenu) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ShowDataMenuFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            Scene scene = new Scene(loader.load());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            ShowDataMenuController showDataMenuController = loader.<ShowDataMenuController>getController();
            showDataMenuController.initData(id_uzytkownik);
            stage.setResizable(false);
            stage.show();
		}
	}
	
	public void initData(int id_uzytkownik) {
		this.id_uzytkownik = id_uzytkownik;
		Connection conn;
		String connStr = "jdbc:h2:~/db/ledgerdatabase;";
		tcYear.setCellValueFactory(new PropertyValueFactory<YearTable, String>("year"));
		tcYearVat.setCellValueFactory(new PropertyValueFactory<YearTable, String>("year"));
		conn = openConnection(connStr);
		tableViewLedgers.setItems(FXCollections.observableArrayList(getLedgers(conn)));
		if(ifVatUser(conn)) {
			tableViewLedgersVat.setItems(FXCollections.observableArrayList(getLedgers(conn)));
		}
		closeConnection(conn);
	}
	
	private String getLedgerName(Connection conn, int id_rok) {
		String name = "";
		try {
			String query = String.format("select rok.nazwa "
						+ "from rok " 
					+ "where rok.id_rok = '%d'", id_rok);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				name = rs.getString(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania nazwy ksiegi: " + ex);
		}
		return name;
	}
	
	private Boolean ifVatUser(Connection conn) {
		Boolean vat = false;
		try {
			String query = String.format("select vat from uzytkownik where id_uzytkownik = '%d'", id_uzytkownik);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				if(rs.getString(1).equals("tak")) {
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
	
	private ArrayList<YearTable> getLedgers(Connection conn) {
		ArrayList<YearTable> ledgers = new ArrayList<>();
		try {
			String query = String.format("select id_rok, year(data) from rok where id_uzytkownik = '%d'", this.id_uzytkownik);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				ledgers.add(new YearTable(rs.getInt(1), rs.getString(2)));
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
