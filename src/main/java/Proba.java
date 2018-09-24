import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

public class Proba {
	public static void main(String[] args) {
		OHLCSeries series = new OHLCSeries("1");
		
		System.out.println(	series.getNotify()	);
		
		series.add(
			new OHLCItem(
				new FixedMillisecond(System.currentTimeMillis()),//period,
				0,//open,
				0,//high,
				0,//low,
				0//close
			),
			0
		);
		
		System.out.println(series.getItemCount());
	}
}
