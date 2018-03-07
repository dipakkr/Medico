package com.parivartan.medico.model;

/**
 * Created by root on 3/8/18.
 */

public class PatientDetail {

    String email;
    String name;
    String age;
    String height;
    String weight;

    public PatientDetail(String email, String name, String age, String height, String weight) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    public String getWeight() {
        return weight;
    }
    public String getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

}
