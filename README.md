AlarmDemo - Beispielprojekt
===========================

Die AlarmDemo-App verwendet den AlarmManager, um zu einer vorgegebenen
Uhrzeit eine einmalige Benachrichtigung auf dem Gerät zu erzeugen.

Bestandteile der App
--------------------

* MainActivity: Eingabe von Datum und Uhrzeit des Alarms sowie der
  auszugebenden Nachricht. Setzen und Löschen des Alarms über zwei
  Buttons
* AlarmReceiver: Ein BroadcastReceiver, der bei Erreichen der Alarmzeit
  aktiviert wird und die Benachrichtigung ausgibt

Verwendete Mechanismen
----------------------

* PendingIntent: Ein PendingIntent wird verwendet, um einen Intent der
  App zu einem späteren Zeitpunkt zu verwenden. In unserem Fall ist
  es der Intent notifyIntent, der dafür sorgt, dass die Benachrichtigung
  ausgegeben wird.
* AlarmManager: Eine Klasse zum Zugriff auf den Systemdienst "ALARM_SERVICE".
  Kann einmalige oder wiederkehrende Alarme auslösen. Die Aktion des
  Alarms ist der im PendingIntent enthaltene Intent.

Verwendung der Demo
-------------------

Das Repository kann in Android Studio heruntergeladen und gestartet werden.

Quellen
-------

Teile des Sourcecodes stammen aus dem Beispielprojekt "Stand Up!", das  
der Entwickler-Dokumentation für Android von Google entstammt.


Copyright 2021 Prof. Dr. Helmut Roderus, Ansbach University of Applied Sciences

Licensed to the Apache Software Foundation (ASF). You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
