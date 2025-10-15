package com.myfund.advice.controller;

import com.myfund.advice.model.AdviceRequest;
import com.myfund.advice.model.AdviceResponse;
import com.myfund.advice.service.AdviceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供 /advice 接口，面向前端应用或客服系统。
 */
@RestController
@RequestMapping("/advice")
public class AdviceController {

    private final AdviceService adviceService;

    public AdviceController(AdviceService adviceService) {
        this.adviceService = adviceService;
    }

    @PostMapping
    public ResponseEntity<AdviceResponse> generateAdvice(@Valid @RequestBody AdviceRequest request) {
        AdviceResponse response = adviceService.generateAdvice(request);
        return ResponseEntity.ok(response);
    }
}
