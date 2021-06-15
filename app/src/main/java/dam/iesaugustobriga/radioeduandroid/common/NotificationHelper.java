package dam.iesaugustobriga.radioeduandroid.common;

import android.content.Context;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import dam.iesaugustobriga.radioeduandroid.R;

public class NotificationHelper {

    public static void displayNotification(Context context, String title, String text){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_podcasts);
        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(context);
        notificationCompat.notify(1, mBuilder.build());
    }

}
