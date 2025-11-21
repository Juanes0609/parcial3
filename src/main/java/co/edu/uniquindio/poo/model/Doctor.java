package co.edu.uniquindio.poo.model;

public class Doctor extends Person{
    private String specialty;
    private String licenseNumber;

    public Doctor(String id, String name, String phone, String specialty, String licenseNumber) {
        super(id, name, phone);
        this.specialty = specialty;
        this.licenseNumber = licenseNumber;
    }

    @Override
    public String getDetails() {
        return "Médico: " + getName() + ", Especialidad: " + specialty;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
}

