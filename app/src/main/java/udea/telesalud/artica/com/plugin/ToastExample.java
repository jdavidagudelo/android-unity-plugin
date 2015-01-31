package udea.telesalud.artica.com.plugin;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class ToastExample {

    private Context context;
    private Location lastLocation = null;
    private LocationManager myLocationManager;
    private String PROVIDER = LocationManager.GPS_PROVIDER;
    private static ToastExample instance;
    private float distance = 0;
    private long time = 0;
    private long currentTime = 0;

    public ToastExample() {

        ToastExample.instance = this;

    }

    public static ToastExample instance() {
        if(instance == null) {
            instance = new ToastExample();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
        time = System.currentTimeMillis();
        myLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        lastLocation = myLocationManager.getLastKnownLocation(PROVIDER);
        myLocationManager.requestLocationUpdates(
                PROVIDER,     //provider
                0,       //minTime
                0,       //minDistance
                myLocationListener);
    }
    public long getCurrentTime()
    {
        return  System.currentTimeMillis()-time;
    }
    public String getLocation()
    {
        if(lastLocation == null)
        {
            return "No location available";
        }
        return lastLocation.toString();
    }
    public float getCurrentDistance()
    {
        return distance;
    }
    public void showMessage(String message) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
    }
    private LocationListener myLocationListener
            = new LocationListener(){

        @Override
        public void onLocationChanged(Location location) {
            distance += lastLocation.distanceTo(location);
            long actualTime = System.currentTimeMillis();
            currentTime +=  actualTime - time;
            time = actualTime;
            lastLocation = location;
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }};
}
