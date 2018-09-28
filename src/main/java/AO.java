import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

public class AO {
	public static TimeSeries run(OHLCSeries data, int fastPeriod, int slowPeriod) {// 5, 34
		TimeSeries result = new TimeSeries("");
		
		//AO = SMA(High+Low)/2, 5 Periods) - SMA(High+Low/2, 34 Periods)
		
		OHLCSeries ser = new OHLCSeries("");
		for(int key=0; key < data.getItemCount(); key++) {
			ser.add(
				((OHLCItem)data.getDataItem(key)).getPeriod(),	//period,
				0,		//open,
				0,		//high,
				0,		//low,
				((OHLCItem)data.getDataItem(key)).getHighValue()+((OHLCItem)data.getDataItem(key)).getLowValue() / 2	//close
			);
		}
		
		TimeSeries ts5 = SMA.run(ser, 5);
		TimeSeries ts34 = SMA.run(ser, 34);
		
		for(int key=0; key < ts34.getItemCount(); key++) {
			result.add(
				ts34.getDataItem(key).getPeriod(),
				ts34.getDataItem(key).getValue().doubleValue() - ts5.getDataItem(key).getValue().doubleValue() 
			);
		}
		
		return result;
	}
}
