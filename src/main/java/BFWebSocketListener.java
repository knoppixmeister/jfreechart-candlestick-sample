import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;
import okhttp3.*;

public class BFWebSocketListener extends WebSocketListener {
	private OHLCSeriesCollection collection;

	private org.json.JSONArray	arr = null,
								arr2 = null,
								arr3 = null;

	public BFWebSocketListener(OHLCSeriesCollection collection) {
		this.collection = collection;
	}

	@Override
	public void onOpen(WebSocket socket, Response response) {
		System.out.println("BF_ON_OPEN");

		socket.send("{\"event\":\"subscribe\",\"channel\":\"candles\",\"key\":\"trade:1m:tETHUSD\"}");
	}

	@Override
	public void onFailure(WebSocket webSocket, Throwable t, Response response) {
		t.printStackTrace();
	}

	@Override
	public void onClosing(WebSocket webSocket, int code, String reason) {
		System.out.println("BF_SOCKET_CLOSING. CODE: "+code+" "+reason);
	}

	@Override
	public void onClosed(WebSocket webSocket, int code, String reason) {
		System.out.println("BF_SOCKET_CLOSED. CODE: "+code+" "+reason);
	}

	@Override
	public void onMessage(WebSocket socket, String text) {
		System.out.println("BF_ON_MESSAGE. "+text);

		if(text.contains("event") || text.contains("hb")) return;
		
		arr = new org.json.JSONArray(text);

		if(arr.get(1) == null) return;

		arr2 = new org.json.JSONArray(arr.get(1).toString().trim());

		//System.out.println(	arr2.length()	);

		if(arr2.length() > 6) {//add first data
			//OHLCSeriesCollection col = new OHLCSeriesCollection();
			OHLCSeries ser = new OHLCSeries("");
			//col.addSeries(ser);

			for(int i=0; i<arr2.length(); i++) {
				arr3 = new org.json.JSONArray(arr2.get(i).toString().trim());

				OHLCItem ohlcItem = new OHLCItem(
					new FixedMillisecond(Long.parseLong(arr3.get(0).toString().trim())),
					Double.parseDouble(arr3.get(1).toString().trim()),	//open,
					Double.parseDouble(arr3.get(3).toString().trim()),	//high,
					Double.parseDouble(arr3.get(4).toString().trim()),	//low,
					Double.parseDouble(arr3.get(2).toString().trim()),	//close
					Double.parseDouble(arr3.get(5).toString().trim())	//volume
				);

				//collection.getSeries(0).add(ohlcItem);
				
				ser.add(ohlcItem);
			}
			
			
			if(ser.getItemCount() > 0) {
				collection.removeAllSeries();
				collection.addSeries(ser);
			}
			
		}
		else {//update last data
			//System.out.println("6_items: "+	arr2.toString().trim()	);
			
			arr3 = new org.json.JSONArray(arr2.toString().trim());
			
			long newTime 		= Long.parseLong(arr3.get(0).toString().trim());
			long lastOhlcTime 	= ((OHLCItem)collection.getSeries(0).getDataItem(collection.getSeries(0).getItemCount()-1)).getPeriod().getFirstMillisecond();
			
			if(newTime > lastOhlcTime) {//add new candle data
				OHLCItem ohlcItem = new OHLCItem(
					new FixedMillisecond(newTime),
					Double.parseDouble(arr3.get(1).toString().trim()),	//open,
					Double.parseDouble(arr3.get(3).toString().trim()),	//high,
					Double.parseDouble(arr3.get(4).toString().trim()),	//low,
					Double.parseDouble(arr3.get(2).toString().trim()),	//close
					Double.parseDouble(arr3.get(5).toString().trim())	//volume
				);

				collection.getSeries(0).add(ohlcItem);
			}
			else {//update last candle
				double newPrice = Double.parseDouble(arr3.get(2).toString().trim());

				//System.out.println("STOCK_NEW_PR: "+newPrice);

				if(collection.getSeriesCount() > 0) {
					collection.getSeries(0).updatePrice(newPrice);
				}

				/*
				collection.getSeries(0).remove(
					collection.getSeries(0).getItemCount()-1
				);

				OHLCItem ohlcItem = new OHLCItem(
					new FixedMillisecond(newTime),
					Double.parseDouble(arr3.get(1).toString().trim()),	//open,
					Double.parseDouble(arr3.get(3).toString().trim()),	//high,
					Double.parseDouble(arr3.get(4).toString().trim()),	//low,
					Double.parseDouble(arr3.get(2).toString().trim()),	//close
					Double.parseDouble(arr3.get(5).toString().trim())	//volume
				);

				collection.getSeries(0).add(ohlcItem);
				*/
			}

			//System.out.println(	"ITEMS_IN_SERIES: "		);
		}

		System.out.println("");		
	}
}
