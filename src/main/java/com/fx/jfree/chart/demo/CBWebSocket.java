package com.fx.jfree.chart.demo;

import java.sql.*;
import java.util.concurrent.*;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.joda.time.DateTime;
import org.json.JSONObject;

import com.fx.jfree.chart.candlestick.JfreeCandlestickChart;
import com.fx.jfree.chart.common.Constants;
import com.fx.jfree.chart.model.Trade;
import com.fx.jfree.chart.utils.TimeUtils;

@WebSocket(maxTextMessageSize = 64 * 1024)
public class CBWebSocket
{
	private final CountDownLatch closeLatch;
	@SuppressWarnings("unused")
	private Session session;

	private JfreeCandlestickChart jfreeCandlestickChart;
	
	public CBWebSocket(JfreeCandlestickChart jfreeCandlestickChart) {
		this.closeLatch = new CountDownLatch(1);
		this.jfreeCandlestickChart = jfreeCandlestickChart;
	}

	public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
		return this.closeLatch.await(duration, unit);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		System.out.println("Connection closed: "+statusCode+" - "+reason);

		this.session = null;
		this.closeLatch.countDown();
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.println("Got connect: "+session);
		this.session = session;

		try {
			session.getRemote().sendString("{\"type\":\"subscribe\",\"channels\":[{\"name\":\"ticker\",\"product_ids\":[\"BTC-USD\"]}]}");

			//session.close(StatusCode.NORMAL,"I'm done");
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		System.out.println("Got msg: "+msg);

		JSONObject json = new JSONObject(msg);

		if(json.getString("type").toLowerCase().equals("ticker")) {
			//final String side 		=	json.has("side") ? json.getString("side") : null;
			final String productId 	=	json.getString("product_id");
			final String timeStr 	=	json.has("time") ? json.getString("time") : null;
			//Float price 			=	Float.parseFloat(json.getString("price"));

			//final String from_coin = productId != null && !productId.equals("") ? productId.substring(0, productId.indexOf("-")) : null;
			//final String to_coin = productId != null && !productId.equals("") ? productId.substring(productId.indexOf("-")+1) : null;

			/*
			final Float amount =	json.has("last_size") ?
									Float.parseFloat(json.getString("last_size")) :
									new Float(0);
			*/

			DateTime dateTime = new DateTime(timeStr);//"2014-09-01T19:22:43.000Z"

			Trade t = new Trade(
				productId,
				dateTime.getMillis(),
				//TimeUtils.convertToMillisTime("09:30:00.000"),
				Double.parseDouble(json.getString("price")),
				Long.parseLong("0")
			);

			this.jfreeCandlestickChart.onTrade(t);

			//System.out.println("ADDED");
			
			/*
			new Thread(new Runnable() {
				@Override
				public void run() {
					DateTime dt = null;
					if(timeStr != null) dt = new DateTime(timeStr);

					if(dt != null) {
						String m = String.format("%2s", dt.getMonthOfYear()+"").replace(" ", "0");
						String d = String.format("%2s", dt.getDayOfMonth()+"").replace(' ', '0');

						String h 	=	String.format("%2s", dt.getHourOfDay()+"").replace(' ', '0');
						String min	=	String.format("%2s", dt.getMinuteOfHour()+"").replace(' ', '0');
						String sec	=	String.format("%2s", dt.getSecondOfMinute()+"").replace(' ', '0');

						/*
						try {
							st.executeUpdate(
								"INSERT INTO data (exchange, from_coin, to_coin, price, side, datetime, amount) "+
								"VALUES ('coinbase', '"+from_coin+"', '"+to_coin+"', "+price+", '"+side.toLowerCase()+"', '"+dt.getYear()+"-"+m+"-"+d+" "+h+":"+min+":"+sec+"', "+amount+")"
							);
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						*

						System.out.println("PRODUCT: "+productId+"; SIDE: "+side+"; PRICE: "+price+"; AMOUNT: "+amount+(dt != null ? "; EXCH_TM: "+dt.getYear()+"-"+m+"-"+d+" "+h+":"+min+":"+sec : ""));
					}
				}
			}).start();
			*/
		}
	}
}