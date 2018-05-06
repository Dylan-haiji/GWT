package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.i18n.client.Constants;
/**
 * 
 * @ClassName: StockWatcherConstants  
 * @Description: �����ַ���������GWT��������
 * @author yang hai ji
 * @date 2018��4��25��
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
