package co.edu.uniquindio.poo.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import co.edu.uniquindio.poo.model.Doctor;
import co.edu.uniquindio.poo.services.ClinicDataStore;
import co.edu.uniquindio.poo.services.DoctorFactory;
import co.edu.uniquindio.poo.services.PersonFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class DoctorController implements Initializable {

    private final ClinicDataStore dataStore = ClinicDataStore.getInstance();
    private final PersonFactory doctorFactory = new DoctorFactory();

    @FXML
    private TableView<Doctor> doctorTable;
    @FXML
    private TableColumn<Doctor, String> idColumn;
    @FXML
    private TableColumn<Doctor, String> nameColumn;
    @FXML
    private TableColumn<Doctor, String> phoneColumn;
    @FXML
    private TableColumn<Doctor, String> specialtyColumn;
    @FXML
    private TableColumn<Doctor, String> licenseColumn;

    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField specialtyField;
    @FXML
    private TextField licenseField;

    private ObservableList<Doctor> doctorData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.doctorData = dataStore.getDoctors();
        doctorTable.setItems(doctorData);


        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        specialtyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSpecialty()));
        licenseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLicenseNumber()));


        doctorTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDoctorDetails(newValue));
    }

    /** Muestra los detalles del médico seleccionado en los campos de texto. */
    private void showDoctorDetails(Doctor doctor) {
        if (doctor != null) {
            idField.setText(doctor.getId());
            nameField.setText(doctor.getName());
            phoneField.setText(doctor.getPhone());
            specialtyField.setText(doctor.getSpecialty());
            licenseField.setText(doctor.getLicenseNumber());
        } else {
            clearFields();
        }
    }

    /** Limpia los campos de texto del formulario. */
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        specialtyField.setText("");
        licenseField.setText("");
    }

    /** Maneja la acción de agregar un nuevo médico. */
    @FXML
    private void handleAddDoctor() {
        try {
            if (isInputValid()) {
                Doctor newDoctor = (Doctor) doctorFactory.createPerson(
                        idField.getText(),
                        nameField.getText(),
                        phoneField.getText(),
                        specialtyField.getText(),
                        licenseField.getText());

                dataStore.addDoctor(newDoctor);
                clearFields();
                showAlert("Éxito", "Médico agregado correctamente.", Alert.AlertType.INFORMATION);
            }
        } catch (IllegalArgumentException e) {
            showAlert("Error de Datos", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "No se pudo agregar el médico.", Alert.AlertType.ERROR);
        }
    }

    /** Maneja la acción de editar el médico seleccionado. */
    @FXML
    private void handleEditDoctor() {
        Doctor selectedDoctor = doctorTable.getSelectionModel().getSelectedItem();
        if (selectedDoctor != null && isInputValid()) {
            try {
        
                Doctor updatedDoctor = (Doctor) doctorFactory.createPerson(
                        idField.getText(),
                        nameField.getText(),
                        phoneField.getText(),
                        specialtyField.getText(),
                        licenseField.getText());

                dataStore.editDoctor(selectedDoctor.getId(), updatedDoctor);
                showAlert("Éxito", "Médico editado correctamente.", Alert.AlertType.INFORMATION);
                doctorTable.refresh();
            } catch (IllegalArgumentException e) {
                showAlert("Error de Datos", e.getMessage(), Alert.AlertType.ERROR);
            }
        } else if (selectedDoctor == null) {
            showAlert("No Seleccionado", "Por favor, selecciona un médico de la tabla.", Alert.AlertType.WARNING);
        }
    }

    /** Maneja la acción de eliminar el médico seleccionado. */
    @FXML
    private void handleDeleteDoctor() {
        Doctor selectedDoctor = doctorTable.getSelectionModel().getSelectedItem();
        if (selectedDoctor != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de que quieres eliminar a " + selectedDoctor.getName() + "?", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("Eliminar Médico");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    dataStore.removeDoctor(selectedDoctor.getId());
                    clearFields();
                    showAlert("Éxito", "Médico eliminado.", Alert.AlertType.INFORMATION);
                }
            });
        } else {
            showAlert("No Seleccionado", "Por favor, selecciona un médico de la tabla.", Alert.AlertType.WARNING);
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
        if (specialtyField.getText() == null || specialtyField.getText().isEmpty()) {
            errorMessage += "Especialidad inválida.\n";
        }
        if (licenseField.getText() == null || licenseField.getText().isEmpty()) {
            errorMessage += "Licencia inválida.\n";
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
    
    public TableView<Doctor> getDoctorTable() {
        return doctorTable;
    }
}