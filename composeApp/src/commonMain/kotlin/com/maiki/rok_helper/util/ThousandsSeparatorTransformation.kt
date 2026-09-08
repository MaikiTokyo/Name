package com.maiki.rok_helper.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class ThousandsSeparatorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val out = StringBuilder()
        for (i in originalText.indices) {
            out.append(originalText[i])
            val remainingDigits = originalText.length - 1 - i
            if (remainingDigits > 0 && remainingDigits % 3 == 0) {
                out.append('.')
            }
        }

        val transformedText = out.toString()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                var transformedOffset = 0
                var originalOffset = 0
                while (originalOffset < offset && transformedOffset < transformedText.length) {
                    if (transformedText[transformedOffset] != '.') {
                        originalOffset++
                    }
                    transformedOffset++
                }
                return transformedOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                var transformedOffset = 0
                var originalOffset = 0
                while (transformedOffset < offset && transformedOffset < transformedText.length) {
                    if (transformedText[transformedOffset] != '.') {
                        originalOffset++
                    }
                    transformedOffset++
                }
                return originalOffset
            }
        }

        return TransformedText(AnnotatedString(transformedText), offsetMapping)
    }
}
