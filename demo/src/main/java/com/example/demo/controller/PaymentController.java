package com.example.demo.controller;

import com.example.demo.dto.payment.CreatePaymentRequest;
import com.example.demo.dto.payment.PaymentResponse;
import com.example.demo.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "APIs for recording and viewing customer payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Record payment",
            description = """
                    Records a customer payment and allocates the payment
                    across one or more cultivation jobs.
                    The total allocation must equal the payment amount.
                    """)
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get payment",
            description = "Returns payment details including job allocations")
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }

    @Operation(summary = "Get payments",
            description = "Returns paginated payments")
    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> getPayments(@RequestParam(required = false) Long customerId,
                                                             @RequestParam(required = false) LocalDate fromDate,
                                                             @RequestParam(required = false) LocalDate toDate,
                                                             @PageableDefault(size = 20, sort = "paymentDate") Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPayments(
                customerId,
                fromDate,
                toDate,
                pageable
                )
        );
    }
}
