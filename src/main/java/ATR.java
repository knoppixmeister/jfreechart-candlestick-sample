import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.ohlc.*;

public class ATR {
	public static TimeSeries run(OHLCSeries data, int period) {
		TimeSeries result = new TimeSeries("");

		/*
		$High_minus_Low  = null;
		$High_minus_Close_past = null;
		$Low_minus_Close_past = null;
		$TR = null;
		$TR_sum = 0;
		*/
		double high_minus_low = 0;
		double high_minus_close_past = -1;
		double low_minus_close_past = -1;
		double tr = 0;
		double tr_sum = 0;

		/*
		//loop data
		foreach($data as $key => $row){
			$High_minus_Low = $data[$key]['high'] - $data[$key]['low'];
			if($key >= 1){
				$High_minus_Close_past = abs($data[$key]['high'] - $data[$key - 1]['close']);
				$Low_minus_Close_past = abs($data[$key]['low'] - $data[$key - 1]['close']);
			}
		
			if(isset($High_minus_Close_past) && isset($Low_minus_Close_past)){
				$TR = max($High_minus_Low, $High_minus_Close_past, $Low_minus_Close_past);
				//sum first TRs for first ATR avg
				if ($key <= $period)
					$TR_sum += $TR;
			}
			
			//first ATR
			if ($key == $period){
				$atr = $TR_sum / $period;
				$data[$key]['val'] = $atr;
				$previous_ATR = $atr;
			}
			//remaining ATR
			if($key > $period){
				$atr = (($previous_ATR * ($period - 1)) + $TR) / $period;
				$data[$key]['val'] = $atr;
				$previous_ATR = $atr;
			}
		}
		*/
		
		for(int key=0; key < data.getItemCount(); key++) {
			high_minus_low = ((OHLCItem)data.getDataItem(key)).getHighValue() - ((OHLCItem)data.getDataItem(key)).getLowValue();
			if(key >= 1) {
				high_minus_close_past = Math.abs(
											((OHLCItem)data.getDataItem(key)).getHighValue() - 
											((OHLCItem)data.getDataItem(key-1)).getCloseValue()
										);
				low_minus_close_past = 	Math.abs(
											((OHLCItem)data.getDataItem(key)).getLowValue() - 
											((OHLCItem)data.getDataItem(key-1)).getCloseValue()
										);
			}
			
			if(high_minus_close_past > -1 && low_minus_close_past > -1) {
				tr = Math.max(Math.max(high_minus_low, high_minus_close_past), low_minus_close_past);
				if(key <= period) tr_sum += tr;
			}
			
			//first ATR
			double atr, previous_atr = 0;
			if(key == period) {
				atr = tr_sum / period;
				result.add(((OHLCItem)data.getDataItem(key)).getPeriod(), atr);
				previous_atr = atr;
			}

			//remaining ATR
			if(key > period) {
				atr = ((previous_atr * (period-1)) + tr) / period;
				result.add(((OHLCItem)data.getDataItem(key)).getPeriod(), atr);
				previous_atr = atr;
			}
		}

		return result;
	}
}
