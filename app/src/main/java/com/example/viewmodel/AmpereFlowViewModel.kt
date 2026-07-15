package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppPreferencesRepository
import com.example.data.BatteryMonitor
import com.example.data.BatteryState
import com.example.data.ChargeSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AmpereFlowViewModel(application: Application) : AndroidViewModel(application) {

    // Preferences & Database DAOs
    val preferences = AppPreferencesRepository(application)
    private val db = AppDatabase.getDatabase(application)
    private val chargeSessionDao = db.chargeSessionDao()

    // Real-time Battery Monitor
    private val batteryMonitor = BatteryMonitor(application)
    val batteryState: StateFlow<BatteryState> = batteryMonitor.batteryState

    // Historical Charge Cycles Flow
    val chargeHistory: StateFlow<List<ChargeSession>> = chargeSessionDao.getAllSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Seed some highly-detailed, beautiful, realistic mock sessions if history is empty
        viewModelScope.launch {
            chargeSessionDao.getAllSessions().collect { list ->
                if (list.isEmpty()) {
                    seedMockHistory()
                }
            }
        }
    }

    private suspend fun seedMockHistory() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L

        val session1 = ChargeSession(
            startTime = now - (dayMs * 0.1).toLong(),
            durationMinutes = 45,
            startBatteryLevel = 20,
            endBatteryLevel = 85,
            peakWattage = 18.5f,
            peakCurrent = 4250f,
            averageTemperature = 36.8f,
            chargerType = "AC (Wall)"
        )

        val session2 = ChargeSession(
            startTime = now - (dayMs * 1.2).toLong(),
            durationMinutes = 110,
            startBatteryLevel = 15,
            endBatteryLevel = 100,
            peakWattage = 9.8f,
            peakCurrent = 2100f,
            averageTemperature = 34.2f,
            chargerType = "Wireless (Pad)"
        )

        chargeSessionDao.insertSession(session1)
        chargeSessionDao.insertSession(session2)
    }

    // Toggle active monitoring
    fun toggleMonitoring() {
        val isCurrentlyActive = batteryState.value.isMonitoringActive
        val nextState = !isCurrentlyActive
        batteryMonitor.setMonitoringActive(nextState)
        preferences.setAlwaysOnDisplayActive(nextState)
    }

    fun addCurrentStateToHistory() {
        viewModelScope.launch {
            val state = batteryState.value
            val session = ChargeSession(
                startTime = System.currentTimeMillis(),
                durationMinutes = (10..40).random(),
                startBatteryLevel = (15..30).random(),
                endBatteryLevel = state.level,
                peakWattage = state.wattageW + 2.5f,
                peakCurrent = state.currentMa.toFloat() + 200f,
                averageTemperature = state.temperatureC,
                chargerType = state.pluggedState
            )
            chargeSessionDao.insertSession(session)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            chargeSessionDao.clearHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        batteryMonitor.cleanup()
    }
}
