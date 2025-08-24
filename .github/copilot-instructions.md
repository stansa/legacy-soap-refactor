# Legacy SOAP Shopping Cart Application

Spring Boot SOAP web service application implementing a shopping cart system with in-memory storage. Uses SOAP/XML messaging with Spring WS framework and JAXB for XML binding.

**ALWAYS follow these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.**

## Working Effectively

### Bootstrap and Build the Application
- **Requirement**: Java 17+ (application runs fine despite Java 8 target in pom.xml)
- **CRITICAL TIMING**: Set timeouts to 60+ minutes for Maven builds. NEVER CANCEL builds early.

```bash
# Initial build - downloads dependencies, takes ~30 seconds first time
mvn clean compile
# NEVER CANCEL: First build with dependencies takes ~30 seconds, subsequent builds ~5 seconds

# Full build with packaging - takes ~20 seconds
mvn clean package
# NEVER CANCEL: Package builds take ~20 seconds. Set timeout to 60+ minutes for safety.

# Run tests (currently no tests exist)
mvn test
# Takes ~2 seconds - no tests to run
```

### Run the Application

#### Using Maven (Development)
```bash
mvn spring-boot:run
# Starts in ~2 seconds, runs on port 8081
# Application ready when you see: "Started LegacySoapApplication in X.XXX seconds"
```

#### Using JAR (Production)
```bash
# After mvn clean package
java -jar target/legacy-soap-shopping-cart-0.0.1-SNAPSHOT.jar
# Starts in ~2 seconds, runs on port 8081
```

### Code Quality and Validation
```bash
# Run checkstyle (will find 46+ violations - this is expected)
mvn checkstyle:check
# Takes ~3 seconds, fails with code style violations
# This is NORMAL - the codebase has style issues that are not your responsibility to fix
```

## Application Architecture

### Key Components
- **Main Application**: `src/main/java/com/example/legacysoap/LegacySoapApplication.java` - Spring Boot entry point
- **SOAP Endpoint**: `src/main/java/com/example/legacysoap/ShoppingCartEndpoint.java` - All SOAP operations
- **Web Service Config**: `src/main/java/com/example/legacysoap/WebServiceConfig.java` - SOAP/WSDL configuration
- **Client Code**: `src/main/java/com/example/legacysoap/client/ShoppingCartClient.java` - Example SOAP client
- **Schema Definition**: `src/main/resources/schema.xsd` - SOAP contract definition
- **Generated Classes**: `target/generated-sources/jaxb/com/example/legacysoap/domain/` - JAXB generated from XSD

### SOAP Operations Available
- **AddItem** - Add product to cart with quantity
- **GetCart** - Retrieve all items in cart
- **RemoveItem** - Remove product from cart completely  
- **UpdateQuantity** - Update quantity of existing product
- **ClearCart** - Remove all items from cart
- **Checkout** - Process cart (placeholder implementation)

### Important URLs
- **Application**: http://localhost:8081
- **WSDL**: http://localhost:8081/ws/shoppingCart.wsdl
- **SOAP Endpoint**: http://localhost:8081/ws

## Validation and Testing

### CRITICAL: Manual Validation Requirements
**ALWAYS run through complete SOAP workflow after making changes:**

1. **Start the application** using one of the run methods above
2. **Verify WSDL is accessible**:
   ```bash
   curl -I http://localhost:8081/ws/shoppingCart.wsdl
   # Should return HTTP/1.1 405 (HEAD not allowed, but service is running)
   
   curl -s http://localhost:8081/ws/shoppingCart.wsdl | head -10
   # Should return WSDL XML content
   ```

3. **Test AddItem operation**:
   ```bash
   cat > /tmp/add_item_request.xml << 'EOF'
   <?xml version="1.0" encoding="UTF-8"?>
   <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cart="http://example.com/shoppingcart">
       <soap:Header/>
       <soap:Body>
           <cart:AddItemRequest>
               <cart:productId>LAPTOP001</cart:productId>
               <cart:quantity>2</cart:quantity>
           </cart:AddItemRequest>
       </soap:Body>
   </soap:Envelope>
   EOF
   
   curl -X POST \
     -H "Content-Type: text/xml; charset=utf-8" \
     -H "SOAPAction: \"\"" \
     -d @/tmp/add_item_request.xml \
     http://localhost:8081/ws
   # Should return: <ns2:AddItemResponse><ns2:success>true</ns2:success></ns2:AddItemResponse>
   ```

