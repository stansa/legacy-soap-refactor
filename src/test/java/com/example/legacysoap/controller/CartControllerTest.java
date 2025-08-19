package com.example.legacysoap.controller;

import com.example.legacysoap.dto.AddItemRequest;
import com.example.legacysoap.dto.CartItem;
import com.example.legacysoap.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * JUnit 5 tests for REST CartController using Java 17/Spring Boot 3.x
 * Tests the refactored SOAP to REST migration with MockMvc
 */
import com.example.legacysoap.config.RestApiConfig;
import com.example.legacysoap.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@WebMvcTest(CartController.class)
@Import({RestApiConfig.class, GlobalExceptionHandler.class})
@DisplayName("CartController REST API Tests")
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Add Item Tests")
    class AddItemTests {

        @Test
        @DisplayName("Should return 201 Created when adding valid item")
        void addItem_ShouldReturnCreated_WhenValidRequest() throws Exception {
            // Given
            AddItemRequest request = new AddItemRequest("PROD123", 2);
            CartItem cartItem = new CartItem("PROD123", 2);
            when(cartService.addItem("PROD123", 2)).thenReturn(cartItem);

            // When & Then
            mockMvc.perform(post("/api/v1/cart/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.productId").value("PROD123"))
                    .andExpect(jsonPath("$.quantity").value(2))
                    .andExpect(jsonPath("$.success").value(true));

            verify(cartService).addItem("PROD123", 2);
        }

        @Test
        @DisplayName("Should return 201 Created when incrementing existing item")
        void addItem_ShouldReturnCreated_WhenIncrementingExistingItem() throws Exception {
            // Given
            AddItemRequest request = new AddItemRequest("PROD456", 3);
            CartItem cartItem = new CartItem("PROD456", 5); // Already had 2, now 5
            when(cartService.addItem("PROD456", 3)).thenReturn(cartItem);

            // When & Then
            mockMvc.perform(post("/api/v1/cart/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.productId").value("PROD456"))
                    .andExpect(jsonPath("$.quantity").value(5))
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when quantity is zero")
        void addItem_ShouldReturnBadRequest_WhenQuantityIsZero() throws Exception {
            // Given
            AddItemRequest request = new AddItemRequest("PROD123", 0);

            // When & Then
            mockMvc.perform(post("/api/v1/cart/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(cartService, never()).addItem(anyString(), anyInt());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when quantity is negative")
        void addItem_ShouldReturnBadRequest_WhenQuantityIsNegative() throws Exception {
            // Given
            AddItemRequest request = new AddItemRequest("PROD123", -1);

            // When & Then
            mockMvc.perform(post("/api/v1/cart/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(cartService, never()).addItem(anyString(), anyInt());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when product ID is blank")
        void addItem_ShouldReturnBadRequest_WhenProductIdIsBlank() throws Exception {
            // Given
            AddItemRequest request = new AddItemRequest("", 1);

            // When & Then
            mockMvc.perform(post("/api/v1/cart/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(cartService, never()).addItem(anyString(), anyInt());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when product ID is null")
        void addItem_ShouldReturnBadRequest_WhenProductIdIsNull() throws Exception {
            // Given - Create JSON manually to include null
            String requestJson = "{\"productId\":null,\"quantity\":1}";

            // When & Then
            mockMvc.perform(post("/api/v1/cart/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isBadRequest());

            verify(cartService, never()).addItem(anyString(), anyInt());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when service throws IllegalArgumentException")
        void addItem_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
            // Given
            AddItemRequest request = new AddItemRequest("PROD123", 1);
            when(cartService.addItem("PROD123", 1))
                    .thenThrow(new IllegalArgumentException("Invalid product"));

            // When & Then
            mockMvc.perform(post("/api/v1/cart/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("Should return 500 Internal Server Error when unexpected exception occurs")
        void addItem_ShouldReturnInternalServerError_WhenUnexpectedExceptionOccurs() throws Exception {
            // Given
            AddItemRequest request = new AddItemRequest("PROD123", 1);
            when(cartService.addItem("PROD123", 1))
                    .thenThrow(new RuntimeException("Database connection failed"));

            // When & Then
            mockMvc.perform(post("/api/v1/cart/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("Get Cart Tests")
    class GetCartTests {

        @Test
        @DisplayName("Should return 200 OK with empty cart when no items")
        void getCart_ShouldReturnOk_WhenCartIsEmpty() throws Exception {
            // Given
            when(cartService.getCartItems()).thenReturn(List.of());

            // When & Then
            mockMvc.perform(get("/api/v1/cart")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items").isEmpty())
                    .andExpect(jsonPath("$.totalItems").value(0))
                    .andExpect(jsonPath("$.totalQuantity").value(0));

            verify(cartService).getCartItems();
        }

        @Test
        @DisplayName("Should return 200 OK with single item in cart")
        void getCart_ShouldReturnOk_WhenCartHasSingleItem() throws Exception {
            // Given
            List<CartItem> cartItems = List.of(new CartItem("PROD123", 2));
            when(cartService.getCartItems()).thenReturn(cartItems);

            // When & Then
            mockMvc.perform(get("/api/v1/cart")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items").isNotEmpty())
                    .andExpect(jsonPath("$.items[0].productId").value("PROD123"))
                    .andExpect(jsonPath("$.items[0].quantity").value(2))
                    .andExpect(jsonPath("$.totalItems").value(1))
                    .andExpect(jsonPath("$.totalQuantity").value(2));
        }

        @Test
        @DisplayName("Should return 200 OK with multiple items in cart")
        void getCart_ShouldReturnOk_WhenCartHasMultipleItems() throws Exception {
            // Given
            List<CartItem> cartItems = List.of(
                new CartItem("PROD123", 2),
                new CartItem("PROD456", 3),
                new CartItem("PROD789", 1)
            );
            when(cartService.getCartItems()).thenReturn(cartItems);

            // When & Then
            mockMvc.perform(get("/api/v1/cart")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items").isNotEmpty())
                    .andExpect(jsonPath("$.items[0].productId").value("PROD123"))
                    .andExpect(jsonPath("$.items[0].quantity").value(2))
                    .andExpect(jsonPath("$.items[1].productId").value("PROD456"))
                    .andExpect(jsonPath("$.items[1].quantity").value(3))
                    .andExpect(jsonPath("$.items[2].productId").value("PROD789"))
                    .andExpect(jsonPath("$.items[2].quantity").value(1))
                    .andExpect(jsonPath("$.totalItems").value(3))
                    .andExpect(jsonPath("$.totalQuantity").value(6));
        }

        @Test
        @DisplayName("Should handle service exception gracefully")
        void getCart_ShouldHandleServiceException_Gracefully() throws Exception {
            // Given
            when(cartService.getCartItems()).thenThrow(new RuntimeException("Service unavailable"));

            // When & Then
            mockMvc.perform(get("/api/v1/cart")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("Content Type and Header Tests")
    class ContentTypeTests {

        @Test
        @DisplayName("Should accept application/json content type")
        void addItem_ShouldAcceptApplicationJson() throws Exception {
            // Given
            AddItemRequest request = new AddItemRequest("PROD123", 1);
            CartItem cartItem = new CartItem("PROD123", 1);
            when(cartService.addItem("PROD123", 1)).thenReturn(cartItem);

            // When & Then
            mockMvc.perform(post("/api/v1/cart/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Should return 415 Unsupported Media Type for XML content")
        void addItem_ShouldReturnUnsupportedMediaType_ForXmlContent() throws Exception {
            // Given
            String xmlContent = "<addItemRequest><productId>PROD123</productId><quantity>1</quantity></addItemRequest>";

            // When & Then
            mockMvc.perform(post("/api/v1/cart/items")
                    .contentType(MediaType.APPLICATION_XML)
                    .content(xmlContent))
                    .andExpect(status().isUnsupportedMediaType());
        }
    }
}
