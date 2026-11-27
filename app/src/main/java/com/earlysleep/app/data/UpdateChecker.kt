package com.earlysleep.app.data

import com.earlysleep.app.BuildConfig
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

const val GITHUB_REPO_URL = "https://github.com/Bais233/early-sleep"

sealed interface UpdateCheckResult {
    data class Available(val version: String, val releaseUrl: String) : UpdateCheckResult
    data object UpToDate : UpdateCheckResult
    data class Failed(val message: String) : UpdateCheckResult
}

object UpdateChecker {

    private const val API_URL =
        "https://api.github.com/repos/Bais233/early-sleep/releases/latest"

    suspend fun checkForUpdate(): UpdateCheckResult = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            connection = (URL(API_URL).openConnection() as HttpURLConnection).apply {
                connectTimeout = 8_000
                readTimeout = 8_000
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", "EarlySleep-Android")
            }
            when (val code = connection.responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    val body = connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }
                    val json = JSONObject(body)
                    val latestVersion = json.optString("tag_name")
                        .trim()
                        .removePrefix("v")
                    val releaseUrl = json.optString("html_url")
                    when {
                        latestVersion.isBlank() -> UpdateCheckResult.Failed("未获取到版本信息")
                        isNewer(latestVersion, BuildConfig.VERSION_NAME) ->
                            UpdateCheckResult.Available(latestVersion, releaseUrl)

                        else -> UpdateCheckResult.UpToDate
                    }
                }

                HttpURLConnection.HTTP_NOT_FOUND ->
                    UpdateCheckResult.Failed("无法访问仓库，仓库可能尚未公开")

                HttpURLConnection.HTTP_FORBIDDEN ->
                    UpdateCheckResult.Failed("请求过于频繁，请稍后再试")

                else -> UpdateCheckResult.Failed("检查失败（HTTP $code）")
            }
        } catch (e: Exception) {
            UpdateCheckResult.Failed("网络错误：${e.message ?: "请稍后重试"}")
        } finally {
            connection?.disconnect()
        }
    }

    private fun isNewer(latest: String, current: String): Boolean {
        fun parse(version: String): List<Int> =
            version.substringBefore('-')
                .split('.')
                .mapNotNull { it.toIntOrNull() }

        val latestParts = parse(latest)
        val currentParts = parse(current)
        for (index in 0 until maxOf(latestParts.size, currentParts.size)) {
            val latestPart = latestParts.getOrElse(index) { 0 }
            val currentPart = currentParts.getOrElse(index) { 0 }
            if (latestPart != currentPart) return latestPart > currentPart
        }
        return false
    }
}
