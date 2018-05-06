package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.i18n.client.Constants;
/**
 * 
 * @ClassName: StockWatcherConstants  
 * @Description: 用于字符串常量，GWT常量界面
 * @author yang hai ji
 * @date 2018年4月25日
 */
public interface StockWatcherConstants extends Constants{
	@DefaultStringValue("StockWatcher")
	String stockWatcher();
	
	@DefaultStringValue("Symbol")
    String symbol();

    @DefaultStringValue("Price")
    String price();

    @DefaultStringValue("Change")
    String change();

    @DefaultStringValue("Remove")
    String remove();

    @DefaultStringValue("Add")
    String add();
}
