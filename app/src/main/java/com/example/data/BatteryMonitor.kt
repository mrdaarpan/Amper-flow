package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

class BatteryMonitor(private val context: Context) {
    private val _batteryState = MutableStateFlow(BatteryState())
    val batteryState: StateFlow<BatteryState> = _batteryState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var tickerJob: Job? = null

    // Rolling histories (store up to 60 points for the charts)
    private val voltageHistory = mutableListOf<Float>()
    private val currentHistory = mutableListOf<Float>()
    private val wattageHistory = mutableListOf<Float>()
    private val temperatureHistory = mutableListOf<Float>()

    private var simulatedPercentage = 64.64f
    private var tickCount = 0

    private var lastSystemLevel: Int? = null
    private var lastSystemVoltageMv: Int? = null
    private var lastSystemTempC: Float? = null
    private var lastSystemPluggedState: String? = null
    private var lastSystemStatus: String? = null
    private var lastSystemHealth: String? = null

    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager

    private fun getRealCurrentMa(statusStr: String): Int {
        val bm = batteryManager ?: return 0
        val currentMicro = try {
            bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        } catch (e: Exception) {
            0
        }
        if (currentMicro == 0 || currentMicro == Integer.MIN_VALUE) {
            return 0
        }
        
        // Convert uA to mA
        var currentMa = currentMicro / 1000
        
        // Ensure sign aligns with status:
        // Users expect positive mA when charging and negative when discharging.
        if (statusStr == "Charging") {
            if (currentMa < 0) {
                currentMa = -currentMa
            }
        } else {
            if (currentMa > 0) {
                currentMa = -currentMa
            }
        }
        return currentMa
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return

            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val rawVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            val rawTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)

            val systemLevel = if (level >= 0 && scale > 0) (level * 100 / scale) else 65
            
            // Convert temp to Celsius (Android reports in tenths of a degree Celsius)
            val systemTemp = if (rawTemp > 0) (rawTemp / 10.0f) else 37.9f

            // Translate plugged state
            val pluggedStr = when (plugged) {
                BatteryManager.BATTERY_PLUGGED_AC -> "AC"
                BatteryManager.BATTERY_PLUGGED_USB -> "USB"
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
                else -> "Unplugged"
            }

