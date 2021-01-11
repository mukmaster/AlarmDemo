/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.planetmuk.android.alarmdemo;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

import de.planetmuk.android.alarmdemo.R;

/**
 * Dies ist die MainActivity der AlarmDemo-App. Sie stellt Eingabefelder zum Setzen der Alarmzeit
 * und einem Benachrichtigungstext zur Verfügung. Wenn der User die Daten für einen Alarm eingegeben
 * hat, wird dem AlarmManager ein PendingIntent übergeben. Dieser wird zur gegebenen Zeit vom
 * Android-System aktiviert und losgeschickt, worauf die Benachrichtigung erzeugt wird.
 */
public class MainActivity extends AppCompatActivity {

    // Kennzeichen für das Logfile
    final protected String APP_TAG = "AlarmDemoApp(Main)";

    // Die folgenden Deklarationen benötigen wir für den NotificationChannel
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotificationManager;
    private PendingIntent notifyPendingIntent;

    /**
     * Initialisierung der MainActivity.
     * @param savedInstanceState Aktueller Zustand der Instanz.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Variablen zum Zugriff auf die beiden Eingabefelder und die beiden Buttons der View
        final EditText alarmDateView = findViewById(R.id.editAlarmDate);
        final EditText alarmTimeView = findViewById(R.id.editAlarmTime);
        final EditText alarmMessageView = findViewById(R.id.editMessageText);
        final Button setButtonView = findViewById(R.id.setAlarmButton);
        final Button cancelButtonView = findViewById(R.id.cancelAlarmButton);

        // Zugriff auf den Notification-Service
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Wir holen uns den Zugriff auf den Systemdienst AlarmManger
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // EventHandling: Hier setzen wir noch den OnClick-Listener für unseren Button. Dieser ist
        // dafür zuständig, die Eingabefelder auszulesen und den Alarm zu registrieren.
        setButtonView.setOnClickListener
                (new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Schritt 1: Die Eingabefelder (Datum, Uhrzeit und Nachricht) auslesen.
                        // Dabei die Alarmzeit in eine zur Weiterverarbeitung geeignete Systemzeit
                        // in Millisekunden umwandeln.
                        String alarmDateText = alarmDateView.getText().toString();
                        String alarmTimeText = alarmTimeView.getText().toString();
                        String alarmMessage = alarmMessageView.getText().toString();

                        // Einen Zeit-String nach dem Schema "Datum und Uhrzeit" zusammensetzen
                        String alarmDateTimeText = alarmDateText + " " + alarmTimeText;
                        DateFormat formatter = new SimpleDateFormat("dd.mm.yyyy hh:mm");
                        Date alarmDate = null;
                        try {
                            alarmDate = formatter.parse(alarmDateTimeText);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // und schließlich in Systemzeit (Millisekunden seit 1.1.1970) wandeln
                        long alarmInMillis = alarmDate.getTime();


                        // Schritt 2: Einen Intent erzeugen, der (später!) die Benachrichtigung
                        // auslösen wird. Titel und Text der Nachricht mit in den Intent einbauen.
                        Intent notifyIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                        notifyIntent.putExtra("notificationTitle", getString(R.string.notification_title));
                        notifyIntent.putExtra("notificationText", alarmMessage);

                        // Schritt 3: Aus dem Intent einen PendingIntent erzeugen.
                        // Ein PendingIntent ist ein verzögerter Intent, der, wenn der Alarmzeitpunkt
                        // gekommen ist, den notifyIntent auslöst.
                        notifyPendingIntent = PendingIntent.getBroadcast
                                (getApplicationContext(), NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        // Schritt 4: Die Alarmzeit und den PendingIntent dem Alarmmanager übergeben.
                        // Wir verwenden ein RTC_WAKEUP (Realtime-Clock): Das Smartphone wird, falls
                        // erforderlich, aufgeweckt und der notifyPendingIntent ausgeführt.
                        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmInMillis, notifyPendingIntent);

                        // Schritt 5: Ein Toast für den User und eine Meldung für's Logfile
                        Date now = new Date();
                        long nowInMillis = now.getTime();
                        long diffInSeconds = (alarmInMillis - nowInMillis) / 1000;
                        String logString = "Alarm (" + alarmMessage + ") startet in " + diffInSeconds / 60 + " Minuten und " + diffInSeconds % 60 + " Sekunden";
                        Toast.makeText(getApplicationContext(), logString, Toast.LENGTH_SHORT).show();
                        Log.i(APP_TAG, logString);
                    }
                });

        // Der OnClickListener für den Cancel-Button löscht Alarme und Notifikationen
        cancelButtonView.setOnClickListener
                (new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         // Alle Alarme und Notifikationen löschen
                         mNotificationManager.cancelAll();
                         if (alarmManager != null) {
                             alarmManager.cancel(notifyPendingIntent);
                         }

                         // Und wieder in Toast und ein kurzer Eintrag ins Log
                         String logString = "Alarm wurde gelöscht";
                         Log.i(APP_TAG, logString);
                         Toast.makeText(getApplicationContext(), logString, Toast.LENGTH_SHORT).show();
                     }
                 }
                );

        // Letzte Aktion in onCreate: Erzeugen des NotifikationChannel-Objekts
        createNotificationChannel();
    }


        /**
         * Legt einen NotificationChannel an (das geht ab Android 8.0 Oreo)
         */
        public void createNotificationChannel () {

            // Das NotificationManager-Objekt erhalten wir vom entspr. Systemdienst
            mNotificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            // Hier wird die Android-Version gecheckt
            if (android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.O) {

                // Jetzt den NotificationChannel mit den erforderlichen Parametern anlegen
                NotificationChannel notificationChannel = new NotificationChannel
                        (PRIMARY_CHANNEL_ID,
                                getString(R.string.notification_channel_name),
                                NotificationManager.IMPORTANCE_HIGH);

                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setDescription(getString(R.string.notification_channel_description));
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }