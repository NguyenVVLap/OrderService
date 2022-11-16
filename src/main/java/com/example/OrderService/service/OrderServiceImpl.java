package com.example.OrderService.service;

import java.util.List;
import java.util.Optional;

import com.example.OrderService.entity.Order;
import com.example.OrderService.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderRepository orderRepository;

	@Override
	public List<Order> getOrders() {
		return orderRepository.findAll();
	}

	@Override
	public void saveOrder(Order theOrder) {
		orderRepository.save(theOrder);
	}

	@Override
	public void deleteOrder(long id) {
		orderRepository.deleteById(id);
	}

	@Override
	public List<Order> getOrderByUser(long userId) {
		return orderRepository.findByUserId(userId);
	}

	@Override
	public Order getOrderById(long id) {
		Order order = null;
		
		Optional<Order> result = orderRepository.findById(id);
		
		if (result.isPresent()) {
			order = result.get();
		} else {
			throw new RuntimeException("Don't find order id - " + id);
		}
		
		return order;
	}

	@Override
	public Order findTop1OrderById() {
		return orderRepository.findTop1ByOrderByIdDesc();
	}

	
}
