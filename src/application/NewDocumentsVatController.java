package application;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.plaf.basic.BasicToolBarUI.DockingListener;

import components.ExcelCreation;
import database.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.DocumentTable;
import model.Month;

public class NewDocumentsVatController {
	
	private int id_uzytkownik;
	private String year;
	private String month;
	private HashMap<String, Integer> monthsMap;
	private HashMap<String, String> contractors;
	private ArrayList<String> nameContractors;
	@FXML
	private Button btnLogOut;
	@FXML
	private Button btnMainMenu;
	@FXML
	private Button btnNewDocument;
	@FXML
	private Button btnCreateExcel;
	@FXML
	private Button btnShowDocument;
	@FXML
	private Text textNewDocument;
	@FXML
	private ChoiceBox<String> choiceBoxDocumentTypes;
	@FXML
	private TextField textFieldNumberDocument;
	@FXML
	private DatePicker datePickerDateDocument;
	@FXML
	private TextField textFieldNet1;
	@FXML
	private TextField textFieldNet2;
	@FXML
	private TextField textFieldNet3;
	@FXML
	private TextField textFieldNet4;
	@FXML
	private TextField textFieldNet5;
	@FXML
	private TextField textFieldVat1;
	@FXML
	private TextField textFieldVat2;
	@FXML
	private TextField textFieldVat3;
	@FXML
	private TextField textFieldVat4;
	@FXML
	private TextField textFieldVat5;
	@FXML
	private TextArea textAreaDescription;
	@FXML
	private ComboBox<String> comboBoxNameContractor;
	@FXML
	private TextField textFieldAddressContractor;
	@FXML
	private TableView<DocumentTable> tableViewDocuments;
	@FXML
	private TableColumn tcNumber;
	@FXML
	private TableColumn tcMoney;
	@FXML
	private TableColumn tcName;
	@FXML
	private TableColumn tcDate;
	@FXML
	private TableColumn tcDocumentType;
	@FXML 
	private Text textError;
	@FXML
	private AnchorPane anchorPaneEditor;
	@FXML
	public void handleButtonAction(ActionEvent event) throws IOException {
		Parent root;
		Stage stage = null;
		Connection conn;
		LocalDate ld;
		Calendar c;
		String numberDocument;
		String documentType;
		Date date;
		String grossAmount;
		String description;
		String nameContractor;
		String addressContractor;
		String netAmount1 = "";
		String vatAmount1 = "";
		String netAmount2 = "";
		String vatAmount2 = "";
		String netAmount3 = "";
		String vatAmount3 = "";
		String netAmount4 = "";
		String vatAmount4 = "";
		String netAmount5 = "";
		String vatAmount5 = "";
		if(event.getSource()==btnLogOut) {
			stage = (Stage) anchorPaneEditor.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("WelcomeWindowFXML.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
			
		} else if(event.getSource()==btnNewDocument) {
			Double netSum = 0.0;
			Double vatSum = 0.0;
			String connStr = "jdbc:h2:~/db/ledgerdatabase;";
			conn = openConnection(connStr);
				if(!textFieldNumberDocument.getText().isEmpty() && choiceBoxDocumentTypes.getValue()!=null && datePickerDateDocument.getValue()!=null && !textAreaDescription.getText().isEmpty() && comboBoxNameContractor.getValue()!=null && !textFieldAddressContractor.getText().isEmpty() && !textFieldNet1.getText().isEmpty() && !textFieldVat1.getText().isEmpty() && !textFieldNet2.getText().isEmpty() && !textFieldVat2.getText().isEmpty() && !textFieldNet3.getText().isEmpty() && !textFieldVat3.getText().isEmpty() && !textFieldNet4.getText().isEmpty() && !textFieldVat4.getText().isEmpty() && !textFieldNet5.getText().isEmpty() && !textFieldVat5.getText().isEmpty()) {
					numberDocument = textFieldNumberDocument.getText().toString();
					documentType = choiceBoxDocumentTypes.getValue().toString();
					ld = datePickerDateDocument.getValue();
					c =  Calendar.getInstance();
					c.set(ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth());
					date = c.getTime();
					netAmount1 = textFieldNet1.getText().toString();
					vatAmount1 = textFieldVat1.getText().toString();
					netAmount2 = textFieldNet2.getText().toString();
					vatAmount2 = textFieldVat2.getText().toString();
					netAmount3 = textFieldNet3.getText().toString();
					vatAmount3 = textFieldVat3.getText().toString();
					netAmount4 = textFieldNet4.getText().toString();
					vatAmount4 = textFieldVat4.getText().toString();
					netAmount5 = textFieldNet5.getText().toString();
					vatAmount5 = textFieldVat5.getText().toString();
					netSum = Double.valueOf(netAmount1.replace(',', '.')) + Double.valueOf(netAmount2.replace(',', '.')) + Double.valueOf(netAmount3.replace(',', '.')) + Double.valueOf(netAmount4.replace(',', '.')) + Double.valueOf(netAmount5.replace(',', '.'));
					vatSum = Double.valueOf(vatAmount1.replace(',', '.')) + Double.valueOf(vatAmount2.replace(',', '.')) + Double.valueOf(vatAmount3.replace(',', '.')) + Double.valueOf(vatAmount4.replace(',', '.')) + Double.valueOf(vatAmount5.replace(',', '.'));
					grossAmount = String.valueOf(netSum + vatSum).replace('.', ',');
					description = textAreaDescription.getText().toString();
					nameContractor = comboBoxNameContractor.getSelectionModel().getSelectedItem();
					addressContractor = textFieldAddressContractor.getText().toString();
					
					Double amount = Double.valueOf(grossAmount.replace(',', '.'));
					if(documentType.equals("zakup œrodków trwa³ych")) {
						if(amount > 3500) {
							if(addNewDocument(conn, numberDocument, documentType, date, netAmount1, vatAmount1, netAmount2, vatAmount2, netAmount3, vatAmount3, netAmount4, vatAmount4, netAmount5, vatAmount5, grossAmount, description, nameContractor, addressContractor)) {
								FXMLLoader loader = new FXMLLoader(getClass().getResource("NewDocumentsFXML.fxml"));
					            stage = (Stage) anchorPaneEditor.getScene().getWindow();
					            Scene scene = new Scene(loader.load());
								scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					            stage.setScene(scene);
					            NewDocumentsController newDocumentsController = loader.<NewDocumentsController>getController();
					            newDocumentsController.initData(id_uzytkownik, year, month);
					            stage.setResizable(false);
					            stage.show();
							} else {
								textError.setText("Wyst¹pi³ b³¹d, nie dodano dokumentu");
							}
						} else {
							textError.setText("Kwota œrodka trwa³ego musi przekroczyæ 3500 z³");
						}
					} else if(documentType.equals("zakup wyposa¿enia")) {
								if(amount > 1500) {
									if(addNewDocument(conn, numberDocument, documentType, date, netAmount1, vatAmount1, netAmount2, vatAmount2, netAmount3, vatAmount3, netAmount4, vatAmount4, netAmount5, vatAmount5, grossAmount, description, nameContractor, addressContractor) && addNewDocument(conn, numberDocument, "zakupy/wydatki", date, netAmount1, vatAmount1, netAmount2, vatAmount2, netAmount3, vatAmount3, netAmount4, vatAmount4, netAmount5, vatAmount5, grossAmount, description, nameContractor, addressContractor)) {
										FXMLLoader loader = new FXMLLoader(getClass().getResource("NewDocumentsFXML.fxml"));
							            stage = (Stage) anchorPaneEditor.getScene().getWindow();
							            Scene scene = new Scene(loader.load());
										scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
							            stage.setScene(scene);
							            NewDocumentsController newDocumentsController = loader.<NewDocumentsController>getController();
							            newDocumentsController.initData(id_uzytkownik, year, month);
							            stage.setResizable(false);
							            stage.show();
									} else {
										textError.setText("Wyst¹pi³ b³¹d, nie dodano dokumentu");
									}
						} else {
							textError.setText("Kwota wyposa¿enia musi przekroczyæ 1500 z³");
						}
					} else {
						if(addNewDocument(conn, numberDocument, documentType, date, netAmount1, vatAmount1, netAmount2, vatAmount2, netAmount3, vatAmount3, netAmount4, vatAmount4, netAmount5, vatAmount5, grossAmount, description, nameContractor, addressContractor)) {
							FXMLLoader loader = new FXMLLoader(getClass().getResource("NewDocumentsVatFXML.fxml"));
				            stage = (Stage) anchorPaneEditor.getScene().getWindow();
				            Scene scene = new Scene(loader.load());
							scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				            stage.setScene(scene);
				            NewDocumentsVatController newDocumentsVatController = loader.<NewDocumentsVatController>getController();
				            newDocumentsVatController.initData(id_uzytkownik, year, month);
				            stage.setResizable(false);
				            stage.show();
						} else {
							textError.setText("Wyst¹pi³ b³¹d, nie dodano dokumentu");
						}
					}
				} else {
					textError.setText("Pola nie s¹ wype³nione!");
				}
			closeConnection(conn);
		} else if(event.getSource()==btnCreateExcel) {
			String connStr = "jdbc:h2:~/db/ledgerdatabase;";
			conn = openConnection(connStr);
				if(createExcel(conn)) {
					if(createExcelWithVat(conn)) {
						
					} else {
						textError.setText("Wyst¹pi³ b³¹d, nie utworzono pliku z VAT");
					}
				} else {
					textError.setText("Wyst¹pi³ b³¹d, nie utworzono pliku");
				}	
		} else if(event.getSource()==btnShowDocument) {
			int documentId;
			DocumentTable documentTable = tableViewDocuments.getSelectionModel().getSelectedItem();
			if(documentTable!=null) {
				documentId = documentTable.getId();
				if(documentTable.getDocumentType().equals("zakup œrodków trwa³ych")) {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("FixedAssetDetailFXML.fxml"));
		            stage = (Stage) anchorPaneEditor.getScene().getWindow();
		            Scene scene = new Scene(loader.load());
					scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		            stage.setScene(scene);
		            FixedAssetDetailController fixedAssetDetailController = loader.<FixedAssetDetailController>getController();
		            fixedAssetDetailController.initData(id_uzytkownik, documentId);
		            stage.setResizable(false);
		            stage.show();
				} else if(documentTable.getDocumentType().equals("zakup wyposa¿enia")) {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("EquipmentDetailFXML.fxml"));
		            stage = (Stage) anchorPaneEditor.getScene().getWindow();
		            Scene scene = new Scene(loader.load());
					scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		            stage.setScene(scene);
		            EquipmentDetailController equipmentDetailController = loader.<EquipmentDetailController>getController();
		            equipmentDetailController.initData(id_uzytkownik, documentId);
		            stage.setResizable(false);
		            stage.show();
				} else {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("DocumentDetailFXML.fxml"));
		            stage = (Stage) anchorPaneEditor.getScene().getWindow();
		            Scene scene = new Scene(loader.load());
					scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		            stage.setScene(scene);
		            DocumentDetailController documentDetailController = loader.<DocumentDetailController>getController();
		            documentDetailController.initData(id_uzytkownik, documentId);
		            stage.setResizable(false);
		            stage.show(); 
				}
			} else {
				textError.setText("Nie wybrano dokumentu!");
			}
		} else if(event.getSource()==comboBoxNameContractor) {
			textFieldAddressContractor.setText(contractors.get(comboBoxNameContractor.getSelectionModel().getSelectedItem()));
		} else if(event.getSource()==btnMainMenu) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("NewMonthLedgerFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            Scene scene = new Scene(loader.load());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            NewMonthLedgerController newMonthLedgerController = loader.<NewMonthLedgerController>getController();
            newMonthLedgerController.initData(id_uzytkownik, year);
            stage.setResizable(false);
            stage.show(); 
		}
	}
	
	private Boolean createExcel(Connection conn) {
		Boolean value = false;
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapTax = new HashMap<String, Object>();
		ArrayList<String> documentTypes = new ArrayList<>();
		Month monthModel = null;
		int monthNumber = monthsMap.get(month);
		Double netAmount = 0.00;
		Double incomeSum = 0.00;
		Double rewardSum = 0.00;
		Double expenseSum = 0.00;
		Double boughtGoodsSum = 0.00;
		Double expenseInSum = 0.00;
		Double finalExpenseSum = 0.00;
		Double finalResultSum = 0.00;
		Double finalTax = 0.00;
		Double yearTaxSum = 0.00;
			try {
				String query = String.format("select typ_dokumentu.nazwa, dokument_ksiegowy.lp, dokument_ksiegowy.numer, "
						+ "dokument_ksiegowy.data, kontrahent.nazwa, kontrahent.adres, "
						+ "dokument_ksiegowy.opis, dokument_ksiegowy.id_dokument_ksiegowy "
				+ "from dokument_ksiegowy, miesiac, uzytkownik, rok, typ_dokumentu, kontrahent " 
				+ "where dokument_ksiegowy.id_uzytkownik = uzytkownik.id_uzytkownik "
				+ "and dokument_ksiegowy.id_miesiac = miesiac.id_miesiac "
				+ "and miesiac.id_rok = rok.id_rok "
				+ "and dokument_ksiegowy.id_typ_dokumentu = typ_dokumentu.id_typ_dokumentu "
				+ "and dokument_ksiegowy.id_kontrahent = kontrahent.id_kontrahent "
				+ "and uzytkownik.id_uzytkownik = '%d' "
				+ "and month(miesiac.data) = '%d' "
				+ "and typ_dokumentu.nazwa != 'zakup œrodków trwa³ych' "
				+ "and typ_dokumentu.nazwa != 'zakup wyposa¿enia' "
				+ "and year(rok.data) = '%s'", id_uzytkownik, monthNumber, year);
				Statement stm = conn.createStatement();
				ResultSet rs = stm.executeQuery(query);
				int i = 1;
				while(rs.next()) {
					if(!rs.getString(1).equals("zakup œrodków trwa³ych") && !rs.getString(1).equals("zakup wyposa¿enia")) {
						netAmount = 0.0;
						map.put("lp" + i, "" + rs.getInt(2));
						map.put("date" + i, rs.getString(4));
						map.put("number" + i, rs.getString(3));
						map.put("nameC" + i, rs.getString(5));
						map.put("addressC" + i, rs.getString(6));
						map.put("description" + i, rs.getString(7));
						
						try {
							String query_net = String.format("select kwota_netto.wartosc "
									+ "from dokument_ksiegowy, kwota_netto, dokument_kwota_netto " 
							+ "where dokument_kwota_netto.id_kwota_netto = kwota_netto.id_kwota_netto "
							+ "and dokument_kwota_netto.id_dokument_ksiegowy = dokument_ksiegowy.id_dokument_ksiegowy "
							+ "and dokument_ksiegowy.id_dokument_ksiegowy = '%d' ", rs.getInt(8));
							Statement stm_net = conn.createStatement();
							ResultSet rs_net = stm_net.executeQuery(query_net);
							while(rs_net.next()) {
								netAmount += rs_net.getDouble(1);
							}
						} catch(SQLException ex) {
							System.out.println("B³¹d pobierania danych o kwocie netto: " + ex);
						}
						
						if(rs.getString(1).equals("przychody")) {
							map.put("income" + i, String.format("%.2f", netAmount).replace('.', ','));
							map.put("incomeR" + i, "");
							map.put("incomeS" + i, String.format("%.2f", netAmount).replace('.', ','));
							map.put("buyG" + i, "");
							map.put("expenseIn" + i, "");
							map.put("rewards" + i, "");
							map.put("expenseR" + i, "");
							map.put("expenseS" + i, "0,00");
							map.put("expenseResearchD" + i, "");
							map.put("expenseResearch" + i, "");
							map.put("comment" + i, "");
							
							incomeSum += netAmount;
							
						} else if(rs.getString(1).equals("wynagrodzenia")) {
							map.put("rewards" + i, String.format("%.2f", netAmount).replace('.', ','));
							map.put("income" + i, "");
							map.put("incomeR" + i, "");
							map.put("incomeS" + i, "0,00");
							map.put("buyG" + i, "");
							map.put("expenseIn" + i, "");
							map.put("expenseR" + i, "");
							map.put("expenseS" + i, String.format("%.2f", netAmount).replace('.', ','));
							map.put("expenseResearchD" + i, "");
							map.put("expenseResearch" + i, "");
							map.put("comment" + i, "");
							
							rewardSum += netAmount;
							
						} else if(rs.getString(1).equals("zakupy/wydatki") || rs.getString(1).equals("import us³ug i WNT") || rs.getString(1).equals("pozosta³e wydatki/dowody wewnêtrzne")) {
							map.put("expenseR" + i, String.format("%.2f", netAmount).replace('.', ','));
							map.put("income" + i, "");
							map.put("incomeR" + i, "");
							map.put("incomeS" + i, "0,00");
							map.put("buyG" + i, "");
							map.put("expenseIn" + i, "");
							map.put("rewards" + i, "");
							map.put("expenseS" + i, String.format("%.2f", netAmount).replace('.', ','));
							map.put("expenseResearchD" + i, "");
							map.put("expenseResearch" + i, "");
							map.put("comment" + i, "");
							
							expenseSum += netAmount;
							
						} else if(rs.getString(1).equals("zakup towarów i materia³ów") || rs.getString(1).equals("import towarów i WNT")) {
							map.put("buyG" + i, String.format("%.2f", netAmount).replace('.', ','));
							map.put("income" + i, "");
							map.put("incomeR" + i, "");
							map.put("incomeS" + i, "0,00");
							map.put("expenseR" + i, "");
							map.put("expenseIn" + i, "");
							map.put("rewards" + i, "");
							map.put("expenseS" + i, String.format("%.2f", netAmount).replace('.', ','));
							map.put("expenseResearchD" + i, "");
							map.put("expenseResearch" + i, "");
							map.put("comment" + i, "");
							
							boughtGoodsSum += netAmount;
							
						} else if(rs.getString(1).equals("koszty uboczne zakupu"))	{
							map.put("expenseIn" + i, String.format("%.2f", netAmount).replace('.', ','));
							map.put("income" + i, "");
							map.put("incomeR" + i, "");
							map.put("incomeS" + i, "0,00");
							map.put("buyG" + i, "");
							map.put("rewards" + i, "");
							map.put("expenseR" + i, "");
							map.put("expenseS" + i, String.format("%.2f", netAmount).replace('.', ','));
							map.put("expenseResearchD" + i, "");
							map.put("expenseResearch" + i, "");
							map.put("comment" + i, "");
							
							expenseInSum += netAmount;
						}
						i++;
					}	 
				}
			} catch(SQLException ex) {
				System.out.println("B³¹d pobierania danych o dokumentach: " + ex);
			}
			
			String strIncomeSum = String.valueOf(incomeSum);
			String strExpenseSum = String.valueOf(expenseSum);
			String strRewardSum = String.valueOf(rewardSum);
			String strBoughtGoodsSum = String.valueOf(boughtGoodsSum);
			String strExpenseInSum = String.valueOf(expenseInSum);
			 
			BigDecimal income = new BigDecimal(strIncomeSum);
			BigDecimal incomeR = new BigDecimal("0.00");
			BigDecimal expense = new BigDecimal(strExpenseSum); 
			BigDecimal reward = new BigDecimal(strRewardSum); 
			BigDecimal buyG = new BigDecimal(strBoughtGoodsSum);
			BigDecimal expenseIn = new BigDecimal(strExpenseInSum);
			BigDecimal expenseRes = new BigDecimal("0.00");
			
			map.put("sheetI", String.format("%.2f", incomeSum).replace('.', ','));
			map.put("sheetIR", "0,00");
			map.put("sheetIS", String.format("%.2f", incomeSum).replace('.', ','));
			
			map.put("sheetBuyG", String.format("%.2f", boughtGoodsSum).replace('.', ','));
			
			map.put("sheetExpenseIn", String.format("%.2f", expenseInSum).replace('.', ','));
			
			map.put("sheetRewards", String.format("%.2f", rewardSum).replace('.', ','));
			map.put("sheetExpense", String.format("%.2f", expenseSum).replace('.', ','));
			map.put("sheetExpenseS", String.format("%.2f", rewardSum + expenseSum).replace('.', ','));
			
			
			if(updateMonthWithSums(conn, income, incomeR, buyG, expenseIn, reward, expense, expenseRes)) {
				String taxWay = getTaxWay(conn);
				Double tax = 0.0;
				if(monthsMap.get(month) == 1) {
					map.put("previousI", "0,00");
					map.put("previousIR", "0,00");
					map.put("previousIS", "0,00");
					map.put("previousBuyG", "0,00");
					map.put("previousExpenseIn", "0,00");
					map.put("previousRewards", "0,00");
					map.put("previousExpense", "0,00");
					map.put("previousExpenseS", "0,00");
					map.put("previousExpenseResearch", "0,00");
					
					map.put("yearI", String.format("%.2f", incomeSum).replace('.', ','));
					map.put("yearIR", "0,00");
					map.put("yearIS", String.format("%.2f", incomeSum + 0.0).replace('.', ','));
					map.put("yearBuyG", String.format("%.2f", boughtGoodsSum).replace('.', ','));
					map.put("yearExpenseIn", String.format("%.2f", expenseInSum).replace('.', ','));
					map.put("yearRewards", String.format("%.2f", rewardSum).replace('.', ','));
					map.put("yearExpense", String.format("%.2f", expenseSum).replace('.', ','));
					map.put("yearExpenseS", String.format("%.2f", rewardSum + expenseSum).replace('.', ','));
					map.put("yearExpenseResearch", "0,00");
					
					mapTax.put("income", String.format("%.2f", incomeSum).replace('.', ','));
					mapTax.put("incomeSum", String.format("%.2f", incomeSum).replace('.', ','));
					
					finalExpenseSum = boughtGoodsSum + expenseInSum + rewardSum + expenseSum;
					
					mapTax.put("expense", String.format("%.2f", finalExpenseSum).replace('.', ','));
					mapTax.put("expenseSum", String.format("%.2f", finalExpenseSum).replace('.', ','));
					
					finalResultSum = incomeSum - finalExpenseSum;
					
					mapTax.put("result", String.format("%.2f", finalResultSum).replace('.', ','));
					mapTax.put("resultSum", String.format("%.2f", finalResultSum).replace('.', ','));
					
					mapTax.put("resultSumMinusLoss", String.format("%.2f", finalResultSum).replace('.', ','));
					
					mapTax.put("resultSumRounded", String.format("%d", finalResultSum.intValue()));
					
					String taxScaleRate = "";
					if(finalResultSum > 0) {
						if(taxWay.equals("zasady ogólne")) {
							if(finalResultSum < 85528) {
								tax = finalResultSum * 0.18;
								taxScaleRate = "18%";
							} else {
								tax = finalResultSum * 0.32;
								taxScaleRate = "32%";
							}	
						} else if(taxWay.equals("podatek liniowy")) {
							tax = finalResultSum * 0.19;
							taxScaleRate = "19%";
						}
						 
						mapTax.put("taxScaleRate", "Podatek nale¿ny wed³ug skali " + taxScaleRate);
						mapTax.put("taxValue", String.format("%.2f", tax).replace('.', ','));
						mapTax.put("taxValueRounded", String.format("%d", tax.intValue()));
						mapTax.put("healthIns", String.format("%.2f", 255.99).replace('.', ','));
						mapTax.put("taxValueMinusHI", String.format("%.2f", tax.intValue() - 255.99).replace('.', ','));
						mapTax.put("yearTax", "0,00");
						finalTax = tax.intValue() - 255.99;
						mapTax.put("finalTax", String.format("%.2f", finalTax).replace('.', ','));
						mapTax.put("finalTaxRounded", String.format("%d", finalTax.intValue()));
					} else {
						mapTax.put("taxValue", "0,00");
						mapTax.put("taxValueRounded", "0,00");
						mapTax.put("healthIns", String.format("%.2f", 255.99).replace('.', ','));
						mapTax.put("taxValueMinusHI", "0,00");
						mapTax.put("yearTax", "0,00");
						mapTax.put("finalTax", "0,00");
						mapTax.put("finalTaxRounded", "0,00"); 
					}
					
				} else {
					monthModel = getMonthSums(conn);
					if(month!=null) {
						map.put("previousI", String.format("%.2f", monthModel.getIncome()).replace('.', ','));
						map.put("previousIR", String.format("%.2f", monthModel.getIncomeR()).replace('.', ','));
						map.put("previousIS", String.format("%.2f",monthModel.getIncome() + monthModel.getIncomeR()).replace('.', ',')); //String.valueOf(monthModel.getIncome() + monthModel.getIncomeR()).replace('.', ','));
						map.put("previousBuyG", String.format("%.2f", monthModel.getBuyG()).replace('.', ','));
						map.put("previousExpenseIn", String.format("%.2f", monthModel.getExpenseIn()).replace('.', ','));
						map.put("previousRewards", String.format("%.2f", monthModel.getReward()).replace('.', ','));
						map.put("previousExpense", String.format("%.2f", monthModel.getExpense()).replace('.', ','));
						map.put("previousExpenseS", String.format("%.2f", monthModel.getReward() + monthModel.getExpense()).replace('.', ','));
						map.put("previousExpenseResearch", String.format("%.2f", monthModel.getExpenseRes()).replace('.', ','));
						
						map.put("yearI", String.format("%.2f", monthModel.getIncome() + incomeSum).replace('.', ','));
						map.put("yearIR", String.format("%.2f", monthModel.getIncomeR() + 0.00).replace('.', ','));
						map.put("yearIS", String.format("%.2f", monthModel.getIncome() + monthModel.getIncomeR() + incomeSum + 0.0).replace('.', ','));
						map.put("yearBuyG", String.format("%.2f", monthModel.getBuyG() + boughtGoodsSum).replace('.', ','));
						map.put("yearExpenseIn", String.format("%.2f", monthModel.getExpenseIn() + expenseInSum).replace('.', ','));
						map.put("yearRewards", String.format("%.2f", monthModel.getReward() + rewardSum).replace('.', ','));
						map.put("yearExpense", String.format("%.2f", monthModel.getExpense() + expenseSum).replace('.', ','));
						map.put("yearExpenseS", String.format("%.2f", monthModel.getReward() + monthModel.getExpense() + rewardSum + expenseSum).replace('.', ','));
						map.put("yearExpenseResearch", String.format("%.2f", monthModel.getExpenseRes() + 0.0).replace('.', ','));
						
						mapTax.put("income", String.format("%.2f", monthModel.getIncome() + incomeSum).replace('.', ','));
						mapTax.put("incomeSum", String.format("%.2f", monthModel.getIncome() + incomeSum).replace('.', ','));
						
						finalExpenseSum = monthModel.getBuyG() + boughtGoodsSum + monthModel.getExpenseIn() + expenseInSum + monthModel.getReward() + rewardSum + monthModel.getExpense() + expenseSum;
						
						mapTax.put("expense", String.format("%.2f", finalExpenseSum).replace('.', ','));
						mapTax.put("expenseSum", String.format("%.2f", finalExpenseSum).replace('.', ','));
						
						finalResultSum = monthModel.getIncome() + incomeSum - finalExpenseSum;
						
						mapTax.put("result", String.format("%.2f", finalResultSum).replace('.', ','));
						mapTax.put("resultSum", String.format("%.2f", finalResultSum).replace('.', ','));
						
						mapTax.put("resultSumMinusLoss", String.format("%.2f", finalResultSum).replace('.', ','));
						
						mapTax.put("resultSumRounded", String.format("%d", finalResultSum.intValue()));
						yearTaxSum = getMonthTaxes(conn);
						if(finalResultSum > 0) {
							if(taxWay.equals("zasady ogólne")) {
								if(finalResultSum < 85528) {
									tax = finalResultSum * 0.18;
								} else {
									tax = finalResultSum * 0.32;
								}	
							} else if(taxWay.equals("podatek liniowy")) {
								tax = finalResultSum * 0.19;
							}				
							mapTax.put("taxValue", String.format("%.2f", tax).replace('.', ','));
							mapTax.put("taxValueRounded", String.format("%d", tax.intValue()));
							mapTax.put("healthIns", String.format("%.2f", 255.99 * monthNumber).replace('.', ','));
							mapTax.put("taxValueMinusHI", String.format("%.2f", tax.intValue() - 255.99 * monthNumber).replace('.', ','));
							mapTax.put("yearTax", String.format("%d", yearTaxSum.intValue()));
							finalTax = tax.intValue() - 255.99 * monthNumber - yearTaxSum;
							mapTax.put("finalTax", String.format("%.2f", finalTax).replace('.', ','));
							mapTax.put("finalTaxRounded", String.format("%d", finalTax.intValue()));
						} else {
							mapTax.put("taxValue", "0,00");
							mapTax.put("taxValueRounded", "0,00");
							mapTax.put("healthIns", String.format("%.2f", 255.99 * monthNumber).replace('.', ','));
							mapTax.put("taxValueMinusHI", "0,00");
							mapTax.put("yearTax", String.format("%d", yearTaxSum.intValue()));
							mapTax.put("finalTax", "0,00");
							mapTax.put("finalTaxRounded", "0,00");
						}
					} else {
						System.out.println("Nie pobrano danych z poprzednich miesiecy");
					}
				}
				
				ExcelCreation createLedger = new ExcelCreation(System.getProperty("user.dir") + "/Templates/template_kpir.xlsx" , 0, System.getProperty("user.dir") + "/CreatedFiles/KPIR_" + year + "_" + getUserNip(conn) + ".xlsx", month + " " + year, map, month + " " + year);
			    createLedger.doExcel();
			    
			    if(updateLedgerWithName(conn, "KPIR_" + year + "_" + getUserNip(conn) + ".xlsx")) {
			    	BigDecimal bgFinalTax = new BigDecimal(String.valueOf(finalTax.intValue()));
				    
				    if(updateMonthWithTax(conn, bgFinalTax)) {
				    	ExcelCreation createTax = new ExcelCreation(System.getProperty("user.dir") + "/Templates/template_podatek.xlsx", 0, System.getProperty("user.dir") + "/CreatedFiles/ZALICZKA_" + year + "_" + getUserNip(conn) + ".xlsx", month + " " + year, mapTax, month + " " + year);
					    createTax.doExcel();
				    	value = true;
				    } else {
				    	System.out.println("Nie zaktualizowano tabeli miesiac z podatkiem");
				    }
			    } else {
			    	System.out.println("Nie zaktualizowano tabeli rok z nazw¹");
			    }
			} else {
				System.out.println("Nie zaktualizowano tabeli miesiac");
			}
		return value;
	}
	
	private Boolean createExcelWithVat(Connection conn) {
		Boolean value = false;
		Map<String, Object> map = new HashMap<String, Object>();
		int monthNumber = monthsMap.get(month);
		Double netAD = 0.00;
		Double vatAD = 0.00;
		Double netAC = 0.00;
		Double vatAC = 0.00;
		Double totalSumNetD1 = 0.00;
		Double totalSumVatD1 = 0.00;
		Double totalSumNetD2 = 0.00;
		Double totalSumVatD2 = 0.00;
		Double totalSumNetD3 = 0.00;
		Double totalSumVatD3 = 0.00;
		Double totalSumNetD4 = 0.00;
		Double totalSumVatD4 = 0.00;
		
		Double totalSumNetC1 = 0.00;
		Double totalSumVatC1 = 0.00;
		Double totalSumNetC2 = 0.00;
		Double totalSumVatC2 = 0.00;
		Double totalSumNetC3 = 0.00;
		Double totalSumVatC3 = 0.00;
		Double totalSumNetC4 = 0.00;
		Double totalSumVatC4 = 0.00;

		try {
			String query = String.format("select typ_dokumentu.nazwa, dokument_ksiegowy.lp, dokument_ksiegowy.numer, "
					+ "dokument_ksiegowy.data, kontrahent.nazwa, kontrahent.adres, "
					+ "dokument_ksiegowy.opis, dokument_ksiegowy.id_dokument_ksiegowy "
			+ "from dokument_ksiegowy, miesiac, uzytkownik, rok, typ_dokumentu, kontrahent " 
			+ "where dokument_ksiegowy.id_uzytkownik = uzytkownik.id_uzytkownik "
			+ "and dokument_ksiegowy.id_miesiac = miesiac.id_miesiac "
			+ "and miesiac.id_rok = rok.id_rok "
			+ "and dokument_ksiegowy.id_typ_dokumentu = typ_dokumentu.id_typ_dokumentu "
			+ "and dokument_ksiegowy.id_kontrahent = kontrahent.id_kontrahent "
			+ "and uzytkownik.id_uzytkownik = '%d' "
			+ "and month(miesiac.data) = '%d' "
			+ "and typ_dokumentu.nazwa != 'zakup œrodków trwa³ych' "
			+ "and typ_dokumentu.nazwa != 'zakup wyposa¿enia' "
			+ "and year(rok.data) = '%s'", id_uzytkownik, monthNumber, year);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			int i = 1;
			int j = 1;
			while(rs.next()) {
				if(!rs.getString(1).equals("zakup œrodków trwa³ych") && !rs.getString(1).equals("zakup wyposa¿enia")) {
					try {
						String query_net = String.format("select kwota_netto.wartosc, kwota_vat.wartosc, stawka_podatku.wartosc "
								+ "from dokument_ksiegowy, kwota_netto, dokument_kwota_netto, kwota_vat, dokument_kwota_vat, stawka_podatku " 
						+ "where dokument_kwota_netto.id_kwota_netto = kwota_netto.id_kwota_netto "
						+ "and dokument_kwota_netto.id_dokument_ksiegowy = dokument_ksiegowy.id_dokument_ksiegowy "
						+ "and dokument_kwota_vat.id_kwota_vat = kwota_vat.id_kwota_vat "
						+ "and dokument_kwota_vat.id_dokument_ksiegowy = dokument_ksiegowy.id_dokument_ksiegowy "
						+ "and kwota_netto.id_stawka_podatku = stawka_podatku.id_stawka_podatku "
						+ "and kwota_vat.id_stawka_podatku = stawka_podatku.id_stawka_podatku "
						+ "and stawka_podatku.wartosc != 'zwolniona' "
						+ "and dokument_ksiegowy.id_dokument_ksiegowy = '%d' ", rs.getInt(8));
						Statement stm_net = conn.createStatement();
						ResultSet rs_net = stm_net.executeQuery(query_net);
						while(rs_net.next()) {
							if(rs.getString(1).equals("przychody")) {
								if(rs_net.getString(3).equals("23")) {
									totalSumNetD1 += rs_net.getDouble(1);
									totalSumVatD1 += rs_net.getDouble(2);
								} else if(rs_net.getString(3).equals("8")) {
									totalSumNetD2 += rs_net.getDouble(1);
									totalSumVatD2 += rs_net.getDouble(2);
								} else if(rs_net.getString(3).equals("5")) {
									totalSumNetD3 += rs_net.getDouble(1);
									totalSumVatD3 += rs_net.getDouble(2);
								} else if(rs_net.getString(3).equals("0")) {
									totalSumNetD4 += rs_net.getDouble(1);
									totalSumVatD4 += rs_net.getDouble(2);
								}
								map.put("lpD" + i, "" + rs.getInt(2));
								map.put("dateD" + i, rs.getString(4));
								map.put("numberD" + i, rs.getString(3));
								map.put("nameCD" + i, rs.getString(5));
								map.put("descriptionD" + i, rs.getString(7));
								map.put("taxD" + i, rs_net.getString(3));
								map.put("netAD" + i, rs_net.getString(1).replace('.', ','));
								map.put("vatAD" + i, rs_net.getString(2).replace('.', ','));
								netAD += rs_net.getDouble(1);
								vatAD += rs_net.getDouble(2);
								i++;
							} else {
								if(rs_net.getString(3).equals("23")) {
									totalSumNetC1 += rs_net.getDouble(1);
									totalSumVatC1 += rs_net.getDouble(2);
								} else if(rs_net.getString(3).equals("8")) {
									totalSumNetC2 += rs_net.getDouble(1);
									totalSumVatC2 += rs_net.getDouble(2);
								} else if(rs_net.getString(3).equals("5")) {
									totalSumNetC3 += rs_net.getDouble(1);
									totalSumVatC3 += rs_net.getDouble(2);
								} else if(rs_net.getString(3).equals("0")) {
									totalSumNetC4 += rs_net.getDouble(1);
									totalSumVatC4 += rs_net.getDouble(2);
								}
								map.put("lpC" + j, "" + rs.getInt(2));
								map.put("dateC" + j, rs.getString(4));
								map.put("numberC" + j, rs.getString(3));
								map.put("nameCC" + j, rs.getString(5));
								map.put("descriptionC" + j, rs.getString(7));
								map.put("taxC" + j, rs_net.getString(3));
								map.put("netAC" + j, rs_net.getString(1).replace('.', ','));
								map.put("vatAC" + j, rs_net.getString(2).replace('.', ','));
								netAC += rs_net.getDouble(1);
								vatAC += rs_net.getDouble(2);
								j++;
							}
						}
					} catch(SQLException ex) {
						System.out.println("B³¹d pobierania danych o kwotach: " + ex);
					}
				}
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych o dokumentach: " + ex);
		}
		map.put("sumNetD", String.format("%.2f", netAD).replace('.', ','));
		map.put("sumVatD", String.format("%.2f", vatAD).replace('.', ','));
		map.put("sumNetC", String.format("%.2f", netAC).replace('.', ','));
		map.put("sumVatC", String.format("%.2f", vatAC).replace('.', ','));
		
		map.put("totalSumNetD1", String.format("%.2f", totalSumNetD1).replace('.', ','));
		map.put("totalSumVatD1", String.format("%.2f", totalSumVatD1).replace('.', ','));
		map.put("totalSumGrosD1", String.format("%.2f", totalSumNetD1 + totalSumVatD1).replace('.', ','));
		map.put("totalSumNetD2", String.format("%.2f", totalSumNetD2).replace('.', ','));
		map.put("totalSumVatD2", String.format("%.2f", totalSumVatD2).replace('.', ','));
		map.put("totalSumGrosD2", String.format("%.2f", totalSumNetD2 + totalSumVatD2).replace('.', ','));
		map.put("totalSumNetD3", String.format("%.2f", totalSumNetD3).replace('.', ','));
		map.put("totalSumVatD3", String.format("%.2f", totalSumVatD3).replace('.', ','));
		map.put("totalSumGrosD3", String.format("%.2f", totalSumNetD3 + totalSumVatD3).replace('.', ','));
		map.put("totalSumNetD4", String.format("%.2f", totalSumNetD4).replace('.', ','));
		map.put("totalSumVatD4", String.format("%.2f", totalSumVatD4).replace('.', ','));
		map.put("totalSumGrosD4", String.format("%.2f", totalSumNetD4 + totalSumVatD4).replace('.', ','));
		
		Double totalSumNetD = totalSumNetD1 + totalSumNetD2 + totalSumNetD3 + totalSumNetD4;
		Double totalSumVatD = totalSumVatD1 + totalSumVatD2 + totalSumVatD3 + totalSumVatD4;
		Double totalSumGrosD = totalSumNetD1 + totalSumVatD1 + totalSumNetD2 + totalSumVatD2 + totalSumNetD3 + totalSumVatD3 + totalSumNetD4 + totalSumVatD4;
		
		map.put("totalSumNetD", String.format("%.2f", totalSumNetD).replace('.', ','));
		map.put("totalSumVatD", String.format("%.2f", totalSumVatD).replace('.', ','));
		map.put("totalSumGrosD", String.format("%.2f", totalSumGrosD).replace('.', ','));
		
		map.put("totalSumNetC1", String.format("%.2f", totalSumNetC1).replace('.', ','));
		map.put("totalSumVatC1", String.format("%.2f", totalSumVatC1).replace('.', ','));
		map.put("totalSumGrosC1", String.format("%.2f", totalSumNetC1 + totalSumVatC1).replace('.', ','));
		map.put("totalSumNetC2", String.format("%.2f", totalSumNetC2).replace('.', ','));
		map.put("totalSumVatC2", String.format("%.2f", totalSumVatC2).replace('.', ','));
		map.put("totalSumGrosC2", String.format("%.2f", totalSumNetC2 + totalSumVatC2).replace('.', ','));
		map.put("totalSumNetC3", String.format("%.2f", totalSumNetC3).replace('.', ','));
		map.put("totalSumVatC3", String.format("%.2f", totalSumVatC3).replace('.', ','));
		map.put("totalSumGrosC3", String.format("%.2f", totalSumNetC3 + totalSumVatC3).replace('.', ','));
		map.put("totalSumNetC4", String.format("%.2f", totalSumNetC4).replace('.', ','));
		map.put("totalSumVatC4", String.format("%.2f", totalSumVatC4).replace('.', ','));
		map.put("totalSumGrosC4", String.format("%.2f", totalSumNetC4 + totalSumVatC4).replace('.', ','));
		
		Double totalSumNetC = totalSumNetC1 + totalSumNetC2 + totalSumNetC3 + totalSumNetC4;
		Double totalSumVatC = totalSumVatC1 + totalSumVatC2 + totalSumVatC3 + totalSumVatC4;
		Double totalSumGrosC = totalSumNetC1 + totalSumVatC1 + totalSumNetC2 + totalSumVatC2 + totalSumNetC3 + totalSumVatC3 + totalSumNetC4 + totalSumVatC4;
		
		map.put("totalSumNetC", String.format("%.2f", totalSumNetC).replace('.', ','));
		map.put("totalSumVatC", String.format("%.2f", totalSumVatC).replace('.', ','));
		map.put("totalSumGrosC", String.format("%.2f", totalSumGrosC).replace('.', ','));
		if(totalSumVatD - totalSumVatC > 0) {
			map.put("finalVat", String.format("%.2f", totalSumVatD - totalSumVatC).replace('.', ','));
		} else {
			map.put("finalVat", String.format("%.2f", 0.00).replace('.', ','));
		}
		
		BigDecimal bgTotalSumVatD = new BigDecimal(String.valueOf(totalSumVatD.intValue()));
		BigDecimal bgTotalSumVatC = new BigDecimal(String.valueOf(totalSumVatC.intValue()));
		
		if(updateMonthWithTaxVat(conn, bgTotalSumVatD, bgTotalSumVatC)) {
			ExcelCreation createLedger = new ExcelCreation(System.getProperty("user.dir") + "/Templates/template_vat.xlsx" , 0, System.getProperty("user.dir") + "/CreatedFiles/VAT_" + year + "_" + getUserNip(conn) + ".xlsx", month + " " + year, map, month + " " + year);
		    createLedger.doExcel();
	    	value = true;
	    } else {
	    	System.out.println("Nie zaktualizowano tabeli miesiac z podatkiem VAT");
	    }
		return value;
	}
	
	private Boolean updateMonthWithTax(Connection conn, BigDecimal tax) {
		Boolean result = false;
		int monthNumber = monthsMap.get(month);
			try {
				String query = String.format("update miesiac set podatek = '%s' "
						+ "where id_miesiac = (select miesiac.id_miesiac from miesiac, rok, uzytkownik where miesiac.id_rok = rok.id_rok "
						+ "and rok.id_uzytkownik = uzytkownik.id_uzytkownik and uzytkownik.id_uzytkownik = '%d' and month(miesiac.data) = '%d' and year(rok.data) = '%s')", tax, id_uzytkownik, monthNumber, year); 
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
	
	private Boolean updateLedgerWithName(Connection conn, String name) {
		Boolean result = false;
			try {
				String query = String.format("update rok set nazwa = '%s' "
						+ "where id_rok = (select rok.id_rok from rok, uzytkownik where uzytkownik.id_uzytkownik = rok.id_uzytkownik "
						+ "and uzytkownik.id_uzytkownik = '%d' and year(rok.data) = '%s')", name, id_uzytkownik, year); 
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
	
	private Boolean updateMonthWithTaxVat(Connection conn, BigDecimal taxD, BigDecimal taxC) {
		Boolean result = false;
		int monthNumber = monthsMap.get(month);
			try {
				String query = String.format("update miesiac set vat_nalezny = '%s', vat_naliczony = '%s' "
						+ "where id_miesiac = (select miesiac.id_miesiac from miesiac, rok, uzytkownik where miesiac.id_rok = rok.id_rok "
						+ "and rok.id_uzytkownik = uzytkownik.id_uzytkownik and uzytkownik.id_uzytkownik = '%d' and month(miesiac.data) = '%d' and year(rok.data) = '%s')", taxD, taxC, id_uzytkownik, monthNumber, year); 
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
	
	private String getTaxWay(Connection conn) {
		String way = "";
		try {
			String query = String.format("select sposob_opodatkowania.nazwa "
						+ "from uzytkownik, sposob_opodatkowania " 
					+ "where uzytkownik.id_sposob_opodatkowania = sposob_opodatkowania.id_sposob_opodatkowania "
					+ "and uzytkownik.id_uzytkownik = '%d'", id_uzytkownik);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				way = rs.getString(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania nazwy sposobu opodatkowania: " + ex);
		}
		return way;
	}
	
	private Double getMonthTaxes(Connection conn) {
		Double tax = 0.00;
		int monthNumber = monthsMap.get(month);
		try {
			String query = String.format("select miesiac.podatek "
						+ "from miesiac, uzytkownik, rok " 
					+ "where miesiac.id_rok = rok.id_rok "
					+ "and rok.id_uzytkownik = uzytkownik.id_uzytkownik "
					+ "and uzytkownik.id_uzytkownik = '%d' "
					+ "and month(miesiac.data) >= '1' "
					+ "and month(miesiac.data) < '%d' "
					+ "and year(rok.data) = '%s'", id_uzytkownik, monthNumber, year);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				tax += rs.getDouble(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych miesiaca: " + ex);
		}
		return tax;
	}
	
	private Month getMonthSums(Connection conn) {
		Month monthModel = new Month();
		Double income = 0.00;
		Double incomeR = 0.00;
		Double buyG = 0.00;
		Double incomeIn = 0.00;
		Double reward = 0.00;
		Double expense = 0.00;
		Double expenseRes = 0.00;
		int monthNumber = monthsMap.get(month);
		try {
			String query = String.format("select miesiac.suma_wartosc_sprz_towar, miesiac.suma_poz_przych, miesiac.suma_zak_towar, "
						+ "miesiac.suma_koszt_ub, miesiac.suma_wynagrodzen, miesiac.suma_wydatkow, miesiac.suma_koszt_bad_rozw "
						+ "from miesiac, uzytkownik, rok " 
					+ "where miesiac.id_rok = rok.id_rok "
					+ "and rok.id_uzytkownik = uzytkownik.id_uzytkownik "
					+ "and uzytkownik.id_uzytkownik = '%d' "
					+ "and month(miesiac.data) >= '1' "
					+ "and month(miesiac.data) < '%d' "
					+ "and year(rok.data) = '%s'", id_uzytkownik, monthNumber, year);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				income += rs.getDouble(1);
				incomeR += rs.getDouble(2);
				buyG += rs.getDouble(3);
				incomeIn += rs.getDouble(4);
				reward += rs.getDouble(5);
				expense += rs.getDouble(6);
				expenseRes += rs.getDouble(7);	
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych miesiaca: " + ex);
		}
		monthModel.setIncome(income);
		monthModel.setIncomeR(incomeR);
		monthModel.setBuyG(buyG);
		monthModel.setExpenseIn(incomeIn);
		monthModel.setReward(reward);
		monthModel.setExpense(expense);
		monthModel.setExpenseRes(expenseRes);
		return monthModel;
	}
	
	private Boolean updateMonthWithSums(Connection conn, BigDecimal incomeSum, BigDecimal incomeRSum, BigDecimal buyGSum, BigDecimal expenseInSum, BigDecimal rewardSum, BigDecimal expenseSum, BigDecimal expenseResSum) {
		Boolean result = false;
		int monthNumber = monthsMap.get(month);
		if(monthNumber == 1) {
			try {
				String query = String.format("update miesiac set suma_wartosc_sprz_towar = '%s', suma_poz_przych = '%s', suma_zak_towar = '%s', "
						+ "suma_koszt_ub = '%s', suma_wynagrodzen = '%s', suma_wydatkow = '%s', suma_koszt_bad_rozw = '%s', lp = %d "
						+ "where id_miesiac = (select miesiac.id_miesiac from miesiac, rok, uzytkownik where miesiac.id_rok = rok.id_rok "
						+ "and rok.id_uzytkownik = uzytkownik.id_uzytkownik and uzytkownik.id_uzytkownik = '%d' and month(miesiac.data) = '%d' and year(rok.data) = '%s')", incomeSum, incomeRSum, buyGSum, expenseInSum, rewardSum, expenseSum, expenseResSum, getDocumentOrderNumber(conn), id_uzytkownik, monthNumber, year); 
				Statement stm = conn.createStatement();
				int count = stm.executeUpdate(query);
				System.out.println("Liczba dodanych rekordów " + count);
				result = true;
			} catch (SQLException e) {
				System.out.println("B³¹d przy przetwarzaniu danych: " + e);
				result = false;
			}
		} else {
			try {
				String query = String.format("update miesiac set suma_wartosc_sprz_towar = '%s', suma_poz_przych = '%s', suma_zak_towar = '%s', "
						+ "suma_koszt_ub = '%s', suma_wynagrodzen = '%s', suma_wydatkow = '%s', suma_koszt_bad_rozw = '%s', lp = %d "
						+ "where id_miesiac = (select miesiac.id_miesiac from miesiac, rok where miesiac.id_rok = rok.id_rok "
						+ "and month(miesiac.data) = '%d' and year(rok.data) = '%s')", incomeSum, incomeRSum, buyGSum, expenseInSum, rewardSum, expenseSum, expenseResSum, getDocumentOrderNumber(conn) + getOrderNumberFromLastMonth(conn), monthNumber, year); 
				Statement stm = conn.createStatement();
				int count = stm.executeUpdate(query);
				System.out.println("Liczba dodanych rekordów " + count);
				result = true;
			} catch (SQLException e) {
				System.out.println("B³¹d przy przetwarzaniu danych: " + e);
				result = false;
			}
		}
		
		return result;
	}
	
	private Boolean addNewDocument(Connection conn, String numberDocument, String documentType, Date date, String netAmount1, String vatAmount1, String netAmount2, String vatAmount2, String netAmount3, String vatAmount3, String netAmount4, String vatAmount4, String netAmount5, String vatAmount5, String grossAmount, String description, String nameContractor, String addressContractor) {
		Boolean result;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println(dateFormat.format(date));
		int idDocumentType = getIdDocumentType(conn, documentType);
		int idMonth = getIdMonth(conn);
		int orderNumber = 0;
		int idContractor = 0; 
		int idDocument = 0;
		idContractor = getIdContractor(conn, nameContractor, addressContractor);
		if(idContractor==0) {
			idContractor = addContractor(conn, nameContractor, addressContractor);
			if(idContractor==0) {
				return false;
			}
		}
		if(monthsMap.get(month) == 1) {
			orderNumber = getDocumentOrderNumber(conn) + 1;
		} else {
			orderNumber = getOrderNumberFromLastMonth(conn) + getDocumentOrderNumber(conn) + 1;
		}
		
		BigDecimal netA1;
		BigDecimal netA2;
		BigDecimal netA3;
		BigDecimal netA4;
		BigDecimal netA5;
		BigDecimal grossA;
		BigDecimal vatA1;
		BigDecimal vatA2;
		BigDecimal vatA3;
		BigDecimal vatA4;
		BigDecimal vatA5;
		String strGrossAmount = grossAmount.replaceAll(",",".");
		System.out.println(strGrossAmount);
		grossA = new BigDecimal(strGrossAmount);
		System.out.println(grossA);
			netA1 = new BigDecimal(netAmount1.replaceAll(",","."));
			netA1 = netA1.setScale(2, RoundingMode.HALF_UP);
			vatA1 = new BigDecimal(vatAmount1.replaceAll(",","."));
			vatA1 = vatA1.setScale(2, RoundingMode.HALF_UP);
			netA2 = new BigDecimal(netAmount2.replaceAll(",","."));
			netA2 = netA2.setScale(2, RoundingMode.HALF_UP);
			vatA2 = new BigDecimal(vatAmount2.replaceAll(",","."));
			vatA2 = vatA2.setScale(2, RoundingMode.HALF_UP);
			netA3 = new BigDecimal(netAmount3.replaceAll(",","."));
			netA3 = netA3.setScale(2, RoundingMode.HALF_UP);
			vatA3 = new BigDecimal(vatAmount3.replaceAll(",","."));
			vatA3 = vatA3.setScale(2, RoundingMode.HALF_UP);
			netA4 = new BigDecimal(netAmount4.replaceAll(",","."));
			netA4 = netA4.setScale(2, RoundingMode.HALF_UP);
			vatA4 = new BigDecimal(vatAmount4.replaceAll(",","."));
			vatA4 = vatA4.setScale(2, RoundingMode.HALF_UP);
			netA5 = new BigDecimal(netAmount5.replaceAll(",","."));
			netA5 = netA5.setScale(2, RoundingMode.HALF_UP);
			vatA5 = new BigDecimal(vatAmount5.replaceAll(",","."));
			vatA5 = vatA5.setScale(2, RoundingMode.HALF_UP);
			try {
				String query = String.format("insert into dokument_ksiegowy(id_uzytkownik, id_typ_dokumentu, id_miesiac, id_kontrahent, numer, data, "
						+ "kwota_brutto, opis, lp) "
						+ "values('%d','%d','%d','%d','%s','%s','%s','%s','%d')", 
						id_uzytkownik, idDocumentType, idMonth, idContractor, numberDocument, dateFormat.format(date), grossA, description, orderNumber); 
				Statement stm = conn.createStatement();
				idDocument = stm.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
				ResultSet rs = stm.getGeneratedKeys();
				if (rs.next()){
				    idDocument=rs.getInt(1);
				}
				System.out.println("Id dokumentu" + idDocument);
				result = true;
			} catch (SQLException e) {
				System.out.println("B³¹d przy przetwarzaniu danych: " + e);
				result = false;
			}
			if(netA1.compareTo(BigDecimal.ZERO) != 0) {
				if(netA2.compareTo(BigDecimal.ZERO) != 0) {
					if(netA3.compareTo(BigDecimal.ZERO) != 0) {
						if(netA4.compareTo(BigDecimal.ZERO) != 0) {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1) 
										&& insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2) 
										&& insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3) 
										&& insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4) 
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1) 
										&& insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2) 
										&& insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3) 
										&& insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4)) {
								
								} else {
									result = false;
								}
							}
						} else {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1) 
										&& insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2) 
										&& insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3) 
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1) 
										&& insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2) 
										&& insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3)) {
								
								} else {
									result = false;
								}
							}
						}
					} else {
						if(netA4.compareTo(BigDecimal.ZERO) != 0) {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1) 
										&& insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2)  
										&& insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4) 
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1) 
										&& insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2)  
										&& insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4)) {
								
								} else {
									result = false;
								}
							}
						} else {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1) 
										&& insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2)  
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1) 
										&& insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2)) {
								
								} else {
									result = false;
								}
							}
						}
					}
				} else {
					if(netA3.compareTo(BigDecimal.ZERO) != 0) {
						if(netA4.compareTo(BigDecimal.ZERO) != 0) {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1)  
										&& insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3) 
										&& insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4) 
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1)  
										&& insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3) 
										&& insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4)) {
								
								} else {
									result = false;
								}
							}
						} else {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1)  
										&& insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3) 
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1) 
										&& insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3)) {
								
								} else {
									result = false;
								}
							}
						}
					} else {
						if(netA4.compareTo(BigDecimal.ZERO) != 0) {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1) 
										&& insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4) 
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1)  
										&& insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4)) {
								
								} else {
									result = false;
								}
							}
						} else {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1) 
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 1, netA1) && insertVatAmountToDocument(conn, idDocument, 1, vatA1)) {
								
								} else {
									result = false;
								}
							}
						}
					}
				}
			} else {
				if(netA2.compareTo(BigDecimal.ZERO) != 0) {
					if(netA3.compareTo(BigDecimal.ZERO) != 0) {
						if(netA4.compareTo(BigDecimal.ZERO) != 0) {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2) 
										&& insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3) 
										&& insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4) 
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2) 
										&& insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3) 
										&& insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4)) {
								
								} else {
									result = false;
								}
							}
						} else {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2) 
										&& insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3) 
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2) 
										&& insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3)) {
								
								} else {
									result = false;
								}
							}
						}
					} else {
						if(netA4.compareTo(BigDecimal.ZERO) != 0) {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2)  
										&& insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4) 
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2)  
										&& insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4)) {
								
								} else {
									result = false;
								}
							}
						} else {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2)  
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 2, netA2) && insertVatAmountToDocument(conn, idDocument, 2, vatA2)) {
								
								} else {
									result = false;
								}
							}
						}
					}
				} else {
					if(netA3.compareTo(BigDecimal.ZERO) != 0) {
						if(netA4.compareTo(BigDecimal.ZERO) != 0) {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3) 
										&& insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4) 
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3) 
										&& insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4)) {
								
								} else {
									result = false;
								}
							}
						} else {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3) 
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 3, netA3) && insertVatAmountToDocument(conn, idDocument, 3, vatA3)) {
								
								} else {
									result = false;
								}
							}
						}
					} else {
						if(netA4.compareTo(BigDecimal.ZERO) != 0) {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4) 
										&& insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
								if(insertNetAmountToDocument(conn, idDocument, 4, netA4) && insertVatAmountToDocument(conn, idDocument, 4, vatA4)) {
								
								} else {
									result = false;
								}
							}
						} else {
							if(netA5.compareTo(BigDecimal.ZERO) != 0) {
								if(insertNetAmountToDocument(conn,idDocument, 5, netA5) && insertVatAmountToDocument(conn, idDocument, 5, vatA5)) {
								
								} else {
									result = false;
								}
							} else {
									result = false;
							}
						}
					}
				}
			}
		return result;
	}
	
	private Boolean insertNetAmountToDocument(Connection conn, int idDocument, int idTax, BigDecimal netAmount) {
		Boolean value = false;
		int idAmount = 0;
		try {
			String query = String.format("insert into kwota_netto (id_stawka_podatku, wartosc) "
					+ "values('%d', '%s')", idTax, netAmount); 
			Statement stm = conn.createStatement();
			idAmount = stm.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stm.getGeneratedKeys();
			if (rs.next()){
			    idAmount=rs.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("B³¹d przy przetwarzaniu danych: " + e);
		}
		try {
			String query = String.format("insert into dokument_kwota_netto (id_dokument_ksiegowy, id_kwota_netto) "
					+ "values('%d', '%d')", idDocument, idAmount); 
			Statement stm = conn.createStatement();
			int count = stm.executeUpdate(query);
			System.out.println("Ilosc dodanych rekordow" + count);
			value = true;
		} catch (SQLException e) {
			System.out.println("B³¹d przy przetwarzaniu danych: " + e);
			value = false;
		}
		return value;
	}
	
	private Boolean insertVatAmountToDocument(Connection conn, int idDocument, int idTax, BigDecimal vatAmount) {
		Boolean value = false;
		int idAmount = 0;
		try {
			String query = String.format("insert into kwota_vat (id_stawka_podatku, wartosc) "
					+ "values('%d', '%s')", idTax, vatAmount); 
			Statement stm = conn.createStatement();
			idAmount = stm.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stm.getGeneratedKeys();
			if (rs.next()){
			    idAmount=rs.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("B³¹d przy przetwarzaniu danych: " + e);
		}
		try {
			String query = String.format("insert into dokument_kwota_vat (id_dokument_ksiegowy, id_kwota_vat) "
					+ "values('%d', '%d')", idDocument, idAmount); 
			Statement stm = conn.createStatement();
			int count = stm.executeUpdate(query);
			System.out.println("Ilosc dodanych rekordow " + count);
			value = true;
		} catch (SQLException e) {
			System.out.println("B³¹d przy przetwarzaniu danych: " + e);
			value = false;
		}
		return value;
	}
	
	private int getIdDocumentType(Connection conn, String nazwa) {
		int id = 0;
		try {
			String query = String.format("select id_typ_dokumentu from typ_dokumentu where nazwa = '%s'", nazwa);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				id = rs.getInt(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania id typu_dokumentu: " + ex);
		}
		return id;
	}
	
	private int getDocumentOrderNumber(Connection conn) {
		int id = 0;
		int monthNumber = monthsMap.get(month);
		try {
			String query = String.format("select count(*) from dokument_ksiegowy, typ_dokumentu, miesiac, uzytkownik, rok " 
					+ "where dokument_ksiegowy.id_uzytkownik = uzytkownik.id_uzytkownik "
					+ "and dokument_ksiegowy.id_typ_dokumentu = typ_dokumentu.id_typ_dokumentu "
					+ "and dokument_ksiegowy.id_miesiac = miesiac.id_miesiac "
					+ "and miesiac.id_rok = rok.id_rok "
					+ "and typ_dokumentu.nazwa != 'zakup œrodków trwa³ych' "
					+ "and typ_dokumentu.nazwa != 'zakup wyposa¿enia' "
					+ "and uzytkownik.id_uzytkownik = '%d' "
					+ "and month(miesiac.data) = '%d' "
					+ "and year(rok.data) = '%s'", id_uzytkownik, monthNumber, year);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				id = rs.getInt(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania id miesiaca: " + ex);
		}
		return id;
	}
	
	private int getOrderNumberFromLastMonth(Connection conn) {
		int id = 0;
		int monthNumber = monthsMap.get(month);
		try {
			String query = String.format("select lp from miesiac, uzytkownik, rok " 
					+ "where miesiac.id_rok = rok.id_rok "
					+ "and rok.id_uzytkownik = uzytkownik.id_uzytkownik "
					+ "and uzytkownik.id_uzytkownik = '%d' "
					+ "and month(miesiac.data) = '%d' "
					+ "and year(rok.data) = '%s'", id_uzytkownik, monthNumber - 1, year);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				id = rs.getInt(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania lp miesiaca: " + ex);
		}
		return id;
	}
	
	private int getIdMonth(Connection conn) {
		int id = 0;
		int monthNumber = monthsMap.get(month);
		try {
			String query = String.format("select miesiac.id_miesiac from miesiac, rok, uzytkownik where miesiac.id_rok = rok.id_rok "
					+ "and rok.id_uzytkownik = uzytkownik.id_uzytkownik "
					+ "and uzytkownik.id_uzytkownik = '%d' "
					+ "and year(rok.data) = '%s' "
					+ "and month(miesiac.data) = '%d'", id_uzytkownik, year, monthNumber);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				id = rs.getInt(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania id miesiaca: " + ex);
		}
		return id;
	}
	
	private int getIdContractor(Connection conn, String name, String address) {
		int id = 0;
		try {
			String query = String.format("select kontrahent.id_kontrahent from kontrahent "
					+ "where lower(kontrahent.nazwa) = lower('%s')", name);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				id = rs.getInt(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania id kontrahenta: " + ex);
		}
		return id;
	}
	
	private int addContractor(Connection conn, String name, String address) {
		int id = 0;
		try {
			String query = String.format("insert into kontrahent(nazwa,adres) values('%s','%s')", name, address); 
			Statement stm = conn.createStatement();
			id = stm.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			System.out.println("ID dodanego rekordu " + id);
		} catch (SQLException e) {
			System.out.println("B³¹d przy przetwarzaniu danych: " + e);
		}
		return id;
	}
	
	public void initData(int id_uzytkownik, String year, String month) {
		this.id_uzytkownik = id_uzytkownik;
		this.year = year;
		this.month = month;
		Date input = new Date();
		LocalDate date = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		datePickerDateDocument.setValue(date);
		contractors = null;
		nameContractors = new ArrayList<>();
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
		int monthNumber = monthsMap.get(month);
		textNewDocument.setText("Dodawanie dokumentów na " + month + " " + year);
		tcNumber.setCellValueFactory(new PropertyValueFactory<DocumentTable, String>("number"));
		tcMoney.setCellValueFactory(new PropertyValueFactory<DocumentTable, String>("grossAmount"));
		tcName.setCellValueFactory(new PropertyValueFactory<DocumentTable, String>("nameContractor"));
		tcDate.setCellValueFactory(new PropertyValueFactory<DocumentTable, String>("date"));
		tcDocumentType.setCellValueFactory(new PropertyValueFactory<DocumentTable, String>("documentType"));
		Connection conn;
		String connStr = "jdbc:h2:~/db/ledgerdatabase;";
		conn = openConnection(connStr);
		choiceBoxDocumentTypes.setItems(FXCollections.observableArrayList(getDocumentTypes(conn)));
		tableViewDocuments.setItems(FXCollections.observableArrayList(getDocuments(conn, monthNumber)));
		contractors = getContractors(conn);
		for ( String key : contractors.keySet() ) {
		    nameContractors.add(key);
		}
		comboBoxNameContractor.setItems(FXCollections.observableArrayList(nameContractors));
		closeConnection(conn);
		setAmountFields();
		textFieldAddressContractor.setPromptText("[Miejscowoœæ] [kod_pocztowy], [ulica] [numer]");
		choiceBoxDocumentTypes.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue.equals("zakup œrodków trwa³ych")) {
					textError.setText("Amortyzacja w szczegó³ach, kwota > 3500 z³");
					Tooltip tooltipDes = new Tooltip();
					tooltipDes.setText("Przyk³adowo samochód - typ, marka, pojemnoœæ silnika,\n numer rejestracyjny, numer podwozia/nadwozia,\n rok produkcji");
					textAreaDescription.setTooltip(tooltipDes);
				} else if(newValue.equals("zakup wyposa¿enia")) {
					textError.setText("U¿ytkowanie poni¿ej roku, kwota > 1500 z³");
				}
			}
		});
	}
	
	private HashMap<String, String> getContractors(Connection conn) {
		HashMap<String, String> list = new LinkedHashMap<>();
		try {
			String query = "select nazwa, adres from kontrahent";
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				list.put(rs.getString(1), rs.getString(2));
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych o kontrahentach: " + ex);
		}
		return list;
	}
	
	private void setAmountFields() {
		DecimalFormat format = new DecimalFormat( "#.00" );

		textFieldNet1.setTextFormatter( new TextFormatter<>(c ->
		{
		    if ( c.getControlNewText().isEmpty() )
		    {
		        return c;
		    }

		    ParsePosition parsePosition = new ParsePosition( 0 );
		    Object object = format.parse( c.getControlNewText(), parsePosition );

		    if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
		    {
		        return null;
		    }
		    else
		    {
		        return c;
		    }
		}));
		textFieldVat1.setTextFormatter( new TextFormatter<>(c ->
		{
		    if ( c.getControlNewText().isEmpty() )
		    {
		        return c;
		    }

		    ParsePosition parsePosition = new ParsePosition( 0 );
		    Object object = format.parse( c.getControlNewText(), parsePosition );

		    if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
		    {
		        return null;
		    }
		    else
		    {
		        return c;
		    }
		}));
		textFieldNet2.setTextFormatter( new TextFormatter<>(c ->
		{
		    if ( c.getControlNewText().isEmpty() )
		    {
		        return c;
		    }

		    ParsePosition parsePosition = new ParsePosition( 0 );
		    Object object = format.parse( c.getControlNewText(), parsePosition );

		    if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
		    {
		        return null;
		    }
		    else
		    {
		        return c;
		    }
		}));
		textFieldVat2.setTextFormatter( new TextFormatter<>(c ->
		{
		    if ( c.getControlNewText().isEmpty() )
		    {
		        return c;
		    }

		    ParsePosition parsePosition = new ParsePosition( 0 );
		    Object object = format.parse( c.getControlNewText(), parsePosition );

		    if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
		    {
		        return null;
		    }
		    else
		    {
		        return c;
		    }
		}));
		textFieldNet3.setTextFormatter( new TextFormatter<>(c ->
		{
		    if ( c.getControlNewText().isEmpty() )
		    {
		        return c;
		    }

		    ParsePosition parsePosition = new ParsePosition( 0 );
		    Object object = format.parse( c.getControlNewText(), parsePosition );

		    if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
		    {
		        return null;
		    }
		    else
		    {
		        return c;
		    }
		}));
		textFieldVat3.setTextFormatter( new TextFormatter<>(c ->
		{
		    if ( c.getControlNewText().isEmpty() )
		    {
		        return c;
		    }

		    ParsePosition parsePosition = new ParsePosition( 0 );
		    Object object = format.parse( c.getControlNewText(), parsePosition );

		    if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
		    {
		        return null;
		    }
		    else
		    {
		        return c;
		    }
		}));
		textFieldNet4.setTextFormatter( new TextFormatter<>(c ->
		{
		    if ( c.getControlNewText().isEmpty() )
		    {
		        return c;
		    }

		    ParsePosition parsePosition = new ParsePosition( 0 );
		    Object object = format.parse( c.getControlNewText(), parsePosition );

		    if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
		    {
		        return null;
		    }
		    else
		    {
		        return c;
		    }
		}));
		textFieldVat4.setTextFormatter( new TextFormatter<>(c ->
		{
		    if ( c.getControlNewText().isEmpty() )
		    {
		        return c;
		    }

		    ParsePosition parsePosition = new ParsePosition( 0 );
		    Object object = format.parse( c.getControlNewText(), parsePosition );

		    if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
		    {
		        return null;
		    }
		    else
		    {
		        return c;
		    }
		}));
		textFieldNet5.setTextFormatter( new TextFormatter<>(c ->
		{
		    if ( c.getControlNewText().isEmpty() )
		    {
		        return c;
		    }

		    ParsePosition parsePosition = new ParsePosition( 0 );
		    Object object = format.parse( c.getControlNewText(), parsePosition );

		    if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
		    {
		        return null;
		    }
		    else
		    {
		        return c;
		    }
		}));
		textFieldVat5.setTextFormatter( new TextFormatter<>(c ->
		{
		    if ( c.getControlNewText().isEmpty() )
		    {
		        return c;
		    }

		    ParsePosition parsePosition = new ParsePosition( 0 );
		    Object object = format.parse( c.getControlNewText(), parsePosition );

		    if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
		    {
		        return null;
		    }
		    else
		    {
		        return c;
		    }
		}));
		textFieldNet1.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(!newValue.isEmpty()) {
					textFieldVat1.setText(String.format("%.2f", Double.valueOf(newValue.replace(',', '.')) * 0.23).replace('.', ','));
				}
			}
			
		});
		textFieldNet2.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(!newValue.isEmpty()) {
					textFieldVat2.setText(String.format("%.2f", Double.valueOf(newValue.replace(',', '.')) * 0.08).replace('.', ','));
				}
			}
			
		});
		textFieldNet3.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(!newValue.isEmpty()) {
					textFieldVat3.setText(String.format("%.2f", Double.valueOf(newValue.replace(',', '.')) * 0.05).replace('.', ','));
				}
				
			}
			
		});
		textFieldNet4.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				textFieldVat4.setText("0,00");
			}
			
		});
		textFieldNet5.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				textFieldVat5.setText("0,00");
			}
			
		});
	}
	
	private ArrayList<DocumentTable> getDocuments(Connection conn, int month) {
		ArrayList<DocumentTable> documents = new ArrayList<>();
		try {
			String query = String.format("select dokument_ksiegowy.id_dokument_ksiegowy, dokument_ksiegowy.numer, dokument_ksiegowy.data, dokument_ksiegowy.kwota_brutto, kontrahent.nazwa, typ_dokumentu.nazwa "
			+ "from dokument_ksiegowy, typ_dokumentu, miesiac, uzytkownik, rok, kontrahent " 
			+ "where dokument_ksiegowy.id_uzytkownik = uzytkownik.id_uzytkownik "
			+ "and dokument_ksiegowy.id_typ_dokumentu = typ_dokumentu.id_typ_dokumentu "
			+ "and dokument_ksiegowy.id_kontrahent = kontrahent.id_kontrahent "
			+ "and dokument_ksiegowy.id_miesiac = miesiac.id_miesiac "
			+ "and miesiac.id_rok = rok.id_rok "
			+ "and uzytkownik.id_uzytkownik = '%d' "
			+ "and month(miesiac.data) = '%d' "
			+ "and year(rok.data) = '%s' "
			+ "group by typ_dokumentu.nazwa, dokument_ksiegowy.id_dokument_ksiegowy  "
			+ "order by typ_dokumentu.nazwa", id_uzytkownik, month, year);
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
	
	private String getUserNip(Connection conn) {
		String nip = "";
		try {
			String query = String.format("select nip from uzytkownik where id_uzytkownik = '%d'", id_uzytkownik);
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				nip = rs.getString(1);
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych o u¿ytkowniku: " + ex);
		}
		return nip;
	}
	
	private ArrayList<String> getDocumentTypes(Connection conn) {
		ArrayList<String> documentTypes = new ArrayList<>();
		try {
			String query = "select nazwa from typ_dokumentu";
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while(rs.next()) {
				documentTypes.add(rs.getString(1));
			}
		} catch(SQLException ex) {
			System.out.println("B³¹d pobierania danych o typach dokumentów: " + ex);
		}
		return documentTypes;
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
