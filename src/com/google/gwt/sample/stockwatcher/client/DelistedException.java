package com.google.gwt.sample.stockwatcher.client;

import java.io.Serializable;

/**
 * 
 * @ClassName: DelistedException
 * @Description: �쳣������
 * @author yang hai ji
 * @date 2018��4��25��
 */
public class DelistedException extends Exception implements Serializable {
	private String symbol;

	public DelistedException() {
	}

	public DelistedException(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return this.symbol;
	}
}