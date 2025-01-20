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







# Usability TEST plan for ”Roomy“

Test Setup: 
The goal of the usability test is to evaluate whether the navigation between groups and other core features of the app is intuitive for the users, focusing on how easy it is for users to complete tasks such as account creation, managing groups, adding items, and changing user settings.

### Test Methods:
•	Screen Recording: The users' screens will be recorded during the test to capture both the user interaction and any potential challenges they face.
•	Observational Notes: The moderator will take notes during the test, focusing on user behaviors, reactions, and any points of confusion.
•	Task Time: Track how long it takes each user to complete each task from start to finish.
•	Error Counting: Document any errors, such as failed attempts to complete a task, incorrect selections, or misunderstandings of interface elements.

### Target Group: 
•	Demographics: Students between the ages of 18 and 30, and people living in households with others (i.e., potentially managing shared resources or group activities).
•	Target Users: This target group is selected because they are likely familiar with apps, digital interfaces, and group-based features, making them a good representative sample for usability testing.
test group: are students between 18 and 30 years and people that lives in a household with others. 

### Hypothesis: 
We believe users will find it intuitive to navigate between different groups because the navigation bar is consistently placed and uses familiar icons.

### Metrics: 
•	Success Rate
•	Error Rate 
•	Think Aloud Observation
•	Task completion time 
•	SUS
•	Ease 

### Tasks:

The tasks will be done step by step like the bullet points. The success rate, error rate and the completion time will be tracked for the whole task. 
The time gets recorded by screen recordings and will be evaluated afterwards by reviewing the videos. 
After each task will be a USE questionnaire.
A sus will be handed to the participant after he finished all tasks. 

### Task 1: Create an Account and Create a Group
Please create an account and then create a group. Once the group is created, add the users “Jakob” and “Niklas” to your group.
•	Create an account 
•	Create a group 
•	Add members to the group



### Task 2: Add items and get a recipe with the items in the inventory
Please add “cola” to your shopping list and add “banana” and milk” to your inventory. Furthermore get a recipe with the items in the inventory. 
•	Add items to your shopping list
•	Add items to your inventory
•	Get a recipe

### Task 3: Change the username, delete an item and kick group members.
Please change your username, delete an item in your group, and kick either “Jakob” or “Niklas” out of the group. 
•	Change username
•	Delete and item from the group 
•	Remove one of the group members



## Heuristic Evaluation:

### Visibility of System Status
There is no information when the username field on the sign-up screen is empty
-	Check for the length of the username and display a red text underneath the username input field. Do this also for the password.

### Match Between System and the Real World
The “Add Group” button on the home screen is a small plus in the middle of the screen. Users might expect a bigger plus icon in the bottom-right corner (following common app conventions).
-	Move the button to a more familiar position

The add user plus button on the create group screen is inside the text field, which could be confusing for the user.
-	Place the add user button next to or underneath the text field

### User Control and Freedom
Misleading back arrow on the side screen. These arrows redirect the user to the home screen instead of the last screen.
-	Ensure the back button behaves like a standard back button on the side screens like member screen. 

Group name can not be changed after creating the group. 
-	Let the creator of the group change the group name. 

User can kick himself of the group without a warning/second authentication. 
-	Remove this button this doesn’t make sense. Instead add a leave group button. 

### Consistency and Standards
Font size differ slightly between screens. 
-	Use a standardized typography and font sizes across the app that are defined in the themes file.

### Error Prevention
Its possible to add users twice to a group. 
-	Add an error message if the username is already used and tell the user to use another nickname

Its possible to create a group without a name.
-	Its mandatory to have a group name with at least 3 characters 

### Recognition Rather Than Recall
No critical issues found.

### Flexibility and Efficiency of Use
Users cannot add multiple items to the shopping list at once; each item must be added individually.
-	Consider a multi -add feature to speed up the efficiency

### Aesthetic and Minimalist Design
No critical issues found. 

### Help Users Recognize, Diagnose, and Recover from Errors
No clear guidance how to add items to the shopping list.
-	Add a short description when no items are in the shopping list

There is no tutorial/explanation how to delete items from a list
-	Display a short description after the first item is added to the shopping list
-	Add a FAQ screen to the app

### Help and Documentation
No in-app FAQ or Help section for quick reference.
-	Create a Help section within the settings on the profile screen


### Most critical issues found: 
-	User Control and Freedom: Confusing back arrows and the ability for a user to remove themselves from a group without confirmation.
-	Error Prevention: Being able to add duplicate users or create a group without a name.
-	Visibility of System Status: No feedback when mandatory fields (e.g., username) are empty.
