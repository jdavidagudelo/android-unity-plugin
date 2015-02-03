package udea.telesalud.artica.com.plugin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
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
    private static int numMessagesOne = 1;
    private static int numMessagesTwo = 1;
    private int notificationIdOne = 111;
    private int notificationIdTwo = 112;
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
        return  currentTime;
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
        context.startService(new Intent(context, MyService.class));
        sendNotification();
    }

    protected void displayNotificationOne() {
        if(context == null)
        {
            return;
        }
        Resources res = context.getResources();
        // Invoking the default notification service
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("New Message with explicit intent");
        mBuilder.setContentText("New message from javacodegeeks received");
        mBuilder.setTicker("Explicit: New Message Received!");
        int icon = res.getIdentifier("ic_launcher", "drawable", context.getPackageName());
        mBuilder.setSmallIcon(icon);
        // Increase notification number every time a new notification arrives
        mBuilder.setNumber(numMessagesOne);
        numMessagesOne++;
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, NotificationOne.class);
        resultIntent.putExtra("notificationId", notificationIdOne);
        //This ensures that navigating backward from the Activity leads out of the app to Home page
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent
        stackBuilder.addParentStack(NotificationOne.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT
                );
        // start the activity when the user clicks the notification text
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager myNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // pass the Notification object to the system
        myNotificationManager.notify(notificationIdOne, mBuilder.build());
    }
    protected void displayNotificationTwo() {
        if(context==null)
        {
            return;
        }
        Resources res = context.getResources();
        // Invoking the default notification service
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("New Message with implicit intent");
        mBuilder.setContentText("New message from javacodegeeks received...");
        mBuilder.setTicker("Implicit: New Message Received!");
        int icon = res.getIdentifier("ic_launcher", "drawable",
                context.getPackageName());
        mBuilder.setSmallIcon(icon);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = new String[3];
        events[0] = "1) Message for implicit intent";
        events[1] = "2) big view Notification";
        events[2] = "3) from javacodegeeks!";
        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("More Details:");
        // Moves events into the big view
        for (String event : events) {
            inboxStyle.addLine(event);
        }
        mBuilder.setStyle(inboxStyle);
        // Increase notification number every time a new notification arrives
        mBuilder.setNumber(++numMessagesTwo);
        // when the user presses the notification, it is auto-removed
        mBuilder.setAutoCancel(true);
        // Creates an implicit intent
        Intent resultIntent = new Intent("com.example.javacodegeeks.TEL_INTENT",
                Uri.parse("tel:123456789"));
        resultIntent.putExtra("from", "javacodegeeks");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(NotificationTwo.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager myNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // pass the Notification object to the system
        myNotificationManager.notify(notificationIdTwo, mBuilder.build());
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
    private boolean mIsBound = false;
    private MyService mBoundService;
    public void sendNotification()
    {
        doBindService();
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((MyService.LocalBinder)service).getService();

            // Tell the user about this for our demo.
            Toast.makeText(context, "Local service connected",
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            Toast.makeText(context, "Local service disconnected",
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        context.bindService(new Intent(context,
                MyService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            context.unbindService(mConnection);
            mIsBound = false;
        }
    }
}
