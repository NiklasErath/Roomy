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
        mapOf("role" to "system", "content" to "You will provide a Recipe based on the given Items. You can expect that base items like spices and flower are at home already. If it is not possible to create a Meal with just the given items and some very basics that should be at home like spices tell the user to add more Items to the list to get a recipe. If a meal might be almost possible and only 1 or 2 items are missing, Tell the user FIRST that he could buy these items to create the following recipe. Under no circumstance should you return a recipe buildt with a lot of ingredients that are not in the given List  "),
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