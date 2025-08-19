package com.example.legacysoap.service;

import com.example.legacysoap.dto.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration tests for CartService using Java 17/Spring Boot 3.x
 * Verifies thread-safety and core business logic.
 */
@SpringBootTest
@DisplayName("CartService Integration Tests")
class CartServiceIntegrationTest {

    @Autowired
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService.clearCart();
    }

    @Test
    @DisplayName("Should add a new item to the cart")
    void addItem_shouldAddNewItem() {
        CartItem item = cartService.addItem("PROD1", 2);
        assertThat(item.productId()).isEqualTo("PROD1");
        assertThat(item.quantity()).isEqualTo(2);
        assertThat(cartService.getTotalItems()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should increment quantity of an existing item")
    void addItem_shouldIncrementExistingItem() {
        cartService.addItem("PROD1", 2);
        CartItem item = cartService.addItem("PROD1", 3);
        assertThat(item.productId()).isEqualTo("PROD1");
        assertThat(item.quantity()).isEqualTo(5);
        assertThat(cartService.getTotalItems()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw exception for invalid quantity")
    void addItem_shouldThrowExceptionForInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addItem("PROD1", 0));
        assertThrows(IllegalArgumentException.class, () -> cartService.addItem("PROD1", -1));
    }

    @Test
    @DisplayName("Should retrieve all cart items")
    void getCartItems_shouldReturnAllItems() {
        cartService.addItem("PROD1", 2);
        cartService.addItem("PROD2", 5);
        List<CartItem> items = cartService.getCartItems();
        assertThat(items).hasSize(2);
        assertThat(items).containsExactlyInAnyOrder(
            new CartItem("PROD1", 2),
            new CartItem("PROD2", 5)
        );
    }

    @Test
    @DisplayName("Should handle concurrent additions correctly")
    void addItem_shouldBeThreadSafe() throws InterruptedException {
        int numberOfThreads = 10;
        int additionsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < additionsPerThread; j++) {
                    cartService.addItem("CONCURRENT_PROD", 1);
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertThat(cartService.getTotalQuantity()).isEqualTo(numberOfThreads * additionsPerThread);
        assertThat(cartService.getTotalItems()).isEqualTo(1);
    }
}
