package com.google.gwt.sample.stockwatcher.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sun.java.swing.plaf.windows.resources.windows;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class StockWatcher implements EntryPoint {
	// 定时器标识
	private static final int REFRESH_INTERVAL = 5000; // ms
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable stocksFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private Button addStockButton = new Button("Add");
	private Label lastUpdatedLabel = new Label();
	private ArrayList<String> stocks = new ArrayList<String>();
	// 创建代理类
	private StockPriceServiceAsync stockPriceSvc = GWT.create(StockPriceService.class);
	private Label errorMsgLabel = new Label();
	
	// 指定访问JSON数据的URL，web.xml中已经配置了stockPrices的路径。
	private static final String JSON_URL = GWT.getModuleBaseURL() + "stockPrices?q=";
	//实例化国际化
	private StockWatcherConstants constants = GWT.create(StockWatcherConstants.class);
	private StockWatcherMessages messages = GWT.create(StockWatcherMessages.class);
	
	/**
	 * Entry point method.
	 */
	public void onModuleLoad() {
		//从常量属性文件中获取窗口标题，
		//应用程序标题，Add Stock按钮以及Flex表格的列标题的值
		Window.setTitle(constants.stockWatcher());
		RootPanel.get("appTitle").add(new Label(constants.stockWatcher()));
		addStockButton = new Button(constants.add());
		
		// 创建表头
		stocksFlexTable.setText(0, 0, constants.symbol());
	    stocksFlexTable.setText(0, 1, constants.price());
	    stocksFlexTable.setText(0, 2, constants.change());
	    stocksFlexTable.setText(0, 3, constants.remove());
		// 指定库存表单
		stocksFlexTable.setCellPadding(6);
		// 添加样式---css
		stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
		stocksFlexTable.addStyleName("watchList");
		stocksFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(0, 3, "watchListRemoveColumn");

		// Assemble Add Stock panel.
		addPanel.add(newSymbolTextBox);
		addPanel.add(addStockButton);

		// Assemble Main panel.
		mainPanel.add(stocksFlexTable);
		mainPanel.add(addPanel);
		mainPanel.add(lastUpdatedLabel);

		// 添加按钮辅助类属性到addPanel
		addPanel.add(newSymbolTextBox);
		addPanel.add(addStockButton);
		addPanel.addStyleName("addPanel");

		// errorMsgLabel添加辅助类属性，
		// 并且在加载StockWatcher时不显示它。
		// 将错误消息添加到stocksFlexTable上面的Main面板。
		addPanel.add(newSymbolTextBox);
		addPanel.add(addStockButton);
		addPanel.addStyleName("addPanel");

		// Assemble Main panel.
		errorMsgLabel.setStyleName("errorMessage");
		errorMsgLabel.setVisible(false);

		mainPanel.add(errorMsgLabel);
		mainPanel.add(stocksFlexTable);
		mainPanel.add(addPanel);
		mainPanel.add(lastUpdatedLabel);

		// Assemble Add Stock panel.
	    addPanel.add(newSymbolTextBox);
	    addPanel.add(addStockButton);
	    addPanel.addStyleName("addPanel");

	    // Assemble Main panel.
	    errorMsgLabel.setStyleName("errorMessage");
	    errorMsgLabel.setVisible(false);

	    mainPanel.add(errorMsgLabel);
	    mainPanel.add(stocksFlexTable);
	    mainPanel.add(addPanel);
	    mainPanel.add(lastUpdatedLabel);
		// Remove按钮添加一个辅助的依赖类属性。
		Button removeStockButton = new Button("x");
		removeStockButton.addStyleDependentName("remove");

		// Associate the Main panel with the HTML host page.
		RootPanel.get("stockList").add(mainPanel);

		// Move cursor focus to the input box.
		newSymbolTextBox.setFocus(true);
		addStockButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addStock();
			}
		});

		newSymbolTextBox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					addStock();
				}
			}
		});

		// Move cursor focus to the input box.
		newSymbolTextBox.setFocus(true);

		// Setup timer to refresh list automatically.
		Timer refreshTimer = new Timer() {
			@Override
			public void run() {
				refreshWatchList();
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

	}
	private void addStock() {
		final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
		newSymbolTextBox.setFocus(true);
		
		/*
		 * 股票代码条目的警报消息
		 */
		if (!symbol.matches("^[0-9a-zA-Z\\.]{1,10}$")) {
		      Window.alert(messages.invalidSymbol(symbol));
		      newSymbolTextBox.selectAll();
		      return;
		 }

		newSymbolTextBox.setText("");

		// TODO Don't add the stock if it's already in the table.
		if (stocks.contains(symbol))
			return;
		// 添加css样式
		int row = stocksFlexTable.getRowCount();
		stocks.add(symbol);
		stocksFlexTable.setText(row, 0, symbol);
		stocksFlexTable.setWidget(row, 2, new Label());
		stocksFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(row, 3, "watchListRemoveColumn");
		// TODO Add a button to remove this stock from the table.
		Button removeStockButton = new Button("x");
		removeStockButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int removedIndex = stocks.indexOf(symbol);
				stocks.remove(removedIndex);
				stocksFlexTable.removeRow(removedIndex + 1);
			}
		});
		stocksFlexTable.setWidget(row, 3, removeStockButton);
		// TODO Get the stock price.
	}

	// 创建refreshWatchList根
	// 您将采用refreshWatchList方法中的功能并将其从客户端移动到服务器。
	// 目前，您将refreshWatchList方法传递给一组股票代码，
	// 并返回相应的股票数据。然后它调用updateTable方法来使用库存数据填充FlexTable。
	private void refreshWatchList() {
		  if (stocks.size() == 0) {
		    return;
		  }

		  String url = JSON_URL;

		  // Append watch list stock symbols to query URL.
		  Iterator<String> iter = stocks.iterator();
		  while (iter.hasNext()) {
		    url += iter.next();
		    if (iter.hasNext()) {
		      url += "+";
		    }
		  }

		  url = URL.encode(url);
		  
		  //向服务器发送请求并捕获任何错误。
		  RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

		  try{
			  Request request = builder.sendRequest(null,new RequestCallback(){
				  public void onError(Request request,Throwable exception){
					  displayError("Could not retrieve JSON");}

		    public void onResponseReceived(Request request, Response response) {
		      if (200 == response.getStatusCode()) {
		        updateTable(JsonUtils.<JsArray<StockData>>safeEval(response.getText()));
		      } else {
		        displayError("Couldn't retrieve JSON (" + response.getStatusText()
		            + ")");
		      }
		    }
		  }); } catch(RequestException e){displayError("Could not retrieve JSON"); }


		// 访问代理实现所需要的方法
		/*
		 * if (stockPriceSvc == null) { stockPriceSvc =
		 * GWT.create(StockPriceService.class); }
		 * 
		 * AsyncCallback<StockPrice[]> callback = new
		 * AsyncCallback<StockPrice[]>() {
		 * 
		 * 错误的异常处理
		 * 
		 * public void onFailure(Throwable caught) { String details =
		 * caught.getMessage(); if (caught instanceof DelistedException) {
		 * details = "Company '" + ((DelistedException) caught).getSymbol() +
		 * "' was delisted"; } errorMsgLabel.setText("Error: " + details);
		 * errorMsgLabel.setVisible(true); }
		 * 
		 * public void onSuccess(StockPrice[] result) { updateTable(result); }
		 * }; stockPriceSvc.getPrices(stocks.toArray(new String[0]), callback);
		 */
	}

	private void updateTable(JsArray<StockData> prices) {
		  for (int i=0; i < prices.length(); i++) {
		    updateTable(prices.get(i));
		  }

		  // Display timestamp showing last refresh.
		  lastUpdatedLabel.setText("Last update : " +
		      DateTimeFormat.getMediumDateTimeFormat().format(new Date()));

		  // Clear any errors.
		  errorMsgLabel.setVisible(false);
		}

	private void updateTable(StockData price) {
		// Make sure the stock is still in the stock table.
		if (!stocks.contains(price.getSymbol())) {
			return;
		}

		int row = stocks.indexOf(price.getSymbol()) + 1;

		// Format the data in the Price and Change fields.
		String priceText = NumberFormat.getFormat("#,##0.00").format(price.getPrice());
		NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
		String changeText = changeFormat.format(price.getChange());
		String changePercentText = changeFormat.format(price.getChangePercent());

		// 删除对Change列（第2列）的setText调用。
		// 创建一个Label小部件的实例并将其称为changeWidget
		stocksFlexTable.setText(row, 1, priceText);
		Label changeWidget = (Label) stocksFlexTable.getWidget(row, 2);
		changeWidget.setText(changeText + " (" + changePercentText + "%)");

		// 根据值改变颜色
		String changeStyleName = "noChange";
		if (price.getChangePercent() < -0.1f) {
			changeStyleName = "negativeChange";
		} else if (price.getChangePercent() > 0.1f) {
			changeStyleName = "positiveChange";
		}

		changeWidget.setStyleName(changeStyleName);
	}

	/**
	   * If can't get JSON, display error message.
	   * @param error
	   */
	  private void displayError(String error) {
	    errorMsgLabel.setText("Error: " + error);
	    errorMsgLabel.setVisible(true);
	  }

	/*
	 * private void updateTable(StockPrice[] prices) { for (int i = 0; i <
	 * prices.length; i++) { updateTable(prices[i]); }
	 * 
	 * 清除所有的错误
	 * 
	 * // Display timestamp showing last refresh.
	 * lastUpdatedLabel.setText("Last update : " +
	 * DateTimeFormat.getMediumDateTimeFormat().format(new Date()));
	 * 
	 * // Clear any errors. errorMsgLabel.setVisible(false); }
	 */

	/*
	 * private void updateTable(StockPrice price) { // Make sure the stock is
	 * still in the stock table. if (!stocks.contains(price.getSymbol())) {
	 * return; }
	 * 
	 * int row = stocks.indexOf(price.getSymbol()) + 1;
	 * 
	 * // Format the data in the Price and Change fields. String priceText =
	 * NumberFormat.getFormat("#,##0.00").format(price.getPrice()); NumberFormat
	 * changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00"); String
	 * changeText = changeFormat.format(price.getChange()); String
	 * changePercentText = changeFormat.format(price.getChangePercent());
	 * 
	 * // 删除对Change列（第2列）的setText调用。 // 创建一个Label小部件的实例并将其称为changeWidget
	 * stocksFlexTable.setText(row, 1, priceText); Label changeWidget = (Label)
	 * stocksFlexTable.getWidget(row, 2); changeWidget.setText(changeText + " ("
	 * + changePercentText + "%)");
	 * 
	 * // 根据值改变颜色 String changeStyleName = "noChange"; if
	 * (price.getChangePercent() < -0.1f) { changeStyleName = "negativeChange";
	 * } else if (price.getChangePercent() > 0.1f) { changeStyleName =
	 * "positiveChange"; }
	 * 
	 * changeWidget.setStyleName(changeStyleName); }
	 */
}
