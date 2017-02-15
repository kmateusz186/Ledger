package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import database.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CreateUserController implements Initializable {
	@FXML
	private TextField textFieldLogin;
	@FXML
	private PasswordField passwordFieldPassword;
	@FXML
	private PasswordField passwordFieldRepeatPassword;
	@FXML
	private TextField textFieldName;
	@FXML
	private TextField textFieldSurname;
	@FXML
	private ChoiceBox<String> choiceBoxVAT;
	@FXML
	private ChoiceBox<String> choiceBoxWayTax;
	@FXML
	private ChoiceBox<String> choiceBoxPeriod;
	@FXML
	private TextField textFieldNip;
	@FXML
	private TextField textFieldNameCompany;
	@FXML
	private TextField textFieldAddress;
	@FXML
	private AnchorPane anchorPaneEditor;
	@FXML
	private GridPane gridPaneEditor;
	@FXML
	private Button btnBackToLogIn;
	@FXML
	private Button btnCreateUser;
	@FXML
	private Text textError;
	
	private static final String CONN_STR = "jdbc:h2:"+ System.getProperty("user.dir") + "/db/ledgerdatabase;";
	

	@FXML
	public void handleButtonAction(ActionEvent event) throws IOException {
		Connection conn;
		Stage stage = null;
		Parent root;
		if (event.getSource() == btnCreateUser) {
			
			if(!textFieldLogin.getText().isEmpty() && !passwordFieldPassword.getText().equals("") && !passwordFieldRepeatPassword.getText().equals("") && !textFieldName.getText().isEmpty() && !textFieldSurname.getText().isEmpty() && choiceBoxVAT.getValue()!=null && choiceBoxWayTax.getValue()!=null && choiceBoxPeriod.getValue()!=null && !textFieldNip.getText().isEmpty() && !textFieldNameCompany.getText().isEmpty() && !textFieldAddress.getText().isEmpty()) {
				if(passwordFieldPassword.getText().equals(passwordFieldRepeatPassword.getText())) {
					System.out.println(passwordFieldPassword.getText() + " " + passwordFieldRepeatPassword.getText());
																			
					String login = textFieldLogin.getText().toString();
					String password = passwordFieldPassword.getText();
					String name = textFieldName.getText().toLowerCase();
					String surname = textFieldSurname.getText().toLowerCase();
					String vat = choiceBoxVAT.getValue().toString();
					String wayTax = choiceBoxWayTax.getValue().toString();
					String period = choiceBoxPeriod.getValue().toString();
					String nip = textFieldNip.getText().toString();
					String nameCompany = textFieldNameCompany.getText().toString();
					String address = textFieldAddress.getText().toString();
					conn = openConnection(CONN_STR);
					
					if(!ifUserExists(conn, login)) {
						if(addUser(conn, login, password, name, surname, vat, wayTax, period, nip, nameCompany, address)) {
							stage = (Stage) anchorPaneEditor.getScene().getWindow();
							root = FXMLLoader.load(getClass().getResource("WelcomeWindowFXML.fxml"));
							Scene scene = new Scene(root);
							scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
							stage.setScene(scene);
							stage.setResizable(false);
							stage.show();
						} else {
							textError.setText("Wyst¹pi³ b³¹d, nie dodano u¿ytkownika");
						}
					} else {
						textError.setText("U¿ytkownik z podanym loginem ju¿ istnieje!");
					}
					
					closeConnection(conn);
				}
				else{
					textError.setText("Has³a nie s¹ takie same!");
				}
			} else {
				textError.setText("Pola nie s¹ wype³nione!");
			}
		} else if (event.getSource() == btnBackToLogIn) {
			stage = (Stage) anchorPaneEditor.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("WelcomeWindowFXML.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		}
	}
	
	private Boolean ifUserExists(Connection conn, String login) {
		Boolean exist = false;
		try {
			String query = String.format("select count(*) from uzytkownik where login = '%s'", login);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				if(rs.getInt(1)==1) {
					exist = true;
				}
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d sprawdzenia czy u¿ytkownik istnieje: " + ex);
		}
		return exist;
	}
	
	private Boolean addUser(Connection conn, String login, String password, String name, String surname, String vat, String wayTax, String period, String nip, String nameCompany, String address) {
		Boolean result;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		try {
			String query = String.format("insert into uzytkownik(login,haslo, imie, nazwisko, id_sposob_opodatkowania, id_okres_rozliczania, vat, data_stworzenia_uzytkownika, nip, adres_firmy, nazwa_firmy) values('%s','%s','%s','%s','%d','%d','%s','%s','%s','%s','%s')", login, password, name, surname, getWayTaxId(conn, wayTax), getPeriodId(conn, period), vat, dateFormat.format(date), nip, address, nameCompany); 
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
	
	private int getWayTaxId(Connection conn, String wayTaxName) {
		int wayTaxId = 0;
		try {
			String query = String.format("select id_sposob_opodatkowania from sposob_opodatkowania where nazwa = '%s'", wayTaxName);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				wayTaxId = rs.getInt(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych o sposobach opodatkowania: " + ex);
		}
		return wayTaxId;
	}
	
	private int getPeriodId(Connection conn, String periodName) {
		int periodId = 0;
		try {
			String query = String.format("select id_okres_rozliczania from okres_rozliczania where nazwa = '%s'", periodName);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				periodId = rs.getInt(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych o okresach rozliczenia: " + ex);
		}
		return periodId;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Connection conn;
		 textFieldNip.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
	                textFieldNip.setText(newValue.replaceAll("[^\\d]", ""));
	            }	
			} 
		});
		choiceBoxVAT.setItems(FXCollections.observableArrayList("tak", "nie"));
		conn = openConnection(CONN_STR);
		choiceBoxWayTax.setItems(FXCollections.observableArrayList(getWayTaxList(conn)));
		choiceBoxPeriod.setItems(FXCollections.observableArrayList(getPeriodList(conn)));
		closeConnection(conn);	
	}
	
	private ArrayList<String> getWayTaxList(Connection conn) {
		ArrayList<String> wayTaxList = new ArrayList<>();
		try {
			String query = "select nazwa from sposob_opodatkowania";
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				wayTaxList.add(rs.getString(1));
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych o sposobach opodatkowania: " + ex);
		}
		return wayTaxList;
	}
	
	private ArrayList<String> getPeriodList(Connection conn) {
		ArrayList<String> wayPeriodList = new ArrayList<>();
		try {
			String query = "select nazwa from okres_rozliczania";
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				wayPeriodList.add(rs.getString(1));
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych o okresach rozliczania: " + ex);
		}
		return wayPeriodList;
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