4. **Test GetCart operation**:
   ```bash
   cat > /tmp/get_cart_request.xml << 'EOF'
   <?xml version="1.0" encoding="UTF-8"?>
   <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cart="http://example.com/shoppingcart">
       <soap:Header/>
       <soap:Body>
           <cart:GetCartRequest/>
       </soap:Body>
   </soap:Envelope>
   EOF
   
   curl -X POST \
     -H "Content-Type: text/xml; charset=utf-8" \
     -H "SOAPAction: \"\"" \
     -d @/tmp/get_cart_request.xml \
     http://localhost:8081/ws
   # Should return cart contents with LAPTOP001 quantity 2
   ```

### Pre-commit Validation
**ALWAYS run these checks before committing changes:**
```bash
# 1. Clean build to ensure no compile errors
mvn clean compile
# Must succeed - takes ~5-30 seconds

# 2. Full package build
mvn clean package  
# Must succeed - takes ~20 seconds

# 3. Start application and test at least one SOAP operation
mvn spring-boot:run &
sleep 5
# Run one of the SOAP tests above
pkill -f "spring-boot:run"
```

## Common Tasks and File Locations

### Maven Build Outputs
```bash
ls -la target/
# Contains:
# - legacy-soap-shopping-cart-0.0.1-SNAPSHOT.jar (~20MB executable JAR)
# - classes/ - compiled Java classes
# - generated-sources/jaxb/ - JAXB generated domain classes
```

### Configuration Files
```bash
# Application configuration
cat src/main/resources/application.properties
# Contains: server.port=8081

# SOAP schema definition  
cat src/main/resources/schema.xsd
# Defines all SOAP request/response structures
```

### Project Structure Overview
```bash
ls -la src/main/java/com/example/legacysoap/
# LegacySoapApplication.java - Main Spring Boot class
# ShoppingCartEndpoint.java - SOAP endpoint with all operations
# WebServiceConfig.java - Spring WS configuration
# client/ShoppingCartClient.java - Example SOAP client
```

## Known Issues and Workarounds

### JAXB Runtime Issue (RESOLVED)
- **Problem**: "Implementation of JAXB-API has not been found on module path or classpath"
- **Solution**: Added JAXB runtime dependencies to pom.xml (already fixed in current version)
- **Required Dependencies**:
  ```xml
  <dependency>
      <groupId>javax.xml.bind</groupId>
      <artifactId>jaxb-api</artifactId>
  </dependency>
  <dependency>
      <groupId>org.glassfish.jaxb</groupId>
      <artifactId>jaxb-runtime</artifactId>
  </dependency>
  ```

### Code Style Issues
- **Status**: Checkstyle finds 46+ violations - this is NORMAL and expected
- **Action**: Do NOT fix style violations unless specifically asked
- **Impact**: Does not affect application functionality

### No Tests
- **Status**: Project has no unit tests (mvn test finds "No tests to run")
- **Impact**: Cannot validate changes via automated tests
- **Mitigation**: Use manual SOAP testing procedures above

### In-Memory Storage
- **Limitation**: Shopping cart data is stored in memory (HashMap)
- **Impact**: Data lost on application restart
- **Behavior**: Expected - this is the intended design

## Build Environment Details

### Java and Maven Versions
- **Java**: OpenJDK 17+ (application targets Java 8 but runs on 17+)
- **Maven**: Apache Maven 3.9.11+
- **Spring Boot**: 2.7.18

### Port Configuration
- **Default Port**: 8081 (configured in application.properties)
- **SOAP Endpoint**: /ws
- **WSDL Location**: /ws/shoppingCart.wsdl

### Timing Expectations
- **Initial Maven build**: ~30 seconds (downloads dependencies)
- **Subsequent builds**: ~5-20 seconds  
- **Application startup**: ~2 seconds
- **Checkstyle validation**: ~3 seconds
- **SOAP request response**: <1 second

**CRITICAL**: Always set Maven timeout to 60+ minutes. NEVER CANCEL builds or long-running commands.