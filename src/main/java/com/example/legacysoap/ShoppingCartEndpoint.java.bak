// src/main/java/com/example/legacysoap/ShoppingCartEndpoint.java
package com.example.legacysoap;

import com.example.legacysoap.domain.AddItemRequest;
import com.example.legacysoap.domain.AddItemResponse;
import com.example.legacysoap.domain.GetCartRequest;
import com.example.legacysoap.domain.GetCartResponse;
import com.example.legacysoap.domain.RemoveItemRequest;
import com.example.legacysoap.domain.RemoveItemResponse;
import com.example.legacysoap.domain.UpdateQuantityRequest;
import com.example.legacysoap.domain.UpdateQuantityResponse;
import com.example.legacysoap.domain.ClearCartRequest;
import com.example.legacysoap.domain.ClearCartResponse;
import com.example.legacysoap.domain.CheckoutRequest;
import com.example.legacysoap.domain.CheckoutResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.HashMap;
import java.util.Map;

@Endpoint
public class ShoppingCartEndpoint {
    private static final String NAMESPACE_URI = "http://example.com/shoppingcart";
    private Map<String, Integer> cart = new HashMap<>(); // In-memory cart

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AddItemRequest")
    @ResponsePayload
    public AddItemResponse addItem(@RequestPayload AddItemRequest request) {
        cart.put(request.getProductId(), cart.getOrDefault(request.getProductId(), 0) + request.getQuantity());
        AddItemResponse response = new AddItemResponse();
        response.setSuccess(true);
        return response;
    }

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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "RemoveItemRequest")
    @ResponsePayload
    public RemoveItemResponse removeItem(@RequestPayload RemoveItemRequest request) {
        cart.remove(request.getProductId());
        RemoveItemResponse response = new RemoveItemResponse();
        response.setSuccess(true);
        return response;
    }

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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ClearCartRequest")
    @ResponsePayload
    public ClearCartResponse clearCart(@RequestPayload ClearCartRequest request) {
        cart.clear();
        ClearCartResponse response = new ClearCartResponse();
        response.setSuccess(true);
        return response;
    }
}