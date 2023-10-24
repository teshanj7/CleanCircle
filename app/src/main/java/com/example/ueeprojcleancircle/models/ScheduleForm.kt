package com.example.ueeprojcleancircle.models

data class ScheduleForm(
    var nic: String? = null,
    var email: String? = null,
    var wasteType: String? = null,
    val estimatedWeight: String? = null,
    val date: String?,
    var remarks: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var status: String? = null
){
    // Add an empty no-argument constructor
    constructor() : this("", "", "", "", "", "", 0.0, 0.0, "")
}

