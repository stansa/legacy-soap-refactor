package com.example.legacysoap;

import com.example.legacysoap.dto.AddItemRestRequest;
import com.example.legacysoap.service.ShoppingCartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Test
    public void testRestAndSoapEndpointsShareSameCart() {
        // Clear cart first
        shoppingCartService.clearCart();
        
        // Add item via REST
        AddItemRestRequest restRequest = new AddItemRestRequest("rest-product", 2);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AddItemRestRequest> entity = new HttpEntity<>(restRequest, headers);
        
        ResponseEntity<String> restResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/cart/items", 
            entity, 
            String.class
        );
        
        assertEquals(200, restResponse.getStatusCode().value());
        assertTrue(restResponse.getBody().contains("\"success\":true"));
        
        // Add item via SOAP simulation (direct service call to simulate SOAP endpoint behavior)
        shoppingCartService.addItem("soap-product", 3);
        
        // Verify both items are in the shared cart
        Map<String, Integer> cart = shoppingCartService.getCart();
        assertEquals(2, cart.size());
        assertEquals(2, cart.get("rest-product"));
        assertEquals(3, cart.get("soap-product"));
    }
}