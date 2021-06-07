# v1.2.0

* Add a broadcasting panel for easier control of the game for live broadcasts.
* Code refactoring.

Bug fixes:
* Pressing Escape will no longer exit the application.



# v1.1.0

* Show the car Class (GT3, GT4, ST, CUP) as a colored triangle in the live timing panel.
* Added a camera control panel to allow changing of the camera and hud pages and starting instant replays.
  * Predefined options to start an instant replay from 15s, 30s, or 60s back.
  * Option to start an instant replay for custom duration and starting point.
* When a replay is playing, the header will now turn blue and show the remaining replay time.
* Added the aproximate replay time for an incident in the incident panel.
* Enabled extensions are now remembered when closing the program.

Bug fixes:
* The entry list will no longer write duplicate entries into the spreadsheet. 
* Dont calculate the green flag offset in the google Sheets Extension if we join the session while the race is already active.


### v1.0.1

* Prevent a Bug where the Google API was blocked and could not start.
* Add an option to select the Google API credentials file from within the program.
* Make the program remember the connection settings and the credentials file path.

# v1.0.0

Initial Release
