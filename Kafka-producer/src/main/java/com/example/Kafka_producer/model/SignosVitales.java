package com.example.Kafka_producer.model;

public class SignosVitales {
    private int frecuenciaCardiaca;
    private int presionArterialSistolica;
    private int presionArterialDiastolica;
    private double temperatura;
    private int saturacionOxigeno;

    // Constructor sin parámetros
    public SignosVitales() {
    }
    
    // Constructor con parámetros
    public SignosVitales(int frecuenciaCardiaca, int presionArterialSistolica, 
                        int presionArterialDiastolica, double temperatura,
                        int saturacionOxigeno) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.presionArterialSistolica = presionArterialSistolica;
        this.presionArterialDiastolica = presionArterialDiastolica;
        this.temperatura = temperatura;
        this.saturacionOxigeno = saturacionOxigeno;
    }

    // Getters y Setters
    public int getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public int getPresionArterialSistolica() {
        return presionArterialSistolica;
    }

    public int getPresionArterialDiastolica() {
        return presionArterialDiastolica;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public int getSaturacionOxigeno() {
        return saturacionOxigeno;
    }

    // Setters
    public void setFrecuenciaCardiaca(int frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public void setPresionArterialSistolica(int presionArterialSistolica) {
        this.presionArterialSistolica = presionArterialSistolica;
    }

    public void setPresionArterialDiastolica(int presionArterialDiastolica) {
        this.presionArterialDiastolica = presionArterialDiastolica;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public void setSaturacionOxigeno(int saturacionOxigeno) {
        this.saturacionOxigeno = saturacionOxigeno;
    }

    @Override
    public String toString() {
        return "{" +
                "\"frecuenciaCardiaca\":" + frecuenciaCardiaca + "," +
                "\"presionArterialSistolica\":" + presionArterialSistolica + "," +
                "\"presionArterialDiastolica\":" + presionArterialDiastolica + "," +
                "\"temperatura\":" + temperatura + "," +
                "\"saturacionOxigeno\":" + saturacionOxigeno +
                "}";
    }
}
