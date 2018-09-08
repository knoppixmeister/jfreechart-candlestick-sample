package com.fx.jfree.chart.demo;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.ohlc.OHLC;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;

public class Test extends JFrame {
	private CandlestickRenderer candlestickRenderer;
	OHLCSeriesCollection ohlcSeriesCollection;

	public Test() {
		setBounds(100, 100, 500, 400);

		/*
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(new File("C:\\Users\\test\\Desktop\\jfreecandlestickchart-example-code\\src\\main\\java\\com\\fx\\jfree\\chart\\demo\\crosshair.cur"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		Cursor cursor = toolkit.createCustomCursor(bi, new Point(5, 5), "");
		setCursor(cursor);
		*/

		ohlcSeriesCollection = new OHLCSeriesCollection();

		candlestickRenderer = new CandlestickRenderer(-1);

		candlestickRenderer.addChangeListener(new RendererChangeListener() {
			public void rendererChanged(RendererChangeEvent event) {
				System.out.println("asdasdasd");
			}
		});

		OHLCSeries ohlcSeries = new OHLCSeries("");

		OHLCItem ohlcItem =	new OHLCItem(new FixedMillisecond(), 6000, 8000, 4000, 5000);

		ohlcSeries.add(ohlcItem);
		ohlcSeriesCollection.addSeries(ohlcSeries);

		ohlcItem =	new OHLCItem(new FixedMillisecond(System.currentTimeMillis()+10000), 5500, 9000, 3000, 5500);
		ohlcSeries.add(ohlcItem);
		
		org.jfree.data.Range range = candlestickRenderer.findRangeBounds(ohlcSeriesCollection);

		System.out.println(range.getUpperBound());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		g.setColor(Color.RED);
		g.drawLine(10, 10, 100, 100);

		candlestickRenderer.drawItem(
			(Graphics2D)g,
			null,					//state,
			new Rectangle(100, 100),//dataArea,
			null,					//info,
			new XYPlot(),			//plot,
			null,					//domainAxis,
			new NumberAxis(""),
			ohlcSeriesCollection,	//dataset,
			0, 						//series,
			0, 						//item,
			null,					//crosshairState,
			1						//pass
		);
	}

	public static void main(String[] args) {
		new Test();
	}
}
