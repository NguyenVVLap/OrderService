package com.example.OrderService.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
	@Id
	private long id;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "totalPrice")
	private double totalPrice;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "customerName")
	private String customerName;
	
	@Column(name = "isCash")
	private boolean isCash;
	
	@Column(name = "isPaid")	
	private boolean isPaid;
	
	@Column(name = "isCompleted")	
	private boolean isCompleted;
	
	@Column(name = "invoice_date")
	private Date invoiceDate;

	@Column(name = "user_id")
	private long userId;

}
