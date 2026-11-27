package com.earlysleep.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earlysleep.app.data.calculateStreak
import com.earlysleep.app.ui.components.SleepCard
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun RecordsScreen(checkIns: Set<LocalDate>) {
    val scheme = MaterialTheme.colorScheme
    val today = LocalDate.now()
    val streak = calculateStreak(checkIns, today)
    val lastSevenDays = remember(today) { (6 downTo 0).map { today.minusDays(it.toLong()) } }
    val recentCheckIns = remember(checkIns) {
        checkIns.sortedWith(compareByDescending { it.toEpochDay() }).take(30)
    }
    val monthCheckIns = checkIns.count { it.year == today.year && it.month == today.month }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("M月d日 EEEE", Locale.CHINA) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        Text(
            text = "早睡记录",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = scheme.onBackground
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "每一个早睡的夜晚，都在为明天充电",
            fontSize = 13.sp,
            color = scheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        SleepCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "连续早睡", fontSize = 13.sp, color = scheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$streak",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = scheme.secondary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "天",
                            fontSize = 14.sp,
                            color = scheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Rounded.LocalFireDepartment,
                    contentDescription = null,
                    tint = scheme.secondary,
                    modifier = Modifier.size(40.dp)
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = scheme.outline.copy(alpha = 0.3f)
            )
            Text(
                text = "本月已打卡 $monthCheckIns 天 · 累计 ${checkIns.size} 天",
                fontSize = 12.sp,
                color = scheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(16.dp))

        SleepCard {
            Text(
                text = "最近 7 天",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = scheme.onBackground
            )
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                lastSevenDays.forEach { date ->
                    val checked = checkIns.contains(date)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = weekdayLabel(date.dayOfWeek),
                            fontSize = 11.sp,
                            color = if (date == today) scheme.secondary else scheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (checked) scheme.secondary else scheme.onSurface.copy(alpha = 0.05f)
                                )
                                .then(
                                    if (date == today) {
                                        Modifier.border(1.dp, scheme.primary, CircleShape)
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (checked) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = scheme.onSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = "${date.dayOfMonth}",
                                    fontSize = 12.sp,
                                    color = scheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "打卡历史",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = scheme.onBackground,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        if (recentCheckIns.isEmpty()) {
            SleepCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NightsStay,
                        contentDescription = null,
                        tint = scheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "还没有打卡记录",
                        fontSize = 14.sp,
                        color = scheme.onBackground
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "今晚 23:00 前入睡，点下「今晚已入睡」",
                        fontSize = 12.sp,
                        color = scheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            SleepCard(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)) {
                recentCheckIns.forEachIndexed { index, date ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(scheme.primary.copy(alpha = 0.10f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bedtime,
                                contentDescription = null,
                                tint = scheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = date.format(dateFormatter),
                            fontSize = 14.sp,
                            color = scheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "已打卡",
                            fontSize = 12.sp,
                            color = scheme.secondary
                        )
                    }
                    if (index != recentCheckIns.lastIndex) {
                        HorizontalDivider(color = scheme.outline.copy(alpha = 0.25f))
                    }
                }
            }
        }
    }
}

private fun weekdayLabel(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
    DayOfWeek.MONDAY -> "周一"
    DayOfWeek.TUESDAY -> "周二"
    DayOfWeek.WEDNESDAY -> "周三"
    DayOfWeek.THURSDAY -> "周四"
    DayOfWeek.FRIDAY -> "周五"
    DayOfWeek.SATURDAY -> "周六"
    DayOfWeek.SUNDAY -> "周日"
}
