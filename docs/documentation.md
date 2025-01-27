# Documentation 

### Initial Wíreframes

![mockups](images/wireframes.png)


### Development Process

We started the Code lab with splitting the initial work into Database Setup[Niklas] and Compose Setup with Navigation[Jakob]. After some minor Hiccups we were able to finish our base setup withing the 2nd day and started working on more detailed screens and features.

In the first week we finished almost all of our main features including group creation, item creation, balances and AI Recipe features. The functionality was all there, finalizing these features and some refactoring was done in the 2nd week.

Fetching and writing data to supabase was not a problem in itself, but rather optimizing the requests we sent to receive all necessary data at once without repeating unnecessary calls. We tackled this at the start of the 2nd week, including a restructuring of our different states, unifying them into one data Class.
This helped a lot for the general structure of our project, also simplifying the process of adding new features later.

The last days, were spent fixing bugs, polishing the layout and adding colors for the final screen design. 
Overall, our process worked well for us. We communicated frequently to avoid overlap/conflicts in our commits and split the work accordingly.

### Database structure:

Our database includes an auth.user table which has all the login information and is connected to the user_information table which includes the username. Furthermore, we decided to have a parent_group table where all the users connections to a group are set. For all the group information we created a separate table to differentiate the group from the user.

We created a payments and balance table for all the financial documentation in the app. When a payment is created, it will also add a new balance.

E.g. if there are user x and user y in one group and user x buys something in the supermarket it will create a payment and a balance with user x (owed_by) owes user y (owed_to). If user x already lent user y something, then the amount gets increased otherwise if user y owes user y, then it will be decreased or deleted if the amount is smaller than 0 after the calculation. The amount will be divided between all the actual users in the group.

There is also a table for the items a user adds to their lists. All Items have a group id included as well as an Icon id which lets us easily fetch and display all items from a group.

![database](images/database.png)


### Final Results

![screenshots](images/screenshots.png)

<video width="640" height="360" controls>
  <source src="images/screenrecording.mp4" type="video/mp4">
  Your browser does not support the video tag.
</video>

### Reflection Niklas:

The first challenge I encountered was connecting Supabase to the app. I had to learn the syntax of the Supabase dependency to sign in and retrieve the session token for the current session. After I became familiar with Supabase integration in Kotlin, I began thinking about the database structure, which wasn’t a major issue because I had worked with databases on other projects. It took me a long time to figure out how to use Supabase with Kotlin, but I learned a lot during this process. 

Once the database integration was finished, we were able to display all the data of the logged-in user, including the groups they’re in, along with all the group information and members. The next big challenge was working on the balance calculation and update function with the database.

This was complicated because, for example, if user X buys something for 12€, it means user Y and user Z in the group owe user X 4€ each. However, it could be that before this purchase, user X already owes user Y 13€ and user Z 3€. So, I had to think about the counterbalance and how to structure this in the database. This took me a lot of hours. 

The rest of the project was npt as challenging, as I had experience working with Kotlin in Android Studio from previous projects. 

### Reflection Jakob:

I started out on the first day with setting up the base Project Structure, Navigation, Screens, and basic UI. A challenge I encountered was making sure the App renders smoothly and as intended. Always waiting for a database's response led to delayed rendering and would feel off at times. I solved this by implementing a global Group State, which updates locally in sync with the database. In essence this allowed me to update all parts of the UI instantly by changing the state and updating the database in the background. The App would then fetch from the database only when reentering the Homepage or when the user pulls down to refresh the page.



I was also responsible for implementing the OpenAI API, which turned out to be quite a simple process.  The Retrofit Library simplified HTTP Requests with Kotlin, and after adding the API Key it worked out fine. Finetuning the prompt to make sure the response is adequate for our use case was a little tricky but a fun process overall.



Finally, styling the UI with Compose was very unintuitive at first. It is great for basic elements and quick layouts, but for more complex things (expanding item search bar, custom text fields) it was very time-consuming to get it exactly right.

Towards the End we improved a lot on utilizing compose and I am happy about how the app feels and looks now. 


# Final thoughts:

The final app meets the initial concept, and we've already added some additional features, such as AI recipe recommendations based on the items in the inventory. Furthermore, the fetch and update functions of the database could be improved, as well as the database structure (e.g., connecting tables in the database). Other potential improvements include adding a tutorial for first-time users, setting up a separate server to handle OpenAI requests securely,  more customization options like changing the profile picture, theme, group colors, etc. 

However, there are still many features missing in the app that could enhance the user experience and improve its efficiency. We are happy with the base of the app and are motivated to continue working on it in the future, adding more features and possibly even developing it for iOS as well. 

Overall, we are really pleased with how the app turned out, and we are proud of the team for how well we structured the entire project, including time management and work distribution. The best part was definitely our little team-building event after the first week. 
