import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

public class SMA {
	public static TimeSeries run(OHLCSeries data, int period) {
		TimeSeries result = new TimeSeries("");

		double sum, sma;
		for(int key=0; key < data.getItemCount(); key++) {
			if(key >= period) {
				sum = 0;
				sma = 0;

				for(int i = key-(period-1); i <= key; i++) {
					sum += ((OHLCItem) data.getDataItem(i)).getCloseValue();
				}

				sma = sum / period;

				result.add(((OHLCItem)data.getDataItem(key)).getPeriod(), sma);
			}
		}
		
		return result;
	}

	public static TimeSeries run(TimeSeries data, int period) {
		TimeSeries result = new TimeSeries("");
		
		double sum = 0, sma = 0;
		for(int key=0; key < data.getItemCount(); key++) {
			if(key >= period) {
				sum = 0;
				
				for(int i = key-(period-1); i <= key; i++) {
					sum +=	data.getDataItem(key).getValue().doubleValue();
				}
				
				sma = sum / period;
				
				result.add(data.getDataItem(key).getPeriod(), sma);
			}
		}
		
		return result;
	}
}
