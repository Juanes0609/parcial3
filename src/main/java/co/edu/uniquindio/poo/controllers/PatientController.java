package co.edu.uniquindio.poo.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

import co.edu.uniquindio.poo.model.Patient;
import co.edu.uniquindio.poo.services.ClinicDataStore;
import co.edu.uniquindio.poo.services.PatientFactory;
import co.edu.uniquindio.poo.services.PersonFactory;

public class PatientController implements Initializable {
    private final ClinicDataStore dataStore = ClinicDataStore.getInstance();
    private final PersonFactory patientFactory = new PatientFactory();

    @FXML
    private TableView<Patient> patientTable;
    @FXML
    private TableColumn<Patient, String> idColumn;
    @FXML
    private TableColumn<Patient, String> nameColumn;
    @FXML
    private TableColumn<Patient, String> phoneColumn;
    @FXML
    private TableColumn<Patient, String> historyColumn;
    @FXML
    private TableColumn<Patient, String> addressColumn;

    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField historyField;
    @FXML
    private TextField addressField;

    private ObservableList<Patient> patientData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.patientData = dataStore.getPatients();
        patientTable.setItems(patientData);

        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        historyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHistoryNumber()));
        addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));

        patientTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPatientDetails(newValue));
    }

    /** Muestra los detalles del paciente seleccionado en los campos de texto. */
    private void showPatientDetails(Patient patient) {
        if (patient != null) {
            idField.setText(patient.getId());
            nameField.setText(patient.getName());
            phoneField.setText(patient.getPhone());
            historyField.setText(patient.getHistoryNumber());
            addressField.setText(patient.getAddress());
        } else {
            clearFields();
        }
    }

    /** Limpia los campos de texto del formulario. */
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        historyField.setText("");
        addressField.setText("");
    }

    /** Maneja la acción de agregar un nuevo paciente. */
    @FXML
    private void handleAddPatient() {
        try {
            if (isInputValid()) {
                Patient newPatient = (Patient) patientFactory.createPerson(
                        idField.getText(),
                        nameField.getText(),
                        phoneField.getText(),
                        historyField.getText(),
                        addressField.getText());

                dataStore.addPatient(newPatient);
                clearFields();
                showAlert("Éxito", "Paciente agregado correctamente.", Alert.AlertType.INFORMATION);
            }
        } catch (IllegalArgumentException e) {
            showAlert("Error de Datos", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "No se pudo agregar el paciente.", Alert.AlertType.ERROR);
        }
    }

    /** Maneja la acción de editar el paciente seleccionado. */
    @FXML
    private void handleEditPatient() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null && isInputValid()) {
            try {

                Patient updatedPatient = (Patient) patientFactory.createPerson(
                        idField.getText(),
                        nameField.getText(),
                        phoneField.getText(),
                        historyField.getText(),
                        addressField.getText());

                dataStore.editPatient(selectedPatient.getId(), updatedPatient);
                showAlert("Éxito", "Paciente editado correctamente.", Alert.AlertType.INFORMATION);
                patientTable.refresh();
            } catch (IllegalArgumentException e) {
                showAlert("Error de Datos", e.getMessage(), Alert.AlertType.ERROR);
            }
        } else if (selectedPatient == null) {
            showAlert("No Seleccionado", "Por favor, selecciona un paciente de la tabla.", Alert.AlertType.WARNING);
        }
    }

    /** Maneja la acción de eliminar el paciente seleccionado. */
    @FXML
    private void handleDeletePatient() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Estás seguro de que quieres eliminar a " + selectedPatient.getName() + "?", ButtonType.YES,
                    ButtonType.NO);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("Eliminar Paciente");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    dataStore.removePatient(selectedPatient.getId());
                    clearFields();
                    showAlert("Éxito", "Paciente eliminado.", Alert.AlertType.INFORMATION);
                }
            });
        } else {
            showAlert("No Seleccionado", "Por favor, selecciona un paciente de la tabla.", Alert.AlertType.WARNING);
        }
    }

    /** Valida la entrada del usuario. */
    private boolean isInputValid() {
        String errorMessage = "";
        if (idField.getText() == null || idField.getText().isEmpty()) {
            errorMessage += "ID inválido.\n";
        }
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += "Nombre inválido.\n";
        }
        if (phoneField.getText() == null || phoneField.getText().isEmpty()) {
            errorMessage += "Teléfono inválido.\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Campos Inválidos", "Corrige los siguientes errores:\n" + errorMessage, Alert.AlertType.ERROR);
            return false;
        }
    }

    /** Muestra un cuadro de diálogo de alerta. */
    private void showAlert(String title, String content, Alert.AlertType type) {
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
