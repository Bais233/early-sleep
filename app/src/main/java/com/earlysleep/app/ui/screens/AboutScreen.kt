package com.earlysleep.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earlysleep.app.BuildConfig
import com.earlysleep.app.data.GITHUB_REPO_URL
import com.earlysleep.app.data.UpdateCheckResult
import com.earlysleep.app.data.UpdateChecker
import com.earlysleep.app.ui.components.SleepCard
import kotlinx.coroutines.launch

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    val darkTheme = isSystemInDarkTheme()
    val versionName = BuildConfig.VERSION_NAME
    val scope = rememberCoroutineScope()

    var checking by remember { mutableStateOf(false) }
    var updateResult by remember { mutableStateOf<UpdateCheckResult?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "返回",
                    tint = scheme.onBackground
                )
            }
            Text(
                text = "关于",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = scheme.onBackground
            )
        }

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(scheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (darkTheme) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                    contentDescription = null,
                    tint = scheme.primary,
                    modifier = Modifier.size(38.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "早点睡",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = scheme.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "v$versionName",
                fontSize = 13.sp,
                color = scheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "23:00 前入睡，是对明天最好的投资",
                fontSize = 12.sp,
                color = scheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(24.dp))

        SleepCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { openUrl(context, GITHUB_REPO_URL) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.primary.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Code,
                        contentDescription = null,
                        tint = scheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "GitHub 仓库",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = scheme.onBackground
                    )
                    Text(
                        text = GITHUB_REPO_URL.removePrefix("https://"),
                        fontSize = 12.sp,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                    contentDescription = "打开仓库",
                    tint = scheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        SleepCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.secondary.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SystemUpdate,
                        contentDescription = null,
                        tint = scheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "检查更新",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = scheme.onBackground
                    )
                    Text(
                        text = "当前版本 v$versionName",
                        fontSize = 12.sp,
                        color = scheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Button(
                onClick = {
                    if (!checking) {
                        checking = true
                        updateResult = null
                        scope.launch {
                            updateResult = UpdateChecker.checkForUpdate()
                            checking = false
                        }
                    }
                },
                enabled = !checking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = scheme.secondary,
                    contentColor = scheme.onSecondary,
                    disabledContainerColor = scheme.surfaceVariant,
                    disabledContentColor = scheme.onSurfaceVariant
                )
            ) {
                if (checking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = scheme.onSecondary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = "检查中…", fontSize = 14.sp)
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(text = "检查更新", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            when (val result = updateResult) {
                is UpdateCheckResult.Available -> {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "发现新版本 v${result.version}",
                            fontSize = 13.sp,
                            color = scheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                        TextButton(onClick = { openUrl(context, result.releaseUrl) }) {
                            Text(text = "去下载", fontSize = 12.sp, color = scheme.primary)
                        }
                    }
                }

                is UpdateCheckResult.UpToDate -> {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "已是最新版本",
                        fontSize = 13.sp,
                        color = scheme.secondary
                    )
                }

                is UpdateCheckResult.Failed -> {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = result.message,
                        fontSize = 12.sp,
                        color = scheme.tertiary
                    )
                }

                null -> Unit
            }
        }

        Spacer(Modifier.height(16.dp))

        SleepCard {
            Text(
                text = "应用信息",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = scheme.onBackground
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "版本 v$versionName · 最低支持 Android 9（API 28）",
                fontSize = 12.sp,
                color = scheme.onSurfaceVariant
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = scheme.outline.copy(alpha = 0.25f)
            )
            Text(
                text = "检查更新时会访问 GitHub 公开 API，不收集、不上传任何个人数据。打卡与设置只保存在本机。",
                fontSize = 12.sp,
                color = scheme.onSurfaceVariant,
                lineHeight = 19.sp
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "早点睡 · 23:00 必须入睡",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 11.sp,
            color = scheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private fun openUrl(context: Context, url: String) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
