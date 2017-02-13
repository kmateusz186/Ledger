package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;

import database.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.DocumentTable;

public class EquipmentDetailController {
	
	private HashMap<Integer, String> monthsMap;
	private int id_uzytkownik;
	private int id_document;
	private DocumentTable documentTable = null;
	@FXML
	private Button btnLogOut;
	@FXML
	private Button btnGoToLedger;
	@FXML
	private Button btnMainMenu;
	@FXML
	private Text textNumber;
	@FXML
	private Text textDate;
	@FXML
	private Text textNameContractor;
	@FXML
	private Text textAddressContractor;
	@FXML
	private Text textNetAmount;
	@FXML
	private Text textGrossAmount;
	@FXML
	private Text textVatAmount;
	@FXML
	private Text textDocumentType;
	@FXML
	private Text textDescription;
	@FXML
	private Text textLedger;
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
		} else if(event.getSource()==btnGoToLedger) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("NewLedgerFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            Scene scene = new Scene(loader.load());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            NewLedgerController newLedgerController = loader.<NewLedgerController>getController();
            newLedgerController.initData(id_uzytkownik);
            stage.setResizable(false);
            stage.show();
		} else if(event.getSource()==btnMainMenu) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EquipmentFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            Scene scene = new Scene(loader.load());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            EquipmentController equipmentController = loader.<EquipmentController>getController();
            equipmentController.initData(id_uzytkownik);
            stage.setResizable(false);
            stage.show();
		}
	}
	
	public void initData(int id_uzytkownik, int id_document) {
		this.id_uzytkownik = id_uzytkownik;
		this.id_document = id_document;
		monthsMap = new LinkedHashMap<>();
		monthsMap.put(1, "stycze�");
		monthsMap.put(2, "luty");
		monthsMap.put(3, "marzec");
		monthsMap.put(4, "kwiecie�");
		monthsMap.put(5, "maj");
		monthsMap.put(6, "czerwiec");
		monthsMap.put(7, "lipiec");
		monthsMap.put(8, "sierpie�");
		monthsMap.put(9, "wrzesie�");
		monthsMap.put(10, "pa�dziernik");
		monthsMap.put(11, "listopad");
		monthsMap.put(12, "grudzie�");
		Connection conn;
		String connStr = "jdbc:h2:~/db/ledgerdatabase;";
		conn = openConnection(connStr);
		documentTable = getDocument(conn);
		if(documentTable!=null) {
			textNumber.setText(documentTable.getNumber());
			textDate.setText(documentTable.getDate());
			textNameContractor.setText(documentTable.getNameContractor());
			String[] parts = documentTable.getAddressContractor().split(", ");
			textAddressContractor.setText(parts[1] + '\n' + parts[0]);
			if(documentTable.getNetAmount()!=null) {
				textNetAmount.setText(documentTable.getNetAmount().replace('.', ',') + " z�");
			} else {
				textNetAmount.setText("-");
			}
			textGrossAmount.setText(documentTable.getGrossAmount().replace('.', ',') + " z�");
			textDescription.setText(documentTable.getDescription());			
		}
		closeConnection(conn);
		String year = documentTable.getDate().substring(0,4);
		int month = Integer.valueOf(documentTable.getDate().substring(5,7));
		String monthName = monthsMap.get(month);
		textLedger.setText("KPiR - wpisano do ksi�gi" + " na " + monthName + " " + year + " jako zakupy/wydatki");
	}
	
	private DocumentTable getDocument(Connection conn) {
		DocumentTable document = null;
		Double netAmount = 0.0;
		Double vatAmount = 0.0;
		try {
			String query = String.format("select dokument_ksiegowy.id_dokument_ksiegowy, dokument_ksiegowy.numer, dokument_ksiegowy.kwota_brutto, kontrahent.nazwa, dokument_ksiegowy.data, dokument_ksiegowy.opis, kontrahent.adres, typ_dokumentu.nazwa "
					+ "from dokument_ksiegowy, typ_dokumentu, kontrahent " 
			+ "where dokument_ksiegowy.id_typ_dokumentu = typ_dokumentu.id_typ_dokumentu "
			+ "and dokument_ksiegowy.id_kontrahent = kontrahent.id_kontrahent "
			+ "and dokument_ksiegowy.id_dokument_ksiegowy = '%d' ", id_document);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				document = new DocumentTable(rs.getString(2), rs.getInt(1), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8));
			}
		} catch(SQLException ex) {
			System.out.println("B��d pobierania danych o dokumencie: " + ex);
		}
		
		try {
			String query = String.format("select kwota_netto.wartosc "
					+ "from dokument_ksiegowy, kwota_netto, dokument_kwota_netto " 
			+ "where dokument_kwota_netto.id_kwota_netto = kwota_netto.id_kwota_netto "
			+ "and dokument_kwota_netto.id_dokument_ksiegowy = dokument_ksiegowy.id_dokument_ksiegowy "
			+ "and dokument_ksiegowy.id_dokument_ksiegowy = '%d' ", document.getId());
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				netAmount += rs.getDouble(1);
			}
		} catch(SQLException ex) {
			System.out.println("B��d pobierania danych o kwocie netto: " + ex);
		}
		if(netAmount == 0.0) {
			document.setNetAmount(null);
		} else {
			document.setNetAmount(String.valueOf(netAmount));
		}
		
		try {
			String query = String.format("select kwota_vat.wartosc "
					+ "from dokument_ksiegowy, kwota_vat, dokument_kwota_vat " 
			+ "where dokument_kwota_vat.id_kwota_vat = kwota_vat.id_kwota_vat "
			+ "and dokument_kwota_vat.id_dokument_ksiegowy = dokument_ksiegowy.id_dokument_ksiegowy "
			+ "and dokument_ksiegowy.id_dokument_ksiegowy = '%d' ", document.getId());
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				vatAmount += rs.getDouble(1);
			}
		} catch(SQLException ex) {
			System.out.println("B��d pobierania danych o kwocie vat: " + ex);
		}
		if(vatAmount == 0.0) {
			document.setVatAmount(null);
		} else {
			document.setVatAmount(String.valueOf(vatAmount));
		}
		
		if(document.getGrossAmount() == null) {
			document.setGrossAmount(String.valueOf(netAmount + vatAmount));
		}
		return document;
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
