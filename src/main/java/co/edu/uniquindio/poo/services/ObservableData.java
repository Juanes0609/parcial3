package co.edu.uniquindio.poo.services;

public interface ObservableData {
    
    void registerObserver(DataObserver observer);
    void unregisterObserver(DataObserver observer);
    void notifyObservers(String dataType);
}
