package com.maiki.rok_helper.util

import kotlinx.browser.window

actual fun openUrl(url: String) {
    window.open(url, "_blank")
}
