package com.example.legacysoap.controller;

import com.example.legacysoap.dto.AddItemRestRequest;
import com.example.legacysoap.dto.AddItemRestResponse;
import com.example.legacysoap.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartRestController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/items")
    public ResponseEntity<AddItemRestResponse> addItem(@RequestBody AddItemRestRequest request) {
        shoppingCartService.addItem(request.getProductId(), request.getQuantity());
        AddItemRestResponse response = new AddItemRestResponse(true);
        return ResponseEntity.ok(response);
    }
}