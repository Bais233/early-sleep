package com.earlysleep.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earlysleep.app.alarm.SleepAlarmScheduler
import com.earlysleep.app.data.calculateStreak
import com.earlysleep.app.ui.components.SleepCard
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay

private val SleepTips = listOf(
    "睡前 1 小时放下手机，蓝光会推迟褪黑素分泌。",
    "卧室温度保持在 18–22℃，更容易进入深睡眠。",
    "下午 3 点后少喝咖啡，咖啡因在体内要代谢 6 小时左右。",
    "每天固定时间上床，生物钟会越来越准时。",
    "睡前用温水泡脚 10 分钟，帮身体慢慢放松下来。",
    "睡前把明天要做的事写下来，大脑更容易安静。"
)

@Composable
fun HomeScreen(
    remindersEnabled: Boolean,
    checkIns: Set<LocalDate>,
    onToggleReminder: (Boolean) -> Unit,
    onCheckIn: () -> Unit,
) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    val darkTheme = isSystemInDarkTheme()
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(1_000L)
        }
    }

    val today = now.toLocalDate()
    val checkedInToday = checkIns.contains(today)
    val streak = calculateStreak(checkIns, today)

    val bedtime = today.atTime(SleepAlarmScheduler.BEDTIME_HOUR, SleepAlarmScheduler.BEDTIME_MINUTE)
    val overdue = !now.isBefore(bedtime)
    val secondsLeft = if (overdue) {
        Duration.between(bedtime, now).seconds
    } else {
        Duration.between(now, bedtime).seconds
    }
    val countdown = String.format(
        Locale.US,
        "%02d:%02d:%02d",
        secondsLeft / 3600,
        secondsLeft % 3600 / 60,
        secondsLeft % 60
    )

    val eveningStart = today.atTime(18, 0)
    val eveningSeconds = Duration.between(eveningStart, bedtime).seconds.toFloat()
    val progress = if (overdue) {
        1f
    } else {
        (Duration.between(eveningStart, now).seconds / eveningSeconds).coerceIn(0f, 1f)
    }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("M月d日 EEEE", Locale.CHINA) }
    val greeting = when (now.hour) {
        in 0..4 -> "夜深了"
        in 5..10 -> "早上好"
        in 11..17 -> "下午好"
        in 18..22 -> "晚上好"
        else -> "该睡觉了"
    }
    val tip = remember(today) { SleepTips[(today.toEpochDay() % SleepTips.size).toInt()] }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = greeting,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = scheme.onBackground
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = now.format(dateFormatter),
                    fontSize = 13.sp,
                    color = scheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(scheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (darkTheme) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                    contentDescription = if (darkTheme) "当前为深色模式" else "当前为浅色模式",
                    tint = scheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(256.dp)) {
                val strokeWidth = 12.dp.toPx()
                val inset = strokeWidth / 2f
                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                drawArc(
                    color = scheme.onSurface.copy(alpha = 0.08f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.sweepGradient(listOf(scheme.secondary, scheme.primary, scheme.secondary)),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (overdue) "已超过建议入睡时间" else "距离 23:00 入睡",
                    fontSize = 13.sp,
                    color = scheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = countdown,
                    fontSize = 42.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (overdue) scheme.tertiary else scheme.onBackground,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (overdue) "放下手机，立刻去睡" else "今晚也要在 23:00 前入睡",
                    fontSize = 12.sp,
                    color = if (overdue) scheme.tertiary else scheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(scheme.onSurface.copy(alpha = 0.06f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (remindersEnabled) scheme.secondary else scheme.onSurfaceVariant)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (remindersEnabled) "每日 23:00 提醒已开启" else "每日提醒未开启",
                    fontSize = 12.sp,
                    color = if (remindersEnabled) scheme.secondary else scheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = {
                if (!checkedInToday) {
                    onCheckIn()
                    Toast.makeText(context, "打卡成功，晚安好梦", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !checkedInToday,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = scheme.secondary,
                contentColor = scheme.onSecondary,
                disabledContainerColor = scheme.surfaceVariant,
                disabledContentColor = scheme.onSurfaceVariant
            )
        ) {
            Icon(
                imageVector = if (checkedInToday) Icons.Rounded.CheckCircle else Icons.Rounded.Bedtime,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (checkedInToday) "今晚已打卡" else "今晚已入睡",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = when {
                checkedInToday -> "连续早睡 $streak 天，继续保持"
                streak > 0 -> "已连续早睡 $streak 天，今晚别断签"
                else -> "从今晚开始，养成 23:00 入睡的习惯"
            },
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = scheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        SleepCard(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(scheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NotificationsActive,
                        contentDescription = null,
                        tint = scheme.primary,
                        modifier = Modifier.size(21.dp)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "每日入睡提醒",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = scheme.onBackground
                    )
                    Text(
                        text = if (remindersEnabled) "每天 23:00 · 已开启" else "每天 23:00 · 已关闭",
                        fontSize = 12.sp,
                        color = scheme.onSurfaceVariant
                    )
                }
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
        }

        Spacer(Modifier.height(16.dp))

        SleepCard {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.secondary.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TipsAndUpdates,
                        contentDescription = null,
                        tint = scheme.secondary,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "睡眠小贴士",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = scheme.secondary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = tip,
                        fontSize = 12.sp,
                        color = scheme.onSurfaceVariant,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}
