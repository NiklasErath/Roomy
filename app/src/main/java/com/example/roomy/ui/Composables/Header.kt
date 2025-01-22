package com.example.roomy.ui.Composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roomy.ui.Screens
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.States.GroupState
import okhttp3.internal.notifyAll


@Composable
fun Header(
    navController: NavController,
    currentDestination: String?,
    group: GroupState
) {

    val maxFontSize = 28

    val fontSize = if (group.groupName.length > 16) {
        maxFontSize - (group.groupName.length / 3)
    } else {
        maxFontSize
    }

    Column(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .padding(top = 40.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentDestination == "Groups") {
                    IconButton(
                        onClick = { navController.navigate(Screens.Home.name) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }

                    Text(
                        text = group.groupName,
                        fontSize = fontSize.sp,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .clickable { navController.navigate(Screens.GroupMembers.name) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        UserProfileCirclesStacked(GroupMembersUiState(group.groupMembers))
                    }
                } else if (currentDestination == "GroupMembers" || currentDestination == "RecipeSuggestion") {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )


                    }
                    Text(
                        text = group.groupName,
                        fontSize = fontSize.sp,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .clickable {
                                if (currentDestination != Screens.GroupMembers.name) {
                                    navController.navigate(Screens.GroupMembers.name)
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        UserProfileCirclesStacked(GroupMembersUiState(group.groupMembers))
                    }
                } else {
                    Text(
                        text = group.groupName,
                        fontSize = fontSize.sp,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Start
                    )

                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .clickable { navController.navigate(Screens.GroupMembers.name) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        UserProfileCirclesStacked(GroupMembersUiState(group.groupMembers))
                    }
                }
            }
        }
    }
}
