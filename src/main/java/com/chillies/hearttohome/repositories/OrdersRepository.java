package com.chillies.hearttohome.repositories;

import com.chillies.hearttohome.DTO.AllOrdersDTO;
import com.chillies.hearttohome.entity.GiftOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<GiftOrder,Long> {

    List<GiftOrder> findByUserIdOrderByIdDesc(Long userId);
    List<AllOrdersDTO> findAllByOrderByIdDesc();
}
