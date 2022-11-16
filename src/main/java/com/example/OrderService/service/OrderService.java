package com.example.OrderService.service;

import com.example.OrderService.entity.Order;

import java.util.List;


public interface OrderService {
	public List<Order> getOrders();
	public void saveOrder(Order theOrder);
	public void deleteOrder(long id);
	public List<Order> getOrderByUser(long userId);
	public Order getOrderById(long id);
	public Order findTop1OrderById();
}
