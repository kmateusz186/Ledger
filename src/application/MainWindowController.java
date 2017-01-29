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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainWindowController implements Initializable {

	@FXML
	private Button btnCreateUser;
	@FXML
	private Button btnLogIn;
	@FXML
	private TextField textFieldLogin;
	@FXML
	private Text textError;
	@FXML 
	private PasswordField passwordFieldPassword;
	@FXML
	private GridPane gridPaneEditor;
	
	 @FXML
	    public void handleButtonAction(ActionEvent event) throws IOException {
		 	Stage stage = null;
	    	Parent root;
		 	if(event.getSource()==btnCreateUser) {
		 		stage = (Stage) gridPaneEditor.getScene().getWindow();
	        	root = FXMLLoader.load(getClass().getResource("CreateUserFXML.fxml"));
	        	Scene scene = new Scene(root);
	        	scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	            stage.setScene(scene);
	            stage.setResizable(false);
	            stage.show();
		 	}
		 	else if(event.getSource()==btnLogIn) {
		 		Connection conn;
		 		int id_uzytkownik;
		 		if(!textFieldLogin.getText().isEmpty() && !passwordFieldPassword.getText().equals("")) {
		 			String login = textFieldLogin.getText().toString();
			 		String password = passwordFieldPassword.getText();
			 		String connStr = "jdbc:h2:~/db/ledgerdatabase;";
			 		conn = openConnection(connStr);
			 		if(logIn(conn, login, password)) {
			 			id_uzytkownik = getIdUser(conn, login);
			 			//stage = (Stage) gridPaneEditor.getScene().getWindow();
			        	//root = FXMLLoader.load(getClass().getResource("MainMenuWindowFXML.fxml"));
			        	//Scene scene = new Scene(root);
			        	//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			            //stage.setScene(scene);
			            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenuWindowFXML.fxml"));
			            stage = (Stage) gridPaneEditor.getScene().getWindow();
			            stage.setScene(new Scene(loader.load()));
			            MainMenuWindowController mainMenuWindowController = loader.<MainMenuWindowController>getController();
			            mainMenuWindowController.initData(id_uzytkownik);
			            stage.setResizable(false);
			            stage.show();
			 		} else {
			 			textError.setText("B³êdne dane logowania");
			 		}
			 		closeConnection(conn);
		 		} else {
		 			textError.setText("Pola nie s¹ wype³nione");
		 		}
		 		
		 	}
	 }
	 
	 private Boolean logIn(Connection conn, String login, String password) {
			Boolean logInValue = false;
			try {
				String query = String.format("select count(*) from uzytkownik where login = '%s' and haslo = '%s'", login, password);
				Statement stm = conn.createStatement();
				ResultSet rs = stm.executeQuery(query);
				while(rs.next()) {
					if(rs.getInt(1)==1) {
						logInValue = true;
					}
				}
			} catch(SQLException ex) {
				System.out.println("B³¹d logowania uzytkownika: " + ex);
			}
			return logInValue;
		}
	 
	 private int getIdUser(Connection conn, String login) {
			int id_uzytkownik = 0;
			try {
				String query = String.format("select id_uzytkownik from uzytkownik where login = '%s'", login);
				Statement stm = conn.createStatement();
				ResultSet rs = stm.executeQuery(query);
				while(rs.next()) {
					id_uzytkownik = rs.getInt(1);
				}
			} catch(SQLException ex) {
				System.out.println("B³¹d logowania uzytkownika: " + ex);
			}
			return id_uzytkownik;
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
			Connection conn;
			String connStr = "jdbc:h2:~/db/ledgerdatabase;"
					
							+ "INIT=create table if not exists sposob_opodatkowania (id_sposob_opodatkowania integer auto_increment primary key, " 
							+ "nazwa varchar(255) not null)\\;"
							
							+ "merge into sposob_opodatkowania key(nazwa) values(1, 'zasady ogólne')\\;"
							+ "merge into sposob_opodatkowania key(nazwa) values(2, 'podatek liniowy')\\;"
							
							+ "create table if not exists okres_rozliczania (id_okres_rozliczania integer auto_increment primary key, "
							+ "nazwa varchar(255) not null)\\;"
							
							+ "merge into okres_rozliczania key(nazwa) values(1, 'miesiêczny')\\;"
							+ "merge into okres_rozliczania key(nazwa) values(2, 'kwartalny')\\;"
							
							+ "create table if not exists uzytkownik (id_uzytkownik integer auto_increment primary key, "
							+ "login varchar(255) not null unique, "
							+ "haslo varchar(255) not null, "
							+ "imie varchar(255) not null, "
							+ "nazwisko varchar(255) not null, "
							+ "id_sposob_opodatkowania integer, "
							+ "id_okres_rozliczania integer, "
							+ "vat varchar(3) not null, "
							+ "data_stworzenia_uzytkownika date not null, "
							+ "nip varchar(10) not null, "
							+ "adres_firmy varchar(255) not null, "
							+ "nazwa_firmy varchar(255) not null, "
							+ "CONSTRAINT FK_uzytkownik_sposob FOREIGN KEY (id_sposob_opodatkowania) REFERENCES sposob_opodatkowania (id_sposob_opodatkowania), "
							+ "CONSTRAINT FK_uzytkownik_okres FOREIGN KEY (id_okres_rozliczania) REFERENCES okres_rozliczania (id_okres_rozliczania)"
							+ ")\\;"
							
							+ "create table if not exists typ_dokumentu (id_typ_dokumentu integer auto_increment primary key, "
							+ "nazwa varchar(255) not null)\\;"
							
							+ "merge into typ_dokumentu key(nazwa) values(1, 'przychody')\\;"
							+ "merge into typ_dokumentu key(nazwa) values(2, 'zakupy/wydatki')\\;"
							+ "merge into typ_dokumentu key(nazwa) values(3, 'wynagrodzenia')\\;"
							+ "merge into typ_dokumentu key(nazwa) values(4, 'zakup œrodków trwa³ych')\\;"
							+ "merge into typ_dokumentu key(nazwa) values(5, 'zakup wyposa¿enia')\\;"
							+ "merge into typ_dokumentu key(nazwa) values(6, 'import towarów i WNT')\\;"
							+ "merge into typ_dokumentu key(nazwa) values(7, 'import us³ug i WNT')\\;"
							+ "merge into typ_dokumentu key(nazwa) values(9, 'zakup towarów i materia³ów')\\;"
							+ "merge into typ_dokumentu key(nazwa) values(10, 'koszty uboczne zakupu')\\;"
							+ "merge into typ_dokumentu key(nazwa) values(11, 'pozosta³e wydatki/dowody wewnêtrzne')\\;"
							
							+ "create table if not exists rok (id_rok integer auto_increment primary key, "
							+ "id_uzytkownik integer, "
							+ "nazwa varchar(255) not null, "
							+ "data date not null, "
							+ "CONSTRAINT FK_rok_uzytkownik FOREIGN KEY (id_uzytkownik) REFERENCES uzytkownik (id_uzytkownik)"
							+ ")\\;"
							
							+ "create table if not exists miesiac (id_miesiac integer auto_increment primary key, "
							+ "id_rok integer, "
							+ "data date not null, "
							+ "suma_wartosc_sprz_towar decimal(10,2), "
							+ "suma_poz_przych decimal(10,2), "
							+ "suma_zak_towar decimal(10,2), "
							+ "suma_koszt_ub decimal(10,2), "
							+ "suma_wynagrodzen decimal(10,2), "
							+ "suma_wydatkow decimal(10,2), "
							+ "suma_koszt_bad_rozw decimal(10,2), "
							+ "podatek decimal(10,2), "
							+ "lp integer, "
							+ "CONSTRAINT FK_miesiac_rok FOREIGN KEY (id_rok) REFERENCES rok (id_rok)"
							+ ")\\;"
							
							+ "create table if not exists dokument_ksiegowy (id_dokument_ksiegowy integer auto_increment primary key, "
							+ "id_uzytkownik integer, "
							+ "id_typ_dokumentu integer, "
							+ "id_miesiac integer, "
							+ "numer varchar(255) not null, "
							+ "data date not null, "
							+ "kwota_netto decimal(10,2), "
							+ "kwota_brutto decimal(10,2) not null, "
							+ "opis varchar(255) not null, "
							+ "kwota_vat decimal(10,2), "
							+ "lp integer not null, "
							+ "nazwa_kontrahenta varchar(255) not null, "
							+ "adres_kontrahenta varchar(255) not null, "
							+ "CONSTRAINT FK_dokument_uzytkownik FOREIGN KEY (id_uzytkownik) REFERENCES uzytkownik (id_uzytkownik), "
							+ "CONSTRAINT FK_dokument_typ_dokumentu FOREIGN KEY (id_typ_dokumentu) REFERENCES typ_dokumentu (id_typ_dokumentu), "
							+ "CONSTRAINT FK_dokument_miesiac FOREIGN KEY (id_miesiac) REFERENCES miesiac (id_miesiac)"
							+ ")\\;"
							
							+ "create table if not exists miesiac_vat (id_miesiac_vat integer auto_increment primary key, "
							+ "id_miesiac integer, "
							+ "data date, "
							+ "suma_przychodow_brutto decimal(10,2), "
							+ "suma_kosztow_brutto decimal(10,2), "
							+ "podatek_vat_do_urzedu decimal(10,2), "
							+ "CONSTRAINT FK_miesiac_vat_miesiac FOREIGN KEY (id_miesiac) REFERENCES miesiac (id_miesiac)"
							+ ");";
		conn = openConnection(connStr);
		closeConnection(conn);	
		}

}
