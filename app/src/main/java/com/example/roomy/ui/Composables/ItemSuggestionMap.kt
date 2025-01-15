package com.example.roomy.ui.Composables

import com.example.roomy.R

object ItemSuggestionMap {

    val nameToIconId: HashMap<String, Int> = hashMapOf(
        "placeholder" to R.drawable.letter_a,
        "banana" to R.drawable.banana,
        "cheese" to R.drawable.cheese,
        "cherry" to R.drawable.cherry,
        "cola" to R.drawable.cola,
        "eggs" to R.drawable.eggs,
        "milk" to R.drawable.milk,
        "steak" to R.drawable.steak,

        )
}

val suggestedItems = listOf("banana", "cheese", "cola", "milk", "cherry", "eggs", "steak", "unknown")
