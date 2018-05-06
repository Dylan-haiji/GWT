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
	// ��ʱ����ʶ
	private static final int REFRESH_INTERVAL = 5000; // ms
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable stocksFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private Button addStockButton = new Button("Add");
	private Label lastUpdatedLabel = new Label();
	private ArrayList<String> stocks = new ArrayList<String>();
	// ����������
	private StockPriceServiceAsync stockPriceSvc = GWT.create(StockPriceService.class);
	private Label errorMsgLabel = new Label();
	
	// ָ������JSON���ݵ�URL��web.xml���Ѿ�������stockPrices��·����
	private static final String JSON_URL = GWT.getModuleBaseURL() + "stockPrices?q=";
	//ʵ�������ʻ�
	private StockWatcherConstants constants = GWT.create(StockWatcherConstants.class);
	private StockWatcherMessages messages = GWT.create(StockWatcherMessages.class);
	
	/**
	 * Entry point method.
	 */
	public void onModuleLoad() {
		//�ӳ��������ļ��л�ȡ���ڱ��⣬
		//Ӧ�ó�����⣬Add Stock��ť�Լ�Flex�����б����ֵ
		Window.setTitle(constants.stockWatcher());
		RootPanel.get("appTitle").add(new Label(constants.stockWatcher()));
		addStockButton = new Button(constants.add());
		
		// ������ͷ
		stocksFlexTable.setText(0, 0, constants.symbol());
	    stocksFlexTable.setText(0, 1, constants.price());
	    stocksFlexTable.setText(0, 2, constants.change());
	    stocksFlexTable.setText(0, 3, constants.remove());
		// ָ������
		stocksFlexTable.setCellPadding(6);
		// �����ʽ---css
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

		// ��Ӱ�ť���������Ե�addPanel
		addPanel.add(newSymbolTextBox);
		addPanel.add(addStockButton);
		addPanel.addStyleName("addPanel");

		// errorMsgLabel��Ӹ��������ԣ�
		// �����ڼ���StockWatcherʱ����ʾ����
		// ��������Ϣ��ӵ�stocksFlexTable�����Main��塣
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
		// Remove��ť���һ�����������������ԡ�
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
		 * ��Ʊ������Ŀ�ľ�����Ϣ
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
		// ���css��ʽ
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

	// ����refreshWatchList��
	// ��������refreshWatchList�����еĹ��ܲ�����ӿͻ����ƶ�����������
	// Ŀǰ������refreshWatchList�������ݸ�һ���Ʊ���룬
	// ��������Ӧ�Ĺ�Ʊ���ݡ�Ȼ��������updateTable������ʹ�ÿ���������FlexTable��
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
		  
		  //��������������󲢲����κδ���
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


		// ���ʴ���ʵ������Ҫ�ķ���
		/*
		 * if (stockPriceSvc == null) { stockPriceSvc =
		 * GWT.create(StockPriceService.class); }
		 * 
		 * AsyncCallback<StockPrice[]> callback = new
		 * AsyncCallback<StockPrice[]>() {
		 * 
		 * ������쳣����
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

		// ɾ����Change�У���2�У���setText���á�
		// ����һ��LabelС������ʵ���������ΪchangeWidget
		stocksFlexTable.setText(row, 1, priceText);
		Label changeWidget = (Label) stocksFlexTable.getWidget(row, 2);
		changeWidget.setText(changeText + " (" + changePercentText + "%)");

		// ����ֵ�ı���ɫ
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
	 * ������еĴ���
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
	 * // ɾ����Change�У���2�У���setText���á� // ����һ��LabelС������ʵ���������ΪchangeWidget
	 * stocksFlexTable.setText(row, 1, priceText); Label changeWidget = (Label)
	 * stocksFlexTable.getWidget(row, 2); changeWidget.setText(changeText + " ("
	 * + changePercentText + "%)");
	 * 
	 * // ����ֵ�ı���ɫ String changeStyleName = "noChange"; if
	 * (price.getChangePercent() < -0.1f) { changeStyleName = "negativeChange";
	 * } else if (price.getChangePercent() > 0.1f) { changeStyleName =
	 * "positiveChange"; }
	 * 
	 * changeWidget.setStyleName(changeStyleName); }
	 */
}
