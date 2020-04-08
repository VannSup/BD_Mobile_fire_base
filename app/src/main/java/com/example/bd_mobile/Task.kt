package com.example.bd_mobile

data class Task(
    val firebaseId: String = "",
    var name: String = "",
    var isChecked: Boolean = false
)