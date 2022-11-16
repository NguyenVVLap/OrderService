package com.example.OrderService.repository;

import java.util.List;

import com.example.OrderService.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OrderRepository extends JpaRepository<Order, Long>{
	@Query(nativeQuery = true, value = "select * from orders where user_id = :user_id order by invoice_date desc")
	public List<Order> findByUserId(@Param("user_id") long userId);
	public Order findTop1ByOrderByIdDesc();
}
