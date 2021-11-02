// Socket that connects to ACC Race Control.
ws = new WebSocket("ws://localhost:8080/socket");
// entries in the timing tower, maps carId to element.
var timingTowerEntries = {};
var updatedIds = [];
var focusedCarEntry = null;

ws.onmessage = function (event) {
    let obj = JSON.parse(event.data);

    if (obj.type === "ClockMessage") {
        onClockMessage(obj);
        onSessionUpdate();
    } else if (obj.type === "CarStateMessage") {
        onCarStateMessage(obj);
    }
}

function onSessionUpdate() {
    for (const [key, value] of Object.entries(timingTowerEntries)) {
        let found = false;
        for (const id of updatedIds) {
            if (id === key) {
                found = true;
            }
        }
        if (found === false) {
            console.log("delete key", key, key in updatedIds);
            value.parentNode.removeChild(value);
            delete timingTowerEntries[key];
        }
    }
    updatedIds = [];
}

function onClockMessage(msg) {
    document.getElementsByClassName("clockTimeRemaining")[0].innerHTML
            = toDurationShort(msg.timeRemaining);
    document.getElementsByClassName("clockSessionName")[0].innerHTML
            = msg.sessionName;

    let localTime = msg.localTime;
    let ampm = "AM";
    if (localTime < 43200) {
        localTime = localTime - 43200;
        ampm = "PM";
    }
    document.getElementsByClassName("clockLocalTime")[0].innerHTML
            = toLocalTime(localTime);
    document.getElementsByClassName("clockLocalTimeAMPM")[0].innerHTML = ampm;
}

function onCarStateMessage(msg) {
    if (!(msg.id in timingTowerEntries)) {
        let entry = createTimingTowerEntry();
        let nameElement = entry.querySelector(".timingTowerEntryName");
        let numberElement = entry.querySelector(".timingTowerEntryNumber");
        nameElement.innerHTML = msg.name;
        numberElement.innerHTML = msg.number;

        let numberBox = entry.querySelector(".timingTowerEntryNumberBox");
        if (msg.driverCategory == "PLATINUM" || msg.driverCategory == "GOLD") {
            numberBox.classList.add("timingTowerEntryNumberGold");
        } else if (msg.driverCategory == "SILVER") {
            numberBox.classList.add("timingTowerEntryNumberSilver");
        } else if (msg.driverCategory == "BRONZE") {
            numberBox.classList.add("timingTowerEntryNumberBronze");
        }
        console.log(msg.driverCategory);

        timingTowerEntries[msg.id] = entry;
        document.getElementsByClassName("timingTowerList")[0].append(entry);
    }
    let entry = timingTowerEntries[msg.id];
    let positionElement = entry.querySelector(".timingTowerEntryPosition");
    entry.style.order = msg.pos;
    positionElement.innerHTML = msg.pos;

    if (msg.isFocused === "true") {
        if (focusedCarEntry !== null) {
            focusedCarEntry.querySelector(".timingTowerEntryPositionBox")
                    .classList.remove("timingTowerEntryPositionBoxFocused");
        }
        entry.querySelector(".timingTowerEntryPositionBox")
                .classList.add("timingTowerEntryPositionBoxFocused");
        focusedCarEntry = entry;
    }

    updatedIds.push(msg.id);
}

function createTimingTowerEntry() {
    let base = document.createElement("div");
    base.classList.add("timingTowerEntry");

    let positionBox = document.createElement("div");
    positionBox.classList.add("timingTowerEntryPositionBox");
    let position = document.createElement("div");
    position.classList.add("timingTowerEntryPosition");
    positionBox.append(position);

    let nameBox = document.createElement("div");
    nameBox.classList.add("timingTowerEntryNameBox");
    let name = document.createElement("div");
    name.classList.add("timingTowerEntryName");
    nameBox.append(name);

    let numberBox = document.createElement("div");
    numberBox.classList.add("timingTowerEntryNumberBox");
    let number = document.createElement("div");
    number.classList.add("timingTowerEntryNumber");
    numberBox.append(number)

    base.append(positionBox);
    base.append(nameBox);
    base.append(numberBox);
    return base;
}

function toDuration(millis) {
    let ms = millis % 1000;
    let remaining = (millis - ms) / 1000;
    let s = remaining % 60;
    remaining = (remaining - s) / 60;
    let m = remaining % 60;
    remaining = (remaining - m) / 60;
    let h = remaining % 60;
    return ("" + h).padStart(2, "0") + ":"
            + ("" + m).padStart(2, "0") + ":"
            + ("" + s).padStart(2, "0");
}

function toDurationShort(millis) {
    let ms = millis % 1000;
    let remaining = (millis - ms) / 1000;
    let s = remaining % 60;
    remaining = (remaining - s) / 60;
    let m = remaining % 60;
    remaining = (remaining - m) / 60;
    let h = remaining % 60;
    if (h > 0) {
        return ("" + h).padStart(2, "0") + ":"
                + ("" + m).padStart(2, "0") + ":"
                + ("" + s).padStart(2, "0");
    } else {
        return ("" + m).padStart(2, "0") + ":"
                + ("" + s).padStart(2, "0");
    }
}

function toLocalTime(seconds) {
    let s = seconds % 60;
    let remaining = (seconds - s) / 60;
    let m = remaining % 60;
    remaining = (remaining - m) / 60;
    let h = remaining % 60;
    return h + ":" + ("" + m).padStart(2, "0");
}

