package co.edu.uniquindio.poo.controllers;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import co.edu.uniquindio.poo.model.Doctor;
import co.edu.uniquindio.poo.services.ClinicDataStore;
import co.edu.uniquindio.poo.services.DoctorFactory;
import co.edu.uniquindio.poo.services.PersonFactory;

public class DoctorController {
    private final ClinicDataStore dataStore = ClinicDataStore.getInstance();
    private final ObservableList<Doctor> doctorData;
    private final PersonFactory doctorFactory = new DoctorFactory();
    private TableView<Doctor> doctorTable;

    public DoctorController(ObservableList<Doctor> doctorData) {
        this.doctorData = doctorData;
    }

    public VBox createView() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f5f5f5;");

        TitledPane formPane = new TitledPane("👨‍⚕️ Registrar Nuevo Médico", createDoctorForm());
        formPane.setCollapsible(false);

        doctorTable = createDoctorTable();

        Label tableLabel = new Label("📋 Lista de Médicos");
        tableLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        content.getChildren().addAll(formPane, new Separator(), tableLabel, doctorTable);
        return content;
    }

    private TableView<Doctor> createDoctorTable() {
        TableView<Doctor> table = new TableView<>(doctorData);

        TableColumn<Doctor, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Doctor, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Doctor, String> phoneCol = new TableColumn<>("Teléfono");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Doctor, String> specialtyCol = new TableColumn<>("Especialidad");
        specialtyCol.setCellValueFactory(new PropertyValueFactory<>("specialty"));

        TableColumn<Doctor, String> licenseCol = new TableColumn<>("Licencia");
        licenseCol.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));

        table.getColumns().addAll(idCol, nameCol, phoneCol, specialtyCol, licenseCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDoctorDetails(newValue));

        return table;
    }

    private GridPane createDoctorForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField specialtyField = new TextField();
        TextField licenseField = new TextField();

        grid.addRow(0, new Label("ID:"), idField);
        grid.addRow(1, new Label("Nombre:"), nameField);
        grid.addRow(2, new Label("Teléfono:"), phoneField);
        grid.addRow(3, new Label("Especialidad:"), specialtyField);
        grid.addRow(4, new Label("Nro. Licencia:"), licenseField);

        Button saveButton = new Button("✓ Guardar Médico");
        saveButton.getStyleClass().add("success-button");
        saveButton.setOnAction(e -> {
            try {

                Doctor doctor = (Doctor) doctorFactory.createPerson(
                        idField.getText(),
                        nameField.getText(),
                        phoneField.getText(),
                        specialtyField.getText(),
                        licenseField.getText());

                dataStore.addDoctor(doctor);

                idField.clear();
                nameField.clear();
                phoneField.clear();
                specialtyField.clear();
                licenseField.clear();

                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Médico registrado correctamente.");

            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error de Registro", "Verifique todos los campos.");
            }
        });

        grid.add(saveButton, 1, 5);
        return grid;
    }

    private void showDoctorDetails(Doctor doctor) {
        // Not implemented for programmatic
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public TableView<Doctor> getDoctorTable() {
        return doctorTable;
    }
}