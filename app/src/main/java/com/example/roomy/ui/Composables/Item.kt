package com.example.roomy.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.roomy.R
import com.example.roomy.db.data.Item

@SuppressLint("ResourceType")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Item(
    item: Item,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    context: Context,
    color: Color = MaterialTheme.colorScheme.primary

) {

    val maxFontSize = 16

    val fontSize = if (item.name.length > 20) {
        maxFontSize - (item.name.length / 20)
    } else {
        maxFontSize
    }

    var resourceId = item.icon

    if (resourceId == null && item.name[0].isLetter()) {

        resourceId = context.resources.getIdentifier(
            item.name[0].lowercase(),
            "drawable",
            context.packageName
        )

        if (resourceId == 0 ) {
            resourceId = R.drawable.placeholder2
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.1f)
            .background(color)
            .padding(bottom = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // val iconId = item.icon ?: R.drawable.placeholder
        val iconId = R.drawable.placeholder


        Icon(
            painterResource(resourceId ?: R.drawable.placeholder2),
            contentDescription = "Letter",
            Modifier
                .weight(1f)
                .size(64.dp)
        )
        Text(
            modifier = Modifier.padding(horizontal = 3.dp),
            text = item.name,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = fontSize.sp,
            style = TextStyle(
                lineHeight = 14.sp,
            )
        )
    }

}
