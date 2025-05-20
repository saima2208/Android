package com.example.mymobileapp.service;

import com.example.mymobileapp.model.Employee;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("employee")
    Call<Employee> saveEmployee(@Body Employee employee);
}
