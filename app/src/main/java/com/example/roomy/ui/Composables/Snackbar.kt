package com.example.roomy.ui.Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex


@Composable
fun Snackbar(snackbarData: SnackbarData, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.padding(horizontal = 20.dp).fillMaxWidth().zIndex(50f)
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.error).fillMaxWidth() // Apply the background here
                .clip(RoundedCornerShape(8.dp)) // Ensure the background respects the rounded corners
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Snackbar message
                Text(
                    text = snackbarData.visuals.message,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}