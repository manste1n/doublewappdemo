![alt text](https://github.com/sda5-walletdroid/walletdroid/blob/master/SnapshotsReadme/logowallet.gif ).   

# Walletdroid- the personal and shared expense tracker

Walletdroid has been designed as an APP to track personal expenses of a user and also to track and split expenses among a group of people. The people splitting expenses using the app may or maynot be the users of the APP.

<h3> Features summary of Walletdroid</h3>
<ul>
<li>Creating a group of people to share expenses with. People added to the group may or maynot be users of the APP</li>
<li>Creating expenses- Expense can be personal to the app user, or can be split with other users of group.</li>
<li>Members of the group can be eliminated from specific expenses. </li>
<li>All the people who split an expense and are APP users, receive APP notification informaring about the creation of expense.
<li>A member of a group can leave a group till there are no expenses assigned to him. Once expenses are assigned, a group member cannot leave unless expenses are settled</li>
<li>The admin of the group can settle expenses for the group at any point in time. App users get notification about settlement request raised.
Non App users get the request via Email or SMS</li>
<li>At any point in time, an APP user can query the expenses based on various filtering criteria.</li>
<li>Expense Analytics are also provided using graphs.</li>

## Getting Started


1. Install Android Studio if it's not pre installed.
2. Clone the repository https://github.com/sda5-walletdroid/walletdroid to your local machine.
3. Import the project in android studio.
4. Make sure you have latest dependencies in gradle files.
5. Connect the application to Firebase-Cloud Firestore database. You can do it in two ways.
6. Recommended way is to connect it from within Android Studio. Go to Tools>Firebase and follow the
instructions.
7. Other way is to use the Firebase console (https://console.firebase.google.com). To add a sample app
to a Firebase project, use the applicationId value specified in the app/build.gradle file of the app as
the Android package name. Download the generated google-services.json file, and copy it to the app/
directory of the sample you wish to run.
8. In Firebase console, open the Database section and create indexes as describe in Prerequisites.
9. In Firebase console, open the Authentication section and enable the sign-in methods. (Email-password
and Google sign in)
10. Once the application is connected to Firebase all is set to build/run.


## Prerequisites
Android SDK Platform 26 and above.
#### Index creation

| Collcetion Id 	| Fields indexed                                  	|
|---------------	|-------------------------------------------------	|
| Expenses      	| category Ascending dateMillisec Ascending       	|
| Expenses      	| expenseAccountIds Arrays logDate Descending     	|
| Expenses      	| expenseAccountIds Arrays dateMillisec Ascending 	|
| Expenses      	| payerAccountId Ascending dateMillisec Ascending 	|

## Examples

### Create a new group with internal and external members, add expenses and edit group details.  

![alt text](https://github.com/sda5-walletdroid/walletdroid/blob/master/SnapshotsReadme/walletGroup4.png )   

### Visualize expense history graphically.   

![alt text](https://github.com/sda5-walletdroid/walletdroid/blob/master/SnapshotsReadme/walletGraph.png )   

### Notifications.   

![alt text](https://github.com/sda5-walletdroid/walletdroid/blob/master/SnapshotsReadme/notifications2.png )   


## Authors

* **Mehdi** - *Initial work* <https://www.linkedin.com/in/mehdi-attarpour-1998549a/>
* **Zeynep** - *Initial work* <https://www.linkedin.com/in/zeynep-dal-a85bb83b/>
* **Obaid** - *Initial work* <https://www.linkedin.com/in/obaid-a-9103a479/>
* **Priyanka** - *Initial work* <https://www.linkedin.com/in/priyanka-karia-359127107/>
* **Manimala** - *Initial work* <https://www.linkedin.com/in/manimala-chakravarti-95188b119/>
* **Sanaz** - *Initial work* <https://www.linkedin.com/in/sanaz-sepehrian-bb0885aa/>


## License

This project is licensed.

## Acknowledgments

* Dena Hussain
* Ric Glassey
* Philipp Haller
* Firdose S
* Marcus Dicander
* Kwabena
* Mazen
* KTH
* Novare Potential
* MPAndroid library for making graphs
