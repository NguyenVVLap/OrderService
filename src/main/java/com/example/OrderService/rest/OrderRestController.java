package com.example.OrderService.rest;

import java.util.*;

import com.example.OrderService.entity.Order;
import com.example.OrderService.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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

	public List<Map<String, Object>> informFallForGetByUserId(Exception e) {
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("message", "Fall to rest template");
		result.add(map);
		return result;
	}

	@GetMapping("/orders/byUserId/{id}")
	@CircuitBreaker(name = "orderGetService", fallbackMethod = "informFallForGetByUserId")
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
	
	public Map<String, Object> informFall(Exception e) {
		Map<String, Object> result = new HashMap<>();
		result.put("message", "Fall to rest template");
		return result;
	}
	
	@PostMapping(path = "/order")
	@CircuitBreaker(name = "orderPostService", fallbackMethod = "informFall")
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
			for (Object orderDetail: orderDetails) {
				restTemplate.postForObject(crmRestUrlOrderDetail + "/orderDetail", orderDetail, String.class);
			}
			orderService.saveOrder(order);
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
	@CircuitBreaker(name = "orderPutService", fallbackMethod = "informFall")
	public Map<String, Object> updateOrder(@RequestBody Order order) {
//		order.setUserId(userId);
//		order.setCash(isCash);
//		order.setPaid(isPaid);
//		order.setCompleted(isCompleted);
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

	public String informFallForDelete(Exception e) {
		return "Fail to rest template for delete";
	}

	@DeleteMapping("/order")
	@CircuitBreaker(name = "orderDeleteService", fallbackMethod = "informFallForDelete")
	public String deleteOrder(@RequestParam("orderId") long orderId) {
		restTemplate.delete(crmRestUrlOrderDetail + "/orderDetail/" + orderId);
		orderService.deleteOrder(orderId);
		return "Deleted order id: " + orderId;
	}
}
