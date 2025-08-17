package com.example.legacysoap.client;

import com.example.legacysoap.domain.AddItemRequest;
import com.example.legacysoap.domain.AddItemResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class ShoppingCartClient extends WebServiceGatewaySupport {
    public AddItemResponse addItem(String productId, int quantity) {
        AddItemRequest request = new AddItemRequest();
        request.setProductId(productId);
        request.setQuantity(quantity);
        return (AddItemResponse) getWebServiceTemplate().marshalSendAndReceive("http://localhost:8080/ws", request);
    }

    // Add getCart, etc.
}
