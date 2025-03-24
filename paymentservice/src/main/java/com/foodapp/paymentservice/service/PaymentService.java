package com.foodapp.paymentservice.service;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.paymentservice.dto.PaymentRequestDTO;
import com.foodapp.paymentservice.dto.PaymentResponseDTO;
import com.foodapp.paymentservice.dto.RefundRequestDTO;
import com.foodapp.paymentservice.entity.Payment;
import com.foodapp.paymentservice.exception.PaymentNotFoundException;
import com.foodapp.paymentservice.repository.PaymentRepository;

import org.slf4j.Logger;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        if(request.getAmount() <= 0) {
            payment.setPaymentStatus("FAILED");
        } else {
            payment.setPaymentStatus("SUCCESS");
        }
        payment.setPaymentDate(java.time.LocalDateTime.now());
        paymentRepository.save(payment);

        logger.info("Payment processed for order id: {}", request.getOrderId());

        return mapToDTO(payment);
    }

    public PaymentResponseDTO getPaymentById(Long id){
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        return mapToDTO(payment);
    }

    public PaymentResponseDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        return mapToDTO(payment);
    }
    
    
    @Transactional
    public String refundRequest(RefundRequestDTO refundRequest) {
        Optional<Payment> paymentOpt = paymentRepository.findById(refundRequest.getPaymentId());

        if(paymentOpt.isEmpty()) {
            return "Payment not found";
        }

        Payment payment = paymentOpt.get();

        if("REFUNDED".equals(payment.getPaymentStatus())){
            return "Payment already refunded";
        } 

        payment.setPaymentStatus("REFUNDED");
        paymentRepository.save(payment);

        return "Refund processed successfully";
        }



    public List<PaymentResponseDTO> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public PaymentResponseDTO updatePaymentStatus(Long id,String request) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        payment.setPaymentStatus(request);
        paymentRepository.save(payment);
        return mapToDTO(payment);
    }

    private PaymentResponseDTO mapToDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrderId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentDate(payment.getPaymentDate());
        return dto;
    }

}
