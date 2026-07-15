package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ampereflow_prefs", Context.MODE_PRIVATE)

    // AOD Settings
    private val _alwaysOnDisplayActive = MutableStateFlow(prefs.getBoolean("aod_active", true))
    val alwaysOnDisplayActive: StateFlow<Boolean> = _alwaysOnDisplayActive.asStateFlow()

    private val _clockStyle = MutableStateFlow(prefs.getString("clock_style", "6:10") ?: "6:10")
    val clockStyle: StateFlow<String> = _clockStyle.asStateFlow()

    private val _speedometerStyle = MutableStateFlow(prefs.getString("speedometer_style", "Full Arc") ?: "Full Arc")
    val speedometerStyle: StateFlow<String> = _speedometerStyle.asStateFlow()

    private val _speedometerColor = MutableStateFlow(prefs.getString("speedometer_color", "Green") ?: "Green")
    val speedometerColor: StateFlow<String> = _speedometerColor.asStateFlow()

    private val _speedometerBrightness = MutableStateFlow(prefs.getFloat("speedometer_brightness", 0.8f))
    val speedometerBrightness: StateFlow<Float> = _speedometerBrightness.asStateFlow()

    private val _showBatteryDetails = MutableStateFlow(prefs.getBoolean("show_battery_details", true))
    val showBatteryDetails: StateFlow<Boolean> = _showBatteryDetails.asStateFlow()

    private val _showWatts = MutableStateFlow(prefs.getBoolean("show_watts", true))
    val showWatts: StateFlow<Boolean> = _showWatts.asStateFlow()

    private val _percentageAsPrimary = MutableStateFlow(prefs.getBoolean("percentage_primary", true))
    val percentageAsPrimary: StateFlow<Boolean> = _percentageAsPrimary.asStateFlow()

    private val _showMoreInfo = MutableStateFlow(prefs.getBoolean("show_more_info", true))
    val showMoreInfo: StateFlow<Boolean> = _showMoreInfo.asStateFlow()

    private val _showMediaPlayer = MutableStateFlow(prefs.getBoolean("show_media_player", false))
    val showMediaPlayer: StateFlow<Boolean> = _showMediaPlayer.asStateFlow()

    private val _showNotifications = MutableStateFlow(prefs.getBoolean("show_notifications", false))
    val showNotifications: StateFlow<Boolean> = _showNotifications.asStateFlow()

    private val _torchEnabled = MutableStateFlow(prefs.getBoolean("torch_enabled", true))
    val torchEnabled: StateFlow<Boolean> = _torchEnabled.asStateFlow()

    private val _cameraEnabled = MutableStateFlow(prefs.getBoolean("camera_enabled", true))
    val cameraEnabled: StateFlow<Boolean> = _cameraEnabled.asStateFlow()

    private val _dimScreenAfter = MutableStateFlow(prefs.getString("dim_screen_after", "Never") ?: "Never")
    val dimScreenAfter: StateFlow<String> = _dimScreenAfter.asStateFlow()

    private val _hideScreenAfter = MutableStateFlow(prefs.getString("hide_screen_after", "Never") ?: "Never")
    val hideScreenAfter: StateFlow<String> = _hideScreenAfter.asStateFlow()

    private val _doubleTapToExit = MutableStateFlow(prefs.getBoolean("double_tap_exit", true))
    val doubleTapToExit: StateFlow<Boolean> = _doubleTapToExit.asStateFlow()

    private val _oledBurnInProtection = MutableStateFlow(prefs.getBoolean("oled_burn_in_protect", true))
    val oledBurnInProtection: StateFlow<Boolean> = _oledBurnInProtection.asStateFlow()

    private val _dateFormat = MutableStateFlow(prefs.getString("date_format", "Tue, Sept 7") ?: "Tue, Sept 7")
    val dateFormat: StateFlow<String> = _dateFormat.asStateFlow()

    private val _is24Hour = MutableStateFlow(prefs.getBoolean("is_24_hour", false))
    val is24Hour: StateFlow<Boolean> = _is24Hour.asStateFlow()

    // General App Settings
    private val _theme = MutableStateFlow(prefs.getString("theme", "Follow system") ?: "Follow system")
    val theme: StateFlow<String> = _theme.asStateFlow()

    private val _appLanguage = MutableStateFlow(prefs.getString("app_language", "English") ?: "English")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    private val _temperatureUnit = MutableStateFlow(prefs.getString("temperature_unit", "Celsius") ?: "Celsius")
    val temperatureUnit: StateFlow<String> = _temperatureUnit.asStateFlow()

    private val _dualCellBatteryMode = MutableStateFlow(prefs.getBoolean("dual_cell_mode", false))
    val dualCellBatteryMode: StateFlow<Boolean> = _dualCellBatteryMode.asStateFlow()

    private val _batteryChargingLimit = MutableStateFlow(prefs.getInt("charging_limit", 80))
    val batteryChargingLimit: StateFlow<Int> = _batteryChargingLimit.asStateFlow()

    private val _batteryChargingLimitEnabled = MutableStateFlow(prefs.getBoolean("charging_limit_enabled", false))
    val batteryChargingLimitEnabled: StateFlow<Boolean> = _batteryChargingLimitEnabled.asStateFlow()

    private val _batteryDischargingLimit = MutableStateFlow(prefs.getInt("discharging_limit", 20))
    val batteryDischargingLimit: StateFlow<Int> = _batteryDischargingLimit.asStateFlow()

    private val _batteryDischargingLimitEnabled = MutableStateFlow(prefs.getBoolean("discharging_limit_enabled", false))
    val batteryDischargingLimitEnabled: StateFlow<Boolean> = _batteryDischargingLimitEnabled.asStateFlow()

    private val _batteryTemperatureLimit = MutableStateFlow(prefs.getInt("temp_limit", 40))
    val batteryTemperatureLimit: StateFlow<Int> = _batteryTemperatureLimit.asStateFlow()

    private val _batteryTemperatureLimitEnabled = MutableStateFlow(prefs.getBoolean("temp_limit_enabled", false))
    val batteryTemperatureLimitEnabled: StateFlow<Boolean> = _batteryTemperatureLimitEnabled.asStateFlow()

    private val _alarmSound = MutableStateFlow(prefs.getString("alarm_sound", "Off") ?: "Off")
    val alarmSound: StateFlow<String> = _alarmSound.asStateFlow()

    private val _notificationFormat = MutableStateFlow(prefs.getString("notification_format", "Standard") ?: "Standard")
    val notificationFormat: StateFlow<String> = _notificationFormat.asStateFlow()

    private val _customMaxCapacity = MutableStateFlow(prefs.getInt("custom_max_capacity", 4181))
    val customMaxCapacity: StateFlow<Int> = _customMaxCapacity.asStateFlow()

    // Saver methods
    fun setAlwaysOnDisplayActive(value: Boolean) {
        prefs.edit().putBoolean("aod_active", value).apply()
        _alwaysOnDisplayActive.value = value
    }

    fun setClockStyle(value: String) {
        prefs.edit().putString("clock_style", value).apply()
        _clockStyle.value = value
    }

    fun setSpeedometerStyle(value: String) {
        prefs.edit().putString("speedometer_style", value).apply()
        _speedometerStyle.value = value
    }

    fun setSpeedometerColor(value: String) {
        prefs.edit().putString("speedometer_color", value).apply()
        _speedometerColor.value = value
    }

    fun setSpeedometerBrightness(value: Float) {
        prefs.edit().putFloat("speedometer_brightness", value).apply()
        _speedometerBrightness.value = value
    }

    fun setShowBatteryDetails(value: Boolean) {
        prefs.edit().putBoolean("show_battery_details", value).apply()
        _showBatteryDetails.value = value
    }

    fun setShowWatts(value: Boolean) {
        prefs.edit().putBoolean("show_watts", value).apply()
        _showWatts.value = value
    }

    fun setPercentageAsPrimary(value: Boolean) {
        prefs.edit().putBoolean("percentage_primary", value).apply()
        _percentageAsPrimary.value = value
    }

    fun setShowMoreInfo(value: Boolean) {
        prefs.edit().putBoolean("show_more_info", value).apply()
        _showMoreInfo.value = value
    }

    fun setShowMediaPlayer(value: Boolean) {
        prefs.edit().putBoolean("show_media_player", value).apply()
        _showMediaPlayer.value = value
    }

    fun setShowNotifications(value: Boolean) {
        prefs.edit().putBoolean("show_notifications", value).apply()
        _showNotifications.value = value
    }

    fun setTorchEnabled(value: Boolean) {
        prefs.edit().putBoolean("torch_enabled", value).apply()
        _torchEnabled.value = value
    }

    fun setCameraEnabled(value: Boolean) {
        prefs.edit().putBoolean("camera_enabled", value).apply()
        _cameraEnabled.value = value
    }

    fun setDimScreenAfter(value: String) {
        prefs.edit().putString("dim_screen_after", value).apply()
        _dimScreenAfter.value = value
    }

    fun setHideScreenAfter(value: String) {
        prefs.edit().putString("hide_screen_after", value).apply()
        _hideScreenAfter.value = value
    }

    fun setDoubleTapToExit(value: Boolean) {
        prefs.edit().putBoolean("double_tap_exit", value).apply()
        _doubleTapToExit.value = value
    }

    fun setOledBurnInProtection(value: Boolean) {
        prefs.edit().putBoolean("oled_burn_in_protect", value).apply()
        _oledBurnInProtection.value = value
    }

    fun setDateFormat(value: String) {
        prefs.edit().putString("date_format", value).apply()
        _dateFormat.value = value
    }

    fun setIs24Hour(value: Boolean) {
        prefs.edit().putBoolean("is_24_hour", value).apply()
        _is24Hour.value = value
    }

    fun setTheme(value: String) {
        prefs.edit().putString("theme", value).apply()
        _theme.value = value
    }

    fun setAppLanguage(value: String) {
        prefs.edit().putString("app_language", value).apply()
        _appLanguage.value = value
    }

    fun setTemperatureUnit(value: String) {
        prefs.edit().putString("temperature_unit", value).apply()
        _temperatureUnit.value = value
    }

    fun setDualCellBatteryMode(value: Boolean) {
        prefs.edit().putBoolean("dual_cell_mode", value).apply()
        _dualCellBatteryMode.value = value
    }

    fun setBatteryChargingLimit(value: Int) {
        prefs.edit().putInt("charging_limit", value).apply()
        _batteryChargingLimit.value = value
    }

    fun setBatteryChargingLimitEnabled(value: Boolean) {
        prefs.edit().putBoolean("charging_limit_enabled", value).apply()
        _batteryChargingLimitEnabled.value = value
    }

    fun setBatteryDischargingLimit(value: Int) {
        prefs.edit().putInt("discharging_limit", value).apply()
        _batteryDischargingLimit.value = value
    }

    fun setBatteryDischargingLimitEnabled(value: Boolean) {
        prefs.edit().putBoolean("discharging_limit_enabled", value).apply()
        _batteryDischargingLimitEnabled.value = value
    }

    fun setBatteryTemperatureLimit(value: Int) {
        prefs.edit().putInt("temp_limit", value).apply()
        _batteryTemperatureLimit.value = value
    }

    fun setBatteryTemperatureLimitEnabled(value: Boolean) {
        prefs.edit().putBoolean("temp_limit_enabled", value).apply()
        _batteryTemperatureLimitEnabled.value = value
    }

    fun setAlarmSound(value: String) {
        prefs.edit().putString("alarm_sound", value).apply()
        _alarmSound.value = value
    }

    fun setNotificationFormat(value: String) {
        prefs.edit().putString("notification_format", value).apply()
        _notificationFormat.value = value
    }

    fun setCustomMaxCapacity(value: Int) {
        prefs.edit().putInt("custom_max_capacity", value).apply()
        _customMaxCapacity.value = value
    }
}
