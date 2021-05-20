package com.example.parcmarc

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

fun Bundle.toParamsString() = keySet().map { "$it -> ${get(it)}" }.joinToString("\n")

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("FOO", "Received message ${intent.action} with\n${intent.extras?.toParamsString()}")

        val notification = Notification.Builder(context, Notification.CATEGORY_REMINDER).run {
            setSmallIcon(R.drawable.ic_baseline_info_24)
            setContentTitle("A new day, a new parking spot")
            setContentTitle("Just a friendly reminder to log your parking spot.")
            build()
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, notification)


    }
}
