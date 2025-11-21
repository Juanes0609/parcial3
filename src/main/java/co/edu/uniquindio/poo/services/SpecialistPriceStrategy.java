package co.edu.uniquindio.poo.services;

public class SpecialistPriceStrategy implements PriceStrategy {
    private static final double SPECIALIST_SURCHARGE = 1.25;

    @Override
    public double calculatePrice(double basePrice) {
        return basePrice * SPECIALIST_SURCHARGE;
    }

}
