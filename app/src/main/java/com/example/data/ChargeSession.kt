package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "charge_sessions")
data class ChargeSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: Long,
    val durationMinutes: Int,
    val startBatteryLevel: Int,
    val endBatteryLevel: Int,
    val peakWattage: Float,
    val peakCurrent: Float,
    val averageTemperature: Float,
    val chargerType: String
)
