package co.edu.uniquindio.poo.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import co.edu.uniquindio.poo.model.Appointment;
import co.edu.uniquindio.poo.model.Doctor;
import co.edu.uniquindio.poo.model.Patient;
import co.edu.uniquindio.poo.services.ClinicDataStore;
import co.edu.uniquindio.poo.services.PriceStrategy;
import co.edu.uniquindio.poo.services.SpecialistPriceStrategy;
import co.edu.uniquindio.poo.services.StandardPriceStrategy;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableCell;

public class AppointmentController {
    private final ClinicDataStore dataStore = ClinicDataStore.getInstance();
    private final ObservableList<Appointment> appointmentData;
    private final ObservableList<Patient> patientData;
    private final ObservableList<Doctor> doctorData;
    private TableView<Appointment> appointmentTable;

    public AppointmentController(ObservableList<Appointment> appointmentData, ObservableList<Patient> patientData, ObservableList<Doctor> doctorData) {
        this.appointmentData = appointmentData;
        this.patientData = patientData;
        this.doctorData = doctorData;
    }

    /**
     * Crea y retorna el nodo principal (VBox) para la pestaña de Citas.
     */
    public VBox createView() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f5f5f5;");

        // 1. Formulario de Cita
        TitledPane formPane = new TitledPane("📅 Programar Nueva Cita", createAppointmentForm());
        formPane.setCollapsible(false);

        // 2. Tabla de Citas
        appointmentTable = createAppointmentTable();

        Label tableLabel = new Label("📋 Lista de Citas Programadas");
        tableLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        content.getChildren().addAll(formPane, new Separator(), tableLabel, appointmentTable);
        return content;
    }
    
    private TableView<Appointment> createAppointmentTable() {
        TableView<Appointment> table = new TableView<>(appointmentData);

        TableColumn<Appointment, Integer> idCol = new TableColumn<>("ID Cita");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Paciente");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patient"));

        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Médico");
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        
        TableColumn<Appointment, LocalDateTime> dateCol = new TableColumn<>("Fecha y Hora");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        dateCol.setCellFactory(column -> new TableCell<Appointment, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : formatter.format(item));
            }
        });

        TableColumn<Appointment, Double> basePriceCol = new TableColumn<>("Precio Base");
        basePriceCol.setCellValueFactory(new PropertyValueFactory<>("basePrice"));
        
        TableColumn<Appointment, Double> finalPriceCol = new TableColumn<>("Precio Final");
        finalPriceCol.setCellValueFactory(new PropertyValueFactory<>("finalPrice")); // Usa el precio calculado por Strategy

        table.getColumns().addAll(idCol, patientCol, doctorCol, dateCol, basePriceCol, finalPriceCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private GridPane createAppointmentForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Comboboxes de datos existentes (usando las listas observadas del DataStore)
        ComboBox<Patient> patientCb = new ComboBox<>(patientData);
        patientCb.setPromptText("Seleccione Paciente");
        
        ComboBox<Doctor> doctorCb = new ComboBox<>(doctorData);
        doctorCb.setPromptText("Seleccione Médico");
        
        // Entrada de fecha/hora
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField timeField = new TextField(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        
        TextField priceField = new TextField("50.00");
        
        // Estrategia de Precio
        ComboBox<String> strategyCb = new ComboBox<>(FXCollections.observableArrayList(
            "Estándar (1.0x)", "Especialista (1.25x)"
        ));
        strategyCb.setValue("Estándar (1.0x)"); // Default

        grid.addRow(0, new Label("Paciente:"), patientCb);
        grid.addRow(1, new Label("Médico:"), doctorCb);
        grid.addRow(2, new Label("Fecha:"), datePicker);
        grid.addRow(3, new Label("Hora (HH:mm):"), timeField);
        grid.addRow(4, new Label("Precio Base:"), priceField);
        grid.addRow(5, new Label("Estrategia de Precio:"), strategyCb);


        Button scheduleButton = new Button("📅 Programar Cita");
        scheduleButton.getStyleClass().add("success-button");
        scheduleButton.setOnAction(e -> {
            try {
                // Validación básica de campos
                if (patientCb.getValue() == null || doctorCb.getValue() == null || datePicker.getValue() == null || priceField.getText().isEmpty()) {
                    throw new IllegalArgumentException("Debe completar todos los campos de la cita.");
                }

                // 1. Obtener objetos y datos
                Patient selectedPatient = patientCb.getValue();
                Doctor selectedDoctor = doctorCb.getValue();
                LocalDate date = datePicker.getValue();
                LocalTime time = LocalTime.parse(timeField.getText());
                LocalDateTime dateTime = LocalDateTime.of(date, time);
                double basePrice = Double.parseDouble(priceField.getText());

                // 2. Aplicar Patrón Strategy
                PriceStrategy strategy = switch (strategyCb.getValue()) {
                    case "Especialista (1.25x)" -> new SpecialistPriceStrategy();
                    default -> new StandardPriceStrategy();
                };

                // 3. Crear Cita con la estrategia
                Appointment appointment = new Appointment(
                    selectedPatient,
                    selectedDoctor,
                    dateTime,
                    basePrice,
                    strategy
                );
                
                // 4. Guardar en el Singleton Data Store
                dataStore.addAppointment(appointment); // Esto notifica al Observer en la clase principal
                
                // Limpiar (solo los campos que tienen entrada manual)
                timeField.clear(); priceField.clear(); 
                datePicker.setValue(LocalDate.now());
                strategyCb.setValue("Estándar (1.0x)");

                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Cita programada. Precio Final: " + String.format("$%.2f", appointment.getFinalPrice()));
                
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error al programar cita: " + ex.getMessage());
            }
        });

        grid.add(scheduleButton, 1, 6);
        return grid;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public TableView<Appointment> getAppointmentTable() {
        return appointmentTable;
    }

}
