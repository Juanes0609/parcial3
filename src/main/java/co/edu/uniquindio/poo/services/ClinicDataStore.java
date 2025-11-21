package co.edu.uniquindio.poo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import co.edu.uniquindio.poo.model.Appointment;
import co.edu.uniquindio.poo.model.Doctor;
import co.edu.uniquindio.poo.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Singleton para gestionar los datos de la clínica.
 * Implementa el Patrón Observer (Subject/Observable).
 */
public class ClinicDataStore {
    private static ClinicDataStore instance;
    private final ObservableList<Patient> patients;
    private final ObservableList<Doctor> doctors;
    private final ObservableList<Appointment> appointments;
    private final List<DataObserver> observers;

    private ClinicDataStore() {
        this.patients = FXCollections.observableArrayList();
        this.doctors = FXCollections.observableArrayList();
        this.appointments = FXCollections.observableArrayList();
        this.observers = new ArrayList<>();
    }

    public static ClinicDataStore getInstance() {
        if (instance == null) {
            instance = new ClinicDataStore();
        }
        return instance;
    }

    public void registerObserver(DataObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String dataType) {
        for (DataObserver observer : observers) {
            observer.update(dataType);
        }
    }

    public ObservableList<Patient> getPatients() {
        return patients;
    }

    public void addPatient(Patient patient) {
        if (patients.stream().anyMatch(p -> p.getId().equals(patient.getId()))) {
            throw new IllegalArgumentException("Ya existe un paciente con este ID.");
        }
        this.patients.add(patient);
        notifyObservers("patient");
    }

    public void editPatient(String id, Patient updatedPatient) {
        Optional<Patient> existingPatient = patients.stream().filter(p -> p.getId().equals(id)).findFirst();
        if (existingPatient.isPresent()) {
            Patient p = existingPatient.get();

            p.setName(updatedPatient.getName());
            p.setPhone(updatedPatient.getPhone());
            p.setHistoryNumber(updatedPatient.getHistoryNumber());
            p.setAddress(updatedPatient.getAddress());
            notifyObservers("patient");
        } else {
            throw new IllegalArgumentException("Paciente no encontrado.");
        }
    }

    public void removePatient(String id) {
        boolean removed = this.patients.removeIf(p -> p.getId().equals(id));
        if (removed) {

            this.appointments.removeIf(a -> a.getPatient().getId().equals(id));
            notifyObservers("patient");
            notifyObservers("appointment");
        }
    }

    public ObservableList<Doctor> getDoctors() {
        return doctors;
    }

    public void addDoctor(Doctor doctor) {
        if (doctors.stream().anyMatch(d -> d.getId().equals(doctor.getId()))) {
            throw new IllegalArgumentException("Ya existe un médico con este ID.");
        }
        this.doctors.add(doctor);
        notifyObservers("doctor");
    }

    public void editDoctor(String id, Doctor updatedDoctor) {
        Optional<Doctor> existingDoctor = doctors.stream().filter(d -> d.getId().equals(id)).findFirst();
        if (existingDoctor.isPresent()) {
            Doctor d = existingDoctor.get();

            d.setName(updatedDoctor.getName());
            d.setPhone(updatedDoctor.getPhone());
            d.setSpecialty(updatedDoctor.getSpecialty());
            d.setLicenseNumber(updatedDoctor.getLicenseNumber());
            notifyObservers("doctor");
        } else {
            throw new IllegalArgumentException("Médico no encontrado.");
        }
    }

    public void removeDoctor(String id) {
        boolean removed = this.doctors.removeIf(d -> d.getId().equals(id));
        if (removed) {

            this.appointments.removeIf(a -> a.getDoctor().getId().equals(id));
            notifyObservers("doctor");
            notifyObservers("appointment");
        }
    }

    public ObservableList<Appointment> getAppointments() {
        return appointments;
    }

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
        notifyObservers("appointment");
    }

    public void removeAppointment(Appointment appointment) {
        boolean removed = this.appointments.remove(appointment);
        if (removed) {
            notifyObservers("appointment");
        }
    }
}