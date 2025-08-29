package com.example.demo.dto;

public class Solution {
    private String finalQuery;

    public Solution() {}

    public Solution(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    // Getters and Setters
    public String getFinalQuery() { return finalQuery; }
    public void setFinalQuery(String finalQuery) { this.finalQuery = finalQuery; }
}