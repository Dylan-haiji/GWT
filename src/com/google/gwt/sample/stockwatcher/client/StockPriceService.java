package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/*
 * ��ע��@RemoteServiceRelativePathע�⡣
 * �⽫�����������ģ�����URL��Ĭ��·���������
 */
@RemoteServiceRelativePath("stockPrices")
public interface StockPriceService extends RemoteService{
	StockPrice[] getPrices(String[] symbols)throws DelistedException;
}
