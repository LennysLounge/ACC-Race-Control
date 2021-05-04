# ACC Race Control 
A live timing app for Assett Corsa Competizione
 
Build with netbeans 12 and Gradle 6.2.2

![ACC_Race_Control_Splash](https://user-images.githubusercontent.com/25527438/116937032-eeecba80-ac68-11eb-8ffa-1c2b009a2e05.png)

## Stewarding made easy
ACC Race Control is a tool developed by stewards for stewards. It's aim is it to make stewarding as easy and accessible as possible.

Stewarding a live race has it's challenges, especially when you have 50 cars on track. It is almost impossible for a few stewards to be able to monitor all cars, us sim racers dont have the luxury of a big control room with 25+ cameras. Luckily ACC Race Control is here to make your life easier.

ACC Race Control uses the ACC Broadcasting API to connect to the game and reports any contact between cars, it can then send any incidents to a Google Spreadsheet which allows stewards to monitor and investigate by accessing the spreadsheet. Easy as that. 

This tool was developed together with ACCSimSeries on Simracing.gp. It it used almost daily by around six stewards to monitor to races with more than 40 cars. If you click the link below you can see how we use it to create the stewarding reports, this example is for 2x30 minute races with 40 drivers taking part.

https://docs.google.com/spreadsheets/d/1SSMe8Vte0beENZtI6sL49uqkfW1_s01DtV1A2Kuew8s/edit?usp=sharing

Without this tool that would not be possible.

## What can ACC Race Control do for you?
-  Display live timings with information such as lap times, lap delta, gap to the car ahead etc.
-  A detailed list of all events that happen during a race including driver connections, disconnects, lap completed, accidents, session changes etc.
- A list of crashes which includes information on when they occurred and which cars
- Exports incidents to a Google spreadsheet in real time for live or post race stewarding
- Broadcasting controls such as change camera, focused car and HUD selection


## How to use it

First we need to enable the broadcasting API from Assetto Corsa Competizione.
To do that navigate to  
`...\documents\Assetto Corsa Competizione\Config\broadcasting.json`  
and edit its contents.
Replace the text with:
```
}
    "updListenerPort": 9000,
    "connectionPassword": "asd",
    "commandPassword": ""
}
```
and you are good to go.  
Then download the tool, unpack it and start it by running the `Start.bat` file.

## Google sheets integration

The Google Sheets feature is considered the most useful by multiple sim racing communities, in order to use this function you will need to supply your own API Key.
The API Key should remain secret but you can pass them around your team without a problem so this only has to be done once.
To create your own, first follow the steps outlined here to create a Google Cloud Platform project.
https://developers.google.com/workspace/guides/create-project

Once you have done that, you need to follow the next steps:

1. Open your project and navigate to "API's & Services" -> "OAuth consent screen.
2. Select external and click create
3. Add an App name and an email adress and click continue
4. Click Add or remove scopes
5. Add the "See, edit create and delete spreadsheets" scope
6. Save and continue through the rest
7. Navigate to "Credentials"
8. Click on "+Create Credentials" -> "OAuth client ID"
9. Select Application type "Desktop app", give it a nice name and click create
10. Download the credentials file and place it into the "Google Sheets API Key" folder

If you have any trouble with creating the API Key feel free to contact me under schuengel.leonard@gmail.com


### Connecting to a spreadsheet

![config](https://user-images.githubusercontent.com/25527438/116941363-d03df200-ac6f-11eb-9879-7c271a6220bc.PNG)

Before attempting to connect the client click the "Google API" tab and select enable the Spreadsheet API.
Copy the full URL of the spreadsheet which you have created and wish to use, paste into the "Spreadsheet link" text field.
Please see below a template for the stewards spreadsheet, please feel free to use.
https://docs.google.com/spreadsheets/d/1AM93wWuMfkqVEmOAcOePI7mKKDs0P9aG2tRGzi_QLEA/edit?usp=sharing You can create a copy of the template to your own Google drive.

The tool is designed to target different sheets in the Spreadsheet depending on which session is taking place. During Practice and Qualifying any incidents will be sent to the Practice and Qualifying sheet respectively.
During the Race session the tool will count the number of race sessions, race one will be sent to "Race 1", the second to "Race 2" and the third to "Race 3" and so on. 

If you plan to use your own spreadsheet you are able to modify which cells the tool will write the incidents.
ACC Race Control will firstly look at the "Find empty row" range for an empty row to insert an incident into, it will write the session time into the session column and the involved cars into the column named the same. You can also add the lap count to the car number in brackets. ( 513[13] )




## Live Timing screen
![Live timing race](https://user-images.githubusercontent.com/25527438/116937657-dc26b580-ac69-11eb-8815-a1c9cca8b85a.PNG)

The live timing screen displays useful information during the race, this includes the current lap, gap to car in front, gap to the leader, last lap, best lap etc.

Double clicking on a car will change the game to focus on that car.
During qualifying or practice it will also show the sector times for their fastest laps as well as the current delta.

![Live Timing](https://user-images.githubusercontent.com/25527438/116937663-de890f80-ac69-11eb-9efa-046a21aa4d99.PNG)

## Incident screen
![Incidents](https://user-images.githubusercontent.com/25527438/116937682-e5b01d80-ac69-11eb-8d15-baaa67555194.PNG)
The incident screen will list all contacts which occur during the race, each row will show the time in the session the contact happened and what cars are involved in the crash.

## Logging screen
![Logging](https://user-images.githubusercontent.com/25527438/116937695-eb0d6800-ac69-11eb-88c9-5b6f831fb654.PNG)
The logging screen displays all events that happen during the race, which includes an event when a driver completes a lap, when an accident happened, when the session changes.

This can be incredibly useful when you are trying to determine exactly when and in what order something has happened.



