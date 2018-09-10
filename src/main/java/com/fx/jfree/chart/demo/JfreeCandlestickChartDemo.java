package com.fx.jfree.chart.demo;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.fx.jfree.chart.candlestick.JfreeCandlestickChart;
import com.fx.jfree.chart.common.FxMarketPxFeeder;

@SuppressWarnings("serial")
public class JfreeCandlestickChartDemo extends JPanel {
    private static void createAndShowGUI() {
    	//WebSocketClient client = new WebSocketClient();

		//JFrame.setDefaultLookAndFeelDecorated(true);

    	JFrame frame = new JFrame("JfreeCandlestickChartDemo");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JfreeCandlestickChart jfreeCandlestickChart = new JfreeCandlestickChart("");
        new FxMarketPxFeeder(
        	jfreeCandlestickChart,
        	"C:/Users/test/Desktop/jfreecandlestickchart-example-code/twtr.csv",
        	0
        ).run();

        //CBWebSocket socket = new CBWebSocket(jfreeCandlestickChart);

        frame.setContentPane(jfreeCandlestickChart);

        //frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);

        /*
        new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(true) {
						if(!client.isStarted()) client.start();

						client.connect(
							socket,
							new URI("wss://ws-feed.pro.coinbase.com"),
							new ClientUpgradeRequest()
						);

						socket.awaitClose(10000, TimeUnit.DAYS);
					}
				}
				catch (Exception e) {
					e.printStackTrace();

					System.exit(1);
				}
			}
		}).start();
      	*/

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
	        	createAndShowGUI();
			}
		});
	}
}
