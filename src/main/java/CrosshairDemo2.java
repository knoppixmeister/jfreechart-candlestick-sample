import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.DateCellRenderer;
import org.jfree.ui.NumberCellRenderer;

public class CrosshairDemo2 extends ApplicationFrame {
	private static final long serialVersionUID = -870100883094138480L;

	private static class MyDemoPanel extends DemoPanel implements ChartChangeListener, ChartProgressListener {
		private static final long serialVersionUID = -7555284215251087073L;

		private static final int SERIES_COUNT = 4;

        private TimeSeriesCollection[] datasets;

        private TimeSeries[] series;

        private ChartPanel chartPanel;

        private DemoTableModel model;

        public MyDemoPanel() {
        	super(new BorderLayout());

        	datasets = new TimeSeriesCollection[SERIES_COUNT];
        	series = new TimeSeries[SERIES_COUNT];

        	JPanel content = new JPanel(new BorderLayout());

            JFreeChart chart = createChart();
            addChart(chart);
            chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(900, 470));
            chartPanel.setDomainZoomable(true);
            chartPanel.setRangeZoomable(true);
            Border border = BorderFactory.createCompoundBorder(
            	BorderFactory.createEmptyBorder(4, 4, 4, 4),
            	BorderFactory.createEtchedBorder()
            );
            chartPanel.setBorder(border);
            content.add(chartPanel);

