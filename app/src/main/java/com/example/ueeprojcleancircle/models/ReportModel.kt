package com.example.ueeprojcleancircle.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ReportModel(
    val fullName: String,
    val estimateWeight: String,
    val reportCategory: String,
    val reportDatePicker: String,
    val reportImage: String? = "",
    val remarks: String
) {
    // Add an empty no-argument constructor
    constructor() : this("", "", "", "", "", "")
}


