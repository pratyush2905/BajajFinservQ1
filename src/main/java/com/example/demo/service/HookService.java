package com.example.demo.service;
import com.example.demo.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class HookService {

    private final WebClient webClient;

    @Value("${app.webhook.base-url}")
    private String baseUrl;

    @Value("${app.candidate.name}")
    private String candidateName;

    @Value("${app.candidate.reg-no}")
    private String regNo;

    @Value("${app.candidate.email}")
    private String email;

    public HookService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public void processQualifier() {
        try {
            HookResponse webhookResponse = generateWebhook();
            if (webhookResponse == null || webhookResponse.getWebhook() == null || webhookResponse.getAccessToken() == null) {
                System.out.println("Failed webhook generation");
                return;
            }
            System.out.println("Webhook generated !!! ");
            String sqlQuery = solveSqlProblem();
            submitSolution(webhookResponse.getWebhook(), webhookResponse.getAccessToken(), sqlQuery);
            System.out.println("****Process done*****");
        } catch (Exception e) {
            System.out.println("Error in qualifier process: " + e.getMessage());
        }
    }
    private HookResponse generateWebhook() 
    {
        try 
        {
            Hook request = new Hook(candidateName, regNo, email);
            System.out.println("Sending POST request to generate webhook...");
            Mono<HookResponse> responseMono = webClient
                    .post()
                    .uri(baseUrl + "/generateWebhook/JAVA")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(HookResponse.class);
            
            HookResponse response = responseMono.block();
            if (response != null) {
                System.out.println("Webhook URL received");
                System.out.println("Access Token received");
            }
            return response;
        } 
        catch (Exception e) 
        {
            System.out.println("Errorwebhook: " + e.getMessage());
            return null;
        }
    }
    private String solveSqlProblem() 
    {
        String sqlQuery = """
            SELECT 
                e.EMP_ID,
                e.FIRST_NAME,
                e.LAST_NAME,
                d.DEPARTMENT_NAME,
                COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT
            FROM EMPLOYEE e
            JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
            LEFT JOIN EMPLOYEE e2 ON e2.DEPARTMENT = e.DEPARTMENT 
                AND e2.DOB > e.DOB
            GROUP BY e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME
            ORDER BY e.EMP_ID DESC
            """;

        System.out.println("*****SQL Query***");
        return sqlQuery.trim();
    }
    private void submitSolution(String webhookUrl, String accessToken, String sqlQuery) 
    {
        try
        {
            Solution solutionRequest = new Solution(sqlQuery);  
            System.out.println("Submitting solution to webhook...");
            Mono<String> responseMono = webClient
                    .post()
                    .uri(baseUrl + "/testWebhook/JAVA")
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(solutionRequest)
                    .retrieve()
                    .bodyToMono(String.class);
            String response = responseMono.block();
            System.out.println("************Solution submitted successfully*************");
            
        } 
        catch (Exception e) 
        {
            System.out.println("Error submitting solution: " + e.getMessage());
        }
    }
}