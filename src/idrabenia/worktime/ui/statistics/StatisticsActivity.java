package idrabenia.worktime.ui.statistics;

import idrabenia.worktime.R;
import idrabenia.worktime.domain.database.WorkStatisticsDao;
import idrabenia.worktime.domain.database.WorkStatisticsDaoImpl;
import idrabenia.worktime.domain.date.Time;
import idrabenia.worktime.domain.date.TimeFormatter;
import idrabenia.worktime.domain.statistics.DayStatistics;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Calendar;

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
	private static final TimeFormatter timeFormatter = new TimeFormatter();
    private static final NumberFormat timeFormatAdapter = new NumberFormat() {
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
	};
	
	private final WeekDayFormatter weekDayFormatter = new WeekDayFormatter();
    private WorkStatisticsDao statisticsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analytics);
        
        if (statisticsDao == null) {
        	statisticsDao = new WorkStatisticsDaoImpl(this);
        }
        
    	GraphicalView chartView = ChartFactory.getBarChartView(this, makeStatisticsDataSet(), makeGraphViewRenderer(), 
    			BarChart.Type.DEFAULT);
    	LinearLayout layout = (LinearLayout) findViewById(R.id.analytics_layout);
        layout.addView(chartView);
    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		
		statisticsDao.close();
		statisticsDao = null;
	}

	private XYMultipleSeriesRenderer makeGraphViewRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        
        Time totalWeekHours = statisticsDao.calculateLastWeekTotalWorkedTime();
        String title = getString(R.string.week_statistics, timeFormatter.format(totalWeekHours));
        renderer.setChartTitle(title);
        
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
        rendererSeries.setChartValuesFormat(timeFormatAdapter);
        renderer.addSeriesRenderer(rendererSeries);
        
        return renderer;
    }
    
    private XYMultipleSeriesDataset makeStatisticsDataSet() {
    	XYSeries series = new XYSeries("Worked hours at day", 0);

        for (DayStatistics value : statisticsDao.loadLastWeekStatistics()) {
        	double x = weekDayFormatter.getDayCoordinate(value.getDayOfWeek());
        	double y = value.getWorkedHours();
            series.add(x, y);
        }
        
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);
    	
    	return dataset;
    }

    private void generateDayLabels(XYMultipleSeriesRenderer renderer) {
    	renderer.setXLabelsAlign(Align.CENTER);
        for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i += 1) {
            renderer.addXTextLabel(weekDayFormatter.getDayCoordinate(i), weekDayFormatter.getWeekDayTitle(i));
        }
    }
}
