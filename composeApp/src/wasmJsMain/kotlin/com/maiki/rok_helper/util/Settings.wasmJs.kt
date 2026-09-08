package com.maiki.rok_helper.util

import kotlinx.browser.localStorage

actual fun createSettings(): Settings = WasmSettings()

class WasmSettings : Settings {
    override fun getString(key: String, defaultValue: String): String {
        return localStorage.getItem(key) ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        localStorage.setItem(key, value)
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return localStorage.getItem(key)?.toLongOrNull() ?: defaultValue
    }

    override fun putLong(key: String, value: Long) {
        localStorage.setItem(key, value.toString())
    }
}
