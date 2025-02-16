# v1.5.8

Changes:
* Add support for the Ford Mustang GT3
* Enabled yellow flag detection.
    The update of the game that causes this issue was resolved. This update
    makes sure to fix up the yellow flag detection to work with the newest verison
    of the game.

# v1.5.7

Changes:
* Add support for GT2 dlc cars (Audi, KTM, Maserati, Mercedes, Porsche 911, Porsche 935)

Bug fixes:
* Disable yellow flag detection.  
    With version 1.10 and upwards of the game the broadcasting api has changed a little but
    which means that the yellow flag detection does not work correctly and would show every car
    as causing a yellow flag.
    Until this change in the api is reverted or a workaround has been found the
    yellow flag detection will remain disabled.

# v1.5.6

Bug fixes:
* Avoid a crash when using Acc RaceControl on the Nordschleife.
* There is currently a bug with the game which means that some features do not work correctly on the Nordschleife. These features are unlikely to be fixed unless Kunos fixes the bug in the game. Using RaceControl on other tracks behaves like normal.  
A non exhaustive list of incorrectly working features:
    * changing hud pages
    * changing to tv/helicopter camera
    * gap calculation
    * advanced collision detection for endurance races.  
* Fixed a bug where third sector times will be identical to First Sector times for all cars (Fixed by Github@NoComment1105).



# v1.5.5

Bug fixes:
* Fix a bug where cars would disconnect continously after a while.

# v1.5.4

Changes:
* Add support for the Mclaren 720S GT3 Evo.
    
Bug fixes:
* Correctly display the places lost / gained.
* Correctly display the drivers table in the live timing table.
* Fix a potential crash in the accident detection algorithm.


# v1.5.3

Changes:
* Add an experimental features auto cam to automatically control the broadcasting camera.
    To enable the auto cam, check the "enable experimental features" checkbox on the settings page.
    It is an experimental features which means that it is not finished and more work is necessary.
    However it is already quite usable to create a dynamic broadcast without the 
    attention of a human broadcaster. This is particularly usefull for long endurance races
    where a broadcaster is on a break.
    * The auto cam will:
        * share roughly equal screen time between drivers
        * prioritise leading cars over backmarkers
        * prioritise battles between two or more drivers
    
Bug fixes:
* Fixed an issue where disconnecting and reconnecting would crash the program.

# v1.5.2

Changes:
* Add support for version 1.9.
* Add car information for new cars.
* Add track information for Valencia Ricardo Tormo.


# v1.5.1

Changes:
* Add an API to allow to create extensions for Race Control.
    For an example on how to create an extension visit https://github.com/LennysLounge/ACC-Race-Control-Extension-Example.

# v1.5.0

Changes:
* Verified ACC Race Control with google api's.
    * Race Control now comes with a default API key that has been verified by Google, which means it is no longer necessary to generate you own.
        The option to use a custom api key is still there, if there are every problem with the default key.
* Added the option to automatically detect the connection settings from the configuration file.
* Added a relative mode to the live timing which shows cars relative to each other.
* Added support for the American track Pack DLC.

        

# v1.4.2

Changes:
* Added support for the Challengers pack DLC.


# v1.4.1

Bug fixes:
* Fixed a bug where the program would crash because an image could not be loaded. 


# v1.4.0

Changes:
* Added animation to the UI.
* Added an advanced collision detection system to increase the reliability of reported collision for long endurance events.
    * Contacts will now always show at least two cars for every contact.
    * During long endurance events, the collision detection from the game gets continuously worse.
        This causes Race Control to miss some contacts.
        A system was added to help mitigate this.
        Contacts detected by this system are marked as "Possible contact".
* Added a yellow flag system and shows yellow flags in the live timing table.
* Show the current fastest lap holder as purple.
* Adjust rendering of car classes in the car number field.
* Add the constructor logo to the live timing table.
* Add command password to login.
    Loggin in without the correct command password puts Race Control into read only mode.
    In read only mode Race Control cannot control the game and can only read data.
* Rework the Google sheets API integration.
    * Improved the accuracy of the replay time in the google spreadsheet.
    * The entry list is now sorted based on car number.
    * Cars that have spun and caused a yellow flag after a contact will now be suffixed with "Spin" after the lap count.
    * Possible contacts, detected by the advanced collision detection, will be marked with the word "possible" in the cars column.
    * Race Control will now search all sheets of a spreadsheet to find a suitable targets.
* Improve logging to make it easier to find bugs.

Bug fixes:
* Detaching a page now correctly renders it.
* The live timing table will now correctly switch to "Race" when the session changes.

# v1.3.2

Changes:
* Added BMW M4 GT3 support.

# v1.3.1

Changes:
* Automatically scroll a table when at the bottom.

Bug fixes:
* Write the correct lap count to the google spreadsheet.

# v1.3.0

* UI changes (replce the tabs with a menu on the left hand side).
    * All menu items are now detachable by clicking on them with the mouse wheel.

* Changes to the live timing tab.
    * I now the "Live Timing" item in the menu.
    * Added multiple views to the live timing table to give different data.
        * Two views focused on qualifying.
        * One view focused on the race.
        * One view focused on miscellaneous statistics.
        * One view focused on the driver.
        * More miscellaneous changes.
    * Broadcasting control can now be hidden in a collapsable panel.
* The Main live timing table can be detached.

* Changes to the incident tab.
    * Is now the "Race Control" item in the menu.
    * Move google sheets integration configuration here.
        * When in a team event write all team members into the entry list in the google spreadsheet.
        * When a car has finished the session and causes an incident to be logged to the spreadsheet add a "[F]" behind the car number to signal that fact.
    * Add a virtual safety car system to set a strict speed limit.

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


# v1.0.1

* Prevent a Bug where the Google API was blocked and could not start.
* Add an option to select the Google API credentials file from within the program.
* Make the program remember the connection settings and the credentials file path.

# v1.0.0

Initial Release
