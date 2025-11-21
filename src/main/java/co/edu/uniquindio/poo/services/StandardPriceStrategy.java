package co.edu.uniquindio.poo.services;

public class StandardPriceStrategy implements PriceStrategy {
    @Override
    public double calculatePrice(double basePrice) {
        return basePrice;
    }

}
