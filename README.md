# 早点睡 · EarlySleep

一个以「23:00 必须入睡」为核心的安卓睡前提醒 App。当前版本聚焦入睡提醒与打卡，采用底部导航的多页面结构，后续可以在「今晚 / 记录 / 设置」之外继续扩展新功能。

- 仓库：https://github.com/Bais233/early-sleep
- 下载：https://github.com/Bais233/early-sleep/releases

## 功能

- **每日 23:00 精确提醒**：使用 `AlarmManager.setExactAndAllowWhileIdle` 触发高优先级通知，到点提醒放下手机。
- **智能跳过**：当天已经打卡「已入睡」后，23:00 不再重复提醒。
- **通知栏快捷操作**：收到提醒后可直接点「已入睡」打卡，或「再等 10 分钟」稍后提醒。
- **自动恢复**：重启手机、修改时间/时区、应用更新后自动恢复每日提醒；每次打开 App 也会校正闹钟。
- **首页倒计时**：实时倒计时到 23:00，超时后切换为超时状态提醒。
- **早睡记录**：一键打卡、连续早睡天数、最近 7 天视图、打卡历史。
- **深浅色跟随系统**：自动适配系统深色/浅色模式，首页太阳/月亮图标同步切换。
- **关于与检查更新**：二级页面展示仓库地址，可检查 GitHub Release 最新版本。
- **权限引导**：通知权限、精确闹钟权限状态检测与跳转设置。
- **本地数据**：设置和打卡记录通过 DataStore 保存在本机，不上传。

## 界面结构

App 不是单一页面，目前已搭好多页面框架：

| 页面 | 说明 |
| --- | --- |
| 今晚（首页） | 23:00 倒计时、今日打卡、提醒开关、睡眠小贴士 |
| 记录 | 连续早睡天数、最近 7 天打卡、历史记录 |
| 设置 | 提醒开关、权限状态、清空记录、关于入口 |
| 关于（二级页） | 版本信息、GitHub 仓库地址、检查更新 |

新增功能时，在 `AppDestination` 中补一个枚举项，并在 `EarlySleepRoot` 的 `NavHost` 中注册新页面即可。

## 技术栈

- Kotlin 2.1 + Jetpack Compose（Material 3）
- Navigation Compose 底部导航
- DataStore Preferences 本地持久化
- AlarmManager 精确闹钟 + BroadcastReceiver
- NotificationCompat 高优先级通知
- HttpURLConnection 调用 GitHub Releases API 检查更新
- 单 Activity + ViewModel

## 兼容性

- **最低支持 Android 9（API 28）**
- compileSdk / targetSdk 35
- 在 Android 9 上通知无需运行时权限；Android 13+ 会请求通知权限
- Android 12+ 未授予精确闹钟权限时会降级为非精确闹钟，并在设置页提示
- 检查更新需要网络与公开的 GitHub 仓库；私有仓库会提示无法访问

## 构建

用 Android Studio 直接打开项目，或命令行构建：

```bash
# 需要 JDK 17、Android SDK（platform 35、build-tools 35.0.0）
./gradlew assembleDebug
```

产物：`app/build/outputs/apk/debug/app-debug.apk`

### Release 构建

根目录的 `keystore.properties` 用于配置签名（已被 `.gitignore` 忽略，不会入库）：

```properties
storeFile=keystore/earlysleep-release.jks
storePassword=你的密码
keyAlias=earlysleep
keyPassword=你的密码
```

```bash
./gradlew assembleRelease
```

产物：`app/build/outputs/apk/release/app-release.apk`（已开启 R8 混淆与资源裁剪）

安装到手机（开启 USB 调试）：

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 项目结构

```text
app/src/main/java/com/earlysleep/app/
├── MainActivity.kt              # 单 Activity 入口
├── EarlySleepApp.kt             # Application，创建通知渠道
├── alarm/
│   ├── SleepAlarmScheduler.kt   # 23:00 每日闹钟 / 10 分钟稍后提醒
│   ├── SleepReminderReceiver.kt # 闹钟触发、打卡、稍后提醒
│   └── BootReceiver.kt          # 开机、改时间后恢复闹钟
├── notifications/
│   └── SleepNotifications.kt    # 通知渠道与提醒通知
├── data/
│   ├── SleepPrefs.kt            # DataStore 设置与打卡数据
│   ├── SleepStats.kt            # 连续早睡天数计算
│   └── UpdateChecker.kt         # GitHub Release 版本检查
└── ui/
    ├── EarlySleepRoot.kt        # Scaffold + 底部导航 + NavHost
    ├── SleepViewModel.kt        # 页面状态与提醒调度
    ├── components/              # 星空背景、卡片
    ├── screens/                 # 今晚 / 记录 / 设置 / 关于
    └── theme/                   # 深色 / 浅色主题（跟随系统）
```

## 后续规划

- 白噪音 / 助眠音乐
- 睡眠时长记录与周报
- 睡前专注模式（到点自动静音、限制娱乐 App）
- 桌面小组件
- 可自定义入睡时间（当前固定 23:00）

## 更新日志

### v1.0.1

- 深浅色主题跟随系统设置，首页太阳/月亮图标自动切换
- 新增「关于」二级页面：版本信息、GitHub 仓库地址、检查更新
- 当天已打卡后，23:00 不再重复提醒
- 新增 `INTERNET` 权限，仅用于检查 GitHub Release

### v1.0.0

- 首个版本：23:00 精确提醒、通知栏打卡/稍后提醒、早睡记录

## 说明

- 除「检查更新」访问 GitHub 公开 API 外，应用不进行任何网络请求；打卡与设置只保存在本机。
- 提醒依赖系统通知与精确闹钟，请勿在系统设置中限制本应用后台运行，否则可能收不到提醒。
