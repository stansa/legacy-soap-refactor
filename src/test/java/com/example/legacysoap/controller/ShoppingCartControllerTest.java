package com.example.legacysoap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShoppingCartController.class)
@DisplayName("Shopping Cart Controller Tests")
class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        // Clear cart before each test
        mockMvc.perform(post("/api/cart/clear"));
    }

    @Test
    @DisplayName("Should add item to cart successfully")
    void shouldAddItemToCartSuccessfully() throws Exception {
        // Given
        ShoppingCartController.AddItemRequest request = new ShoppingCartController.AddItemRequest();
        request.setProductId("PRODUCT-001");
        request.setQuantity(5);

        // When & Then
        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("Should get empty cart initially")
    void shouldGetEmptyCartInitially() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartItems", hasSize(0)))
                .andExpect(jsonPath("$.cartItems", is(empty())));
    }

    @Test
    @DisplayName("Should get cart with item after adding - single test scenario")
    void shouldGetCartWithItemAfterAdding() throws Exception {
        // Given - Add an item first
        ShoppingCartController.AddItemRequest request = new ShoppingCartController.AddItemRequest();
        request.setProductId("PRODUCT-001");
        request.setQuantity(5);

        // When - Add item
        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // Then - Get cart and verify contents
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartItems", hasSize(1)))
                .andExpect(jsonPath("$.cartItems[0].productId", is("PRODUCT-001")))
                .andExpect(jsonPath("$.cartItems[0].quantity", is(5)));
    }

    @Test
    @DisplayName("Should accumulate quantity for same item - single test scenario")
    void shouldAccumulateQuantityForSameItem() throws Exception {
        // Given
        ShoppingCartController.AddItemRequest request = new ShoppingCartController.AddItemRequest();
        request.setProductId("PRODUCT-001");
        request.setQuantity(3);

        // When - Add same item twice
        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Then - Check cart contents
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartItems", hasSize(1)))
                .andExpect(jsonPath("$.cartItems[0].productId", is("PRODUCT-001")))
                .andExpect(jsonPath("$.cartItems[0].quantity", is(6))); // 3 + 3 = 6
    }

    @Test
    @DisplayName("Should handle multiple different items - single test scenario")
    void shouldHandleMultipleDifferentItems() throws Exception {
        // Given
        ShoppingCartController.AddItemRequest request1 = new ShoppingCartController.AddItemRequest();
        request1.setProductId("PRODUCT-001");
        request1.setQuantity(3);

        ShoppingCartController.AddItemRequest request2 = new ShoppingCartController.AddItemRequest();
        request2.setProductId("PRODUCT-002");
        request2.setQuantity(7);

        // When - Add multiple items
        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        // Then - Get cart and verify contents
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartItems", hasSize(2)))
                .andExpect(jsonPath("$.cartItems[*].productId", containsInAnyOrder("PRODUCT-001", "PRODUCT-002")))
                .andExpect(jsonPath("$.cartItems[*].quantity", containsInAnyOrder(3, 7)));
    }

    @Test
    @DisplayName("Should handle zero quantity in add item request")
    void shouldHandleZeroQuantity() throws Exception {
        // Given
        ShoppingCartController.AddItemRequest request = new ShoppingCartController.AddItemRequest();
        request.setProductId("PRODUCT-001");
        request.setQuantity(0);

        // When & Then
        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // Verify cart state
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems", hasSize(1)))
                .andExpect(jsonPath("$.cartItems[0].productId", is("PRODUCT-001")))
                .andExpect(jsonPath("$.cartItems[0].quantity", is(0)));
    }

    @Test
    @DisplayName("Should handle negative quantity in add item request")
    void shouldHandleNegativeQuantity() throws Exception {
        // Given
        ShoppingCartController.AddItemRequest request = new ShoppingCartController.AddItemRequest();
        request.setProductId("PRODUCT-001");
        request.setQuantity(-5);

        // When & Then
        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // Verify cart state
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems", hasSize(1)))
                .andExpect(jsonPath("$.cartItems[0].productId", is("PRODUCT-001")))
                .andExpect(jsonPath("$.cartItems[0].quantity", is(-5)));
    }

    @Test
    @DisplayName("Should handle empty product ID")
    void shouldHandleEmptyProductId() throws Exception {
        // Given
        ShoppingCartController.AddItemRequest request = new ShoppingCartController.AddItemRequest();
        request.setProductId("");
        request.setQuantity(5);

        // When & Then
        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // Verify cart state
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems", hasSize(1)))
                .andExpect(jsonPath("$.cartItems[0].productId", is("")))
                .andExpect(jsonPath("$.cartItems[0].quantity", is(5)));
    }

    @Test
    @DisplayName("Should handle invalid JSON in add item request")
    void shouldHandleInvalidJsonInAddItemRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle null product ID in JSON")
    void shouldHandleNullProductId() throws Exception {
        // Given - Request with null productId
        String requestWithNullProductId = "{\"productId\":null,\"quantity\":5}";

        // When & Then
        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithNullProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // Verify cart state
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems", hasSize(1)))
                .andExpect(jsonPath("$.cartItems[0].productId", nullValue()))
                .andExpect(jsonPath("$.cartItems[0].quantity", is(5)));
    }
}