            JPanel dashboard = new JPanel(new BorderLayout());
            dashboard.setPreferredSize(new Dimension(400, 120));
            dashboard.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));

            model = new DemoTableModel(SERIES_COUNT);
            for(int row=0; row < SERIES_COUNT; row++) {
                XYPlot plot = (XYPlot) chart.getPlot();
                model.setValueAt(plot.getDataset(row).getSeriesKey(0), row, 0);
                model.setValueAt(new Double("0.00"), row, 1);
                model.setValueAt(new Double("0.00"), row, 2);
                model.setValueAt(new Double("0.00"), row, 3);
                model.setValueAt(new Double("0.00"), row, 4);
                model.setValueAt(new Double("0.00"), row, 5);
                model.setValueAt(new Double("0.00"), row, 6);
            }
            
            JTable table = new JTable(model);
            TableCellRenderer renderer1 = new DateCellRenderer(new SimpleDateFormat("HH:mm:ss"));
            TableCellRenderer renderer2 = new NumberCellRenderer();
            table.getColumnModel().getColumn(1).setCellRenderer(renderer1);
            table.getColumnModel().getColumn(2).setCellRenderer(renderer2);
            table.getColumnModel().getColumn(3).setCellRenderer(renderer1);
            table.getColumnModel().getColumn(4).setCellRenderer(renderer2);
            table.getColumnModel().getColumn(5).setCellRenderer(renderer1);
            table.getColumnModel().getColumn(6).setCellRenderer(renderer2);
            dashboard.add(new JScrollPane(table));
            content.add(dashboard, BorderLayout.SOUTH);
            add(content);
        }

        /**
         * Creates a sample dataset.
         *
         * @param index  the dataset index.
         * @param name  the dataset name.
         * @param base  the starting value.
         * @param start  the starting period.
         * @param count  the number of values to generate.
         *
         * @return The dataset.
         */
        private XYDataset createDataset(int index, String name, double base, RegularTimePeriod start, int count) {
        	series[index] = new TimeSeries(name);
        	RegularTimePeriod period = start;
        	double value = base;

            for(int i = 0; i < count; i++) {
            	series[index].add(period, value);
                period = period.next();
                value = value * (1 + (Math.random() - 0.495) / 10.0);
            }

            datasets[index] = new TimeSeriesCollection();
            datasets[index].addSeries(series[index]);

            return datasets[index];
        }

        public void chartChanged(ChartChangeEvent event) {
        	//System.out.println("BBBB");
        	
            if(this.chartPanel != null) {
                JFreeChart chart = chartPanel.getChart();
                
                if(chart != null) {
                    XYPlot plot = (XYPlot) chart.getPlot();
                    XYDataset dataset = plot.getDataset();
                    Comparable seriesKey = dataset.getSeriesKey(0);
                    
                    double xx = plot.getDomainCrosshairValue();
                    model.setValueAt(seriesKey, 0, 0);
                    long millis = (long) xx;
                    
                    for(int row = 0; row < SERIES_COUNT; row++) {
                    	model.setValueAt(new Long(millis), row, 1);
                        int[] bounds = datasets[row].getSurroundingItems(0, millis);
                        long prevX = 0;
                        long nextX = 0;
                        double prevY = 0.0;
                        double nextY = 0.0;
                        
                        if(bounds[0] >= 0) {
                            TimeSeriesDataItem prevItem = series[row].getDataItem(bounds[0]);
                            prevX = prevItem.getPeriod().getMiddleMillisecond();
                            Number y = prevItem.getValue();
                            if(y != null) {
                                prevY = y.doubleValue();
                                model.setValueAt(new Double(prevY), row, 4);
                            }
                            else model.setValueAt(null, row, 4);
                            
                            model.setValueAt(new Long(prevX), row, 3);
                        }
                        else {
                        	model.setValueAt(new Double(0.00), row, 4);
                        	model.setValueAt(new Double(plot.getDomainAxis().getRange().getLowerBound()), row, 3);
                        }
                        
                        if(bounds[1] >= 0) {
                            TimeSeriesDataItem nextItem = series[row].getDataItem(bounds[1]);
                            nextX = nextItem.getPeriod().getMiddleMillisecond();
                            Number y = nextItem.getValue();
                            
                            if(y != null) {
                                nextY = y.doubleValue();
                                model.setValueAt(new Double(nextY), row, 6);
                            }
                            else model.setValueAt(null, row, 6);

                            model.setValueAt(new Long(nextX), row, 5);
                        }
                        else {
                        	model.setValueAt(new Double(0.00), row, 6);
                        	model.setValueAt(new Double(plot.getDomainAxis().getRange().getUpperBound()), row, 5);
                        }
                        
                        double interpolatedY = 0.0;
                        
                        if((nextX - prevX) > 0) {
                            interpolatedY = prevY + (((double) millis - (double) prevX) / ((double) nextX - (double) prevX)) * (nextY - prevY);
                        }
                        else interpolatedY = prevY;
                        
                        model.setValueAt(new Double(interpolatedY), row, 2);
                    }
                }
            }
        }

        private JFreeChart createChart() {
            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Crosshair Demo 2",
                "Time of Day",
                "Value",
                null,
                true,
                true,
                false
            );

            XYPlot plot = (XYPlot) chart.getPlot();
            XYDataset[] datasets = new XYDataset[SERIES_COUNT];
            for(int i = 0; i < SERIES_COUNT; i++) {
                datasets[i] = createDataset(i, "Series " + i, 100.0 + i * 200.0, new Minute(), 200);
                if(i == 0) plot.setDataset(datasets[i]);
                else {
                    plot.setDataset(i, datasets[i]);
                    plot.setRangeAxis(i, new NumberAxis("Axis " + (i + 1)));
                    plot.mapDatasetToRangeAxis(i, i);
                    plot.setRenderer(i, new XYLineAndShapeRenderer(true, false));
                }
            }
            chart.addChangeListener(this);
            chart.addProgressListener(this);
            plot.setOrientation(PlotOrientation.VERTICAL);

            plot.setDomainCrosshairVisible(true);
            plot.setDomainCrosshairLockedOnData(false);
            plot.setRangeCrosshairVisible(false);
            
            //ChartUtils.applyCurrentTheme(chart);
            
            return chart;
        }

        public void chartProgress(ChartProgressEvent event) {
        	System.out.println("AAAA");
        	
            if(event.getType() != ChartProgressEvent.DRAWING_FINISHED) return;
            
            if(chartPanel != null) {
                JFreeChart c = chartPanel.getChart();
                
                if(c != null) {
                    XYPlot plot = (XYPlot)c.getPlot();
                    XYDataset dataset = plot.getDataset();
                    Comparable seriesKey = dataset.getSeriesKey(0);
                    double xx = //plot.getDomainCrosshairValue();
                    			plot.getRangeCrosshairValue();
                    
                    System.out.println("XX: "+xx);
                    
                    // update the table...
                    
                    model.setValueAt(seriesKey, 0, 0);
                    long millis = (long) xx;
                    model.setValueAt(new Long(millis), 0, 1);

                    for(int i=0; i < SERIES_COUNT; i++) {
                    	int itemIndex = series[i].getIndex(new Minute(new Date(millis)));
                        
                        if(itemIndex >= 0) {
                            TimeSeriesDataItem item = series[i].getDataItem(Math.min(199, Math.max(0, itemIndex)));
                            TimeSeriesDataItem prevItem = series[i].getDataItem(Math.max(0, itemIndex - 1));
                            TimeSeriesDataItem nextItem = series[i].getDataItem(Math.min(199, itemIndex + 1));
                            long x = item.getPeriod().getMiddleMillisecond();
                            double y = item.getValue().doubleValue();
                            long prevX = prevItem.getPeriod().getMiddleMillisecond();
                            double prevY = prevItem.getValue().doubleValue();
                            long nextX = nextItem.getPeriod().getMiddleMillisecond();
                            double nextY = nextItem.getValue().doubleValue();
                            this.model.setValueAt(new Long(x), i, 1);
                            this.model.setValueAt(new Double(y), i, 2);
                            this.model.setValueAt(new Long(prevX), i, 3);
                            this.model.setValueAt(new Double(prevY), i, 4);
                            this.model.setValueAt(new Long(nextX), i, 5);
                            this.model.setValueAt(new Double(nextY), i, 6);
                        }
                    }
                }
            }
        }
	}

    public CrosshairDemo2(String title) {
        super(title);
        
        setContentPane(new MyDemoPanel());
    }

    public static JPanel createDemoPanel() {
        return new MyDemoPanel();
    }

    public static void main(String[] args) {
        CrosshairDemo2 demo = new CrosshairDemo2("JFreeChart: CrosshairDemo2.java");
        demo.pack();
        //RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

    /**
     * A demo table model.
     */
    static class DemoTableModel extends AbstractTableModel implements TableModel {
        private Object[][] data;

        /**
         * Creates a new table model
         *
         * @param rows  the row count.
         */
        public DemoTableModel(int rows) {
        	data = new Object[rows][7];
        }

        /**
         * Returns the column count.
         *
         * @return 7.
         */
        public int getColumnCount() {
            return 7;
        }

        /**
         * Returns the row count.
         *
         * @return The row count.
         */
        public int getRowCount() {
            return data.length;
        }

        /**
         * Returns the value at the specified cell in the table.
         *
         * @param row  the row index.
         * @param column  the column index.
         *
         * @return The value.
         */
        public Object getValueAt(int row, int column) {
            return data[row][column];
        }

        /**
         * Sets the value at the specified cell.
         *
         * @param value  the value.
         * @param row  the row index.
         * @param column  the column index.
         */
        public void setValueAt(Object value, int row, int column) {
        	data[row][column] = value;
        	
            fireTableDataChanged();
        }

        /**
         * Returns the column name.
         *
         * @param column  the column index.
         *
         * @return The column name.
         */
        public String getColumnName(int column) {
            switch(column) {
                case 0 : return "Series Name:";
                case 1 : return "X:";
                case 2 : return "Y:";
                case 3 : return "X (prev)";
                case 4 : return "Y (prev):";
                case 5 : return "X (next):";
                case 6 : return "Y (next):";
            }
            
            return null;
        }
    }
}