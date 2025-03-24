package com.foodapp.deliveryservice.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foodapp.deliveryservice.dto.DeliveryRequestDTO;
import com.foodapp.deliveryservice.dto.DeliveryResponseDTO;
import com.foodapp.deliveryservice.model.Delivery;
import com.foodapp.deliveryservice.respository.DeliveryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public DeliveryResponseDTO getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(() -> new RuntimeException("Delivery not found"));
        return mapToDto(delivery);
    }

    public DeliveryResponseDTO createDelivery(DeliveryRequestDTO request) {
        Delivery delivery = Delivery.builder()
                .orderId(request.getOrderId())
                .deliveryPersonId(request.getDeliveryPersonId())
                .status("PENDING")
                .estimatedDeliveryTime(request.getEstimatedDeliveryTime())
                .build();
        Delivery savedDelivery = deliveryRepository.save(delivery);
        return mapToDto(savedDelivery);
    }

    public DeliveryResponseDTO getDeliveryByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        return mapToDto(delivery);
    }

    public List<DeliveryResponseDTO> getAllDeliveries() {
        List<Delivery> deliveries = deliveryRepository.findAll();
        return deliveries.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public DeliveryResponseDTO updateDeliveryStatus(Long Id, String status) {
        Delivery delivery = deliveryRepository.findById(Id).orElseThrow(() -> new RuntimeException("Delivery not found"));
        delivery.setStatus(status);
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return mapToDto(updatedDelivery);
    }

    public void deleteDelivery(Long id){
        Optional<Delivery> delivery = deliveryRepository.findById(id);
        if(!delivery.isPresent()){
            throw new RuntimeException("Delivery with ID " + id + " does not exist");
        }
        deliveryRepository.deleteById(delivery.get().getId());
    }

    public DeliveryResponseDTO saveDelivery(DeliveryRequestDTO request) {
        // Create a Delivery entity from the request DTO
        Delivery delivery = Delivery.builder()
                .orderId(request.getOrderId())
                .deliveryPersonId(request.getDeliveryPersonId())
                .status("Saved")
                .estimatedDeliveryTime(request.getEstimatedDeliveryTime())
                .build();
    
        // Save to the database
        Delivery savedDelivery = deliveryRepository.save(delivery);
    
        // Convert the saved entity to response DTO
        return mapToDto(savedDelivery);
    }
    

    public DeliveryResponseDTO mapToDto(Delivery delivery) {
        DeliveryResponseDTO dto = new DeliveryResponseDTO();
        dto.setId(delivery.getId());
        dto.setOrderId(delivery.getOrderId());
        dto.setDeliveryPersonId(delivery.getDeliveryPersonId());
        dto.setStatus(delivery.getStatus());
        dto.setEstimatedDeliveryTime(delivery.getEstimatedDeliveryTime());
        return dto;
    }
}
