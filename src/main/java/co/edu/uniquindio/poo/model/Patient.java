package co.edu.uniquindio.poo.model;

public class Patient extends Person {
    private String address; // Campo opcional
    private String historyNumber;

    // Constructor privado para ser usado solo por el Builder
    private Patient(Builder builder) {
        super(builder.id, builder.name, builder.phone);
        this.address = builder.address;
        this.historyNumber = builder.historyNumber;
    }

    // Implementación de método abstracto
    @Override
    public String getDetails() {
        return "Paciente: " + getName() + ", Historia: " + historyNumber;
    }

    // Getters
    public String getAddress() {
        return address;
    }

    public String getHistoryNumber() {
        return historyNumber;
    }

    // Patrón Builder anidado
    public static class Builder {
        // Campos obligatorios
        private final String id;
        private final String name;
        private final String phone;
        private final String historyNumber;

        // Campos opcionales
        private String address = "N/A";

        public Builder(String id, String name, String phone, String historyNumber) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.historyNumber = historyNumber;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Patient build() {
            return new Patient(this);
        }
    }

}
