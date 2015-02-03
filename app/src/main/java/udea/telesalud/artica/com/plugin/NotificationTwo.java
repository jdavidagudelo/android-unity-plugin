package udea.telesalud.artica.com.plugin;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationTwo extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int id = getResources().getIdentifier("notification_two", "layout", getPackageName());
        setContentView(id);
        String output = "Inside the activity of Notification two: ";
        id = getResources().getIdentifier("text2", "id", getPackageName());
        TextView dataIntent = (TextView) findViewById(id);
        // take the data and the extras of the intent
        Uri url = getIntent().getData();
        Bundle extras = getIntent().getExtras();
        output = output + url.toString();
        // if there are extras, add them to the output string
        if (extras != null) {
            output = output + " from " + extras.getString("from");

        }
        dataIntent.setText(output);
    }

}
