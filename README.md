# ACC Race Control 
A live timing app for Assett Corsa Competizione
 
Build with netbeans 12 and Gradle 6.2.2

![ACC_Race_Control_Splash](https://user-images.githubusercontent.com/25527438/116937032-eeecba80-ac68-11eb-8ffa-1c2b009a2e05.png)

## Stewarding made easy
ACC Race Control is a tool developed by stewards for stewards. Its goal is it to make stewarding as easy and as accessable as possible.

Stewarding a race can be quite difficult.
With more than 50 cars on track it is almost impossible for a few stewards to be able to look at all cars.
As sim racer we dont have the luxury of a big control room with 25 cameras all at a different part of the track.
Luckily ACC Race Control makes this easy.

ACC Race Control uses the ACC Broadcasting API to connect to the game and detect any crashes and displays them.
It can then send the incident to a Google Spreadsheet where all the stewards can see it.

All the stewards have to do it look at the spreadsheet and wait for new incidents to come in.
Easy as that.

This tool was developed together with ACCSimSeries on Simracing.gp.
We use it almost daily to live steward races with more than 40 cars and 6 stewards at the same time.
To get an idea of how we use it to create our stewarding reports you can have a look at this report from a race with 40 drivers and 2x30minute races.

https://docs.google.com/spreadsheets/d/1SSMe8Vte0beENZtI6sL49uqkfW1_s01DtV1A2Kuew8s/edit?usp=sharing

Without this tool that would certainly not be possible.


## What can ACC Race Control do for you?

* Display a live timing screen  with all of the relevant information like lap times, lap delta, gap to the car ahead etc.
* A detailed list of all events that happen during a race including driver connections, disconnects, lap completed, accidents, session changes etc.
* A list of crashes with information on when they happened and who was involved.
* Exporting incidents to a spreadsheet in real time for live or post race stewarding.
* Controls to change the camera, focused car, HUD pages for easy broadcasting.

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

The Google Sheets integration is by far the most usefull feature.
To use it you need to supply your own API Key.  
To create your own, first follow the steps outlined here to create a Goole Cloud Platform project.  
https://developers.google.com/workspace/guides/create-project

Then:  
1) Open your project and navigate to "API's & Services" -> "OAuth consent screen.
2) Select external and click create.
3) Add an App name and an email adress and click continue
4) Click Add or remove scopes.
5) Add the "See, edit create and delete spreadsheets" scope.
6) Save and continue through the rest.
7) Navigate to "Credentials".
8) Click on "+Create Credentials" -> "OAuth client ID".
9) Select Application type "Desktop app", give it a nice name and click create.
10) Download the credentials file and place it into the "Google Sheets API Key" folder.

The Credentials should remain secret but you can pass them around your team without a problem so this only has to be done once.

### Connecting to a spreadsheet

![config](https://user-images.githubusercontent.com/25527438/116941363-d03df200-ac6f-11eb-9879-7c271a6220bc.PNG)

Before connecting the client navigate to the "Google API" tab and enable the Spreadsheet API.  
Copy the whole URL of the spreadsheet you would like to use into the "Spreadsheet link" text field.  
If you would like to use our template then feel free to do so.  
https://docs.google.com/spreadsheets/d/1AM93wWuMfkqVEmOAcOePI7mKKDs0P9aG2tRGzi_QLEA/edit?usp=sharing
Simply create a copy of the template into your own google drive.

The tool will target different sheets in the Spreadsheet depending on what session the server is in.  
During Practice and Qualifying any incidents will be sent to the Pracice and Qualifying sheet respectivly.  
During Race sessions the tool will count the ammount of race sessions.
The first Race will be sent to "Race 1", the second to "Race 2" and the third to "Race 3" and so on.

If you want to use your own then you can modify the cells where the tool will write the incidents.  
The tool will first look at the "Find empty row" range for an empty row to write the incident into.
Then it will write the session time into the session column and the involved cars into that column.
You can also add the lap count to the car number in brakets. ( 513[13] )




## Live Timing screen
![Live timing race](https://user-images.githubusercontent.com/25527438/116937657-dc26b580-ac69-11eb-8815-a1c9cca8b85a.PNG)

The live timing screen will show you lots of usefull information during the race. This includes the current lap, gap to car infront, gap to the leader, last lap, best lap etc.

Double clicking on a car will change the game to focus on that car.
During qualifying or practice it will also show the sector times for their fastest laps as well as the current delta.

![Live Timing](https://user-images.githubusercontent.com/25527438/116937663-de890f80-ac69-11eb-9efa-046a21aa4d99.PNG)

## Incident screen
![Incidents](https://user-images.githubusercontent.com/25527438/116937682-e5b01d80-ac69-11eb-8d15-baaa67555194.PNG)
The incident screen will list all contacts that happen during the race. Each row will show the time in the session the contact happened and what cars are involved in the crash.

## Logging screen
![Logging](https://user-images.githubusercontent.com/25527438/116937695-eb0d6800-ac69-11eb-88c9-5b6f831fb654.PNG)
The logging screen will show all events that happen during the race.That includes an event when a driver completes a lap, when an accident happend, when the session changes.

This can become incredibly usefull when you are trying to determine exactly when and in what order something has happened.




