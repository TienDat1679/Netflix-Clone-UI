package com.netflixcloneui.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class UserResponse {
    String id;
    String name;
    String email;
    Integer otp;
    int enabled;
    LocalDate dob;
    Set<RoleResponse> roles;



    LocalDateTime startDate;
    LocalDateTime endDate;
    public UserResponse(String id, String name, String email, Integer otp, int enabled, LocalDate dob, LocalDateTime startDate,
    LocalDateTime endDate, Set<RoleResponse> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.otp = otp;
        this.enabled = enabled;
        this.dob = dob;
        this.roles = roles;
        this.startDate=startDate;
        this.endDate=endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Set<RoleResponse> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleResponse> roles) {
        this.roles = roles;
    }
    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
}
