package com.google.gwt.sample.stockwatcher.client;

import java.io.Serializable;

/**
 * 
 * @ClassName: StockPrice
 * @Description: 现在您有一个StockPrice类来封装股票价格数据，
 *               您可以生成实际数据。为此，您将实施refreshWatchList方法。
 *               记住refreshWatchList方法在用户将股票添加到股票表时调用， 然后在计时器触发时每5秒调用一次。
 * @author yang hai ji
 * @date 2018年4月24日WWW
 */
public class StockPrice implements Serializable {
	private String symbol;
	private double price;
	private double change;

	public StockPrice() {
	}

	public StockPrice(String symbol, double price, double change) {
		this.symbol = symbol;
		this.price = price;
		this.change = change;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public double getPrice() {
		return this.price;
	}

	public double getChange() {
		return this.change;
	}

	public double getChangePercent() {
		return 100.0 * this.change / this.price;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setChange(double change) {
		this.change = change;
	}
}
