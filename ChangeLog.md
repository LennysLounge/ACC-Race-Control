# v1.3.0



# v1.2.1

Bug fixes:
* Prevent an error where clicking in certain spots would freeze Race Control.

# v1.2.0

* Add a broadcasting panel for easier control of the game for live broadcasts.
* You can now start an instant replay for an incident either 20s, 10s, or 5s before the contact happened by clicking on the corresponding button in the incident panel.
* You can now change the focused car by clicking on the car number in the incident panel.
* Add Hot-key to change the camera and hud similar to the ingame Hot-keys.
  * `1` select Cockpit camera
  * `2` select Bonnet camera
  * `3` select Chase camera
  * `F1` cycle car cameras
  * `F2` cycle hud
  * `F3` cycle TV cameras
  * `F6` cycle onboard cameras
  * `Shift + Left` or `Shift + Right` set focus to car infront/behind based on track position
  * `Shift + Up` or `Shift + Down` set focus to car infront/behind based on race position
* Code refactoring.

Bug fixes:
* Pressing Escape will no longer exit the application.
* Put meassures in place to prevent a bug where the font changes when it should not.
* Fix an error where the selection in a text box was shifted from where it should be.
* In multi driver events the live timing will now display the correct driver currently driving.
* Remove the start exe because it was causing problems with windows defender. Use the `Start.bat` instead.



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
