package co.edu.uniquindio.poo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import co.edu.uniquindio.poo.model.Appointment;
import co.edu.uniquindio.poo.model.Doctor;
import co.edu.uniquindio.poo.model.Patient;

public class ClinicDataStore implements ObservableData {
    private static ClinicDataStore instance;

    private final List<Patient> patients;
    private final List<Doctor> doctors;
    private final List<Appointment> appointments;

    private final List<DataObserver> observers;

    private ClinicDataStore() {
        this.patients = new ArrayList<>();
        this.doctors = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.observers = new CopyOnWriteArrayList<>();
    }

    public static ClinicDataStore getInstance() {
        if (instance == null) {
            instance = new ClinicDataStore();
        }
        return instance;
    }

    public void addPatient(Patient patient) {
        this.patients.add(patient);
        notifyObservers("patient");
    }

    public void addDoctor(Doctor doctor) {
        this.doctors.add(doctor);
        notifyObservers("doctor");
    }

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
        notifyObservers("appointment");
    }

    public List<Patient> getPatients() {
        return new ArrayList<>(patients);
    }

    public List<Doctor> getDoctors() {
        return new ArrayList<>(doctors);
    }

    public List<Appointment> getAppointments() {
        return new ArrayList<>(appointments);
    }

    @Override
    public void registerObserver(DataObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void unregisterObserver(DataObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(String dataType) {
        for (DataObserver observer : observers) {
            observer.update(dataType);
        }
    }
}
