package co.edu.uniquindio.poo.services;

import co.edu.uniquindio.poo.model.Doctor;
import co.edu.uniquindio.poo.model.Person;

public class DoctorFactory implements PersonFactory {
    @Override
    public Person createPerson(String id, String name, String phone, String... additionalData) {
        // additionalData[0] = specialty, additionalData[1] = licenseNumber
        String specialty = additionalData.length > 0 ? additionalData[0] : "General";
        String licenseNumber = additionalData.length > 1 ? additionalData[1] : "N/A";

        return new Doctor(id, name, phone, specialty, licenseNumber);
    }

}
