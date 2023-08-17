package com.dazone.crewchatoff

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Vibrator
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dazone.crewchatoff.HTTPs.HttpRequest
import com.dazone.crewchatoff.activity.ChattingActivity
import com.dazone.crewchatoff.constant.Statics
import com.dazone.crewchatoff.database.AllUserDBHelper
import com.dazone.crewchatoff.database.ChatRoomDBHelper
import com.dazone.crewchatoff.dto.AttachDTO
import com.dazone.crewchatoff.dto.ChatRoomDTO
import com.dazone.crewchatoff.dto.ChattingDto
import com.dazone.crewchatoff.dto.ErrorDto
import com.dazone.crewchatoff.dto.NotificationBundleDto
import com.dazone.crewchatoff.fragment.ChattingFragment
import com.dazone.crewchatoff.fragment.CurrentChatListFragment
import com.dazone.crewchatoff.interfaces.OnGetChatRoom
import com.dazone.crewchatoff.utils.Constant
import com.dazone.crewchatoff.utils.CrewChatApplication
import com.dazone.crewchatoff.utils.Prefs
import com.dazone.crewchatoff.utils.TimeUtils
import com.dazone.crewchatoff.utils.Utils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import me.leolin.shortcutbadger.ShortcutBadger
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class FCMCloudMessage : FirebaseMessagingService() {

    private var isEnableN = false
    private var isEnableSound = false
    private var isEnableVibrate = false
    private var isEnableTime = false
    private var isPCVersion = false
    private lateinit var prefs: Prefs
    private lateinit var chattingDto: ChattingDto
    private var listTemp = AllUserDBHelper.getUser()
    private val channelId = "0011001"
    private val channelName = "CrewChat 0011001"
    private val channelIdNonSound = "0022002"
    private val channelNameNonSound = "CrewChat 0022002"
    private lateinit var channel1: NotificationChannel
    private lateinit var channel2:NotificationChannel
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "FCM onMessageReceived")
        prefs = CrewChatApplication.getInstance().prefs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel1 = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel2 = NotificationChannel(channelIdNonSound, channelNameNonSound, NotificationManager.IMPORTANCE_HIGH)
        }

        if (remoteMessage.data.isNotEmpty()) {
            val data: Map<String?, String?> = remoteMessage.data
            val code: Int? = data["Code"]?.toInt()
            prefs = CrewChatApplication.getInstance().prefs

            isEnableN = prefs.getBooleanValue(Statics.ENABLE_NOTIFICATION, true)
            isEnableSound = prefs.getBooleanValue(Statics.ENABLE_SOUND, true)
            isEnableVibrate = prefs.getBooleanValue(Statics.ENABLE_VIBRATE, true)
            isEnableTime = prefs.getBooleanValue(Statics.ENABLE_TIME, false)
            isPCVersion = prefs.getBooleanValue(Statics.ENABLE_NOTIFICATION_WHEN_USING_PC_VERSION, true)

            val hourStart: Int = prefs.getIntValue(Statics.TIME_HOUR_START_NOTIFICATION, 8)
            val minuteStart: Int = prefs.getIntValue(Statics.TIME_MINUTE_START_NOTIFICATION, 0)

            val hourEnd: Int = prefs.getIntValue(Statics.TIME_HOUR_END_NOTIFICATION, 18)
            val minuteEnd: Int = prefs.getIntValue(Statics.TIME_MINUTE_END_NOTIFICATION, 0)

            if (!isEnableN) {
                return
            } else {
                if (isEnableTime) {
                    if (!TimeUtils.isBetweenTime(hourStart, minuteStart, hourEnd, minuteEnd)) {
                        return
                    }
                }
            }

            when (code) {
                1 -> {
                    Log.d("FCM", "Receive new message")
                    receiveCode1(remoteMessage)
                }

                2 -> {
                    Log.d("FCM", "Case 2 Add Chat Room User ")
                    receiveCode2(remoteMessage)
                }

                3 -> {
                    Log.d("FCM", "Case 3 Delete Chat Room User ")
                    chatDeleteMember(remoteMessage)
                }

                4 -> Log.d("FCM", "Case 4 ###")
                5 -> {
                    Log.d("FCM", "Case 5 UpdateMessageUnreadCount")
                    receiveCode5(remoteMessage)
                }

                7,8 -> {
                    Log.d("FCM", "Case 8 Update RoomName")
                    receiveCode8(remoteMessage)
                }

                else -> Log.d("TAG", "Case 0 ###")
            }
        }
    }

    private fun receiveCode1(remoteMessage: RemoteMessage) {
        var data = remoteMessage.data
        if (data.containsKey("Data") && data.containsKey("UserNo")) {
            val userNo: Long? = data["UserNo"]?.toLong()
            if (userNo == Utils.getCurrentId().toLong()) {
                try {
                    val bundleDto = Gson().fromJson(data["Data"], NotificationBundleDto::class.java)
                    val time = bundleDto.strRegDate.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].substring(0, 5)
                    chattingDto = ChattingDto()
                    chattingDto.roomNo = bundleDto.roomNo
                    chattingDto.unreadTotalCount = bundleDto.unreadTotalCount
                    chattingDto.unReadCount = bundleDto.unreadTotalCount
                    chattingDto.message = bundleDto.message
                    chattingDto.messageNo = bundleDto.messageNo
                    chattingDto.writerUserNo = bundleDto.writeUserNo
                    chattingDto.attachNo = bundleDto.attachNo
                    chattingDto.attachFileName = bundleDto.attachFileName
                    chattingDto.attachFileType = bundleDto.attachFileType
                    chattingDto.attachFilePath = bundleDto.attachFilePath
                    chattingDto.attachFileSize = bundleDto.attachFileSize
                    chattingDto.regDate = bundleDto.regDate
                    chattingDto.strRegDate = bundleDto.strRegDate
                    val attachInfo = AttachDTO()
                    attachInfo.type = bundleDto.attachFileType
                    attachInfo.attachNo = bundleDto.attachNo
                    attachInfo.size = bundleDto.attachFileSize
                    attachInfo.fullPath = bundleDto.attachFilePath
                    chattingDto.attachInfo = attachInfo
                    chattingDto.lastedMsg = bundleDto.message
                    chattingDto.msgUserNo = bundleDto.writeUserNo
                    chattingDto.writerUser = bundleDto.writeUserNo
                    if (bundleDto.messageType == 3) {
                        chattingDto.type = 3
                    } else {
                        if (TextUtils.isEmpty(bundleDto.attachFilePath)) {
                            chattingDto.lastedMsgType = Statics.MESSAGE_TYPE_NORMAL
                            chattingDto.type = 0
                        } else {
                            chattingDto.lastedMsgType = Statics.MESSAGE_TYPE_ATTACH
                            chattingDto.type = 2
                        }
                    }
                    chattingDto.lastedMsgAttachType = bundleDto.attachFileType
                    val roomNo: Long = chattingDto.roomNo
                    val unreadCount = bundleDto.unreadTotalCountAtAll
                    if (unreadCount > 0) {
                        ShortcutBadger.applyCount(this, unreadCount) //for 1.1.4
                    } else {
                        ShortcutBadger.removeCount(this)
                    }
                    chattingDto.lastedMsgDate = bundleDto.regDate
                    Thread {
                        val intent = Intent(Statics.ACTION_RECEIVER_NOTIFICATION)
                        intent.putExtra(Statics.GCM_DATA_NOTIFICATOON, Gson().toJson(chattingDto))
                        intent.putExtra(Statics.GCM_NOTIFY, true)
                        intent.putExtra(Statics.CHATTING_DTO, chattingDto)
                        sendBroadcast(intent)
                        ChatRoomDBHelper.updateUnreadTotalCountChatRoom(roomNo, unreadCount.toLong())
                        ChatRoomDBHelper.updateChatRoom(chattingDto.roomNo, chattingDto.lastedMsg, chattingDto.lastedMsgType, chattingDto.lastedMsgAttachType, chattingDto.lastedMsgDate, chattingDto.unreadTotalCount, chattingDto.unReadCount, chattingDto.writerUserNo.toLong())
                    }.start()
                    if (chattingDto.writerUserNo != Utils.getCurrentId()) {
                        val myIntent = Intent(this, ChattingActivity::class.java)
                        myIntent.putExtra(Statics.CHATTING_DTO, chattingDto)
                        myIntent.putExtra(Constant.KEY_INTENT_ROOM_NO, roomNo)
                        val treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, chattingDto.writerUserNo)
                        val isShowNotification = bundleDto.isShowNotification
                        if (roomNo != CrewChatApplication.currentRoomNo) {
                            if (treeUserDTOTemp != null) {
                                if (isShowNotification) {
                                    var url = ""
                                    var name = ""
                                    for (u in listTemp) {
                                        if (u.userNo == chattingDto.writerUserNo) {
                                            url = Prefs().serverSite + u.avatarUrl
                                            name = u.name
                                            break
                                        }
                                    }
                                    if(!url.isNullOrEmpty()) {
                                        val bitmap: Bitmap? = getBitmapFromURL(url)
                                        if (chattingDto.type == 3) {
                                            val msg: Array<String> = chattingDto.message.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                            if (msg.size > 1) sendNotification(msg[1], msg[0], bitmap, myIntent, chattingDto.unreadTotalCount, roomNo, time) else sendNotification(chattingDto.message, name, bitmap, myIntent, chattingDto.unreadTotalCount, roomNo, time)
                                        } else sendNotification(chattingDto.message, name, bitmap, myIntent, chattingDto.unreadTotalCount, roomNo, time)
                                    }

                                }
                            } else {
                                if (isShowNotification) {
                                    sendNotification(chattingDto.message, "Crew Chat", null, myIntent, chattingDto.unreadTotalCount, roomNo, time)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun receiveCode2(remoteMessage: RemoteMessage) {
        try {
            if (remoteMessage.data.containsKey("Data")) {
                /** GET UserNo */
                val bundleDto = Gson().fromJson(remoteMessage.data["Data"], NotificationBundleDto::class.java)
                val roomNo = bundleDto.roomNo
                val intent = Intent(this, ChattingActivity::class.java)
                intent.putExtra(Constant.KEY_INTENT_ROOM_NO, roomNo)
                sendNotification(Utils.getString(R.string.notification_add_user),
                        Utils.getString(R.string.app_name),
                        null,
                        intent,
                        0,
                        roomNo, "")
                if (CurrentChatListFragment.fragment != null && CurrentChatListFragment.fragment.isActive || ChattingFragment.instance != null && ChattingFragment.instance.isActive) {
                    val intentBroadcast = Intent(Constant.INTENT_FILTER_ADD_USER)
                    intentBroadcast.putExtra(Constant.KEY_INTENT_ROOM_NO, roomNo)
                    sendBroadcast(intentBroadcast)
                } else if (CurrentChatListFragment.fragment != null) {
                    CurrentChatListFragment.fragment.isUpdate = true
                    CurrentChatListFragment.fragment.reloadDataSet()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun chatDeleteMember(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.containsKey("Data")) {
            try {
                /** Get RoomNo  */
                val bundleDto = Gson().fromJson(remoteMessage.data["Data"], NotificationBundleDto::class.java)
                val roomNo = bundleDto.roomNo

                /** Send Broadcast  */
                val intent = Intent(Constant.INTENT_FILTER_CHAT_DELETE_USER)
                intent.putExtra(Constant.KEY_INTENT_ROOM_NO, roomNo)
                sendBroadcast(intent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun receiveCode5(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.containsKey("Data")) {
            try {
                val bundleDto = Gson().fromJson(remoteMessage.data["Data"], NotificationBundleDto::class.java)
                val roomNo = bundleDto.roomNo
                var userNo: Long = 0
                if (remoteMessage.data.containsKey("UserNo")) {
                    userNo = remoteMessage.data["UserNo"]?.toLong()?: 0
                }
                val strBaseDate = bundleDto.strBaseDate
                val unReadTotalCount = bundleDto.unreadTotalCount
                val unReadTotalCountAtAll = bundleDto.unreadTotalCountAtAll
                if (unReadTotalCountAtAll > 0) {
                    ShortcutBadger.applyCount(this, unReadTotalCountAtAll) //for 1.1.4
                } else {
                    ShortcutBadger.removeCount(this)
                }

                //Constant.cancelAllNotification(CrewChatApplication.getInstance(), (int) roomNo);
                val intent = Intent(Constant.INTENT_FILTER_GET_MESSAGE_UNREAD_COUNT)
                intent.putExtra(Constant.KEY_INTENT_ROOM_NO, roomNo)
                intent.putExtra(Constant.KEY_INTENT_UNREAD_TOTAL_COUNT, unReadTotalCount)
                intent.putExtra(Constant.KEY_INTENT_USER_NO, userNo)
                intent.putExtra(Constant.KEY_INTENT_BASE_DATE, strBaseDate)
                sendBroadcast(intent)
                if (CurrentChatListFragment.fragment != null) {
                    val flag = CurrentChatListFragment.fragment.active()
                    if (!flag) {
                        CurrentChatListFragment.fragment.updateReadMsgWhenOnPause(roomNo, unReadTotalCount, userNo)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun receiveCode8(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.containsKey("Data")) {
            try {
                val bundleDto = Gson().fromJson(remoteMessage.data["Data"], NotificationBundleDto::class.java)
                val roomNo = bundleDto.roomNo
                HttpRequest.getInstance().GetChatRoom(roomNo, object : OnGetChatRoom {
                    override fun OnGetChatRoomSuccess(chatRoomDTO: ChatRoomDTO) {
                        Thread { ChatRoomDBHelper.updateChatRoom(roomNo, chatRoomDTO.roomTitle.trim { it <= ' ' }) }.start()
                        val intent = Intent(Constant.INTENT_FILTER_UPDATE_ROOM_NAME)
                        intent.putExtra(Statics.ROOM_NO, roomNo)
                        intent.putExtra(Statics.ROOM_TITLE, chatRoomDTO.roomTitle.trim { it <= ' ' })
                        sendBroadcast(intent)
                    }

                    override fun OnGetChatRoomFail(errorDto: ErrorDto) {}
                })
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun sendNotification(msg: String, title: String, avatarUrl: Bitmap?, intent: Intent, unReadCount: Int, roomNo: Long, createTime: String) {
        if (!isPCVersion) {
            return
        }

        var str = msg
        // Add Bundle
        intent.putExtra(Statics.CHATTING_DTO, chattingDto)

        if (chattingDto.attachNo != 0) {
            str = Utils.getString(R.string.notification_file) + chattingDto.attachFileName
            chattingDto.type = 2
            chattingDto.attachInfo = AttachDTO()
            chattingDto.attachInfo.attachNo = chattingDto.attachNo
            chattingDto.attachInfo.type = chattingDto.attachFileType
            chattingDto.attachInfo.fullPath = chattingDto.attachFilePath
            chattingDto.attachInfo.fileName = chattingDto.attachFileName
            chattingDto.attachInfo.size = chattingDto.attachFileSize
        } else {
            if (TextUtils.isEmpty(msg)) {
                str = Utils.getString(R.string.notification_add_user)
            }
        }


        val flag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this,
            System.currentTimeMillis().toInt(), intent, flag)

        val manager = NotificationManagerCompat.from(this)
        val NOTIFICATION_CHANNEL_ID = getString(R.string.default_notification_channel_id)

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.small_icon_chat)
            .setAutoCancel(true)
            .setContentText(str)
            .setContentTitle(title)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)

        avatarUrl?.let {
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(it)
            )
                .setLargeIcon(it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            var modifiedChannel = manager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
            if (modifiedChannel == null) {
                modifiedChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    CHANNEL_NAME_DEFAULT,
                    importance
                ).apply { description = CHANNEL_DESCRIPTION_DEFAULT
                    setShowBadge(false)}
                modifiedChannel.enableVibration(true)
                modifiedChannel.vibrationPattern = longArrayOf(200, 300)
                manager.createNotificationChannel(modifiedChannel)
            }
        }

        // Check notification setting and config notification

        val vibrate = longArrayOf(1000, 1000, 0, 0, 0)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (isEnableSound) {
            builder.setSound(soundUri)
        } else {
            builder.setSound(null)
        }

        if (isEnableVibrate) {
            builder.setVibrate(vibrate)
            val v = CrewChatApplication.getInstance().applicationContext.getSystemService(
                VIBRATOR_SERVICE
            ) as Vibrator
            v.vibrate(500)
        } else {
            val noVibrate = longArrayOf(0, 0, 0, 0, 0)
            builder.setVibrate(noVibrate)
            val v = CrewChatApplication.getInstance().applicationContext.getSystemService(
                VIBRATOR_SERVICE
            ) as Vibrator
            v.vibrate(0)
        }

        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun getTickerText(total: Int): String {
        val result: String = when (total) {
            1 -> "$total New Message"
            else -> "$total New Messages"
        }
        return result
    }

    private fun getBitmapFromURL(strURL: String?): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        const val CHANNEL_NAME_DEFAULT = "SMBM"
        const val CHANNEL_DESCRIPTION_DEFAULT = "smbm.description"
    }
}