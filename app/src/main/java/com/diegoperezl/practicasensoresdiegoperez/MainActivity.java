package com.diegoperezl.practicasensoresdiegoperez;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The MainActivity class is the main activity of the application. It implements
 * AdapterView.OnItemSelectedListener and SensorEventListener to handle sensor events and
 * spinner item selections, respectively.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SensorEventListener {

    private SensorManager mSensorManager;

    // HashMap containing the sensors available for the current device and their types
    private HashMap<String, Integer> sensors = new HashMap<String, Integer>(){{
        put("Acelerómetro", Sensor.TYPE_ACCELEROMETER);
        put("Temperatura", Sensor.TYPE_AMBIENT_TEMPERATURE);
        put("Gravedad", Sensor.TYPE_GRAVITY);
        put("Giroscópio", Sensor.TYPE_GYROSCOPE);
        put("Luminosidad", Sensor.TYPE_LIGHT);
        put("Aceleración lineal", Sensor.TYPE_LINEAR_ACCELERATION);
        put("Campo magnético", Sensor.TYPE_MAGNETIC_FIELD);
        put("Presión", Sensor.TYPE_PRESSURE);
        put("Proximidad", Sensor.TYPE_PROXIMITY);
        put("Humedad relativa", Sensor.TYPE_RELATIVE_HUMIDITY);
        put("Vector de rotación", Sensor.TYPE_ROTATION_VECTOR);
    }};


    LineChart lineChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Get the list of sensors available for the current device and remove those that are not available
        getPhoneSensors();

        // Get a reference to the LineChart view and configure it
        lineChart = findViewById(R.id.chartItem);
        createChart();

        // Set up the spinner widget to allow the user to select a sensor
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        List<String> listaSensores = new ArrayList<>(sensors.keySet());
        System.out.println(listaSensores);
        ArrayAdapter<String> adapter = new ArrayAdapter<>((Context) this, R.layout.spinner_item,listaSensores);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Disable seekbar movement from client
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // Get a list of all the sensors available for the current device and print it to the console
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        System.out.println(deviceSensors);
    }

    /**
     Called when the activity is resumed from a paused state.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     Called when the activity is paused and another activity is being resumed.
     Unregisters the sensor listener to save battery life when the activity is not in foreground.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /**
     * Removes from the sensors map the sensors that are not available in the current device.
     */
    private void getPhoneSensors() {
        Iterator it = sensors.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            int sensor = (int) pair.getValue();
            System.out.println(pair.getKey());
            if (mSensorManager.getDefaultSensor(sensor) == null) {
                it.remove();

            }
        }
    }

    /**
     * Complete the UI with information about the selected sensor.
     * @param type The type of the selected sensor.
     */
    private void setSensorInfo(int type){
        List<Sensor> deviceSensor = mSensorManager.getSensorList(type);
        ((TextView)findViewById(R.id.name)).setText("Nombre: "+deviceSensor.get(0).getName());
        ((TextView)findViewById(R.id.vendor)).setText("Vendedor: "+deviceSensor.get(0).getVendor());
        ((TextView)findViewById(R.id.version)).setText("Version: "+deviceSensor.get(0).getVersion());
        ((TextView)findViewById(R.id.typeSensor)).setText("Tipo sensor: "+deviceSensor.get(0).getType());
        ((TextView)findViewById(R.id.maxRange)).setText("Rango máximo: "+deviceSensor.get(0).getMaximumRange());
        ((TextView)findViewById(R.id.resolution)).setText("Resolución: "+deviceSensor.get(0).getResolution());
        ((TextView)findViewById(R.id.power)).setText("Potencia: "+deviceSensor.get(0).getPower());
        ((TextView)findViewById(R.id.minDelay)).setText("Retardo mínimo: "+deviceSensor.get(0).getMinDelay());
    }

    /**
     * Creates and configures the LineChart object.
     */
    private void createChart(){

        // Disable chart description and user interaction
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getLegend().setEnabled(true);

        // Configure X dataset
        LineDataSet dataSetX = new LineDataSet(new ArrayList<Entry>(), "X");
        dataSetX.setDrawValues(false);
        dataSetX.setDrawCircles(false);
        dataSetX.setLineWidth(2f);
        dataSetX.setColor(Color.BLUE);

        // Configure Y dataset
        LineDataSet dataSetY = new LineDataSet(new ArrayList<Entry>(), "Y");
        dataSetY.setDrawValues(false);
        dataSetY.setDrawCircles(false);
        dataSetY.setLineWidth(2f);
        dataSetY.setColor(Color.GREEN);

        // Configure Z dataset
        LineDataSet dataSetZ = new LineDataSet(new ArrayList<Entry>(), "Z");
        dataSetZ.setDrawValues(false);
        dataSetZ.setDrawCircles(false);
        dataSetZ.setLineWidth(2f);
        dataSetZ.setColor(Color.MAGENTA);

        // Configure X axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(100);

        // Add datasets to chart
        LineData lineData = new LineData(dataSetX, dataSetY, dataSetZ);
        lineChart.setData(lineData);
    }

    /**
     * Sets the chart with a single metric value.
     * @param metric The metric value to add to the chart.
     */
    private void setChart1D(float metric){
        // Update text views with metric values
        ((TextView)findViewById(R.id.xText)).setText("X: "+String.valueOf(metric));
        ((TextView)findViewById(R.id.yText)).setText("");
        ((TextView)findViewById(R.id.zText)).setText("");

        // Add entry to dataset
        LineData data = lineChart.getData();
        LineDataSet dataSet = (LineDataSet) data.getDataSetByIndex(0);
        data.addEntry(new Entry(dataSet.getEntryCount(), metric), 0);

        // Move X axis if necessary
        if(dataSet.getEntryCount()>100)
            moveXAxis(dataSet.getEntryCount());

        // Notify chart data has changed
        data.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    /**
     * Sets the chart with 3 metric values.
     * @param x The metric value for X axis.
     * @param y The metric value for Y axis.
     * @param z The metric value for Z axis.
     */
    private void setChart3D(float x, float y, float z){
        // Update text views with metric values
        ((TextView)findViewById(R.id.xText)).setText("X: "+String.valueOf(x));
        ((TextView)findViewById(R.id.yText)).setText("Y: "+String.valueOf(y));
        ((TextView)findViewById(R.id.zText)).setText("Z: "+String.valueOf(z));

        // Add entries to datasets
        LineData data = lineChart.getData();
        LineDataSet dataSetX = (LineDataSet) data.getDataSetByIndex(0);
        LineDataSet dataSetY = (LineDataSet) data.getDataSetByIndex(1);
        LineDataSet dataSetZ = (LineDataSet) data.getDataSetByIndex(2);
        data.addEntry(new Entry(dataSetX.getEntryCount(), x), 0);
        data.addEntry(new Entry(dataSetY.getEntryCount(), y), 1);
        data.addEntry(new Entry(dataSetZ.getEntryCount(), z), 2);

        // Move X axis if necessary
        if(dataSetX.getEntryCount()>100)
            moveXAxis(dataSetX.getEntryCount());

        // Notify chart data has changed
        data.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    /**
     * Sets the X-axis range of the line chart to display a specified range of values.
     * @param x The maximum value to display on the X-axis.
     */
    private void moveXAxis(float x){
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisMinimum(x-100);
        xAxis.setAxisMaximum(x);
    }

    /**
     * Sets the image displayed in the activity to a specified drawable resource.
     * @param image The resource ID of the drawable to set as the image.
     */
    private void setImageData(int image){
        ImageView img= (ImageView) findViewById(R.id.image);
        img.setImageResource(image);
    }

    /**
     * Method for configuring the UI to display a SeekBar and an image.
     */
    private void configSeek(){
        findViewById(R.id.chart).setVisibility(View.GONE);
        findViewById(R.id.image).setVisibility(View.VISIBLE);
        findViewById(R.id.seek).setVisibility(View.VISIBLE);
    }

    /**
     * Method for configuring the UI to display an image only.
     */
    private void configImage(){
        findViewById(R.id.seek).setVisibility(View.GONE);
        findViewById(R.id.chart).setVisibility(View.GONE);
        findViewById(R.id.image).setVisibility(View.VISIBLE);
    }

    /**
     * Method for configuring the UI to display a LineChart.
     */
    private void configChar(){
        findViewById(R.id.seek).setVisibility(View.GONE);
        findViewById(R.id.image).setVisibility(View.GONE);
        findViewById(R.id.chart).setVisibility(View.VISIBLE);
    }

    /**
     Called when an item in the sensor selection spinner is selected.
     @param adapterView The AdapterView that triggered the selection event
     @param view The View that was selected by the user
     @param i The position of the selected item in the spinner
     @param l The row id of the selected item (not used)
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Get the selected sensor name from the spinner
        String itemSeleccionado = adapterView.getItemAtPosition(i).toString();

        // Look up the corresponding sensor type code
        int sensor = sensors.get(itemSeleccionado);

        // Unregister any previously registered listeners
        mSensorManager.unregisterListener(this);

        // Register a listener for the selected sensor
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(sensor), SensorManager.SENSOR_DELAY_NORMAL);

        // Update the UI to display information about the selected sensor
        setSensorInfo(sensor);

        // Switch on the sensor type to configure the UI appropriately
        switch (sensor){
            case Sensor.TYPE_PROXIMITY:
                configImage();
                setImageData(R.drawable.ic_blackhand);
                break;
            case Sensor.TYPE_LIGHT:
                configSeek();
                setImageData(R.drawable.ic_lightblack);
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                configSeek();
                setImageData(R.drawable.ic_tempnormal);
                break;
            case Sensor.TYPE_PRESSURE:
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_LINEAR_ACCELERATION:
            case Sensor.TYPE_GRAVITY:
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_ROTATION_VECTOR:
            case Sensor.TYPE_RELATIVE_HUMIDITY:
            case Sensor.TYPE_MAGNETIC_FIELD:
                createChart();
                configChar();
                break;
        }
    }

    /**
     Update the UI to display sensor data when a new sensor event is received.
     This method is called on a background thread to avoid blocking the UI thread.
     @param sensorEvent The sensor event that triggered this update
     */
    private void setSensorMetrics(SensorEvent sensorEvent){
        switch (sensorEvent.sensor.getType()){
            case Sensor.TYPE_PRESSURE:
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                float value = sensorEvent.values[0];
                setChart1D(value);
                break;
            case Sensor.TYPE_PROXIMITY:
                float proximidad = sensorEvent.values[0];
                if(proximidad == 0)
                    setImageData(R.drawable.ic_greenhand);
                else
                    setImageData(R.drawable.ic_blackhand);
                break;
            case Sensor.TYPE_LIGHT:
                float luz = sensorEvent.values[0];
                ((SeekBar)findViewById(R.id.seekBar)).setProgress((int) ((luz)*100/400));
                ((TextView)findViewById(R.id.seekLabel)).setText((String.valueOf(luz)));
                if(luz < 100)
                    setImageData(R.drawable.ic_lightblack);
                else if (luz >= 100 && luz < 300)
                    setImageData(R.drawable.ic_lightgrey);
                else
                    setImageData(R.drawable.ic_lightwhite);
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                float temp = sensorEvent.values[0];
                ((SeekBar)findViewById(R.id.seekBar)).setProgress((int) ((temp+273)*100/353));
                ((TextView)findViewById(R.id.seekLabel)).setText((String.valueOf(temp))+"ºC");
                if(temp < 10)
                    setImageData(R.drawable.ic_tempcold);
                else if (temp >= 10 && temp < 30)
                    setImageData(R.drawable.ic_tempnormal);
                else
                    setImageData(R.drawable.ic_temphot);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_GRAVITY:
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_ROTATION_VECTOR:
            case Sensor.TYPE_MAGNETIC_FIELD:
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                setChart3D(x, y, z);
                break;
        }
    }

    /**
     * This method is called when no item is selected in an AdapterView.
     * @param adapterView The AdapterView that was interacted with.
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * This method is called when sensor values have changed.
     * @param sensorEvent The sensor event that contains the new sensor values.
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setSensorMetrics(sensorEvent);
                }catch (Exception e){
                    System.out.println("Error de inserción");
                }

            }
        }).start();
    }

    /**
     * This method is called when the accuracy of a sensor has changed.
     * @param sensor The sensor whose accuracy has changed.
     * @param i The new accuracy of the sensor.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}