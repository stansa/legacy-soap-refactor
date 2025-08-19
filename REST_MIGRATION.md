# REST API Migration - Shopping Cart

## Overview
This project successfully migrates the `addItem` method from SOAP to REST while maintaining full backward compatibility.

## Technical Stack
- **Java 17** (upgraded from Java 8)
- **Spring Boot 3.2.0** (upgraded from 2.7.18)
- **Spring Web Services** (for SOAP endpoints)
- **Spring Web** (for REST endpoints)

## API Endpoints

### REST API
- **POST** `/api/cart/items`
- **Content-Type:** `application/json`
- **Request Body:**
  ```json
  {
    "productId": "string",
    "quantity": number
  }
  ```
- **Response:**
  ```json
  {
    "success": boolean
  }
  ```

### SOAP API (Legacy - Still Available)
- **Endpoint:** `/ws`
- **WSDL:** `/ws/shoppingCart.wsdl`
- **Operation:** `AddItemRequest`

## Architecture

### Shared Components
- `ShoppingCartService` - Centralized business logic and data storage
- Both SOAP and REST endpoints use the same service, ensuring data consistency

### REST Components
- `ShoppingCartRestController` - REST endpoint controller
- `AddItemRestRequest` - REST request DTO
- `AddItemRestResponse` - REST response DTO

### SOAP Components (Existing)
- `ShoppingCartEndpoint` - SOAP endpoint (refactored to use service)
- Generated domain classes from XSD schema

## Testing
- Unit tests for REST controller using MockMvc
- Integration tests verifying both endpoints share the same cart
- All tests pass with Spring Boot 3.x and Java 17

## Migration Benefits
1. **Modern REST API** - JSON-based, RESTful design
2. **Backward Compatibility** - Existing SOAP clients continue to work
3. **Shared State** - Both endpoints operate on the same cart data
4. **Updated Platform** - Java 17 and Spring Boot 3.x for better performance and security
5. **Clean Architecture** - Service layer separation for better maintainability