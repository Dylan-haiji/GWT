package com.google.gwt.sample.stockwatcher.client;

import java.util.Date;

import com.google.gwt.i18n.client.Messages;
/**
 * 
 * @ClassName: StockWatcherMessages  
 * @Description: 国际化日期格式
 * @author yang hai ji
 * @date 2018年4月25日
 */
public interface StockWatcherMessages extends Messages{
	/*指定参数的数量
	 *请注意，消息字符串中都嵌入了{0}。这些占位符将
	 *在运行时通过传递给我们的StockWatcherMessages接口方法的参数进行替换。
	 *如果您有一个需要多个参数的字符串，请按顺序对占位符编号。
	 *例如：myString = First parm is {0}, second parm is {1}, third parm is {2}.
	 *
	 *处理引用的文本
	 *如果您的消息包含单引号（'），与StockWatcher中的很多一样，
	 *您需要用Java属性文件中的两个连续单引号替换它们。通常，GWT消息的格式规则与适用于Java的MessageFormat类的相同。
	 */
	@DefaultMessage("''{0}'' is not a valid symbol.")
	String invalidSymbol(String symbol);

	@DefaultMessage("Last update: {0,date,medium} {0,time,medium}")
	String lastUpdate(Date timestamp);
}
