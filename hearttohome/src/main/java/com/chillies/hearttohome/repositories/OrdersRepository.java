package com.chillies.hearttohome.repositories;

import com.chillies.hearttohome.models.GiftOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<GiftOrder,Long> {

    List<GiftOrder> findByUserId(Long id);
    List<GiftOrder> findByUserIdOrderByIdDesc(Long userId);
    List<GiftOrder> findAllByOrderByIdDesc();
}
