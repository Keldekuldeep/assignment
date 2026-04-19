package com.bajaj.bfhl.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.bajaj.bfhl.model.GenerateRequest;
import com.bajaj.bfhl.service.ApiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private ApiService apiService;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("🚀 App Started...");

        // Step 1: Create request
        GenerateRequest request = new GenerateRequest();
        request.setName("Kuldeep");
        request.setRegNo("REG12347");
        request.setEmail("test@gmail.com");

        // Step 2: Call API
        String response = apiService.callGenerateWebhook(request);
        System.out.println("👉 Raw Response: " + response);

        // Step 3: Parse JSON response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response);

        String token = jsonNode.get("accessToken").asText();
        String webhook = jsonNode.get("webhook").asText();

        System.out.println("✅ Token: " + token);
        System.out.println("✅ Webhook: " + webhook);

        // Step 4: SQL query (practice)
        String finalQuery = "SELECT p.AMOUNT AS SALARY, e.FIRST_NAME || ' ' || e.LAST_NAME AS NAME, EXTRACT(YEAR FROM AGE(e.DOB)) AS AGE, d.DEPARTMENT_NAME FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID WHERE EXTRACT(DAY FROM p.PAYMENT_TIME) <> 1 AND p.AMOUNT = (SELECT MAX(AMOUNT) FROM PAYMENTS WHERE EXTRACT(DAY FROM PAYMENT_TIME) <> 1)";        
        
        // Step 5: Submit using REAL webhook + token
        String result = apiService.callSubmitWebhook(finalQuery, token, webhook);

        System.out.println("✅ Final Response: " + result);
    }
}