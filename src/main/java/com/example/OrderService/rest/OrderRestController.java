package com.example.OrderService.rest;

import java.util.*;

import com.example.OrderService.entity.Order;
import com.example.OrderService.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/api")
public class OrderRestController {
	@Autowired
	private OrderService orderService;

	private final RestTemplate restTemplate = new RestTemplate();
	private final String crmRestUrlOrderDetail = "http://localhost:8005/api";
	private final String crmRestUrlUser = "http://localhost:8006/api";

	@GetMapping("/orders/byUserId/{id}")
	public List<Map<String, Object>> getOrdersById(@PathVariable long id) {
		List<Order> orders = orderService.getOrderByUser(id);
		List<Map<String, Object>> results = new ArrayList<>();
		for (Order order : orders) {
			Object user = restTemplate.getForObject(crmRestUrlUser + "/user/" + order.getUserId(), Object.class);
			ResponseEntity<List<Object>> responseEntity =
					restTemplate.exchange(crmRestUrlOrderDetail + "/orderDetails/byOrderId/" + order.getId(), HttpMethod.GET, null,
							new ParameterizedTypeReference<List<Object>>() {});
			List<Object> orderDetails = responseEntity.getBody();
			Map<String, Object> temp = new HashMap<>();
			temp.put("idOrder", order.getId());
			temp.put("phone", order.getPhone());
			temp.put("totalPrice", order.getTotalPrice());
			temp.put("address", order.getAddress());
			temp.put("customerName", order.getCustomerName());
			temp.put("isCash", order.isCash());
			temp.put("isPaid", order.isPaid());
			temp.put("isCompleted;", order.isCompleted());
			temp.put("invoiceDate", order.getInvoiceDate());
			temp.put("user", user);
			temp.put("orderDetails", orderDetails);
			results.add(temp);
		}
		return results;
	}
	

	
	@PostMapping(path = "/order")
	public Map<String, Object> saveOrder(
			@RequestParam("phone") String phone
			, @RequestParam("totalPrice") double totalPrice
			, @RequestParam("address") String address
			, @RequestParam("customerName") String customerName
			, @RequestParam("userId") long userId
			, @RequestParam("isCash") boolean isCash
			, @RequestParam("isPaid") boolean isPaid
			, @RequestParam("isCompleted") boolean isCompleted
			, @RequestBody List<Object> orderDetails) {
		Order orderTop1 = orderService.findTop1OrderById();
		Order order = null;
		if (orderTop1 != null) {
			order = new Order(orderTop1.getId() + 1, phone
					, totalPrice, address, customerName, isCash
					, isPaid, isCompleted, new Date(), userId);
		} else {
			order = new Order(1, phone, totalPrice, address
					, customerName, isCash, isPaid, isCompleted
					, new Date(), userId);
		}
		if (order != null) {
			orderService.saveOrder(order);
			for (Object orderDetail: orderDetails) {
				restTemplate.postForObject(crmRestUrlOrderDetail + "/orderDetail", orderDetail, String.class);
			}
		}
		Object user = restTemplate.getForObject(crmRestUrlUser + "/user/" + order.getUserId(), Object.class);
		ResponseEntity<List<Object>> responseEntity =
				restTemplate.exchange(crmRestUrlOrderDetail + "/orderDetails/byOrderId/" + order.getId(), HttpMethod.GET, null,
						new ParameterizedTypeReference<List<Object>>() {});
		List<Object> orderDetailReturns = responseEntity.getBody();
		Map<String, Object> temp = new HashMap<>();
		temp.put("idOrder", order.getId());
		temp.put("phone", order.getPhone());
		temp.put("totalPrice", order.getTotalPrice());
		temp.put("address", order.getAddress());
		temp.put("customerName", order.getCustomerName());
		temp.put("isCash", order.isCash());
		temp.put("isPaid", order.isPaid());
		temp.put("isCompleted;", order.isCompleted());
		temp.put("invoiceDate", order.getInvoiceDate());
		temp.put("user", user);
		temp.put("orderDetails", orderDetailReturns);
		return temp;
	}

	@PutMapping("/order")
	public Map<String, Object> updateOrder(@RequestBody Order order
			, @RequestParam("userId") long userId
			, @RequestParam("isCash") boolean isCash
			, @RequestParam("isPaid") boolean isPaid
			, @RequestParam("isCompleted") boolean isCompleted) {
		order.setUserId(userId);
		order.setCash(isCash);
		order.setPaid(isPaid);
		order.setCompleted(isCompleted);
		orderService.saveOrder(order);

		Object user = restTemplate.getForObject(crmRestUrlUser + "/user/" + order.getUserId(), Object.class);
		ResponseEntity<List<Object>> responseEntity =
				restTemplate.exchange(crmRestUrlOrderDetail + "/orderDetails/byOrderId/" + order.getId(), HttpMethod.GET, null,
						new ParameterizedTypeReference<List<Object>>() {});
		List<Object> orderDetailReturns = responseEntity.getBody();
		Map<String, Object> temp = new HashMap<>();
		temp.put("idOrder", order.getId());
		temp.put("phone", order.getPhone());
		temp.put("totalPrice", order.getTotalPrice());
		temp.put("address", order.getAddress());
		temp.put("customerName", order.getCustomerName());
		temp.put("isCash", order.isCash());
		temp.put("isPaid", order.isPaid());
		temp.put("isCompleted;", order.isCompleted());
		temp.put("invoiceDate", order.getInvoiceDate());
		temp.put("user", user);
		temp.put("orderDetails", orderDetailReturns);

		return temp;
	}
}
