package udea.telesalud.artica.com.plugin;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NotificationOpenedActivity extends Activity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        processNotification();
    }

    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        processNotification();
    }

    private void processNotification() {
        Intent intent = getIntent();
        int notificationId = intent.getIntExtra("notificationId", 0);

        if (notificationId != 0) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationId);
        }
        AndroidPlugin.instance().handleNotificationOpened();
        finish();
    }
}