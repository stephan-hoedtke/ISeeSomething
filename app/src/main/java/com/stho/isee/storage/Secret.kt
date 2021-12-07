package com.stho.isee.storage

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(indices = [Index(value = ["name"])])
data class Secret (
    @PrimaryKey val id: Int,
    val value: String,
)

