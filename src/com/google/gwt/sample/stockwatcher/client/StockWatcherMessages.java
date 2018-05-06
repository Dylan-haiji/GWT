package com.google.gwt.sample.stockwatcher.client;

import java.util.Date;

import com.google.gwt.i18n.client.Messages;
/**
 * 
 * @ClassName: StockWatcherMessages  
 * @Description: ���ʻ����ڸ�ʽ
 * @author yang hai ji
 * @date 2018��4��25��
 */
public interface StockWatcherMessages extends Messages{
	/*ָ������������
	 *��ע�⣬��Ϣ�ַ����ж�Ƕ����{0}����Щռλ����
	 *������ʱͨ�����ݸ����ǵ�StockWatcherMessages�ӿڷ����Ĳ��������滻��
	 *�������һ����Ҫ����������ַ������밴˳���ռλ����š�
	 *���磺myString = First parm is {0}, second parm is {1}, third parm is {2}.
	 *
	 *�������õ��ı�
	 *���������Ϣ���������ţ�'������StockWatcher�еĺܶ�һ����
	 *����Ҫ��Java�����ļ��е����������������滻���ǡ�ͨ����GWT��Ϣ�ĸ�ʽ������������Java��MessageFormat�����ͬ��
	 */
	@DefaultMessage("''{0}'' is not a valid symbol.")
	String invalidSymbol(String symbol);

	@DefaultMessage("Last update: {0,date,medium} {0,time,medium}")
	String lastUpdate(Date timestamp);
}
