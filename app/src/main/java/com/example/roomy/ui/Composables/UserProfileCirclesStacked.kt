package com.example.roomy.ui.Composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.roomy.ui.States.GroupMembersUiState

@Composable
fun UserProfileCirclesStacked(groupMemberInformation: GroupMembersUiState,  ) {

    Box(
    Modifier.width(96.dp),
    contentAlignment = Alignment.Center // Ensures content is centered vertically in the Box
    ) {
        val maxVisible = 3
        val memberCount = groupMemberInformation.memberInformation.size
        val extraMembers = memberCount - maxVisible

        // Create a Row to stack the UserProfileCircles without affecting the space between
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Create stacked user profile circles
            groupMemberInformation.memberInformation.take(maxVisible)
                .forEachIndexed { index, groupMembers ->
                    Box(
                        modifier = Modifier
                            .offset(x = (-index * 12).dp) // Adjust the offset to stack the circles
                            .zIndex((maxVisible - index).toFloat()) // Ensure the first circle is on top
                    ) {
                        UserProfileCircle(
                            groupMembers.username,
                            28.dp,
                            Color.Blue
                        )
                    }

                }
        }

        // Ensure vertical centering of the +n text inside the Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
//                                .background(Color.Green),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically // This ensures the text is vertically centered
        ) {
            // Display the extra members count (+n) if needed
            if (extraMembers > 0) {
                Text(
                    text = "+$extraMembers",
                    modifier = Modifier,
                )
            }
        }
    }
}