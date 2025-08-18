# Dependency Migration Guide: Java 8/Spring Boot 2.x → Java 17/Spring Boot 3.x

## Current Dependencies (pom.xml)

### Platform Dependencies
```xml
<!-- CURRENT - TO BE UPDATED -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version> <!-- Spring Boot 2.x -->
</parent>

<properties>
    <java.version>1.8</java.version> <!-- Java 8 -->
</properties>
```

### SOAP-Specific Dependencies
```xml
<!-- CURRENT - TO BE REMOVED -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web-services</artifactId>
</dependency>
<dependency>
    <groupId>wsdl4j</groupId>
    <artifactId>wsdl4j</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ws</groupId>
    <artifactId>spring-ws-core</artifactId>
</dependency>
```

### Build Plugins
```xml
<!-- CURRENT - TO BE REMOVED -->
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>jaxb2-maven-plugin</artifactId>
    <version>2.5.0</version>
    <executions>
        <execution>
            <goals>
                <goal>xjc</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <sources>
            <source>src/main/resources/schema.xsd</source>
        </sources>
        <packageName>com.example.legacysoap.domain</packageName>
    </configuration>
</plugin>
```

## Target Dependencies (Updated pom.xml)

### Updated Platform Dependencies
```xml
<!-- UPDATED -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version> <!-- Latest stable Spring Boot 3.x -->
</parent>

<properties>
    <java.version>17</java.version> <!-- Java 17 -->
</properties>
```

### REST Dependencies
```xml
<!-- EXISTING - Keep these -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- NEW - Add for validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- EXISTING - Keep for testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

## Complete Updated pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>legacy-soap-shopping-cart</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>legacy-soap-shopping-cart</name>
    <description>Shopping Cart API - Migrated from SOAP to REST</description>

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
        <!-- Core Spring Boot Web for REST APIs -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Bean Validation for request validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <!-- Testing -->
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

## Key Changes Summary

### Removed Components
1. **SOAP Dependencies**:
   - `spring-boot-starter-web-services`
   - `wsdl4j`
   - `spring-ws-core`

2. **JAXB Build Plugin**:
   - `jaxb2-maven-plugin` (no longer needed for REST)

3. **Generated Classes**:
   - All classes in `target/generated-sources/jaxb/` directory

### Added Components
1. **Validation Dependency**:
   - `spring-boot-starter-validation` for Bean Validation (JSR-303)

2. **Platform Upgrades**:
   - Java 8 → Java 17
   - Spring Boot 2.7.18 → 3.2.0

### Changed Imports
```java
// SOAP imports to be removed:
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

// REST imports to be added:
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
```

### Files to be Removed
1. `src/main/java/com/example/legacysoap/WebServiceConfig.java`
2. `src/main/java/com/example/legacysoap/ShoppingCartEndpoint.java`
3. `src/main/resources/schema.xsd`
4. All generated SOAP domain classes in `target/generated-sources/`

### Files to be Added
1. REST DTOs in `src/main/java/com/example/legacysoap/rest/dto/`
2. REST Controller in `src/main/java/com/example/legacysoap/rest/controller/`
3. Optional: Service layer for business logic separation

## Migration Steps

1. **Backup Current Code**
   ```bash
   git checkout -b backup-soap-version
   git checkout main
   ```

2. **Update pom.xml**
   - Replace with new dependencies
   - Update Java version to 17

3. **Remove SOAP Components**
   ```bash
   rm src/main/java/com/example/legacysoap/WebServiceConfig.java
   rm src/main/java/com/example/legacysoap/ShoppingCartEndpoint.java
   rm src/main/resources/schema.xsd
   ```

4. **Add REST Components**
   - Copy REST DTOs and controller from sample code
   - Update imports in main application class if needed

5. **Test Build**
   ```bash
   mvn clean compile
   mvn test
   ```

6. **Update Application Properties**
   ```properties
   # Optional: Change server port or add other REST-specific configs
   server.port=8080
   ```

## Breaking Changes to Consider

1. **Client Integration**: Existing SOAP clients will need to be updated
2. **Data Format**: XML → JSON
3. **Endpoint URLs**: `/ws/*` → `/api/cart/*`
4. **Error Responses**: Different format and HTTP status codes
5. **Authentication**: May need to implement REST-specific auth if required

## Testing Strategy

1. **Unit Tests**: Test REST controllers and DTOs
2. **Integration Tests**: Test complete REST endpoints
3. **Performance Tests**: Compare performance with SOAP version
4. **Compatibility Tests**: Ensure no regressions in business logic