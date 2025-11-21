package co.edu.uniquindio.poo.services;

import co.edu.uniquindio.poo.model.Patient;
import co.edu.uniquindio.poo.model.Person;

public class PatientFactory  implements PersonFactory {
    @Override
    public Person createPerson(String id, String name, String phone, String...additionalData) {
        //additionalData [0] = historyNumber, additionalData [1] = address
        String historyNumber = additionalData.length > 0 ? additionalData[0] : "N/A";
        String address = additionalData.length > 1 ? additionalData[1] : "N/A";

        return new Patient.Builder(id, name, phone, historyNumber)
                .address(address)
                .build();
    }

}
