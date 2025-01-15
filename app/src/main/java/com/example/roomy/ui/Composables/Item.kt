package com.example.roomy.ui

import android.content.res.Resources.Theme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.roomy.R
import com.example.roomy.db.data.Item

@Composable
fun Item(
    item: Item,
    updateItem: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.1f)
            .background(MaterialTheme.colorScheme.primary)
            .padding(bottom = 4.dp)
            .clickable { updateItem() },
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painterResource(item.icon?: R.drawable.placeholder),
            contentDescription = "Letter",
            Modifier.weight(1f)
        )
        Text(text = item.name, color = Color.White)
    }
}