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
import com.example.legacysoap.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Map;

@Endpoint
public class ShoppingCartEndpoint {
    private static final String NAMESPACE_URI = "http://example.com/shoppingcart";

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AddItemRequest")
    @ResponsePayload
    public AddItemResponse addItem(@RequestPayload AddItemRequest request) {
        shoppingCartService.addItem(request.getProductId(), request.getQuantity());
        AddItemResponse response = new AddItemResponse();
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetCartRequest")
    @ResponsePayload
    public GetCartResponse getCart(@RequestPayload GetCartRequest request) {
        GetCartResponse response = new GetCartResponse();
        Map<String, Integer> cart = shoppingCartService.getCart();
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
        shoppingCartService.removeItem(request.getProductId());
        RemoveItemResponse response = new RemoveItemResponse();
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UpdateQuantityRequest")
    @ResponsePayload
    public UpdateQuantityResponse updateQuantity(@RequestPayload UpdateQuantityRequest request) {
        boolean success = shoppingCartService.updateQuantity(request.getProductId(), request.getQuantity());
        UpdateQuantityResponse response = new UpdateQuantityResponse();
        response.setSuccess(success);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ClearCartRequest")
    @ResponsePayload
    public ClearCartResponse clearCart(@RequestPayload ClearCartRequest request) {
        shoppingCartService.clearCart();
        ClearCartResponse response = new ClearCartResponse();
        response.setSuccess(true);
        return response;
    }
}