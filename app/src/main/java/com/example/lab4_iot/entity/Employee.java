package com.example.lab4_iot.entity;

import java.sql.Time;
import java.sql.Timestamp;

public class Employee {

    public int id;
    public String first_name;
    public String last_name;
    public String email;
    public String phone_number;
    public Timestamp hire_date; // Considere cambiar esto a un tipo compatible con Timestamp si es necesario
    public Job job_id;
    public double salary; // Cambiado de int a double
    public Employee manager_id;
    public Department department_id;
    public int meeting;
    public Timestamp meeting_date; // Considere cambiar esto a un tipo compatible con Timestamp si es necesario

    // Tus métodos getter y setter aquí


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public Timestamp getHire_date() {
        return hire_date;
    }

    public void setHire_date(Timestamp hire_date) {
        this.hire_date = hire_date;
    }

    public Job getJob_id() {
        return job_id;
    }

    public void setJob_id(Job job_id) {
        this.job_id = job_id;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Employee getManager_id() {
        return manager_id;
    }

    public void setManager_id(Employee manager_id) {
        this.manager_id = manager_id;
    }

    public Department getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(Department department_id) {
        this.department_id = department_id;
    }

    public int getMeeting() {
        return meeting;
    }

    public void setMeeting(int meeting) {
        this.meeting = meeting;
    }

    public Timestamp getMeeting_date() {
        return meeting_date;
    }

    public void setMeeting_date(Timestamp meeting_date) {
        this.meeting_date = meeting_date;
    }
}

