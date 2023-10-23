package com.example.lab4_iot.services;

import com.example.lab4_iot.entity.Employee;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface EmployeeService {


    @GET("employees/byManager")
    Call<List<Employee>> getEmployeesByManager(@Query("managerId") int managerId);
}
