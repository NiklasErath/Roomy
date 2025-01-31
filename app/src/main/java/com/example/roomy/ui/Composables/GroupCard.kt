package com.example.roomy.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roomy.R
import com.example.roomy.db.data.GroupInformation
import com.example.roomy.ui.Composables.UserProfileCirclesStacked
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.States.GroupState
import com.example.roomy.ui.ViewModels.GroupViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupCard(
    groupViewModel: GroupViewModel,
    currentGroup: GroupState,
    navController: NavController,
){

    OutlinedCard(
        modifier = Modifier // set the current group by clicking on a group card
            //navigate to manageMember Screen on long click
            .combinedClickable(
                onClick = {

                    groupViewModel.setCurrentGroupInformation(GroupInformation(id = currentGroup.groupId, creatorId = currentGroup.creatorId, name = currentGroup.groupName))
                    navController.navigate(Screens.Groups.name)
                },
                onLongClick = {
                    groupViewModel.setCurrentGroupInformation(GroupInformation(id = currentGroup.groupId, creatorId = currentGroup.creatorId, name = currentGroup.groupName))
                    navController.navigate(Screens.GroupMembers.name)
                }
            )
            .fillMaxWidth(),

        ) {
        Column (Modifier.fillMaxWidth() .background(Color.White).padding(12.dp).defaultMinSize(minHeight = 120.dp)
            , verticalArrangement = Arrangement.SpaceBetween) {

            Row(
                Modifier
                    .fillMaxWidth()
                , horizontalArrangement = Arrangement.Start
            ) {

                Text(text = " ${currentGroup.groupName}" , fontSize = integerResource(id = R.integer.heading2).sp, color = Color.Black)
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )

            Row(
                Modifier
                    .fillMaxWidth()
                , horizontalArrangement = Arrangement.Start
            ) {

                if(currentGroup.shoppingListItems.size != -1){
                    Box(
                        modifier = Modifier
                            .padding(8.dp) // Adds padding around the Box
                            .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(30.dp)) // Rounded shape
                            .padding(horizontal = 8.dp, vertical = 2.dp), // Padding inside the Box
                        contentAlignment = Alignment.Center // Centers the text inside the Box
                    ) {
                        // Conditional text rendering
                        if (currentGroup.shoppingListItems.isEmpty()) {
                            Text(
                                fontSize = 14.sp,
                                text = "No items",
                            )
                        } else {
                            Text(
                            fontSize = 14.sp,

                            text = "${currentGroup.shoppingListItems.size} item" + (if (currentGroup.shoppingListItems.size > 1) "s" else ""),
                            )
                        }
                    }

                }

            }

            Row(
                Modifier.padding(),

                horizontalArrangement = Arrangement.Start
            ) {

                UserProfileCirclesStacked(GroupMembersUiState(currentGroup.groupMembers))

//            Text(text = " ${groupInformation.name}" , fontSize = integerResource(id = R.integer.heading1).sp)
            }

        }


    }


}