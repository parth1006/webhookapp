package com.bajaj.webhookapp;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebhookTaskRunner {

    @EventListener(ApplicationReadyEvent.class)
    public void onAppStart() {
        RestTemplate restTemplate = new RestTemplate();

        // Step 1: Send POST request to generateWebhook
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Parth Maheshwari");
        requestBody.put("regNo", "22BCE0467");
        requestBody.put("email", "parth.maheshwari2022@vitstudent.ac.in");


        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(url, request, WebhookResponse.class);
        System.out.println(response.getBody());

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String webhookUrl = response.getBody().getWebhook();
            String jwtToken = response.getBody().getAccessToken();
            System.out.println("✅ Webhook: " + webhookUrl);
            System.out.println("✅ Token: " + jwtToken);

            String sql =
                            "SELECT p.AMOUNT AS SALARY, " +
                            "e.FIRST_NAME || ' ' || e.LAST_NAME AS NAME, " +
                            "EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM e.DOB) AS AGE, " +
                            "d.DEPARTMENT_NAME " +
                            "FROM PAYMENTS p " +
                            "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                            "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                            "WHERE EXTRACT(DAY FROM p.PAYMENT_TIME) != 1 " +
                            "ORDER BY p.AMOUNT DESC " +
                            "LIMIT 1;";

            submitSolution(restTemplate, webhookUrl, jwtToken, sql);
        }

        else {
            System.out.println("Failed to generate webhook.");
        }
    }

    private void submitSolution(RestTemplate restTemplate, String webhookUrl, String accessToken, String sql) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", accessToken);
    Map<String, String> body = new HashMap<>();
    body.put("finalQuery", sql);

    HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

    try {
        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, request, String.class);
        System.out.println("Submission status: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody());
    } catch (Exception e) {
        System.out.println("Submission failed: " + e.getMessage());
        if (e instanceof HttpClientErrorException) {
            HttpClientErrorException he = (HttpClientErrorException) e;
            System.out.println("Server Response: " + he.getResponseBodyAsString());
        }
    }
}

}
