package com.parivartan.medico.model;

/**
 * Created by root on 3/8/18.
 */

public class PatientDetail {

    String username;
    String email;
    String name;
    String gender;
    String dob;
    String age;
    String height;
    String weight;
    String phone;
    String address;
    String state;
    String pincode;

    public PatientDetail(){
        super();
    }

    public PatientDetail(String username, String email, String name, String gender, String dob, String age, String height, String weight,
                         String phone, String address, String state, String pincode) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.phone = phone;
        this.address = address;
        this.state = state;
        this.pincode = pincode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}


//{
//        "User_Name":"ankit",
//        "name":"Yash Jain",
//        "gender":"male",
//        "dob":19980922,
//        "age":21,
//        "heght":170,
//        "weight":50,
//        "email":"me.yashjn@gmail.com",
//        "phoneNo":9953123857,
//        "address":"1/1010,Street No.3,Shahdara",
//        "state":"delhi",
//        "pinCode":110032
//}