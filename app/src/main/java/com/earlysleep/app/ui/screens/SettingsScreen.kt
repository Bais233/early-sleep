package com.earlysleep.app.ui.screens

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.earlysleep.app.BuildConfig
import com.earlysleep.app.alarm.SleepAlarmScheduler
import com.earlysleep.app.ui.components.SleepCard
import java.time.LocalDate

@Composable
fun SettingsScreen(
    remindersEnabled: Boolean,
    checkInCount: Int,
    onToggleReminder: (Boolean) -> Unit,
    onClearCheckIns: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    var refresh by remember { mutableIntStateOf(0) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { refresh++ }

    val notificationsGranted = remember(refresh) { hasNotificationPermission(context) }
    val exactAlarmGranted = remember(refresh) { canScheduleExactAlarms(context) }
    var showClearDialog by remember { mutableStateOf(false) }

    val nextTrigger = SleepAlarmScheduler.nextTrigger()
    val nextDayLabel = if (nextTrigger.toLocalDate() == LocalDate.now()) "今天" else "明天"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        Text(
            text = "设置",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = scheme.onBackground
        )
        Spacer(Modifier.height(20.dp))

        SectionLabel("提醒")
        SleepCard(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)) {
            SettingRow(
                icon = Icons.Rounded.NotificationsActive,
                title = "每日 23:00 提醒",
                subtitle = if (remindersEnabled) "下次提醒：$nextDayLabel 23:00" else "提醒已关闭"
            ) {
                Switch(
                    checked = remindersEnabled,
                    onCheckedChange = onToggleReminder,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = scheme.onSecondary,
                        checkedTrackColor = scheme.secondary,
                        uncheckedThumbColor = scheme.onSurfaceVariant,
                        uncheckedTrackColor = scheme.surfaceVariant,
                        uncheckedBorderColor = scheme.outline
                    )
                )
            }

            HorizontalDivider(color = scheme.outline.copy(alpha = 0.25f))

            SettingRow(
                icon = Icons.Rounded.Notifications,
                title = "通知权限",
                subtitle = if (notificationsGranted) "已开启，可收到入睡提醒" else "未开启，到点收不到提醒"
            ) {
                if (notificationsGranted) {
                    StatusPill("已开启", scheme.secondary)
                } else {
                    TextButton(onClick = { openNotificationSettings(context) }) {
                        Text(text = "去开启", fontSize = 12.sp, color = scheme.tertiary)
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                HorizontalDivider(color = scheme.outline.copy(alpha = 0.25f))

                SettingRow(
                    icon = Icons.Rounded.Alarm,
                    title = "精确闹钟权限",
                    subtitle = if (exactAlarmGranted) "已开启，23:00 准时提醒" else "未开启时提醒可能延迟几分钟"
                ) {
                    if (exactAlarmGranted) {
                        StatusPill("已开启", scheme.secondary)
                    } else {
                        TextButton(onClick = { openExactAlarmSettings(context) }) {
                            Text(text = "去开启", fontSize = 12.sp, color = scheme.tertiary)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        SectionLabel("数据")
        SleepCard(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)) {
            SettingRow(
                icon = Icons.Rounded.DeleteOutline,
                title = "清空打卡记录",
                subtitle = "共 $checkInCount 条记录"
            ) {
                TextButton(
                    onClick = { showClearDialog = true },
                    enabled = checkInCount > 0
                ) {
                    Text(
                        text = "清空",
                        fontSize = 12.sp,
                        color = if (checkInCount > 0) scheme.tertiary else scheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        SectionLabel("关于")
        SleepCard(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)) {
            SettingRow(
                icon = Icons.Rounded.Info,
                title = "关于早点睡",
                subtitle = "版本 ${BuildConfig.VERSION_NAME} · 支持 Android 9 及以上",
                onClick = onOpenAbout
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = scheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "23:00 前入睡，是对明天最好的投资。",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp,
            color = scheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = scheme.surfaceVariant,
            title = { Text(text = "清空打卡记录？", color = scheme.onBackground) },
            text = { Text(text = "所有早睡打卡记录将被删除，且无法恢复。", color = scheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearCheckIns()
                        showClearDialog = false
                    }
                ) {
                    Text(text = "清空", color = scheme.tertiary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(text = "取消", color = scheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit = {},
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(scheme.primary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = scheme.primary,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = scheme.onBackground
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        trailing()
    }
}

@Composable
private fun StatusPill(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

private fun hasNotificationPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

private fun canScheduleExactAlarms(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
    val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return false
    return alarmManager.canScheduleExactAlarms()
}

private fun openAppDetails(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:${context.packageName}")
    )
    runCatching { context.startActivity(intent) }
}

private fun openNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    runCatching { context.startActivity(intent) }
        .onFailure { openAppDetails(context) }
}

private fun openExactAlarmSettings(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    val intent = Intent(
        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
        Uri.parse("package:${context.packageName}")
    )
    runCatching { context.startActivity(intent) }
        .onFailure { openAppDetails(context) }
}
