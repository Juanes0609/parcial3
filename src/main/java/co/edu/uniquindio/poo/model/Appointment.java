package co.edu.uniquindio.poo.model;

import java.time.LocalDateTime;

public class Appointment {
    private static int nextAppointmentId = 1;
    private final int id;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime dateTime;
    private double basePrice;
    private PriceStrategy priceStrategy;

    public Appointment(Patient patient, Doctor doctor, LocalDateTime dateTime, double basePrice, PriceStrategy priceStrategy) {
        this.id = nextAppointmentId++;
        this.patient = patient;
        this.doctor = doctor;
        this.dateTime = dateTime;
        this.basePrice = basePrice;
        this.priceStrategy = priceStrategy;
    }

    public double calculateFinalPrice() {
        return priceStrategy.calculatePrice(basePrice);
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public double getFinalPrice() {
        return calculateFinalPrice();
    }
    
    public void setPriceStrategy(PriceStrategy priceStrategy) {
        this.priceStrategy = priceStrategy;
    }
}
