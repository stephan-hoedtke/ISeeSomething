package com.stho.isee.core

import java.util.*

class Entry(
    id: Int,
    category: String,
    title: String,
    url: String,
    user: String,
    password: String,
    description: String,
    created: Calendar,
    modified: Calendar) {

    enum class Status {
        NEW,
        CLEAN,
        MODIFIED,
    }

    var status = Status.CLEAN
        private set

    var id: Int = id
        private set

    var category: String = category
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    var title: String = title
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    var user: String = user
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    var url: String = url
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    var password: String = password
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    var description: String = description
        set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    var created: Calendar = created
        private set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    var modified: Calendar = modified
        private set(value) {
            if (field != value) {
                field = value
                touch()
            }
        }

    val isNew: Boolean
        get() = (status == Status.NEW)

    val isModified: Boolean
        get() = (status != Status.CLEAN)

    private fun touch() {
        modified = Calendar.getInstance()
        status = Status.MODIFIED
    }

    private fun clear() {
        status = Status.CLEAN
    }

    fun clone(): Entry =
        Entry(id, category, title, url, user, password, description, created, modified)

    fun restore(entry: Entry) {
        this.id = entry.id
        this.category = entry.category
        this.title = entry.title
        this.url = entry.url
        this.user = entry.user
        this.password = entry.password
        this.created = entry.created
        this.modified = entry.modified
        this.status = Status.CLEAN
    }


    companion object {
        fun createFromDB(
            id: Int,
            category: String,
            title: String,
            url: String,
            user: String,
            password: String,
            description: String,
            created: Calendar,
            modified: Calendar,
        ) = Entry(id, category, title, url, user, password, description, created, modified).apply {
            status = Status.CLEAN
        }

        fun createNew(
            category: String = "",
            title: String = "",
            url: String = "",
            user: String = "",
            password: String = "",
            description: String = "",
        ) = Entry(0, category, title, url, user, password, description, created = Calendar.getInstance(), modified = Calendar.getInstance()).apply {
            status = Status.NEW
        }
    }
}


