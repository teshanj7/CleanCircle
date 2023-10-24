package com.example.ueeprojcleancircle.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ReportModel(
    val fullName: String?,
    val estimateWeight: String?,
    val reportCategory: String?,
    val reportDatePicker: String?,
    val reportImage: String? = "",
    val remarks: String?,
    val status: String?,
    val latitude: Double,
    val longitude: Double
) {
    // Add an empty no-argument constructor
    constructor() : this("", "", "", "", "", "","Open", 0.0, 0.0)
}


