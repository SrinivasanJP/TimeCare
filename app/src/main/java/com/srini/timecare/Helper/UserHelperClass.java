package com.srini.timecare.Helper;

public class UserHelperClass {
    String fname,lname,age,phone;

    public UserHelperClass() {}

    public UserHelperClass(String fname, String lname, String age, String phone) {
        this.fname = fname;
        this.lname = lname;
        this.age = age;
        this.phone = phone;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
