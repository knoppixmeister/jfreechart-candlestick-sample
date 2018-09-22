import java.util.*;
import org.jfree.data.time.ohlc.*;

// https://github.com/hurdad/doo-forex/blob/master/protected/class/Technical%20Indicators/RSI.php
public class RSI {
	public static Map<Long, Double> run(OHLCSeries data, int period) {
		Map<Long, Double> result = new HashMap<>();
		List<Double> changeArray = new ArrayList<>();

		for(int key=0; key < data.getItemCount(); key++) {
			if(key > 1) {
				double change = ((OHLCItem)data.getDataItem(key)).getCloseValue() - ((OHLCItem)data.getDataItem(key-1)).getCloseValue();

				changeArray.add(0, change);

				if(changeArray.size() > period) changeArray.remove(changeArray.size()-1);
			}

			if(key > period) {
				double sumGain = 0;
				double sumLoss = 0;

				for(int i=0; i<changeArray.size(); i++) {
					if(changeArray.get(i) >= 0) {
						sumGain += changeArray.get(i);
					}
					else if(changeArray.get(i) < 0) {
						sumLoss += Math.abs(changeArray.get(i));
					}
				}

				double avg_gain = sumGain / period;
				double avg_loss = sumLoss / period;

				double rsi = 0;
				if(avg_loss == 0) rsi = 100;
				else rsi = 100 - (100 / ( 1 + (avg_gain / avg_loss)));

				result.put(	((OHLCItem)data.getDataItem(key)).getPeriod().getFirstMillisecond(), rsi);
			}			
		}

		return result;
	}
}
