package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
import model.Amortization;
import model.DocumentTable;

public class FixedAssetDetailController {

	private HashMap<Integer, String> monthsMap;
	private int id_uzytkownik;
	private int id_asset;
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
	private Text textDescription;
	@FXML
	private TableView<Amortization> tableViewNew;
	@FXML
	private TableColumn tcAll;
	@FXML
	private TableColumn tcYear;
	@FXML
	private TableColumn tcMonth;
	@FXML
	private TableView tableViewUsed;
	@FXML
	private TableColumn tcAllUsed;
	@FXML
	private TableColumn tcYearUsed;
	@FXML
	private TableColumn tcMonthUsed;
	@FXML
	private Text textAmortNew;
	@FXML
	private Text textAmortUsed;
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("FixedAssetsFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            Scene scene = new Scene(loader.load());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            FixedAssetsController fixedAssetsController = loader.<FixedAssetsController>getController();
            fixedAssetsController.initData(id_uzytkownik);
            stage.setResizable(false);
            stage.show();
		}
	}
	
	public void initData(int id_uzytkownik, int id_asset) {
		this.id_uzytkownik = id_uzytkownik;
		this.id_asset = id_asset;
		ArrayList<Amortization> tableAmounts = new ArrayList<>();
		monthsMap = new LinkedHashMap<>();
		monthsMap.put(1, "styczeñ");
		monthsMap.put(2, "luty");
		monthsMap.put(3, "marzec");
		monthsMap.put(4, "kwiecieñ");
		monthsMap.put(5, "maj");
		monthsMap.put(6, "czerwiec");
		monthsMap.put(7, "lipiec");
		monthsMap.put(8, "sierpieñ");
		monthsMap.put(9, "wrzesieñ");
		monthsMap.put(10, "paŸdziernik");
		monthsMap.put(11, "listopad");
		monthsMap.put(12, "grudzieñ");
		Double amount = 0.0;
		Connection conn;
		conn = openConnection(CONN_STR);
		documentTable = getFixedAsset(conn);
		if(documentTable!=null) {
			textNumber.setText(documentTable.getNumber());
			textDate.setText(documentTable.getDate());
			textNameContractor.setText(documentTable.getNameContractor());
			String[] parts = documentTable.getAddressContractor().split(", ");
			textAddressContractor.setText(parts[1] + '\n' + parts[0]);
			if(documentTable.getNetAmount()!=null) {
				textNetAmount.setText(documentTable.getNetAmount().replace('.', ',') + " z³");
			} else {
				textNetAmount.setText("-");
			}
			textGrossAmount.setText(documentTable.getGrossAmount().replace('.', ',') + " z³");
			textDescription.setText(documentTable.getDescription());			
		}
		closeConnection(conn);
		tcAll.setCellValueFactory(new PropertyValueFactory<Amortization, String>("allAmount"));
		tcYear.setCellValueFactory(new PropertyValueFactory<Amortization, String>("yearAmount"));
		tcMonth.setCellValueFactory(new PropertyValueFactory<Amortization, String>("monthAmount"));
		amount = Double.valueOf(documentTable.getGrossAmount());
		Amortization amortization = new Amortization(String.valueOf(amount).replace('.', ',') + " z³", String.format("%.2f", amount * 0.2).replace('.', ',') + " z³", String.format("%.2f", (amount * 0.2)/12).replace('.', ',') + " z³");
		tableAmounts.add(amortization);
		tableViewNew.setItems(FXCollections.observableArrayList(tableAmounts));
		
		tcAllUsed.setCellValueFactory(new PropertyValueFactory<Amortization, String>("allAmount"));
		tcYearUsed.setCellValueFactory(new PropertyValueFactory<Amortization, String>("yearAmount"));
		tcMonthUsed.setCellValueFactory(new PropertyValueFactory<Amortization, String>("monthAmount"));
		amortization = new Amortization(String.valueOf(amount).replace('.', ',') + " z³", String.format("%.2f", amount * 0.4).replace('.', ',') + " z³", String.format("%.2f", (amount * 0.4)/12).replace('.', ',') + " z³");
		tableAmounts.remove(0);
		tableAmounts.add(amortization);
		tableViewUsed.setItems(FXCollections.observableArrayList(tableAmounts));
		String year = documentTable.getDate().substring(0,4);
		int month = Integer.valueOf(documentTable.getDate().substring(5,7));
		String monthName = monthsMap.get(month+1);
		textAmortNew.setText("Amortyzacja - nowy œrodek trwa³y" + " od " + monthName + " " + year + " przez 60 miesiêcy jako wydatki/DW");
		textAmortUsed.setText("Amortyzacja - u¿ywany œrodek trwa³y" + " od " + monthName + " " + year + " przez 30 miesiêcy jako wydatki/DW");
	}
	
	private DocumentTable getFixedAsset(Connection conn) {
		DocumentTable document = null;
		Double netAmount = 0.0;
		Double vatAmount = 0.0;
		try {
			String query = String.format("select dokument_ksiegowy.id_dokument_ksiegowy, dokument_ksiegowy.numer, dokument_ksiegowy.kwota_brutto, kontrahent.nazwa, dokument_ksiegowy.data, dokument_ksiegowy.opis, kontrahent.adres "
			+ "from dokument_ksiegowy, kontrahent "
			+ "where dokument_ksiegowy.id_kontrahent = kontrahent.id_kontrahent "
			+ "and dokument_ksiegowy.id_dokument_ksiegowy = '%d'", id_asset);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				document = new DocumentTable(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7));
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych o œrodku trwa³ym: " + ex);
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
			System.out.println("B³¹d pobierania danych o kwocie netto: " + ex);
		}
		if(netAmount == 0.0) {
			document.setNetAmount(null);
		} else {
			document.setNetAmount(String.format("%.2f", netAmount));
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
			System.out.println("B³¹d pobierania danych o kwocie vat: " + ex);
		}
		if(vatAmount == 0.0) {
			document.setVatAmount(null);
		} else {
			document.setVatAmount(String.format("%.2f", vatAmount));
		}
		
		if(document.getGrossAmount() == null) {
			document.setGrossAmount(String.format("%.2f", netAmount + vatAmount));
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
