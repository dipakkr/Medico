package com.parivartan.medico.model;

import java.util.List;

/**
 * Created by root on 3/9/18.
 */

public class PatientHistory {

    String username;
    List<String> allergens;
    List<String> resistance;
    boolean pregnancy;
    boolean diabetes;
    boolean highbloodPressure;

    boolean highCholestrol;
    List<String> others;
    List<String> geneticDisesase;


    public PatientHistory(String username, List<String> allergens, List<String> resistance,
                          boolean pregnancy, boolean diabetes, boolean highbloodPressure, boolean highCholestrol, List<String> others, List<String> geneticDisesase) {
        this.username = username;
        this.allergens = allergens;
        this.resistance = resistance;
        this.pregnancy = pregnancy;
        this.diabetes = diabetes;
        this.highbloodPressure = highbloodPressure;
        this.highCholestrol = highCholestrol;
        this.others = others;
        this.geneticDisesase = geneticDisesase;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<String> allergens) {
        this.allergens = allergens;
    }

    public List<String> getResistance() {
        return resistance;
    }

    public void setResistance(List<String> resistance) {
        this.resistance = resistance;
    }

    public boolean isPregnancy() {
        return pregnancy;
    }

    public void setPregnancy(boolean pregnancy) {
        this.pregnancy = pregnancy;
    }

    public boolean isDiabetes() {
        return diabetes;
    }

    public void setDiabetes(boolean diabetes) {
        this.diabetes = diabetes;
    }

    public boolean isHighbloodPressure() {
        return highbloodPressure;
    }

    public void setHighbloodPressure(boolean highbloodPressure) {
        this.highbloodPressure = highbloodPressure;
    }

    public boolean isHighCholestrol() {
        return highCholestrol;
    }

    public void setHighCholestrol(boolean highCholestrol) {
        this.highCholestrol = highCholestrol;
    }

    public List<String> getOthers() {
        return others;
    }

    public void setOthers(List<String> others) {
        this.others = others;
    }

    public List<String> getGeneticDisesase() {
        return geneticDisesase;
    }

    public void setGeneticDisesase(List<String> geneticDisesase) {
        this.geneticDisesase = geneticDisesase;
    }
}

//{
//        "User_Name": "ankit",
//        "Allergens":  ["pollen","dust"],
//        "Resistance": ["penincilin","cyclofloxacine"],
//        "Pregnancy": false,
//        "diabetes": false,
//        "highBloodPressure": false,
//        "highCholesterol": false,
//        "other": ["hemophilia","arthritist"],
//        "geneticDisease": ["sickel cell anemia","asthama"]
//}
