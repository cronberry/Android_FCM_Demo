package com.cronberry.fcmpushnotification

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cronberry.cronberryfcmdemo.MainActivity
import com.cronberry.cronberryfcmdemo.OreoNotification
import com.cronberry.cronberryfcmdemo.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class FirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("cronberry", "Notiicaiton")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendOreoPushNotification(remoteMessage)
        } else {
            sendNotification(remoteMessage)
        }
    }

    @SuppressLint("LongLogTag")
    private fun sendNotification(remoteMessage: RemoteMessage) {
        if (!isAppIsInBackground(applicationContext)) {
            //foreground app
            Log.e("remoteMessage foreground", remoteMessage.data.toString())
            val title = remoteMessage.notification!!.title
            val body = remoteMessage.notification!!.body
            var resultIntent = Intent(applicationContext, MainActivity::class.java)
            if (remoteMessage.data.containsKey("actionURL")) {
                resultIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(remoteMessage.data["actionURL"]))
            } else {
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationBuilder = NotificationCompat.Builder(
                applicationContext,
                CHANNEL_ID
            )
            notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notification)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setNumber(10)
                .setTicker("Cronberry")
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setContentInfo("Info")
            if (null != remoteMessage.notification!!.imageUrl) {
                val bitmapFromURL = getBitmapFromURL(remoteMessage.notification!!.imageUrl)
                notificationBuilder.setLargeIcon(bitmapFromURL)
                val biy: Bitmap? = null
                notificationBuilder.setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmapFromURL)
                        .bigLargeIcon(biy)
                )
            } else if (remoteMessage.data.containsKey("icon")) {
                val bitmapFromURL = getBitmapFromURL(remoteMessage.notification!!.imageUrl)
                notificationBuilder.setLargeIcon(bitmapFromURL)
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle().bigText(remoteMessage.data["message"])
                )
            }
            notificationManager.notify(1, notificationBuilder.build())
        } else {
            Log.e("remoteMessage background", remoteMessage.data.toString())
            val data: Map<*, *> = remoteMessage.data
            val title: String? = data["title"] as String?
            val body: String? = data["body"] as String?
            var resultIntent = Intent(applicationContext, MainActivity::class.java)
            if (remoteMessage.data.containsKey("actionURL")) {
                resultIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(remoteMessage.data["actionURL"]))
            } else {
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationBuilder = NotificationCompat.Builder(
                applicationContext,
                CHANNEL_ID
            )
            notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notification)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentIntent(pendingIntent)
                .setNumber(10)
                .setTicker("Cronberry")
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")

            if (null != remoteMessage.notification!!.imageUrl) {
                val bitmapFromURL = getBitmapFromURL(remoteMessage.notification!!.imageUrl)
                notificationBuilder.setLargeIcon(bitmapFromURL)
                val biy: Bitmap? = null
                notificationBuilder.setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmapFromURL)
                        .bigLargeIcon(biy)
                )
            } else if (remoteMessage.data.containsKey("icon")) {
                val bitmapFromURL = getBitmapFromURL(remoteMessage.notification!!.imageUrl)
                notificationBuilder.setLargeIcon(bitmapFromURL)
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle().bigText(remoteMessage.data["message"])
                )
            }
            notificationManager.notify(1, notificationBuilder.build())
        }
    }

    @SuppressLint("NewApi")
    private fun sendOreoPushNotification(remoteMessage: RemoteMessage) {
        if (!isAppIsInBackground(applicationContext)) {
            //foreground app
            Log.d("cronberry", "backound ot")
            Log.e("remoteMessage", remoteMessage.data.toString())
            val title = "Hello " + remoteMessage.notification!!.title
            val body = remoteMessage.notification!!.body
            var resultIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cronberry.com"))
            if (remoteMessage.data.containsKey("actionURL")) {
                resultIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(remoteMessage.data["actionURL"]))
            }

            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0 /* Request code */, resultIntent,
                0
            )
            val defaultsound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val oreoNotification = OreoNotification(this)
            val builder: Notification.Builder = oreoNotification.getOreoNotification(
                title,
                body,
                pendingIntent,
                defaultsound,
                java.lang.String.valueOf(R.drawable.ic_launcher_background)
            )
            val i = 0
            if (null != remoteMessage.notification!!.imageUrl) {
                val bitmapFromURL = getBitmapFromURL(remoteMessage.notification!!.imageUrl)
                builder.setLargeIcon(bitmapFromURL)
                val biy: Bitmap? = null
                builder.style = Notification.BigPictureStyle()
                    .bigPicture(bitmapFromURL)
                    .bigLargeIcon(biy)
            } else if (remoteMessage.data.containsKey("icon")) {
                val bitmapFromURL = getBitmapFromURL(remoteMessage.notification!!.imageUrl)
                builder.setLargeIcon(bitmapFromURL)
                builder.style = Notification.BigTextStyle().bigText(remoteMessage.data["message"])
            }
            oreoNotification.manager!!.notify(i, builder.build())
        } else {
            Log.d("cronberry", "backound yes")
            Log.e("remoteMessage", remoteMessage.data.toString())
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            var resultIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cronberry.com"))
            if (remoteMessage.data.containsKey("actionURL")) {
                resultIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(remoteMessage.data["actionURL"]))
            }
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0 /* Request code */, resultIntent,
                0
            )

            val defaultsound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val oreoNotification = OreoNotification(this)
            val builder: Notification.Builder = oreoNotification.getOreoNotification(
                title,
                body,
                pendingIntent,
                defaultsound,
                java.lang.String.valueOf(R.drawable.ic_launcher_background)
            )
            val i = 0
            if (remoteMessage.data.containsKey("imageUrl")) {
                val bitmapFromURL = getBitmapFromURL(remoteMessage.notification!!.imageUrl)
                builder.setLargeIcon(bitmapFromURL)
                val biy: Bitmap? = null
                builder.style = Notification.BigPictureStyle()
                    .bigPicture(bitmapFromURL)
                    .bigLargeIcon(biy)
            } else if (remoteMessage.data.containsKey("icon")) {
                val bitmapFromURL = getBitmapFromURL(remoteMessage.notification!!.imageUrl)
                builder.setLargeIcon(bitmapFromURL)
                builder.style = Notification.BigTextStyle().bigText(remoteMessage.data["message"])
            }
            oreoNotification.manager!!.notify(i, builder.build())
        }
    }

    private fun getBitmapFromURL(strURL: Uri?): Bitmap? {
        return try {
            Log.d("cronberry", "URL: " + strURL.toString())
            val url = URL(strURL.toString())
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

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("NEW_TOKEN = = == = = =", s)
    }

    companion object {
        private const val CHANNEL_ID = "Cronberry"
        fun isAppIsInBackground(context: Context): Boolean {
            var isInBackground = true
            val am =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                val runningProcesses: List<RunningAppProcessInfo> = am.runningAppProcesses
                for (processInfo in runningProcesses) {
                    if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (activeProcess in processInfo.pkgList) {
                            if (activeProcess == context.packageName) {
                                isInBackground = false
                            }
                        }
                    }
                }
            } else {
                val taskInfo: List<ActivityManager.RunningTaskInfo> = am.getRunningTasks(1)
                val componentInfo: ComponentName = taskInfo[0].topActivity!!
                if (componentInfo.packageName == context.packageName) {
                    isInBackground = false
                }
            }
            return isInBackground
        }
    }
}