package udea.telesalud.artica.com.plugin;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationOne extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int id = getResources().getIdentifier("notification_one", "layout", getPackageName());
        setContentView(id);
        CharSequence s = "Inside the activity of Notification one ";
        int currentId = 0;
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            s = "error";
        } else {
            currentId = extras.getInt("notificationId");
        }
        id = getResources().getIdentifier("text1", "id", getPackageName());
        TextView t = (TextView) findViewById(id);
        s = s + "with id = " + currentId;
        t.setText(s);
        NotificationManager myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // remove the notification with the specific id
        myNotificationManager.cancel(currentId);
    }
}
