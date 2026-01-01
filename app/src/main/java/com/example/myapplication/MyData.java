package com.example.myapplication;

// This class represents a single row of data from the database.
public class MyData {
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
}
