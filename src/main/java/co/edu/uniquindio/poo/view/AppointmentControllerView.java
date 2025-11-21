package co.edu.uniquindio.poo.view;

import javax.swing.table.TableColumn;
import javax.swing.text.TableView;

public class AppointmentControllerView {
    private final ClinicDataStore dataStore = ClinicDataStore.getInstance();
    private final ObservableList<Patient> patientData;
    private TableView<Patient> patientTable;
    private final PersonFactory patientFactory = new PatientFactory();

    public PatientControllerView(ObservableList<Patient> patientData) {
        this.patientData = patientData;
    }

    /**
     * Crea y retorna el nodo principal (VBox) para la pestaña de Pacientes.
     */
    public VBox createView() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // 1. Formulario de Paciente
        TitledPane formPane = new TitledPane("Registrar Nuevo Paciente (Patrón Builder)", createPatientForm());
        formPane.setCollapsible(false);

        // 2. Tabla de Pacientes
        patientTable = createPatientTable();

        content.getChildren().addAll(formPane, new Separator(), new Label("Lista de Pacientes"), patientTable);
        return content;
    }

    private TableView<Patient> createPatientTable() {
        TableView<Patient> table = new TableView<>(patientData);

        TableColumn<Patient, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Patient, String> historyCol = new TableColumn<>("Nro. Historia");
        historyCol.setCellValueFactory(new PropertyValueFactory<>("historyNumber"));
        
        TableColumn<Patient, String> addressCol = new TableColumn<>("Dirección");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        table.getColumns().addAll(nameCol, idCol, historyCol, addressCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        TextField historyNumberField = new TextField();
        TextField addressField = new TextField(); // Opcional

        grid.addRow(0, new Label("ID:"), idField);
        grid.addRow(1, new Label("Nombre:"), nameField);
        grid.addRow(2, new Label("Teléfono:"), phoneField);
        grid.addRow(3, new Label("Nro. Historia:"), historyNumberField);
        grid.addRow(4, new Label("Dirección (Opcional):"), addressField);

        Button saveButton = new Button("Guardar Paciente (Builder)");
        saveButton.setOnAction(e -> {
            try {
                // Utiliza el Patrón Builder a través de la Factory
                Patient patient = (Patient) patientFactory.createPerson(
                    idField.getText(),
                    nameField.getText(),
                    phoneField.getText(),
                    historyNumberField.getText(), // additionalData[0]
                    addressField.getText()      // additionalData[1]
                );
                
                dataStore.addPatient(patient); // Esto notifica al Observer en la clase principal
                
                // Limpiar campos
                idField.clear(); nameField.clear(); phoneField.clear(); 
                historyNumberField.clear(); addressField.clear();
                
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Paciente registrado correctamente.");

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

    public TableView<Patient> getPatientTable() {
        return patientTable;
    }

}
