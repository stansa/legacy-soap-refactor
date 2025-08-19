# Phase 3 - SOAP to REST Pattern Identification

## Overview
This document identifies SOAP patterns in the current `ShoppingCartEndpoint.java` implementation and provides REST equivalents for migration to Java 17/Spring Boot 3.x.

## Current SOAP Implementation Analysis

### Identified SOAP Patterns

#### 1. Class-Level Annotations
**Current SOAP Pattern:**
```java
@Endpoint
public class ShoppingCartEndpoint {
    // SOAP endpoint methods
}
```

**REST Equivalent (Java 17/Spring Boot 3.x):**
```java
@RestController
@RequestMapping("/api/v1/cart")
public class ShoppingCartController {
    // REST endpoint methods
}
```

#### 2. Method-Level Operation Mapping
**Current SOAP Pattern:**
```java
@PayloadRoot(namespace = NAMESPACE_URI, localPart = "AddItemRequest")
@ResponsePayload
public AddItemResponse addItem(@RequestPayload AddItemRequest request) {
    // Implementation
}
```

**REST Equivalent (Java 17/Spring Boot 3.x):**
```java
@PostMapping("/items")
public ResponseEntity<AddItemResponse> addItem(@RequestBody @Valid AddItemRequest request) {
    // Implementation
    return ResponseEntity.ok(response);
}
```

#### 3. Parameter and Response Handling
**Current SOAP Pattern:**
- `@RequestPayload` for input parameters
- `@ResponsePayload` for return values
- XML-based serialization/deserialization

**REST Equivalent (Java 17/Spring Boot 3.x):**
- `@RequestBody` for JSON input
- `@PathVariable` for URL parameters
- `@RequestParam` for query parameters
- `ResponseEntity<T>` for HTTP status codes and response bodies
- JSON-based serialization/deserialization

## Operation-Specific Pattern Mapping

### 1. Add Item Operation

#### SOAP Implementation
```java
@PayloadRoot(namespace = NAMESPACE_URI, localPart = "AddItemRequest")
@ResponsePayload
public AddItemResponse addItem(@RequestPayload AddItemRequest request) {
    cart.put(request.getProductId(), 
             cart.getOrDefault(request.getProductId(), 0) + request.getQuantity());
    AddItemResponse response = new AddItemResponse();
    response.setSuccess(true);
    return response;
}
```

#### REST Implementation (Java 17/Spring Boot 3.x)
```java
@PostMapping("/items")
public ResponseEntity<AddItemResponse> addItem(@RequestBody @Valid AddItemRequest request) {
    try {
        cart.put(request.getProductId(), 
                 cart.getOrDefault(request.getProductId(), 0) + request.getQuantity());
        
        var response = new AddItemResponse();
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.badRequest()
                .body(new AddItemResponse(false, e.getMessage()));
    }
}
```

### 2. Get Cart Operation

#### SOAP Implementation
```java
@PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetCartRequest")
@ResponsePayload
public GetCartResponse getCart(@RequestPayload GetCartRequest request) {
    GetCartResponse response = new GetCartResponse();
    cart.forEach((productId, quantity) -> {
        GetCartResponse.CartItems item = new GetCartResponse.CartItems();
        item.setProductId(productId);
        item.setQuantity(quantity);
        response.getCartItems().add(item);
    });
    return response;
}
```

#### REST Implementation (Java 17/Spring Boot 3.x)
```java
@GetMapping
public ResponseEntity<GetCartResponse> getCart() {
    var response = new GetCartResponse();
    cart.forEach((productId, quantity) -> {
        var item = new GetCartResponse.CartItems();
        item.setProductId(productId);
        item.setQuantity(quantity);
        response.getCartItems().add(item);
    });
    return ResponseEntity.ok(response);
}
```

### 3. Remove Item Operation

#### SOAP Implementation
```java
@PayloadRoot(namespace = NAMESPACE_URI, localPart = "RemoveItemRequest")
@ResponsePayload
public RemoveItemResponse removeItem(@RequestPayload RemoveItemRequest request) {
    cart.remove(request.getProductId());
    RemoveItemResponse response = new RemoveItemResponse();
    response.setSuccess(true);
    return response;
}
```

#### REST Implementation (Java 17/Spring Boot 3.x)
```java
@DeleteMapping("/items/{productId}")
public ResponseEntity<RemoveItemResponse> removeItem(@PathVariable String productId) {
    if (cart.containsKey(productId)) {
        cart.remove(productId);
        var response = new RemoveItemResponse();
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    } else {
        return ResponseEntity.notFound().build();
    }
}
```

### 4. Update Quantity Operation

#### SOAP Implementation
```java
@PayloadRoot(namespace = NAMESPACE_URI, localPart = "UpdateQuantityRequest")
@ResponsePayload
public UpdateQuantityResponse updateQuantity(@RequestPayload UpdateQuantityRequest request) {
    if (cart.containsKey(request.getProductId())) {
        cart.put(request.getProductId(), request.getQuantity());
        UpdateQuantityResponse response = new UpdateQuantityResponse();
        response.setSuccess(true);
        return response;
    }
    UpdateQuantityResponse response = new UpdateQuantityResponse();
    response.setSuccess(false);
    return response;
}
```

