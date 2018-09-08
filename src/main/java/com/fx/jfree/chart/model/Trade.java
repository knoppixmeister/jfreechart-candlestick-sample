package com.fx.jfree.chart.model;

public class Trade {	
	private String	stock;
	private long 	time, size;
	private double 	price;

	public Trade(String stock, long time, double price, long size) {
		super();

		this.stock	= stock;
		this.time 	= time;
		this.price	= price;
		this.size	= size;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}
	
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public long getSize() {
		return size;
	}
	
	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "Trade [stock=" + stock + ", time=" + time + ", price=" + price + ", size=" + size + "]";
	}
}
