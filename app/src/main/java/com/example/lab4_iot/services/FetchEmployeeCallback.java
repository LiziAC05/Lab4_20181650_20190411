package com.example.lab4_iot.services;

import com.example.lab4_iot.entity.Employee;

public interface FetchEmployeeCallback {


    void onEmployeeFound(Employee employee);
    void onEmployeeNotFound();
}
