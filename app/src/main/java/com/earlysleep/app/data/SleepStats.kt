package com.earlysleep.app.data

import java.time.LocalDate

/**
 * 连续早睡天数：从今天（今天未打卡则从昨天）开始向前连续统计。
 */
fun calculateStreak(checkIns: Set<LocalDate>, today: LocalDate = LocalDate.now()): Int {
    var cursor = if (checkIns.contains(today)) today else today.minusDays(1)
    var streak = 0
    while (checkIns.contains(cursor)) {
        streak += 1
        cursor = cursor.minusDays(1)
    }
    return streak
}
