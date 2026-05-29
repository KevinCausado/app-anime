package com.kevindev.animeapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kevindev.animeapp.MainActivity
import com.kevindev.animeapp.R

object NotificationHelper {

    private const val CHANNEL_ID = "new_episodes"
    private const val CHANNEL_NAME = "Nuevos episodios"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Notificaciones cuando hay episodios nuevos de animes que seguís"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun showNewEpisode(context: Context, animeTitle: String, episodeNumber: Int, animeId: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to_detail", animeId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            animeId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(animeTitle)
            .setContentText("Episodio $episodeNumber disponible")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(animeId, notification)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS no concedido — el usuario no aprobó el permiso
        }
    }
}
