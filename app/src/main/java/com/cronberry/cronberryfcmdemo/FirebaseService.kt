package com.cronberry.fcmpushnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.cronberry.cronberryfcmdemo.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class FirebaseService : FirebaseMessagingService() {

    private val customTag: String = "cronberry"

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("cronberry", "New token")
        Log.d("cronberry", p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification == null) {
            if (remoteMessage.data.isNotEmpty()) {
                Log.d(customTag, "Message data payload: " + remoteMessage.data)
                sendNotification(remoteMessage.data)
            }
        }
    }

    private fun sendNotification(data: MutableMap<String, String>) {
        val mBuilder = NotificationCompat.Builder(this, "Cronberry")
        // Create a notificationManager object
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // If android version is greater than 8.0 then create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create a notification channel
            val notificationChannel = NotificationChannel(
                "Cronberry",
                "Cronberry Name " + (0..10).random(),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // Set properties to notification channel
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.YELLOW

            try {
                notificationManager.deleteNotificationChannel("Cronberry")
                Log.d("cronberry", "Delete Notiication")
            } catch (ex: Exception) {
                Log.d("cronberry", "excetion " + ex.message)
            }


            if (data.containsKey("vibrate") && data["vibrate"].toString().equals("1", true)) {
                Log.d("cronberry", "vibrate on ")
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern = longArrayOf(100, 200, 300)
            } else {
                notificationChannel.enableVibration(false)
                Log.d("cronberry", "vibrate off ")
            }

            // Pass the notificationChannel object to notificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val defaultSoundUri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        //Create the intent that’ll fire when the user taps the notification//
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse(data["actionURL"]))
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        mBuilder.setContentIntent(pendingIntent)
        if (data.containsKey("image")) {
            val bitmapFromURL = getBitmapFromURL(data["image"])
            mBuilder.setLargeIcon(bitmapFromURL)
            mBuilder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmapFromURL)
                    .bigLargeIcon(null)
            )
        } else if (data.containsKey("icon")) {
            val bitmapFromURL = getBitmapFromURL(data["icon"])
            mBuilder.setLargeIcon(bitmapFromURL)
            mBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(data["message"]))
        }
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        mBuilder.setSound(uri)
        mBuilder.setSmallIcon(R.drawable.notification)
        mBuilder.setContentTitle(data["title"])
        mBuilder.setSound(defaultSoundUri)
        mBuilder.setAutoCancel(true)
        mBuilder.setContentText(data["message"])
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(1, mBuilder.build())
    }

    private fun getBitmapFromURL(strURL: String?): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}