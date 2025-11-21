package co.edu.uniquindio.poo;

import java.time.LocalDateTime;

import co.edu.uniquindio.poo.controllers.AppointmentController;
import co.edu.uniquindio.poo.controllers.DoctorController;
import co.edu.uniquindio.poo.controllers.PatientController;
import co.edu.uniquindio.poo.model.Appointment;
import co.edu.uniquindio.poo.model.Doctor;
import co.edu.uniquindio.poo.model.Patient;
import co.edu.uniquindio.poo.services.ClinicDataStore;
import co.edu.uniquindio.poo.services.DataObserver;
import co.edu.uniquindio.poo.services.DoctorFactory;
import co.edu.uniquindio.poo.services.PatientFactory;
import co.edu.uniquindio.poo.services.PersonFactory;
import co.edu.uniquindio.poo.services.SpecialistPriceStrategy;
import co.edu.uniquindio.poo.services.StandardPriceStrategy;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application implements DataObserver {
    private ClinicDataStore dataStore = ClinicDataStore.getInstance();
    private TabPane tabPane;

    private ObservableList<Patient> patientData;
    private ObservableList<Doctor> doctorData;
    private ObservableList<Appointment> appointmentData;

    private PatientController patientController;
    private DoctorController doctorController;
    private AppointmentController appointmentController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        patientData = FXCollections.observableArrayList(dataStore.getPatients());
        doctorData = FXCollections.observableArrayList(dataStore.getDoctors());
        appointmentData = FXCollections.observableArrayList(dataStore.getAppointments());
        dataStore.registerObserver(this);

        initializeDemoData();

        patientController = new PatientController(patientData);
        doctorController = new DoctorController(doctorData);
        appointmentController = new AppointmentController(appointmentData, patientData, doctorData);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        tabPane = new TabPane();

        Tab patientsTab = createTab("Pacientes", patientController.createView());
        Tab doctorsTab = createTab("Médicos", doctorController.createView());
        Tab appointmentsTab = createTab("Citas", appointmentController.createView());

        tabPane.getTabs().addAll(patientsTab, doctorsTab, appointmentsTab);

        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1200, 750);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setTitle("Gestión de Citas Médicas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Tab createTab(String title, Region content) {
        Tab tab = new Tab(title);
        tab.setContent(new ScrollPane(content));
        tab.setClosable(false);
        return tab;
    }

    @Override
    public void update(String dataType) {
        switch (dataType) {
            case "patient":
                patientData.setAll(dataStore.getPatients());
                break;
            case "doctor":
                doctorData.setAll(dataStore.getDoctors());
                break;
            case "appointment":
                appointmentData.setAll(dataStore.getAppointments());
                break;
        }

        javafx.application.Platform.runLater(() -> {
            if (patientController != null)
                patientController.getPatientTable().refresh();
            if (doctorController != null)
                doctorController.getDoctorTable().refresh();
            if (appointmentController != null)
                appointmentController.getAppointmentTable().refresh();
        });
    }

    private void initializeDemoData() {
        if (dataStore.getPatients().isEmpty() && dataStore.getDoctors().isEmpty()) {
            PersonFactory patientFactory = new PatientFactory();
            PersonFactory doctorFactory = new DoctorFactory();

            Patient p1 = (Patient) patientFactory.createPerson("P001", "Ana Garcia", "555-1234", "H1001",
                    "Calle Falsa 123");
            Patient p2 = (Patient) patientFactory.createPerson("P002", "Luis Perez", "555-5678", "H1002",
                    "Av. Siempreviva 742");

            Doctor d1 = (Doctor) doctorFactory.createPerson("D001", "Dr. Juan Lopez", "555-9876", "Cardiología",
                    "L12345");
            Doctor d2 = (Doctor) doctorFactory.createPerson("D002", "Dra. Maria Gómez", "555-4321", "Medicina General",
                    "L67890");

            dataStore.addPatient(p1);
            dataStore.addPatient(p2);
            dataStore.addDoctor(d1);
            dataStore.addDoctor(d2);

            Appointment a1 = new Appointment(
                    p1, d2, LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
                    50.00, new StandardPriceStrategy());

            Appointment a2 = new Appointment(
                    p2, d1, LocalDateTime.now().plusDays(2).withHour(15).withMinute(30),
                    75.00, new SpecialistPriceStrategy());

            dataStore.addAppointment(a1);
            dataStore.addAppointment(a2);
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
}
