# ACC Race Control 

![ACC_Race_Control_Splash](https://user-images.githubusercontent.com/25527438/136245742-6fff957b-a8af-42ce-8ab9-ff7da8820480.png)  
A tool desinged for stewarding, boradcasting and managing competitive races.
ACC Race Control provides you the necessary information to create high quality stewarding solutions and captivating broadcasts.

## What can ACC Race Control do for you?
- A detailed live timing table with information such as lap times, lap delta, gap to the car ahead, pitstop times and stint times.
- Broadcasting controls such as change camera, focused car and HUD selection
- The option to start a virtual safetycar phase during which any violations are automatically reported.
- Automatic collision detection with information about the cars involved and a replay timestamp.
- Exports incidents to a Google spreadsheet in real time for live or post race stewarding
- A detailed list of all events that happen during a race including driver connections, disconnects, lap completed, collisions, session changes etc.

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

## Stewarding made easy
ACC Race Control is a tool developed by stewards for stewards. It's aim is it to make stewarding as easy and accessible as possible.

Stewarding a live race has it's challenges, especially when you have 50 cars on track. It is almost impossible for a few stewards to be able to monitor all cars. As sim racers we dont have the luxury of a big control room with 25+ cameras. Luckily ACC Race Control is here to make your life easier.

ACC Race Control uses the ACC Broadcasting API to automatically detect any collisions between two or more cars. I can then send any incident to a Google Spreadsheet to allow a team of stewards to review the incidnet. Easy as that.  

This tool was developed together with ACCSimSeries on Simracing.gp. It it used almost daily by around six stewards to monitor races with more than 40 cars. If you click the link below you can see how we use it to create our stewarding reports, this example is for 2x30 minute races with 40 drivers taking part.  
**Example steward report:**  
https://docs.google.com/spreadsheets/d/1SSMe8Vte0beENZtI6sL49uqkfW1_s01DtV1A2Kuew8s/edit?usp=sharing

Without this tool that would not be possible.


