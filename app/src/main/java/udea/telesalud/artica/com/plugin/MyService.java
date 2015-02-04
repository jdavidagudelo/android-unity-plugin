package udea.telesalud.artica.com.plugin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Random;

public class MyService extends Service {

    private NotificationManager notificationManager;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = 112;
    private static int numMessagesOne = 1;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        notificationManager.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(this,"Service stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    public void showNotification() {

       displayNotificationOne();
    }
    protected void displayNotificationOne() {
        Context context = getBaseContext();
        if(getBaseContext() == null)
        {
            return;
        }
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
        PendingIntent contentIntent = PendingIntent.getActivity(context, intentId, AndroidPlugin.instance().getNewBaseIntent().putExtra("notificationId", notificationId), PendingIntent.FLAG_ONE_SHOT);
        // start the activity when the user clicks the notification text
        mBuilder.setContentIntent(contentIntent);
        NotificationManager myNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // pass the Notification object to the system
        myNotificationManager.notify(notificationId, mBuilder.build());
    }
}