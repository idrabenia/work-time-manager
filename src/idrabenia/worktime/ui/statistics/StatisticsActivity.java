package idrabenia.worktime.ui.statistics;

import idrabenia.worktime.R;
import idrabenia.worktime.domain.database.WorkStatisticsDao;
import idrabenia.worktime.domain.database.WorkStatisticsDaoImpl;
import idrabenia.worktime.domain.date.TimeFormatter;
import idrabenia.worktime.domain.date.Week;
import idrabenia.worktime.domain.statistics.DayStatistics;

import java.text.DateFormat;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Ilya Drabenia
 * @since 06.05.13
 */
public class StatisticsActivity extends Activity {
	private static final TimeFormatter timeFormatter = new TimeFormatter();
	
	private final WeekDayFormatter weekDayFormatter = new WeekDayFormatter();
    private WorkStatisticsDao statisticsDao;
    
    private int weekNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.statistics);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        initializeStatisticsDao();
        
    	initializeChart();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.statistics_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    private void initializeChart() {
    	// build chart with statistics
    	GraphicalView chartView = ChartFactory.getBarChartView(this, makeStatisticsDataSet(), makeGraphViewRenderer(), 
    			BarChart.Type.DEFAULT);
    	LinearLayout layout = (LinearLayout) findViewById(R.id.chart_container);
    	layout.removeAllViews();
        layout.addView(chartView);
        
        // set total worked time
        TextView totalLabel = (TextView) findViewById(R.id.total_worked_time_label);
    	String totalWorkedTime = timeFormatter.format(statisticsDao.calculateTotalWorkedTimeFor(weekNumber));
    	totalLabel.setText(getString(R.string.total_spent, totalWorkedTime));
    }
    
    private void initializeStatisticsDao() {
    	if (statisticsDao == null) {
        	statisticsDao = new WorkStatisticsDaoImpl(this);
        }
    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		
		statisticsDao.close();
		statisticsDao = null;
	}
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    private String getChartTitle() {
    	Week week = new Week(weekNumber);
    	
    	DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);
    	String firstDay = dateFormat.format(week.getFirstDayOfWeek());
    	String lastDay = dateFormat.format(week.getLastDayOfWeek()); 
    	
        return getString(R.string.week_statistics, firstDay, lastDay);
    }
    
	private XYMultipleSeriesRenderer makeGraphViewRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle(getChartTitle());
        renderer.setXTitle(getString(R.string.days));
        renderer.setYTitle(getString(R.string.hours));
        renderer.setXLabelsAlign(Paint.Align.RIGHT);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(6.5);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(12);
        renderer.setClickEnabled(false);
        renderer.setExternalZoomEnabled(false);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanLimits(new double[] { -3, 20, -3, 24 });
        renderer.setZoomLimits(new double[] { -10, 20, -10, 24 });
        renderer.setShowGrid(true);
        renderer.setBarSpacing(0);
        renderer.setBarWidth(80.0f);
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
        rendererSeries.setChartValuesFormat(new TimeFormatAdapter());
        renderer.addSeriesRenderer(rendererSeries);
        
        return renderer;
    }
    
    private XYMultipleSeriesDataset makeStatisticsDataSet() {
    	XYSeries series = new XYSeries("Worked hours at day", 0);

        for (DayStatistics value : statisticsDao.loadWeekStatisticsFor(weekNumber)) {
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
        	int coordinate = weekDayFormatter.getDayCoordinate(i);
        	String label = weekDayFormatter.getWeekDayTitle(i);
            renderer.addXTextLabel(coordinate, label);
        }
    }
    
    public boolean showPreviousWeekStatistics(MenuItem item) {
    	if (statisticsDao.isStatisticsAvailableFor(weekNumber - 1)) {
    		weekNumber -= 1;
    	}
    	
    	initializeChart();
    	return true;
    }
    
    public boolean showNextWeekStatistics(MenuItem item) {
    	if (statisticsDao.isStatisticsAvailableFor(weekNumber + 1)) {
    		weekNumber += 1;
    	}
    	
    	initializeChart();
    	return true;
    }
    
}
