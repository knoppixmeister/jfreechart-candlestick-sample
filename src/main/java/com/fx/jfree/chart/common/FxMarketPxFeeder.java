package com.fx.jfree.chart.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.fx.jfree.chart.candlestick.JfreeCandlestickChart;
import com.fx.jfree.chart.model.Trade;
import com.fx.jfree.chart.utils.TimeUtils;

public class FxMarketPxFeeder {
	private JfreeCandlestickChart jfreeCandlestickChart;
	private String stockTradesFile; 
	private int simulationTime;
	private ExecutorService executorService;

	public FxMarketPxFeeder(JfreeCandlestickChart jfreeCandlestickChart, String stockTradesFile, int simulationTime) {
		super();

		this.executorService		=	Executors.newCachedThreadPool();
		this.stockTradesFile		=	stockTradesFile;
		this.jfreeCandlestickChart	=	jfreeCandlestickChart;
		this.simulationTime 		=	simulationTime;
	}

	public void run() {
		executorService.execute(() -> read());
	}

	private void read() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.stockTradesFile)));

			while(true) {
				//Thread.sleep(simulationTime);

				String line = br.readLine();
				if(line != null) {
					String[] tradeElements = line.split(Constants.DELIMITER);

					Trade t = new Trade(
						tradeElements[Constants.STOCK_POS_IDX],
						TimeUtils.convertToMillisTime(tradeElements[Constants.TIME_IDX]),
						Double.parseDouble(tradeElements[Constants.PRICE_POS_IDX]),
						Long.parseLong(tradeElements[Constants.SIZE_IDX])
					);

					jfreeCandlestickChart.onTrade(t);
				}
				else {
					executorService.shutdown();

					break;
				}
			}

			br.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
