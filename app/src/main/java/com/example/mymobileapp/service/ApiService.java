package com.example.mymobileapp.service;

import com.example.mymobileapp.model.Employee;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("employee")
    Call<Employee> saveEmployee(@Body Employee employee);

    @GET("employee")
    Call<List<Employee>> getAllEmployee();
}
