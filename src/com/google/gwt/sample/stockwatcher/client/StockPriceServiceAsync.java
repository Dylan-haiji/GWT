package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
/*
 * AsyncCallback��������������������ݵ���ʧ�ܻ��ǳɹ���������֮һ��
 * 				onFailure��Throwable����onSuccess��T����
 */
public interface StockPriceServiceAsync {
	void getPrices(String[] symbols,AsyncCallback<StockPrice[]> callback);
}
