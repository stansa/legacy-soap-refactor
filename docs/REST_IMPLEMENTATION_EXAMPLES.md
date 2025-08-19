# REST API Implementation Examples

This document provides concrete implementation examples for the proposed REST API design to replace the current SOAP endpoints.

## SOAP vs REST Endpoint Comparison

| SOAP Operation | REST Endpoint | HTTP Method | Request Body | Response Format |
|---------------|---------------|-------------|--------------|-----------------|
| addItem | `/api/v1/cart/items` | POST | `{"productId": "string", "quantity": number}` | `{"success": boolean, "message": "string"}` |
| getCart | `/api/v1/cart` | GET | None | `{"items": [...], "totalItems": number}` |
| removeItem | `/api/v1/cart/items/{productId}` | DELETE | None | `{"success": boolean, "message": "string"}` |
| updateQuantity | `/api/v1/cart/items/{productId}` | PUT | `{"quantity": number}` | `{"success": boolean, "message": "string"}` |
| clearCart | `/api/v1/cart` | DELETE | None | `{"success": boolean, "message": "string"}` |
| checkout | `/api/v1/cart/checkout` | POST | None | `{"success": boolean, "total": number, "orderId": "string"}` |

## Example REST Controller Implementation

```java
package com.example.legacysoap.controller;

import com.example.legacysoap.dto.*;
import com.example.legacysoap.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@CrossOrigin(origins = "*") // Configure appropriately for production
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Add item to cart (equivalent to SOAP addItem)
     * POST /api/v1/cart/items
     */
    @PostMapping("/items")
    public ResponseEntity<OperationResponse> addItem(@Valid @RequestBody AddItemRequest request) {
        try {
            cartService.addItem(request.productId(), request.quantity());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new OperationResponse(true, "Item added successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new OperationResponse(false, e.getMessage()));
        }
    }

    /**
     * Get all cart items (equivalent to SOAP getCart)
     * GET /api/v1/cart
     */
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        List<CartItemResponse> items = cartService.getCartItems();
        int totalItems = items.stream().mapToInt(CartItemResponse::quantity).sum();
        
        return ResponseEntity.ok(new CartResponse(items, totalItems));
    }

    /**
     * Update item quantity (equivalent to SOAP updateQuantity)
     * PUT /api/v1/cart/items/{productId}
     */
    @PutMapping("/items/{productId}")
    public ResponseEntity<OperationResponse> updateQuantity(
            @PathVariable String productId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        
        boolean updated = cartService.updateQuantity(productId, request.quantity());
        
        if (updated) {
            return ResponseEntity.ok(
                new OperationResponse(true, "Quantity updated successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Remove item from cart (equivalent to SOAP removeItem)
     * DELETE /api/v1/cart/items/{productId}
     */
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<OperationResponse> removeItem(@PathVariable String productId) {
        boolean removed = cartService.removeItem(productId);
        
        if (removed) {
            return ResponseEntity.ok(
                new OperationResponse(true, "Item removed successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Clear entire cart (equivalent to SOAP clearCart)
     * DELETE /api/v1/cart
     */
    @DeleteMapping
    public ResponseEntity<OperationResponse> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok(
            new OperationResponse(true, "Cart cleared successfully"));
    }

    /**
     * Checkout cart (equivalent to SOAP checkout - not implemented in original)
     * POST /api/v1/cart/checkout
     */
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout() {
        try {
            CheckoutResult result = cartService.checkout();
            return ResponseEntity.ok(
                new CheckoutResponse(true, result.total(), result.orderId()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                .body(new CheckoutResponse(false, 0.0, null));
        }
    }
}
```

## Data Transfer Objects (DTOs)

```java
// Request DTOs with Bean Validation
package com.example.legacysoap.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddItemRequest(
    @NotBlank(message = "Product ID cannot be blank")
    String productId,
    
    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity
) {}

public record UpdateQuantityRequest(
    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity
) {}

// Response DTOs
public record CartItemResponse(
    String productId,
    int quantity
) {}

public record CartResponse(
    List<CartItemResponse> items,
    int totalItems
) {}

public record OperationResponse(
    boolean success,
    String message
) {}

public record CheckoutResponse(
    boolean success,
    double total,
    String orderId
) {}
```

## Updated POM.xml for Spring Boot 3.x

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>rest-shopping-cart</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>REST Shopping Cart</name>
    <description>RESTful Shopping Cart Service</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Actuator for monitoring -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- OpenAPI Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.2.0</version>
        </dependency>

        <!-- Test Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

## Key Benefits of REST over SOAP

### 1. Simplified Architecture
- **SOAP**: Complex XML schemas, WSDL files, namespaces
- **REST**: Simple JSON payloads, HTTP methods, resource URLs

### 2. Better Performance
- **JSON vs XML**: Smaller payload size, faster parsing
- **HTTP Caching**: Leverage browser and proxy caching
- **Connection Reuse**: HTTP keep-alive support

### 3. Developer Experience
- **Tools**: Better debugging with browser dev tools
- **Testing**: Simple curl commands and REST clients
- **Documentation**: Interactive API docs with Swagger/OpenAPI

### 4. Modern Integration
- **JavaScript**: Native JSON support
- **Mobile Apps**: Lightweight data format
- **Microservices**: Standard communication pattern

## Migration Testing Strategy

### Unit Tests Example
```java
@WebMvcTest(CartController.class)
class CartControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Test
    void addItem_ValidRequest_ReturnsCreated() throws Exception {
        doNothing().when(cartService).addItem(any(), anyInt());

        mockMvc.perform(post("/api/v1/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "product_id": "PROD123",
                        "quantity": 2
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }
}
```

### Integration Tests Example
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CartIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void fullCartWorkflow_Success() {
        // Add item
        AddItemRequest addRequest = new AddItemRequest("PROD123", 2);
        ResponseEntity<OperationResponse> addResponse = 
            restTemplate.postForEntity("/api/v1/cart/items", addRequest, OperationResponse.class);
        
        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(addResponse.getBody().success()).isTrue();

        // Get cart
        ResponseEntity<CartResponse> getResponse = 
            restTemplate.getForEntity("/api/v1/cart", CartResponse.class);
        
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().totalItems()).isEqualTo(2);
    }
}
```

This implementation provides a modern, RESTful replacement for the SOAP service with proper design patterns, validation, error handling, and comprehensive testing.