package com.example.roomy.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roomy.R
import com.example.roomy.db.data.GroupInformation
import com.example.roomy.ui.Composables.UserProfileCirclesStacked
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.States.newGroupState
import com.example.roomy.ui.ViewModels.GroupViewModel

@Composable
fun GroupCard(
    groupViewModel: GroupViewModel,
    group: newGroupState,
    navController: NavController,
){

    OutlinedCard(
        Modifier
            .clickable {

                groupViewModel.setCurrentGroup(GroupInformation(id = group.groupId, creatorId = group.creatorId, name = group.groupName))
                navController.navigate(Screens.Groups.name)
            }
            .fillMaxWidth()
            ,

        ) {
        Column (Modifier.fillMaxWidth().padding(12.dp).height(120.dp), verticalArrangement = Arrangement.SpaceBetween) {

            Row(
                Modifier
                    .fillMaxWidth()
                , horizontalArrangement = Arrangement.Start
            ) {

                Text(text = " ${group.groupName}" , fontSize = integerResource(id = R.integer.heading2).sp)
            }

            Row(
                Modifier
                    .fillMaxWidth()
                , horizontalArrangement = Arrangement.Start
            ) {

                if(group.itemCount != -1){
                    Box(
                        modifier = Modifier
                            .padding(8.dp) // Adds padding around the Box
                            .background(Color.Red, shape = RoundedCornerShape(50.dp)) // Rounded shape
                            .padding(horizontal = 8.dp, vertical = 2.dp), // Padding inside the Box
                        contentAlignment = Alignment.Center // Centers the text inside the Box
                    ) {
                        // Conditional text rendering
                        if (group.itemCount == 0) {
                            Text(
                                fontSize = 14.sp,
                                text = "No Items added",
                            )
                        } else {
                            Text(
                            fontSize = 14.sp,

                            text = "${group.itemCount} items",
                            )
                        }
                    }

                }

            }

            Row(
                Modifier.padding(horizontal = 10.dp),

                horizontalArrangement = Arrangement.Start
            ) {

                UserProfileCirclesStacked(GroupMembersUiState(group.groupMembers))

//            Text(text = " ${groupInformation.name}" , fontSize = integerResource(id = R.integer.heading1).sp)
            }

        }


    }


}