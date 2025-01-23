package com.example.roomy.ui

import android.graphics.drawable.Icon
import androidx.annotation.DimenRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp


@Composable
fun CustomOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = TextStyle.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    placeholder: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None, // Pass the visual transformation here,
    unfocusedLabelColor: Color = Color.Black

) {
    // Custom colors for the TextField
    val customTextFieldColors = TextFieldDefaults.colors(

        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedLabelColor = Color.White,  // Color of the label when focused
        unfocusedLabelColor = unfocusedLabelColor,  // Color of the label when unfocused


        )

    // Using the default OutlinedTextField but applying the custom colors
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        textStyle = textStyle,
        colors = customTextFieldColors,
        keyboardOptions = keyboardOptions,
        placeholder = placeholder,
        visualTransformation = visualTransformation

    )
}
