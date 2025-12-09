package com.infosec.dto;

import java.util.List;

public class DataResponse {
    private String message;
    private List<UserResponse> users;
    private int totalCount;

    public DataResponse() {
    }

    public DataResponse(String message, List<UserResponse> users) {
        this.message = message;
        this.users = users;
        this.totalCount = users != null ? users.size() : 0;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<UserResponse> getUsers() {
        return users;
    }

    public void setUsers(List<UserResponse> users) {
        this.users = users;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}