#### REST Implementation (Java 17/Spring Boot 3.x)
```java
@PutMapping("/items/{productId}")
public ResponseEntity<UpdateQuantityResponse> updateQuantity(
        @PathVariable String productId, 
        @RequestBody @Valid UpdateQuantityRequest request) {
    
    if (cart.containsKey(productId)) {
        cart.put(productId, request.getQuantity());
        var response = new UpdateQuantityResponse();
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    } else {
        var response = new UpdateQuantityResponse();
        response.setSuccess(false);
        return ResponseEntity.notFound().body(response);
    }
}
```

### 5. Clear Cart Operation

#### SOAP Implementation
```java
@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ClearCartRequest")
@ResponsePayload
public ClearCartResponse clearCart(@RequestPayload ClearCartRequest request) {
    cart.clear();
    ClearCartResponse response = new ClearCartResponse();
    response.setSuccess(true);
    return response;
}
```

#### REST Implementation (Java 17/Spring Boot 3.x)
```java
@DeleteMapping
public ResponseEntity<ClearCartResponse> clearCart() {
    cart.clear();
    var response = new ClearCartResponse();
    response.setSuccess(true);
    return ResponseEntity.ok(response);
}
```

## Complete REST Controller Example (Java 17/Spring Boot 3.x)

```java
package com.example.legacysoap.rest;

import com.example.legacysoap.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
public class ShoppingCartController {
    
    private final Map<String, Integer> cart = new HashMap<>();
    
    @PostMapping("/items")
    public ResponseEntity<AddItemResponse> addItem(@RequestBody @Valid AddItemRequest request) {
        cart.put(request.getProductId(), 
                 cart.getOrDefault(request.getProductId(), 0) + request.getQuantity());
        
        var response = new AddItemResponse();
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<GetCartResponse> getCart() {
        var response = new GetCartResponse();
        cart.forEach((productId, quantity) -> {
            var item = new GetCartResponse.CartItems();
            item.setProductId(productId);
            item.setQuantity(quantity);
            response.getCartItems().add(item);
        });
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<RemoveItemResponse> removeItem(@PathVariable String productId) {
        if (cart.containsKey(productId)) {
            cart.remove(productId);
            var response = new RemoveItemResponse();
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/items/{productId}")
    public ResponseEntity<UpdateQuantityResponse> updateQuantity(
            @PathVariable String productId, 
            @RequestBody @Valid UpdateQuantityRequest request) {
        
        if (cart.containsKey(productId)) {
            cart.put(productId, request.getQuantity());
            var response = new UpdateQuantityResponse();
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } else {
            var response = new UpdateQuantityResponse();
            response.setSuccess(false);
            return ResponseEntity.notFound().body(response);
        }
    }
    
    @DeleteMapping
    public ResponseEntity<ClearCartResponse> clearCart() {
        cart.clear();
        var response = new ClearCartResponse();
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }
}
```

## Migration Considerations for Java 17/Spring Boot 3.x

### 1. Dependency Updates
- **From:** `spring-boot-starter-web-services`
- **To:** `spring-boot-starter-web` (for REST endpoints)

### 2. Annotation Changes
- **From:** `@Endpoint` **To:** `@RestController`
- **From:** `@PayloadRoot` **To:** `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- **From:** `@RequestPayload` **To:** `@RequestBody`, `@PathVariable`, `@RequestParam`
- **From:** `@ResponsePayload` **To:** `ResponseEntity<T>`

### 3. HTTP Method Mapping
- **AddItem:** POST `/api/v1/cart/items`
- **GetCart:** GET `/api/v1/cart`
- **RemoveItem:** DELETE `/api/v1/cart/items/{productId}`
- **UpdateQuantity:** PUT `/api/v1/cart/items/{productId}`
- **ClearCart:** DELETE `/api/v1/cart`

### 4. Error Handling
- **SOAP:** Limited to SOAP faults
- **REST:** Full HTTP status codes (200, 201, 400, 404, 500, etc.)

### 5. Data Format
- **SOAP:** XML with strict schema validation
- **REST:** JSON with Bean Validation annotations

### 6. Java 17 Features to Leverage
- `var` keyword for local variables
- Records for immutable DTOs
- Text blocks for multi-line strings
- Pattern matching enhancements

### 7. Modern Spring Boot 3.x Features
- Jakarta EE (javax → jakarta package migration)
- Native compilation support
- Improved observability
- Enhanced security features

## Recommended Next Steps

1. **Phase 4:** Create REST DTOs (potentially using Java 17 records)
2. **Phase 5:** Implement REST controller alongside SOAP endpoint
3. **Phase 6:** Add comprehensive error handling and validation
4. **Phase 7:** Implement service layer abstraction
5. **Phase 8:** Add integration tests for REST endpoints
6. **Phase 9:** Deprecate SOAP endpoints
7. **Phase 10:** Remove SOAP configuration and dependencies