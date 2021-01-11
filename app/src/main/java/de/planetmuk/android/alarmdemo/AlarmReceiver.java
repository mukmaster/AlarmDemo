package de.planetmuk.android.alarmdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import de.planetmuk.android.alarmdemo.R;

/**
 * Dies ist eine Subklasse von BroadcastReceiver. Sie hat als einzige Aufgabe, die Notifikation
 * auszuliefern. Wenn vom System der eingestellte Alarm ausgelöst wird, wird eine Instanz von
 * AlarmReceiver gestartet und der PendingIntent mit seinen Daten in onReceive entgegengenommen.
 */
public class AlarmReceiver extends BroadcastReceiver {

    // allgemeine Deklarationen (Tag für das Log)
    final String APP_TAG = "AlarmDemoApp (Recv)";

    // Die folgenden Deklarationen benötigen wir für den NotificationChannel (s. MainActivity)
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    /**
     * Wird aufgerufen, wenn AlarmReceiver einen Intent empfängt.
     * Liest die Nachricht aus dem Intent und gibt sie über den NotificationManager aus.
     *
     * @param context Context des Receivers.
     * @param intent Intent, der empfangen wurde.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(APP_TAG, "Notification received");
        // Zugriff auf den NotificationManager erhalten
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationTitle, notificationText;
        notificationTitle = intent.getStringExtra("notificationTitle");
        notificationText = intent.getStringExtra("notificationText");
        // Auslieferen der Notifikation mit einer Hilfsmethode
        deliverNotification(context, notificationTitle, notificationText);
        Log.i(APP_TAG, "Notification delivered: " + notificationTitle);
    }

    /**
     * Hilfsmethode zum Erstellen und Ausliefern derBenachrichtigung
     *
     * @param context, Context der Activity.
     */
    private void deliverNotification(Context context, String notificationTitle, String notificationText) {
        // Es wird ein Intent für den Inhalt erstellt und dann in einen PendingIntent eingesetzt
        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID, contentIntent, PendingIntent
                        .FLAG_UPDATE_CURRENT);

        // Notifikation zusammensetzen, u.a. Text und ein Icon für Benachrichtigung einsetzen
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        // Notifikation ausliefern
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
