package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
/*
 * AsyncCallback对象包含两个方法，根据调用失败还是成功调用其中之一：
 * 				onFailure（Throwable）和onSuccess（T）。
 */
public interface StockPriceServiceAsync {
	void getPrices(String[] symbols,AsyncCallback<StockPrice[]> callback);
}
