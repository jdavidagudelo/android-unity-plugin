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
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Random;

/**
 * Clase utilizada para acceso al GPS, las notificaciones y las alarmas de android
 * desde una aplicacion Unity.
 */
public class AndroidPlugin {

    private boolean mIsBound = false;
    private MyService mBoundService;
    /**
     * Contexto de la aplicacion, en unity corresponde a la actividad principal.
     */
    private Context context;
    /**
     * Ultima ubicacion conocida del sujeto.
     */
    private Location lastLocation = null;
    /**
     * Encargado de manejar el acceso a la ubicacion del dispositivo.
     */
    private LocationManager customLocationManager;
    /**
     * Proveedor de servicios de ubicacion. Incializado con el GPS.
     */
    private String PROVIDER = LocationManager.GPS_PROVIDER;
    /**
     * Instancia usada para manejar una unica instancia de la clase durante la ejecucion del programa.
     */
    private static AndroidPlugin instance;
    /**
     * Distancia acumulada del recorrido en metros.
     */
    private float distance = 0;
    /**
     * Tiempo acumulado del recorrido en milisegundos.
     */
    private long time = 0;
    /**
     * Tiempo actual.
     */
    private long currentTime = 0;
    /**
     * Numero de mensajes enviados en la notificacion 1.
     */
    private static int numMessagesOne = 1;
    /**
     * Numero de mensajes enviados en la notificacion 2.
     */
    private static int numMessagesTwo = 1;
    private int notificationIdTwo = 112;

    /**
     * Constructor.
     */
    private AndroidPlugin() {
        AndroidPlugin.instance = this;
    }

    public static AndroidPlugin instance() {
        if (instance == null) {
            instance = new AndroidPlugin();
        }
        return instance;
    }

    /**
     * Inicializa el context de la aplicacion.
     *
     * @param context contexto de la aplicacion. Normalmente corresponde a la
     *                actividad principal de Unity asociada con android.
     */
    public void setContext(Context context) {
        this.context = context;
        time = System.currentTimeMillis();
        customLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        lastLocation = customLocationManager.getLastKnownLocation(PROVIDER);
        customLocationManager.requestLocationUpdates(
                PROVIDER,     //provider
                0,       //minTime
                0,       //minDistance
                myLocationListener);
    }

    /**
     * Tiempo acumulado del recorrido del dispositivo.
     *
     * @return tiempo acumulado del recorrido del dispositivo.
     */
    public long getCurrentTime() {
        return currentTime;
    }

    /**
     * Ultima ubicacion del dispositivo.
     *
     * @return la ultima ubicacion conocida del dispositivo.
     */
    public String getLocation() {
        if (lastLocation == null) {
            return "No location available";
        }
        return lastLocation.toString();
    }

    /**
     * Permite obtener la distancia acumulada recorrida por el dispositivo.
     *
     * @return distancia acumulada recorrida por el dispositivo.
     */
    public float getCurrentDistance() {
        return distance;
    }

    /**
     * @param message
     */
    public void showMessage(String message) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
        //callAlarmService();
        displayNotificationOne();
    }

    /**
     * Permite inicializar el servicio de alarma del dispositivo desde un servicio.
     */
    public void callAlarmService() {
        context.startService(new Intent(context, AlarmService.class));
    }

    /**
     * Intent utilizado para inicializar las notificaciones mostradas en la aplicacion.
     *
     * @return intent utilizado para redirigir las notificaciones del usuario hacia el activity principal del juego.
     */
    public Intent getNewBaseIntent() {
        return new Intent(context, NotificationOpenedActivity.class).addFlags(603979776);
    }

    /**
     * Metodo usado para mostrar una notificacion de forma inmediata.
     */
    protected void displayNotificationOne() {
        if (context == null) {
            return;
        }
        Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
        Resources res = context.getResources();
        // Invoking the default notification service
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("New Message with explicit intent");
        mBuilder.setContentText("New message from javacodegeeks received");
        mBuilder.setTicker("Explicit: New Message Received!");
        int icon = res.getIdentifier("ic_launcher", "drawable", context.getPackageName());
        mBuilder.setSmallIcon(icon);
        // Increase notification number every time a new notification arrives
        mBuilder.setNumber(numMessagesOne);
        numMessagesOne++;
        Random random = new Random();
        int notificationId = random.nextInt();
        int intentId = random.nextInt();
        PendingIntent contentIntent = PendingIntent.getActivity(context, intentId, getNewBaseIntent().putExtra("notificationId", notificationId), PendingIntent.FLAG_ONE_SHOT);
        // start the activity when the user clicks the notification text
        mBuilder.setContentIntent(contentIntent);
        NotificationManager myNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // pass the Notification object to the system
        myNotificationManager.notify(notificationId, mBuilder.build());
    }

    /**
     * Metodo usado para iniciar la actividad principal del juego una vez el usuario hace clic en una notificacion.
     */
    public void handleNotificationOpened() {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        launchIntent.setFlags(131072);
        context.startActivity(launchIntent);
    }

    protected void displayNotificationTwo() {
        if (context == null) {
            return;
        }
        Resources res = context.getResources();
        // Invoking the default notification service
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
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

    /**
     * Metodo usado para reinicializar las variables usadas para medir la distancia, el tiempo y la ubicacion del dispositivo.
     */
    public void restartPath() {
        distance = 0;
        currentTime = 0;
        time = System.currentTimeMillis();
        lastLocation = customLocationManager.getLastKnownLocation(PROVIDER);
    }

    /**
     * Listener que se encarga de actualizar la ubicacion, el tiempo y la distancia recorrida por el dispositivo.
     */
    private LocationListener myLocationListener
            = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            distance += lastLocation.distanceTo(location);
            long actualTime = System.currentTimeMillis();
            currentTime += actualTime - time;
            time = actualTime;
            lastLocation = location;
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };


    public void sendNotification() {
        doBindService();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((MyService.LocalBinder) service).getService();

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
