package co.edu.uniquindio.poo.controllers;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import co.edu.uniquindio.poo.model.Patient;
import co.edu.uniquindio.poo.services.ClinicDataStore;
import co.edu.uniquindio.poo.services.PatientFactory;
import co.edu.uniquindio.poo.services.PersonFactory;

public class PatientController {
    private final ClinicDataStore dataStore = ClinicDataStore.getInstance();
    private final ObservableList<Patient> patientData;
    private final PersonFactory patientFactory = new PatientFactory();
    private TableView<Patient> patientTable;

    public PatientController(ObservableList<Patient> patientData) {
        this.patientData = patientData;
    }

    public VBox createView() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f5f5f5;");

        TitledPane formPane = new TitledPane("👤 Registrar Nuevo Paciente", createPatientForm());
        formPane.setCollapsible(false);

        patientTable = createPatientTable();

        Label tableLabel = new Label("📋 Lista de Pacientes");
        tableLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        content.getChildren().addAll(formPane, new Separator(), tableLabel, patientTable);
        return content;
    }

    private TableView<Patient> createPatientTable() {
        TableView<Patient> table = new TableView<>(patientData);

        TableColumn<Patient, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Patient, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, String> phoneCol = new TableColumn<>("Teléfono");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Patient, String> historyCol = new TableColumn<>("Historia");
        historyCol.setCellValueFactory(new PropertyValueFactory<>("historyNumber"));

        TableColumn<Patient, String> addressCol = new TableColumn<>("Dirección");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        table.getColumns().addAll(idCol, nameCol, phoneCol, historyCol, addressCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPatientDetails(newValue));

        return table;
    }

    private GridPane createPatientForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField historyField = new TextField();
        TextField addressField = new TextField();

        grid.addRow(0, new Label("ID:"), idField);
        grid.addRow(1, new Label("Nombre:"), nameField);
        grid.addRow(2, new Label("Teléfono:"), phoneField);
        grid.addRow(3, new Label("Historia:"), historyField);
        grid.addRow(4, new Label("Dirección:"), addressField);

        Button saveButton = new Button("✓ Guardar Paciente");
        saveButton.getStyleClass().add("success-button");
        saveButton.setOnAction(e -> {
            try {
                Patient patient = (Patient) patientFactory.createPerson(
                        idField.getText(),
                        nameField.getText(),
                        phoneField.getText(),
                        historyField.getText(),
                        addressField.getText());

                dataStore.addPatient(patient);

                idField.clear();
                nameField.clear();
                phoneField.clear();
                historyField.clear();
                addressField.clear();

                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Paciente registrado correctamente.");

            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error de Registro", "Verifique todos los campos.");
            }
        });

        grid.add(saveButton, 1, 5);
        return grid;
    }

    private void showPatientDetails(Patient patient) {
        // Not implemented for programmatic
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public TableView<Patient> getPatientTable() {
        return patientTable;
    }
}
