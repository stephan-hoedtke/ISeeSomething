package com.stho.isee.storage

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(indices = [Index(value = ["id"])], tableName = "secrets")
class Secret (
    @PrimaryKey(autoGenerate = true)
    var id: Long,

    @NonNull
    @ColumnInfo(name = "value")
    var value: String,
)

