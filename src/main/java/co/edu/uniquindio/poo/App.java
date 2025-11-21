package co.edu.uniquindio.poo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application implements DataObserver {
    private final ClinicDataStore dataStore = ClinicDataStore.getInstance();
    private TabPane tabPane;

    private final Map<String, Object> controllers = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        initializeDemoData();
        dataStore.registerObserver(this);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        tabPane = new TabPane();

        try {

            Tab patientsTab = createTabFromFXML("Pacientes", "PatientView.fxml", "patient");
            Tab doctorsTab = createTabFromFXML("Médicos", "DoctorView.fxml", "doctor");
            Tab appointmentsTab = createTabFromFXML("Citas", "AppointmentView.fxml", "appointment");

            tabPane.getTabs().addAll(patientsTab, doctorsTab, appointmentsTab);
        } catch (IOException e) {
            showAlert("Error de Carga", "No se pudo cargar una o más vistas FXML.", Alert.AlertType.ERROR);
            e.printStackTrace();
            return;
        }

        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1200, 750);
        primaryStage.setTitle("Gestión de Citas Médicas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Carga un archivo FXML y crea una pestaña con su contenido.
     * 
     * @param title         El título de la pestaña.
     * @param fxmlName      El nombre del archivo FXML (ej: "PatientView.fxml").
     * @param controllerKey La clave para guardar el controlador en el mapa.
     * @return Una nueva Tab con el contenido FXML.
     * @throws IOException Si el archivo FXML no se encuentra o no se puede cargar.
     */
    private Tab createTabFromFXML(String title, String fxmlName, String controllerKey) throws IOException {
        String fxmlPath = "/co/edu/uniquindio/poo/resources/" + fxmlName;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent content = loader.load();

        controllers.put(controllerKey, loader.getController());

        Tab tab = new Tab(title);
        tab.setContent(new ScrollPane(content));
        tab.setClosable(false);
        return tab;
    }

    /**
     * Implementación del Patrón Observer (Mantiene centralizada la actualización de
     * listas).
     * Este método se llama desde ClinicDataStore cuando se agrega un nuevo dato.
     * * @param dataType El tipo de dato que ha cambiado ("patient", "doctor", o
     * "appointment").
     */
    @Override
    public void update(String dataType) {

        javafx.application.Platform.runLater(() -> {
            switch (dataType) {
                case "patient":
                    PatientController pc = (PatientController) controllers.get("patient");
                    if (pc != null) {
                        pc.getPatientTable().refresh();
                    }
                    break;
                case "doctor":
                    DoctorController dc = (DoctorController) controllers.get("doctor");
                    if (dc != null) {
                        dc.getDoctorTable().refresh();
                    }
                    break;
                case "appointment":
                    AppointmentController ac = (AppointmentController) controllers.get("appointment");
                    if (ac != null) {
                        ac.getAppointmentTable().refresh();
                    }
                    break;
            }
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
