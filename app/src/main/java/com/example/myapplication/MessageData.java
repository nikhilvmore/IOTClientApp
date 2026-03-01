package com.example.myapplication;

public class MessageData {
    public String machine, temp, speed, power, timestamp;

    public MessageData(String machine, String temp, String speed, String power, String timestamp) {
        this.machine = machine;
        this.temp = temp;
        this.speed = speed;
        this.power = power;
        this.timestamp = timestamp;
    }

    public int getMachine() {
        return 0;
    }

    public char[] getTemp() {
        return new char[0];
    }

    public int getSpeed() {
        return 0;
    }

    public int getPower() {
        return 0;
    }

    public int getTimestamp() {
        return 0;
    }
}
