ws = new WebSocket("ws://localhost:8080/socket");
ws.onmessage = function (event) {
    let obj = JSON.parse(event.data);

    if (obj.type === "ClockMessage") {
        onClockMessage(obj);
    }
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

