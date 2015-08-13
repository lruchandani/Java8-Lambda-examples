package com.lalit.streams.examples;

import java.math.BigDecimal;
import java.util.Date;

public class ProductSale {

	private String productName ;
	
	private Integer saleId ;
	
	private Date	saleDate ;
	
	private BigDecimal saleAmount ;
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Integer getSaleId() {
		return saleId;
	}
	public void setSaleId(Integer saleId) {
		this.saleId = saleId;
	}
	public Date getSaleDate() {
		return saleDate;
	}
	public void setSaleDate(Date saleDate) {
		this.saleDate = saleDate;
	}
	public BigDecimal getSaleAmount() {
		return saleAmount;
	}
	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}
	@Override
	public String toString() {
		return "ProductSale [productName=" + productName + ", saleId=" + saleId
				+ ", saleDate=" + saleDate + ", saleAmount=" + saleAmount + "]";
	}
	
}
