package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ShowDataMenuController {

	private int id_uzytkownik;
	@FXML
	private Button btnLogOut;
	@FXML
	private Button btnShowLedgers;
	@FXML
	private Button btnShowAddedDocuments;
	@FXML
	private Button btnGeneratedTaxes;
	@FXML
	private Button btnEquipment;
	@FXML
	private Button btnFixedAssets;
	@FXML
	private Button btnMainMenu;
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
		} else if(event.getSource()==btnShowLedgers) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("LedgersVatFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            Scene scene = new Scene(loader.load());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            LedgersVatController ledgersVatController = loader.<LedgersVatController>getController();
            ledgersVatController.initData(id_uzytkownik);
            stage.setResizable(false);
            stage.show();
		} else if(event.getSource()==btnShowAddedDocuments) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("DocumentsFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            Scene scene = new Scene(loader.load());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            DocumentsController documentsController = loader.<DocumentsController>getController();
            documentsController.initData(id_uzytkownik);
            stage.setResizable(false);
            stage.show();
		} else if (event.getSource()==btnGeneratedTaxes) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("GeneratedTaxesFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            Scene scene = new Scene(loader.load());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            GeneratedTaxesController generatedTaxesController = loader.<GeneratedTaxesController>getController();
            generatedTaxesController.initData(id_uzytkownik);
            stage.setResizable(false);
            stage.show();
		} else if (event.getSource()==btnEquipment) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EquipmentFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            Scene scene = new Scene(loader.load());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            EquipmentController equipmentController = loader.<EquipmentController>getController();
            equipmentController.initData(id_uzytkownik);
            stage.setResizable(false);
            stage.show();
		} else if (event.getSource()==btnFixedAssets) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("FixedAssetsFXML.fxml"));
            stage = (Stage) anchorPaneEditor.getScene().getWindow();
            Scene scene = new Scene(loader.load());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            FixedAssetsController fixedAssetsController = loader.<FixedAssetsController>getController();
            fixedAssetsController.initData(id_uzytkownik);
            stage.setResizable(false);
            stage.show();
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
	
	public void initData(int id_uzytkownik) {
		this.id_uzytkownik = id_uzytkownik;
	}
}
