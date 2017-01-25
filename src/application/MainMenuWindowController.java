package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import database.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.User;

public class MainMenuWindowController implements Initializable {
	
	private int id_uzytkownik;
	@FXML
	private Text textWelcome;
	@FXML
	private Text textCompanyData;
	@FXML
	private Button btnLedger;
	@FXML
	private Button btnShowData;
	@FXML
	private Button btnLogOut;
	@FXML
	private AnchorPane anchorPaneEditor;
	
	@FXML
	public void handleButtonAction(ActionEvent event) throws IOException {
		Parent root;
		Stage stage = null;
		if(event.getSource()==btnLedger) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("NewLedgerFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            NewLedgerController newLedgerController = loader.<NewLedgerController>getController();
            newLedgerController.initData(id_uzytkownik);
            stage.setResizable(false);
            stage.show();
		} else if(event.getSource()==btnShowData) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ShowDataMenuFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            ShowDataMenuController showDataMenuController = loader.<ShowDataMenuController>getController();
            showDataMenuController.initData(id_uzytkownik);
            stage.setResizable(false);
            stage.show();
		} else if (event.getSource()==btnLogOut) {
			stage = (Stage) anchorPaneEditor.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("WelcomeWindowFXML.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		}
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
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void initData(int id_uzytkownik) {
		this.id_uzytkownik = id_uzytkownik;
		
		Connection conn;
		User user;
		String connStr = "jdbc:h2:~/db/ledgerdatabase;";
		conn = openConnection(connStr);
		user = getUser(conn, id_uzytkownik);
		closeConnection(conn);
		String name = user.getName();
		textWelcome.setText("Witaj "+ name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase() + ", ");
		textCompanyData.setText("NIP: " + user.getNip() + " \n" +  user.getNameCompany());
	}
	
	private User getUser(Connection conn, int id) {
		User user = new User();
		try {
			String query = String.format("select imie, nip, nazwa_firmy, adres_firmy from uzytkownik where id_uzytkownik = '%d'", id);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				user.setName(rs.getString(1));
				user.setNip(rs.getString(2));
				user.setNameCompany(rs.getString(3));
				user.setAddress(rs.getString(4));
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych o sposobach opodatkowania: " + ex);
		}
		return user;
	}
}
