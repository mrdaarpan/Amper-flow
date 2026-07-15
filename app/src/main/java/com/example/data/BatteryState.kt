package com.example.data

data class BatteryState(
    val level: Int = 50,
    val percentageDecimal: Float = 50.0f,
    val voltageMv: Int = 4000,
    val currentMa: Int = 1500,
    val wattageW: Float = 6.0f,
    val temperatureC: Float = 35.0f,
    val healthPercent: Int = 95,
    val pluggedState: String = "AC",
    val chargeStatus: String = "Charging",
    val maxCapacityMah: Int = 4181,
    val estimatedCapacityMah: Int = 3970,
    val isMonitoringActive: Boolean = true,
    // Historical trends for the line charts
    val voltageHistory: List<Float> = emptyList(),
    val currentHistory: List<Float> = emptyList(),
    val wattageHistory: List<Float> = emptyList(),
    val temperatureHistory: List<Float> = emptyList(),
    // Charging Speed stats
    val avgSpeedW: Float = 5.0f,
    val peakSpeedW: Float = 16.0f,
    val chargeTime80Min: Int = 123, // 2h 3m
    val chargeTime100Min: Int = 162 // 2h 42m
)
