package com.example.bd_mobile.data.model

data class Task(
    val firebaseId: String = "",
    var name: String = "",
    var isChecked: Boolean = false,
    var createdAt: Long = 0,
    var updatedAt: Long = 0
)