package com.suqir.wasaischedule

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.suqir.wasaischedule.ui.schedule_settings.ScheduleSettingsActivity
import com.suqir.wasaischedule.utils.Const
import com.suqir.wasaischedule.utils.getPrefer
import es.dmoral.toasty.Toasty

class App : Application() {

    var activityCount = 0

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        Toasty.Config.getInstance()
                .setToastTypeface(Typeface.DEFAULT_BOLD)
                .setTextSize(12)
                .apply()
        if (!BuildConfig.DEBUG) {
            AppCenter.start(this, "0de19004-c167-4342-908a-7f077a4934f2",
                    Analytics::class.java, Crashes::class.java)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channelId = "schedule_reminder"
            var channelName = "课程提醒"
            var importance = NotificationManager.IMPORTANCE_HIGH
            createNotificationChannel(this, channelId, channelName, importance)
            channelId = "news"
            channelName = "公告"
            importance = NotificationManager.IMPORTANCE_LOW
            createNotificationChannel(this, channelId, channelName, importance)
        }
        when (getPrefer().getInt(Const.KEY_DAY_NIGHT_THEME, 2)) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            2 -> {
                when {
                    Build.VERSION.SDK_INT >= 29 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                    Build.VERSION.SDK_INT >= 23 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                    }
                    else -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
                activityCount++
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
                activityCount--
                if (activity is ScheduleSettingsActivity && activityCount == 0) {
                    Toasty.info(applicationContext, "对小部件的编辑需要按「返回键」退出设置页面才能生效哦", Toast.LENGTH_LONG).show()
                }
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            }

        })
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context, channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        // 允许这个渠道下的通知显示角标
        channel.setShowBadge(true)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}