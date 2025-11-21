package co.edu.uniquindio.poo.view;

import co.edu.uniquindio.poo.model.Doctor;
import co.edu.uniquindio.poo.services.ClinicDataStore;
import co.edu.uniquindio.poo.services.DoctorFactory;
import co.edu.uniquindio.poo.services.PersonFactory;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class DoctorControllerView {
    private final ClinicDataStore dataStore = ClinicDataStore.getInstance();
    private final ObservableList<Doctor> doctorData;
    private TableView<Doctor> doctorTable;
    private final PersonFactory doctorFactory = new DoctorFactory();

    public DoctorControllerView(ObservableList<Doctor> doctorData) {
        this.doctorData = doctorData;
    }

    public VBox createView() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        TitledPane formPane = new TitledPane("Registrar Nuevo Médico (Patrón Factory)", createDoctorForm());
        formPane.setCollapsible(false);

        doctorTable = createDoctorTable();

        content.getChildren().addAll(formPane, new Separator(), new Label("Lista de Médicos"), doctorTable);
        return content;
    }

    private TableView<Doctor> createDoctorTable() {
        TableView<Doctor> table = new TableView<>(doctorData);

        TableColumn<Doctor, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Doctor, String> specialtyCol = new TableColumn<>("Especialidad");
        specialtyCol.setCellValueFactory(new PropertyValueFactory<>("specialty"));

        TableColumn<Doctor, String> licenseCol = new TableColumn<>("Licencia");
        licenseCol.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));

        table.getColumns().addAll(nameCol, specialtyCol, licenseCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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

        Button saveButton = new Button("Guardar Médico (Factory)");
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
