package idrabenia.worktime.ui.analytics;

import java.text.*;
import java.util.Calendar;

import idrabenia.worktime.R;
import idrabenia.worktime.domain.database.WorkStatisticsDao;
import idrabenia.worktime.domain.database.WorkStatisticsDaoImpl;
import idrabenia.worktime.domain.date.Time;
import idrabenia.worktime.domain.date.TimeFormatter;
import idrabenia.worktime.domain.statistics.DayStatistics;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.widget.LinearLayout;

/**
 * @author Ilya Drabenia
 * @since 06.05.13
 */
public class StatisticsActivity extends Activity {
	private final TimeFormatter timeFormatter = new TimeFormatter();
    private WorkStatisticsDao statisticsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analytics);
        
        statisticsDao = new WorkStatisticsDaoImpl(this);

        LinearLayout layout = (LinearLayout) findViewById(R.id.analytics_layout);
    	GraphicalView chartView = ChartFactory.getBarChartView(this, makeLastWeekStatisticsDataSet(), 
    			makeGraphViewRenderer(), BarChart.Type.DEFAULT);
        layout.addView(chartView);
    }
    
    private XYMultipleSeriesRenderer makeGraphViewRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle(getString(R.string.hours_spent_at_work));
        renderer.setXTitle(getString(R.string.days));
        renderer.setYTitle(getString(R.string.hours));
        renderer.setXLabelsAlign(Paint.Align.RIGHT);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(6.5);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(10);
        renderer.setClickEnabled(false);
        renderer.setExternalZoomEnabled(false);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanLimits(new double[] { -3, 20, -3, 24 });
        renderer.setZoomLimits(new double[] { -10, 20, -10, 24 });
        renderer.setShowGrid(true);
        renderer.setBarSpacing(0);
        renderer.setLabelsColor(Color.WHITE);
        renderer.setAxisTitleTextSize(25);
        renderer.setChartTitleTextSize(35);
        renderer.setLabelsTextSize(25);
        renderer.setShowLegend(false);
        renderer.setMargins(new int[] {50, 30, 15, 20});
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setMarginsColor(Color.BLACK);
        renderer.setXLabels(0);
        generateDayLabels(renderer);
        
        XYSeriesRenderer rendererSeries = new XYSeriesRenderer();
        rendererSeries.setColor(Color.YELLOW);
        rendererSeries.setDisplayChartValues(true);
        rendererSeries.setChartValuesTextSize(25);
        rendererSeries.setChartValuesSpacing(4f);
        rendererSeries.setChartValuesTextAlign(Align.CENTER);
        rendererSeries.setChartValuesFormat(new NumberFormat() {
			private static final long serialVersionUID = 1L;

			@Override
			public Number parse(String string, ParsePosition position) {
				return null;
			}
			
			@Override
			public StringBuffer format(long value, StringBuffer buffer,
					FieldPosition field) {
				return null;
			}
			
			@Override
			public StringBuffer format(double value, StringBuffer buffer,
					FieldPosition field) {
				return new StringBuffer(timeFormatter.format(new Time((long)(value * 3600 * 1000))));
			}
		});
        renderer.addSeriesRenderer(rendererSeries);
        
        return renderer;
    }
    
    private XYMultipleSeriesDataset makeLastWeekStatisticsDataSet() {
    	XYSeries series = new XYSeries("Worked hours at day", 0);

        for (DayStatistics value : statisticsDao.loadLastWeekStatistics()) {
        	double x = value.getDayOfWeek();
        	double y = value.getWorkedHours();
            series.add(x, y);
        }
        
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);
    	
    	return dataset;
    }

    private void generateDayLabels(XYMultipleSeriesRenderer renderer) {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        final int firstWeekDay = Calendar.getInstance().getFirstDayOfWeek();
        final int DAYS_PER_WEEK = 7;
        for (int i = firstWeekDay - 1; i <= firstWeekDay - 1 + DAYS_PER_WEEK; i += 1) {
            renderer.addXTextLabel(i % DAYS_PER_WEEK + 1, dateFormatSymbols.getShortWeekdays()[i % DAYS_PER_WEEK + 1]);
            renderer.setXLabelsAlign(Align.CENTER);
        }
    }

}