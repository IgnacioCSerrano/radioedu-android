package dam.iesaugustobriga.radioeduandroid.common;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class MessagingService extends FirebaseMessagingService {

    // Este método inicia el servicio de mensajería invocado cuando la aplicación recibe la notificación

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null){
            String title = remoteMessage.getNotification().getTitle();
            String text = remoteMessage.getNotification().getBody();
            NotificationHelper.displayNotification(getApplicationContext(), title, text); // llamada al método para mostrar las notificaciones
        }
    }

    @Override
    public void onNewToken(@NotNull String s) {
        super.onNewToken(s);
    }
}