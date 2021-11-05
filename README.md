# HabitTracker

**HabitTracker** is an app that can help you to build good habits. You can track your habits, and learn others’ habits.
# Operating APP

### Open APP

When you open this app, you will see a welcome page. You can sign in on this page. If you are new to the app, you can click on *Create Account* button to register your new account. We need your **full name**, **email**, and **password** to create a new account for you. Then, you can sign in with your **email** and **password**.

### Main Page

After you sign in, you can see the main page. Here are three lists, a list of your *habits needed to finish today*, a list of your *unbroken habits*, and a list of *habit events*. You can click on the three buttons to view each of the three lists. For every item in the three lists, you can swipe to left to **delete** this item.

### View/Edit Habit

You can click on a habit to view its details. When you click on a habit, the page of all details of this habit will show. If you want to edit this habit, you can click on *Edit Habits* to edit this habit. Also, you can create a habit event on this page. You can provide a **photo**, a **location** and/or a **comment** for the habit event. Also, you can denote if you **complete** this event.

### Add Habit

You can also click on an *Add* button on the main page to add a new habit. In the *Add* page, you need to provide a **name**, a **description**, and the **start date**. You can also select the **repeating days** in a week, and whether you want to **share** your habit or not.

### Navigation Menu

There is a navigation menu at the bottom of the screen. The three items are **following**, **follower**, and **profile**.

### Following

If you click on **following**, you can view the list of all users you followed. If you click on a user in this list, you can view his/her profile with a list of his/her shareable habits.

### Follower

If you click on **follower**, you can view the list of your followers and users who apply to follow you. You can choose to accept or deny the applications.

### Profile

If you click on **profile**, you can view your own profile. You can see your name, numbers of your following users and follower, and a list of all your habits.


# Develop APP

You can find the code on [Github Code Page](https://github.com/CMPUT301F21T20/HabitTracker.git)
When we develop our app, we used **firebase**. **Firebase** is a platform developed by Google that enables developers to develop iOS, Android and Web apps. Firebase provides tools for *tracking analytics*, *reporting* and *fixing app crashes*. By now, we create four activities, one class, and six fragments to reach the halfway checkpoint.

# Wiki page on Github

There are five main parts in the [Wiki home page](https://github.com/CMPUT301F21T20/HabitTracker.wiki.git), respectively **CRC Cards**, **UI Mockup**, **UML Diagram**, **Meetings**, and **Firestore Design**.

### CRC Cards

There are three pages for CRC Cards. Each CRC Card has a title of its class name, a list of its responsibilities, and a list of its collaborators.
* [Part 1](https://github.com/CMPUT301F21T20/HabitTracker.wiki.git) includes the CRC Cards for habit related classes, namely Habit Class, Habit Event Class, Habit Calendar Class, and Habit List Class.
* [Part 2](https://github.com/CMPUT301F21T20/HabitTracker.wiki.git) includes the CRC Cards for the habit event related classes. It includes Picture Class and Geolocation Class.
* [Part 3](https://github.com/CMPUT301F21T20/HabitTracker.wiki.git) includes the CRC Cards for the rest of important classes. It includes User Class, Habit Controller Class, Following Class, Sharing Class, and Habit List Controller Class.

### UI Mockup

There is one page for UI Mockup. UI is made by **Figma** and **Lucid**.
[This page](https://github.com/CMPUT301F21T20/HabitTracker.wiki.git) includes all UI pages and connections. 
* The first graph shows sign in page, register page, and the main page of our app.
* The second graph shows the adding habit progress with main page, adding page, and date selection page.
* The third graph shows the viewing/editing habit progress with main page, habit detail page, and editing page.
* The fourth graph shows the deleting habit progress in habit today list and habit unbroken list. We can swipe the item to left to delete this item.
* The fifth graph shows the creating habit event progress with main page (on habit today list), habit detail page, and event creation page.
* The sixth graph shows the viewing/editing habit event progress with main page(on habit event list), habit event viewing page, and habit event editing page.
* The seventh graph shows the deleting habit event progress in habit event list. We can swipe the item to left to delete this item.
* The eighth graph shows the opening personal profile and viewing owner all habits list progress. We can click on the *Profile* button on the bottom navigation menu to see the profile page.
* The ninth graph shows the opening following list and checking a following user's profile progress. We can click on the *Following* button on the bottom navigation menu to see the following list. Then we click on a user to see his/her profile with his/her shareable habits list.
* The tenth graph shows the opening follower list and handling the requests of following progress. We can click on the *Follower* button on the bottom navigation menu to see the follower list with follow requests. We can click on *Accept* to accept the request, and *X* to deny the request or remove the follower.

### UML Diagram

There is one page for UML diagram.
[This page](https://github.com/CMPUT301F21T20/HabitTracker.wiki.git) includes only one graph.
The diagram shows the connections of all our classes, and the firebase.

### Meetings

There are seven pages for our meetings.
Each page has a title with our meeting dates. In each page, we recorded the agendas and Attendees. Some of the meetings includes the role assignments for each member.

### Firestore Design

There is one page for Firestore Design.
[This page](https://github.com/CMPUT301F21T20/HabitTracker.wiki.git) includes our first design for Firestore Proposed. It shows how we store data in Firebase(database)，Every document design has a fully description.
* The first is how we store user data with a users followings and followers.
* The second is how we store the habit with all its information.
* The third is how we store the data organized by date then by HabitID and type.
* The fourth is how we store the requests.

# Issues on Github

Every issue is included in the **Issues** page on Github. If we only assign works to every member, a member will create the issues related his/her work by himself/herself. If a member create the issues during meeting, he will assign the issue to the related member.
When someone finish a issue, he/she will close it.
Most issues have labels to show their complexities and priorities. Some issues have label *Complete by halfway checkpoint* meaning that they should be finished early.

# Project on Github

There is a [Project](https://github.com/orgs/CMPUT301F21T20/projects/1). It includes four boards, namely **Backlog**, **TODO**, **Doing**, and **Done**.
This is used to record our working progress. We can check the state of the issues. This could help us to make sure we finished everything on time.

# Firebase

We use Firebase to store our data.
* Authentication: We can check the users' emails, UID, and their creation dates.
* Firestore Database: Now we have **Habits** and **Users**. All created habits are stored in *Habits*, and all user names are stored in *Users*.
