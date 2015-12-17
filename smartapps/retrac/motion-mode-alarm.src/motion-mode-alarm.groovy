/**
 *  Motion Mode Alarm
 *
 *  Copyright 2015 RetraC
 *
 */
definition(
    name: "Motion Mode Alarm",
    namespace: "RetraC",
    author: "RetraC",
    description: "Enables Motion detection when the mode changes to the selected mode.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("When the mode changes to...") {
		input "alarmMode", "mode", multiple: true
	}
    section("Enable these motion cameras...") {
		input "camUrl", "text", required: true
        input "notify", "bool", title: "Notification?"
	}
    section("Only between these times...") {
    	input "startTime", "time", title: "Start Time", required: false
        input "endTime", "time", title: "End Time", required: false
    }
}

def installed() {
    subscribe(location, checkTime)
}

def updated() {
	unsubscribe()
    subscribe(location, checkTime)
}

def modeAlarm(evt) {
    if (evt.value in alarmMode) {
        log.trace "Mode changed to ${evt.value}. Enabling Motion detection."
        // cameras?.alarmOn()
        sendMessage("Motion detection enabled")    	
    }
    else {
        log.trace "Mode changed to ${evt.value}. Disabling Motion detection."
        // cameras?.alarmOff()
        sendMessage("Motion detection disabled")
    }
}

def checkTime(evt) {
    if(startTime && endTime) {
        def currentTime = new Date()
    	def startUTC = timeToday(startTime)
    	def endUTC = timeToday(endTime)
	    if((currentTime > startUTC && currentTime < endUTC && startUTC < endUTC) || (currentTime > startUTC && startUTC > endUTC) || (currentTime < endUTC && endUTC < startUTC)) {
    		modeAlarm(evt)
    	}
    }
    else {
    	modeAlarm(evt)
    }
}

def sendMessage(msg) {
	if (notify) {
		sendPush msg
	}
}