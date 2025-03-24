package com.foodapp.deliveryservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.foodapp.deliveryservice.dto.DeliveryRequestDTO;
import com.foodapp.deliveryservice.dto.DeliveryResponseDTO;
import com.foodapp.deliveryservice.model.Delivery;
import com.foodapp.deliveryservice.respository.DeliveryRepository;
import com.foodapp.deliveryservice.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryService deliveryService;

    private Delivery mockDelivery;
    private DeliveryRequestDTO deliveryRequestDTO;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        mockDelivery = Delivery.builder()
                .id(1L)
                .orderId(101L)
                .deliveryPersonId(5001L)
                .status("ON THE WAY")
                .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(30))
                .build();

        deliveryRequestDTO = new DeliveryRequestDTO(101L, 5001L, LocalDateTime.now().plusMinutes(30));
    }

    @Test
    void testCreateDelivery() {
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(mockDelivery);

        DeliveryResponseDTO responseDTO = deliveryService.createDelivery(deliveryRequestDTO);

        assertNotNull(responseDTO);
        assertEquals("ON THE WAY", responseDTO.getStatus());
        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    void testFindDeliveryByOrderId() {
        when(deliveryRepository.findByOrderId(101L)).thenReturn(Optional.of(mockDelivery));

        DeliveryResponseDTO responseDTO = deliveryService.getDeliveryByOrderId(101L);

        assertNotNull(responseDTO);
        assertEquals(101L, responseDTO.getOrderId());
        verify(deliveryRepository, times(1)).findByOrderId(101L);
    }

    @Test
    void testFindDeliveryByOrderId_NotFound() {
        when(deliveryRepository.findByOrderId(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deliveryService.getDeliveryByOrderId(999L));
        assertEquals("Delivery not found", exception.getMessage());
    }
    @Test
    void testFindDeliveryById_NotFound() {
        when(deliveryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deliveryService.getDeliveryById(99L));
        assertEquals("Delivery not found", exception.getMessage());
    }

    @Test
    void testGetAllDeliveries() {
        when(deliveryRepository.findAll()).thenReturn(List.of(mockDelivery));

        List<DeliveryResponseDTO> deliveries = deliveryService.getAllDeliveries();

        assertFalse(deliveries.isEmpty());
        assertEquals(1, deliveries.size());
        verify(deliveryRepository, times(1)).findAll();
    }

    @Test
    void testDeleteDelivery() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(mockDelivery));
        doNothing().when(deliveryRepository).deleteById(1L);

        deliveryService.deleteDelivery(1L);

        verify(deliveryRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateDeliveryStatus() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(mockDelivery)); // Mock findById
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(mockDelivery);
    
        DeliveryResponseDTO responseDTO = deliveryService.updateDeliveryStatus(1L, "DELIVERED");
    
        assertNotNull(responseDTO);
        assertEquals("DELIVERED", responseDTO.getStatus());
        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }
    
    @Test
    
    void testSaveDelivery() {
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(mockDelivery);
        
        DeliveryResponseDTO savedDelivery = deliveryService.saveDelivery(deliveryRequestDTO);

        assertNotNull(savedDelivery);
        assertEquals(mockDelivery.getOrderId(), savedDelivery.getOrderId());
        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    void testFindDeliveryById() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(mockDelivery));

        DeliveryResponseDTO foundDelivery = deliveryService.getDeliveryById(1L);

        assertNotNull(foundDelivery);
        assertEquals("ON THE WAY", foundDelivery.getStatus());
        verify(deliveryRepository, times(1)).findById(1L);
    }

}
