# Roomy


Managing shared responsibilities has never been easier! With our app, streamline finances, shopping lists, and group coordination all in one place. Whether you’re sharing a space with family, friends, or roommates, stay organized and stress-free with these key features: 

Seamless Onboarding: Start with a simple login or account creation screen to secure and personalize your experience. 

Group Dashboard: Access all your groups in one place, ensuring you can switch between them effortlessly. 

Group Balance: Track shared expenses effortlessly. Add payments, and the app automatically splits costs evenly across group members. Never lose track of who owes what. 

Shared Shopping List: Add groceries and items to the group shopping list. Mark items as purchased so everyone stays updated. 

Inventory Tracker: Keep tabs on what’s already in the fridge/shelf to avoid duplicate purchases or forgotten essentials. 

Profile Management: Create a profile, connect with other users, and manage multiple groups with ease. 

 

Target group:  
Perfect for families, roommates, or any shared living or collaboration setup, this app eliminates the hassle of managing group finances and grocery lists. Save time, avoid misunderstandings, and make group collaboration smooth and efficient. 
 
Stay organized, stay balanced, and focus on what matters most—your group. 



 
Create a gradle.properties files: 

# Project-wide Gradle settings.
# IDE (e.g. Android Studio) users:
# Gradle settings configured through the IDE *will override*
# any settings specified in this file.
# For more details on how to configure your build environment visit
# http://www.gradle.org/docs/current/userguide/build_environment.html
# Specifies the JVM arguments used for the daemon process.
# The setting is particularly useful for tweaking memory settings.
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
# When configured, Gradle will run in incubating parallel mode.
# This option should only be used with decoupled projects. For more details, visit
# https://developer.android.com/r/tools/gradle-multi-project-decoupled-projects
# org.gradle.parallel=true
# AndroidX package structure to make it clearer which packages are bundled with the
# Android operating system, and which are packaged with your app's APK
# https://developer.android.com/topic/libraries/support-library/androidx-rn
android.useAndroidX=true
# Kotlin code style for this project: "official" or "obsolete":
kotlin.code.style=official
# Enables namespacing of each library's R class so that its R class includes only the
# resources declared in the library itself and none from the library's dependencies,
# thereby reducing the size of the R class for that library
android.nonTransitiveRClass=true


If you have an OpenAI API Key add it here as:

OPENAI_API_KEY="yourkey"