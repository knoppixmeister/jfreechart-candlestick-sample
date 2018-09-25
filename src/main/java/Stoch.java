import java.util.*;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.*;

public class Stoch {
	public static TimeSeriesCollection run(OHLCSeries data, int period, int sma_period) {
		List<Double> high_array = new ArrayList<>();
		List<Double> low_array = new ArrayList<>();
		List<Double> k_array = new ArrayList<>();

		TimeSeriesCollection result = new TimeSeriesCollection();
		TimeSeries ks = new TimeSeries("Ks");
		TimeSeries smas = new TimeSeries("SMAs");
		
		result.addSeries(ks);
		result.addSeries(smas);
		
		//loop data
		for(int key=0; key < data.getItemCount(); key++) {
			//add to front
			//array_unshift($high_array, $data[$key]['high']);
			high_array.add(0, ((OHLCItem)data.getDataItem(key)).getHighValue());

			//pop back if too long
			//if(count($high_array) > $period) array_pop($high_array);
			if(high_array.size() > period) high_array.remove(high_array.size()-1);

			//add to front
			//array_unshift($low_array, $data[$key]['low']);
			low_array.add(0, ((OHLCItem)data.getDataItem(key)).getLowValue());
			
			//pop back if too long
			//if(count($low_array) > $period) array_pop($low_array);
			if(low_array.size() > period) low_array.remove(low_array.size()-1);

			//have enough data to calc stoch
			if(key >= period) {
				//max of highs
				
				/*
				double init = high_array.get(0);
								//$high_array[0];
				
				$h = array_reduce($high_array, function($v, $w) {
					    $v = max($w, $v);
					    return $v;
					}, $init);
				
				//low of lows
				init = $low_array[0];
				$l = array_reduce($low_array, function($v, $w) {
					    $v = min($w, $v);
					    return $v;
					}, $init);
				//calc
				*/
				
				double k = 0;
							//($data[$key]['close'] - $l) / ( $h - $l) * 100;
				//add to front
				//array_unshift($k_array, $k);
				k_array.add(0, k);
				
				//pop back if too long
				//if(count($k_array) > $sma_period) array_pop($k_array);
				
				//save
				//$data[$key]['val'] = $k;
				
				ks.add(((OHLCItem)data.getDataItem(key)).getPeriod(), k);
			}
			
			//have enough data to calc sma
			/*
			if(count($k_array) == $sma_period) {
				//k moving average 
				$sum = array_reduce($k_array, function($result, $item) { 
					    $result += $item;
					    return $result;
					}, 0);
				$sma = $sum / $sma_period;
				
				//save
				$data[$key]['val2'] = $sma;
			}
			*/
			
			if(k_array.size() > sma_period) {
				double sum = 0;
				
				double sma = sum / sma_period;
				
				smas.add(((OHLCItem)data.getDataItem(key)).getPeriod(), sma);
			}
		}

		return result;
	}
}

class ResultValues {
	public double k, sma;
	
	public ResultValues(double k) {
		this.k = k;
	}
}
