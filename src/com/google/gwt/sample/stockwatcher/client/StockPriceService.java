package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/*
 * ：注意@RemoteServiceRelativePath注解。
 * 这将服务与相对于模块基础URL的默认路径相关联。
 */
@RemoteServiceRelativePath("stockPrices")
public interface StockPriceService extends RemoteService{
	StockPrice[] getPrices(String[] symbols)throws DelistedException;
}
