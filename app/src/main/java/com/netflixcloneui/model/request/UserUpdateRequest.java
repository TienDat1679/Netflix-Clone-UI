package com.netflixcloneui.model.request;

import java.time.LocalDate;
import java.util.List;

public class UserUpdateRequest {
    String password;
    String name;
    String dob;
    List<String> roles;
    String image;

    public UserUpdateRequest(String password, String name, String dob, List<String> roles, String image) {
        this.password = password;
        this.name = name;
        this.dob = dob;
        this.roles = roles;
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