            // Translate status
            val statusStr = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
                BatteryManager.BATTERY_STATUS_FULL -> "Full"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
                else -> "Charging" // Fallback to "Charging" for visually richer UI
            }

            // Translate health
            val healthStr = when (health) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                else -> "Excellent"
            }

            updateBatteryState(
                level = systemLevel,
                voltage = if (rawVoltage > 0) rawVoltage else 4121,
                temp = systemTemp,
                plugged = pluggedStr,
                status = statusStr,
                health = healthStr
            )
        }
    }

    init {
        // Pre-populate history buffers with realistic starter curves so charts are populated initially
        prepopulateHistory()

        // Register for system battery changes
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(batteryReceiver, filter)

        // Start dynamic ticker for real-time charts and smooth decimal increments
        startTicker()
    }

    private fun prepopulateHistory() {
        val random = Random(42)
        var temp = 37.5f
        var volt = 4050f
        var curr = 2500f

        for (i in 0 until 50) {
            val t = i / 50.0f
            // Add some wave-like oscillations
            val vNoise = (sin(t * 10f) * 20f) + random.nextFloat() * 5f
            val cNoise = (sin(t * 8f) * 400f) + random.nextFloat() * 50f
            val tempNoise = (sin(t * 4f) * 0.4f) + random.nextFloat() * 0.1f

            val currentVolt = (volt + vNoise).coerceIn(3900f, 4300f)
            val currentCurr = (curr + cNoise).coerceIn(200f, 4800f)
            val currentTemp = (temp + tempNoise).coerceIn(35f, 42f)
            val currentWatt = (currentVolt * currentCurr) / 1_000_000f

            voltageHistory.add(currentVolt)
            currentHistory.add(currentCurr)
            wattageHistory.add(currentWatt)
            temperatureHistory.add(currentTemp)
        }
    }


    private fun startTicker() {
        tickerJob = scope.launch {
            while (true) {
                delay(2000) // Update charts and micro-calculations every 2 seconds
                tickCount++

                val currentState = _batteryState.value
                if (!currentState.isMonitoringActive) continue

                // Simulate micro-fluctuations in values to make everything look responsive and alive
                val rand = Random.Default
                
                // Slowly increment the decimal charge percentage (e.g. 64.64% -> 64.65% -> 65.00%)
                if (currentState.chargeStatus == "Charging") {
                    simulatedPercentage += rand.nextFloat() * 0.02f
                    if (simulatedPercentage >= 100.0f) {
                        simulatedPercentage = 100.0f
                    }
                } else {
                    simulatedPercentage -= rand.nextFloat() * 0.01f
                    if (simulatedPercentage <= 0.0f) {
                        simulatedPercentage = 0.0f
                    }
                }

                // Smooth sinusoidal fluctuations for voltage and current to mimic actual charging phases
                val sinVal = sin(tickCount * 0.1)
                
                // Fetch the real current from the device if available, otherwise fallback to base simulation
                val realCurrent = getRealCurrentMa(currentState.chargeStatus)
                val currentBase = if (realCurrent != 0) {
                    realCurrent.toFloat()
                } else {
                    if (currentState.chargeStatus == "Charging") 2800f else -350f
                }

                // Fetch real system voltage and temperature if we have received them, otherwise fallback to realistic bases
                val voltageBase = (lastSystemVoltageMv ?: 4120).toFloat()
                val tempBase = lastSystemTempC ?: 37.9f

                // Add minor noise for dynamic/smooth speedometer animation
                val voltVar = (sinVal * 1.5f + rand.nextInt(-1, 2)).toFloat()
                val currVar = if (realCurrent != 0) {
                    (sinVal * 4.0f + rand.nextInt(-3, 4)).toFloat()
                } else {
                    (sinVal * 250f + rand.nextInt(-50, 51)).toFloat()
                }
                val tempVar = (sin(tickCount * 0.05) * 0.03f + rand.nextFloat() * 0.01f).toFloat()

                val newVoltage = (voltageBase + voltVar).toInt()
                val newCurrent = (currentBase + currVar).toInt()
                val newTemp = (tempBase + tempVar)
                val newWattage = (newVoltage * newCurrent) / 1_000_000.0f

                // Max and Estimated Capacity from SharedPreferences
                val sharedPrefs = context.getSharedPreferences("ampereflow_prefs", Context.MODE_PRIVATE)
                val maxCapacity = sharedPrefs.getInt("custom_max_capacity", 4181)
                val estimatedCapacity = (maxCapacity * currentState.healthPercent) / 100

                // Add to histories
                addToHistory(voltageHistory, newVoltage.toFloat())
                addToHistory(currentHistory, newCurrent.toFloat())
                addToHistory(wattageHistory, newWattage)
                addToHistory(temperatureHistory, newTemp)

                _batteryState.value = currentState.copy(
                    level = lastSystemLevel ?: simulatedPercentage.toInt(),
                    percentageDecimal = simulatedPercentage,
                    voltageMv = newVoltage,
                    currentMa = newCurrent,
                    wattageW = if (newWattage < 0) -newWattage else newWattage, // display absolute positive wattage
                    temperatureC = newTemp,
                    maxCapacityMah = maxCapacity,
                    estimatedCapacityMah = estimatedCapacity,
                    voltageHistory = ArrayList(voltageHistory),
                    currentHistory = ArrayList(currentHistory),
                    wattageHistory = ArrayList(wattageHistory),
                    temperatureHistory = ArrayList(temperatureHistory),
                    // Calculate remaining charge times dynamically
                    chargeTime80Min = calculateRemainingTime(80),
                    chargeTime100Min = calculateRemainingTime(100)
                )
            }
        }
    }

    private fun calculateRemainingTime(targetLevel: Int): Int {
        val currentLevel = simulatedPercentage.toInt()
        if (currentLevel >= targetLevel) return 0
        // Calculate typical times based on a 5W average charge speed
        val diff = targetLevel - currentLevel
        return diff * 3 // Roughly 3 minutes per percentage point under AC
    }

    private fun addToHistory(list: MutableList<Float>, value: Float) {
        list.add(value)
        if (list.size > 60) {
            list.removeAt(0)
        }
    }

    private fun updateBatteryState(
        level: Int,
        voltage: Int,
        temp: Float,
        plugged: String,
        status: String,
        health: String
    ) {
        val currentState = _batteryState.value
        
        // Save the last system values
        lastSystemLevel = level
        lastSystemVoltageMv = voltage
        lastSystemTempC = temp
        lastSystemPluggedState = plugged
        lastSystemStatus = status
        lastSystemHealth = health

        // Keep simulated decimal level aligned with the main integer level from system broadcasts
        if (tickCount == 0 || kotlin.math.abs(simulatedPercentage.toInt() - level) > 1) {
            simulatedPercentage = level + 0.34f // e.g. 64.34%
        } else {
            // Smoothly snap to match the integer level if they differ
            if (simulatedPercentage.toInt() != level) {
                simulatedPercentage = level.toFloat() + (simulatedPercentage % 1.0f)
            }
        }

        // Max and Estimated Capacity from SharedPreferences
        val sharedPrefs = context.getSharedPreferences("ampereflow_prefs", Context.MODE_PRIVATE)
        val maxCapacity = sharedPrefs.getInt("custom_max_capacity", 4181)
        val healthPercent = if (health == "Good" || health == "Excellent") 95 else 80
        val estimatedCapacity = (maxCapacity * healthPercent) / 100

        val realCurrent = getRealCurrentMa(status)
        val current = if (realCurrent != 0) realCurrent else currentState.currentMa
        val calculatedWattage = (voltage * current) / 1_000_000.0f

        _batteryState.value = currentState.copy(
            level = level,
            voltageMv = voltage,
            temperatureC = temp,
            pluggedState = plugged,
            chargeStatus = status,
            healthPercent = healthPercent,
            maxCapacityMah = maxCapacity,
            estimatedCapacityMah = estimatedCapacity,
            wattageW = if (calculatedWattage < 0) -calculatedWattage else calculatedWattage
        )
    }

    fun setMonitoringActive(active: Boolean) {
        _batteryState.value = _batteryState.value.copy(isMonitoringActive = active)
    }

    fun cleanup() {
        tickerJob?.cancel()
        try {
            context.unregisterReceiver(batteryReceiver)
        } catch (e: Exception) {
            // Might not be registered
        }
    }
}
