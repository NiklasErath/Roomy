package com.example.roomy.ui.Composables

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.roomy.R
import com.example.roomy.db.data.Item
import com.example.roomy.ui.Screens

@Composable
fun AddGroupButton(
    onClick: () -> Unit

) {

    Box(
        Modifier
            .fillMaxSize()
            .zIndex(20F)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(vertical = 40.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .size(80.dp) // Adjusted size for better padding visibility
                    .border(
                        border = BorderStroke(
                            3.dp, Brush.linearGradient(
                                colors = listOf(Color.Cyan, Color.Magenta, Color.Yellow),
                                start = Offset(0f, 0f),
                                end = Offset(100f, 100f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        onClick()

                    }

//                    .padding(10.dp)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Group", Modifier.size(40.dp))

            }

        }
    }

}