package com.example.roomy.db.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

import android.util.Log
import retrofit2.Callback
import retrofit2.Response


data class OpenAIRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Map<String, String>>,
//    Create slider for the user to experiment maybe?
//    Also maybe add options like vegan, vegetarian some other info ...
    val temperature: Double = 0.7

)

data class OpenAIResponse(
    val choices: List <Choice>
)

data class Choice(
    val message:Message
)

data class Message(
    val role: String,
    val content: String
)

interface OpenAIService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    fun getRecipe(@Body request: OpenAIRequest): Call<OpenAIResponse>
}


fun getSuggestedRecipe(shoppingItems: List<String>, callback: (String) -> Unit){
    val messages = listOf(
        mapOf("role" to "system", "content" to "You are a helpful assistant for creating recipes based on the ingredients. Provide fitting recipes with the provided Ingredients but only if it is feasible and Save to eat. " +
                "Also if there is a possibility to create a good meal with one or two more ingredients, suggest the user to buy these items. But always provide an alternative with the items at home if possible"),
        mapOf("role" to "user", "content" to "I have the following items: ${shoppingItems.joinToString(", ")}. Can you suggest a recipe?")
    )

    val request = OpenAIRequest(messages = messages)

    RetrofitInstance.openAIService.getRecipe(request).enqueue(object : Callback<OpenAIResponse> {
        override fun onResponse(call: Call<OpenAIResponse>, response: Response<OpenAIResponse>) {
            if (response.isSuccessful) {
                val recipe = response.body()?.choices?.firstOrNull()?.message?.content
                callback(recipe ?: "No Recipe found")
            } else {
                Log.e("ChatGPT Recipe", "Failed to fetch recipe: ${response.message()}")
                callback("Failed to fetch recipe")

            }
        }

        override fun onFailure(call: Call<OpenAIResponse>, t: Throwable) {
            Log.e("ChatGPT Recipe", "Error: ${t.localizedMessage}")
            callback("Error: ${t.localizedMessage}")

        }
    })
}