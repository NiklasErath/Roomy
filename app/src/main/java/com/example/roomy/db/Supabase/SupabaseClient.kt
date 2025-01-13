package com.example.roomy.db.Supabase

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json


val supabase = createSupabaseClient(

    supabaseUrl = "https://odjezqwvtbdyclghqvyq.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9kamV6cXd2dGJkeWNsZ2hxdnlxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzY3NjE3OTQsImV4cCI6MjA1MjMzNzc5NH0.UNVWxTnbwlThe0WLmDKkLSiErga4O-f47eEuVTTozBU",
) {
    install(Auth)
    install(Postgrest)
    defaultSerializer = KotlinXSerializer(Json)
}

