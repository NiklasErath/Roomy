package com.example.roomy.ui.Composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.roomy.ui.States.GroupMembersUiState

@Composable
fun UserProfileCirclesStacked(groupMemberInformation: GroupMembersUiState, textColor: Color = Color.Black) {

    val circleColors = listOf(
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.surfaceContainer,

    )


    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        val maxVisible = 3
        val memberCount = groupMemberInformation.memberInformation.size
        val extraMembers = memberCount - maxVisible

        Row(
            modifier = Modifier.wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            groupMemberInformation.memberInformation.take(maxVisible)
                .forEachIndexed { index, groupMembers ->
                    Box(
                        modifier = Modifier
                            .offset(x = (-index * 12).dp)
                            .zIndex((maxVisible - index).toFloat())
                    ) {
                        val circleColor = circleColors[index % circleColors.size]

                        UserProfileCircle(
                            groupMembers.username,
                            28.dp,
                            circleColor
                        )
                    }
                }
        }

        if (extraMembers > 0) {
            Text(
                color = textColor,
                text = "+$extraMembers",
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }

}