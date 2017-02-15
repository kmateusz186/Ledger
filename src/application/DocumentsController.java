package application;

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
import model.DocumentTable;

public class DocumentsController {
	private int id_uzytkownik;
	@FXML
	private Button btnLogOut;
	@FXML
	private Button btnShowChosenDocument;
	@FXML
	private Button btnMainMenu;
	@FXML
	private Text textError;
	@FXML
	private TableView<DocumentTable> tableViewDocuments;
	@FXML
	private TableColumn tcNumber;
	@FXML
	private TableColumn tcMoney;
	@FXML
	private TableColumn tcName;
	@FXML
	private TableColumn tcDocumentType;
	@FXML
	private TableColumn tcDate;
	@FXML
	private AnchorPane anchorPaneEditor;
	
	private static final String CONN_STR = "jdbc:h2:"+ System.getProperty("user.dir") + "/db/ledgerdatabase;";
	
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
		} else if(event.getSource()==btnShowChosenDocument) {
			if(tableViewDocuments.getSelectionModel().getSelectedItem()!=null) {
				int id_document = tableViewDocuments.getSelectionModel().getSelectedItem().getId();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("DocumentDetailFXML.fxml"));
	            stage = (Stage) anchorPaneEditor.getScene().getWindow();
	            Scene scene = new Scene(loader.load());
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	            stage.setScene(scene);
	            DocumentDetailController documentDetailController = loader.<DocumentDetailController>getController();
	            documentDetailController.initData(id_uzytkownik, id_document);
	            stage.setResizable(false);
	            stage.show(); 
			} else {
				textError.setText("Nie wybrano dokumentu!");
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
		tcNumber.setCellValueFactory(new PropertyValueFactory<DocumentTable, String>("number"));
		tcMoney.setCellValueFactory(new PropertyValueFactory<DocumentTable, String>("grossAmount"));
		tcName.setCellValueFactory(new PropertyValueFactory<DocumentTable, String>("nameContractor"));
		tcDocumentType.setCellValueFactory(new PropertyValueFactory<DocumentTable, String>("documentType"));
		tcDate.setCellValueFactory(new PropertyValueFactory<DocumentTable, String>("date"));
		conn = openConnection(CONN_STR);
		tableViewDocuments.setItems(FXCollections.observableArrayList(getDocuments(conn)));
		closeConnection(conn);
	}
	
	private ArrayList<DocumentTable> getDocuments(Connection conn) {
		ArrayList<DocumentTable> documents = new ArrayList<>();
		try {
			String query = String.format("select dokument_ksiegowy.id_dokument_ksiegowy, dokument_ksiegowy.numer, dokument_ksiegowy.data, dokument_ksiegowy.kwota_brutto, kontrahent.nazwa, typ_dokumentu.nazwa "
			+ "from dokument_ksiegowy, typ_dokumentu, miesiac, uzytkownik, rok, kontrahent " 
			+ "where dokument_ksiegowy.id_uzytkownik = uzytkownik.id_uzytkownik "
			+ "and dokument_ksiegowy.id_typ_dokumentu = typ_dokumentu.id_typ_dokumentu "
			+ "and dokument_ksiegowy.id_kontrahent = kontrahent.id_kontrahent "
			+ "and uzytkownik.id_uzytkownik = '%d' "
			+ "and typ_dokumentu.nazwa != 'zakup œrodków trwa³ych' "
			+ "and typ_dokumentu.nazwa != 'zakup wyposa¿enia' "
			+ "group by typ_dokumentu.nazwa, dokument_ksiegowy.id_dokument_ksiegowy "
			+ "order by typ_dokumentu.nazwa", id_uzytkownik);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				documents.add(new DocumentTable(rs.getInt(1), rs.getString(2), rs.getString(4), rs.getString(5), rs.getString(3), rs.getString(6)));
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych o dokumentach: " + ex);
		}
		return documents;
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
