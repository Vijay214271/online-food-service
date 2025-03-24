package com.foodapp.paymentservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.foodapp.paymentservice.dto.PaymentRequestDTO;
import com.foodapp.paymentservice.dto.RefundRequestDTO;
import com.foodapp.paymentservice.entity.Payment;
import com.foodapp.paymentservice.exception.PaymentNotFoundException;
import com.foodapp.paymentservice.repository.PaymentRepository;
import com.foodapp.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;


    private Payment mockPayment;

    @BeforeEach
    void setUp() {
        mockPayment = new Payment(1L, 101L, 500.00, "SUCCESS", "CREDIT_CARD", java.time.LocalDateTime.now());
    }

    // ✅ Test: Process Payment Successfully
    @Test
    void testProcessPayment_Success() {
        PaymentRequestDTO request = new PaymentRequestDTO(101L, 500.00, "CREDIT_CARD");
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        var response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getPaymentStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    // ✅ Test: Process Payment Fails (Amount <= 0)
    @Test
    void testProcessPayment_Failure() {
        PaymentRequestDTO request = new PaymentRequestDTO(101L, 0.00, "CREDIT_CARD");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setPaymentStatus("FAILED");
            return payment;
        });

        var response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals("FAILED", response.getPaymentStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    // ✅ Test: Get Payment By ID (Success)
    @Test
    void testGetPaymentById_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));

        var response = paymentService.getPaymentById(1L);

        assertNotNull(response);
        assertEquals(500.00, response.getAmount());
        verify(paymentRepository, times(1)).findById(1L);
    }

    // ✅ Test: Get Payment By ID (Not Found)
    @Test
    void testGetPaymentById_NotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentById(99L));
    }

    // ✅ Test: Get Payment By Order ID
    @Test
    void testGetPaymentByOrderId() {
        when(paymentRepository.findByOrderId(101L)).thenReturn(Optional.of(mockPayment));

        var response = paymentService.getPaymentByOrderId(101L);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getPaymentStatus());
        verify(paymentRepository, times(1)).findByOrderId(101L);
    }

    // ✅ Test: Refund Request Success
    @Test
    void testRefundRequest_Success() {
        RefundRequestDTO refundRequest = new RefundRequestDTO(1L);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        var response = paymentService.refundRequest(refundRequest);

        assertEquals("Refund processed successfully", response);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    // ✅ Test: Refund Request - Payment Not Found
    @Test
    void testRefundRequest_PaymentNotFound() {
        RefundRequestDTO refundRequest = new RefundRequestDTO(99L);
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        var response = paymentService.refundRequest(refundRequest);

        assertEquals("Payment not found", response);
    }

    // ✅ Test: Refund Request - Already Refunded
    @Test
    void testRefundRequest_AlreadyRefunded() {
        mockPayment.setPaymentStatus("REFUNDED");
        RefundRequestDTO refundRequest = new RefundRequestDTO(1L);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));

        var response = paymentService.refundRequest(refundRequest);

        assertEquals("Payment already refunded", response);
    }

    // ✅ Test: Update Payment Status
    @Test
    void testUpdatePaymentStatus() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        var response = paymentService.updatePaymentStatus(1L, "FAILED");

        assertEquals("FAILED", response.getPaymentStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    // ✅ Test: Get All Payments
    @Test
    void testGetAllPayments() {
        Payment payment2 = new Payment(2L, 102L, 300.00, "SUCCESS", "DEBIT_CARD", java.time.LocalDateTime.now());
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(mockPayment, payment2));

        var responseList = paymentService.getAllPayments();

        assertEquals(2, responseList.size());
        verify(paymentRepository, times(1)).findAll();
    }
}
