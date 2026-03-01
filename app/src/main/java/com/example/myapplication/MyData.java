package com.example.myapplication;

// This class represents a single row of data from the database.
public class MyData {
    public int machine;
    public int temp;
    public int power;
    private String machineName;
    private double temperature;
    private int speed;
    private double electricityConsumption;
    private long timestamp;

    public MyData(String machineName, double temperature, int speed, double electricityConsumption, long timestamp) {
        this.machineName = machineName;
        this.temperature = temperature;
        this.speed = speed;
        this.electricityConsumption = electricityConsumption;
        this.timestamp = timestamp;
    }

    // Add getters for each field
    public String getMachineName() { return machineName; }
    public double getTemperature() { return temperature; }
    public int getSpeed() { return speed; }
    public double getElectricityConsumption() { return electricityConsumption; }
    public long getTimestamp() { return timestamp; }

    public int getMachine() {
        return 0;
    }

    public char[] getTemp() {
        return new char[0];
    }

    public int getPower() {
        return 0;
    }

    public char[] getElectricity() {
        return new char[0];
    }
}
