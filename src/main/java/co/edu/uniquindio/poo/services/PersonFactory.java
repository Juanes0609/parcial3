package co.edu.uniquindio.poo.services;

import co.edu.uniquindio.poo.model.Person;

public interface PersonFactory {
    Person createPerson (String id, String name, String phone, String...additionalData);

}
