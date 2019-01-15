# Nanoleaf Binding

This binding integrates the [Nanoleaf Light Panels](https://nanoleaf.me/en/consumer-led-lighting/products/smarter-series/nanoleaf-light-panels-smarter-kit/). It enables you to authenticate, control, and obtain information of a Light Panels device. The binding uses the [Nanoleaf OpenAPI](http://forum.nanoleaf.me/docs/openapi), which requires firmware version [1.5.0](https://helpdesk.nanoleaf.me/hc/en-us/articles/214006129-Light-Panels-Firmware-Release-Notes) or higher.

![Image](doc/Nanoleaf.jpg)

## Supported Things

The binding supports one thing type: lightpanels

## Discovery

A Light Panels device is discovered automatically through mDNS in the local network. Alternatively, you can also provide a things file (see below for more details). After the device is discovered and added as a thing, it needs a valid authentication token that must be obtained by pairing it with your openHAB instance. Without the token the light panels remain in status OFFLINE.

The binding supports pairing of the device with your openHAB instance as follows:
1. Make sure that the Authentication Token in your thing configuration is left empty.
2. Hold down the on-off button of the Light Panels controller for 5-7 seconds until the LED starts flashing in a pattern. This turns the device in pairing mode, and openHAB will try to request an authentication token for it.

Once your openHAB instance successfully requested the authentication token, the light panels status changes to ONLINE, and you can start linking the channels to your items.

## Thing Configuration

The lightpanels thing is configured with the following parameters:

| Config          | Description                                                                           |
| --------------- | ------------------------------------------------------------------------------------- |
| ipAddress       | IP address of the light panels controller (e.g. 192.168.1.100)                        |
| port            | Port number of the light panels contoller. Default is 16021                           |
| authToken       | The authentication token received from the controller after successful pairing.       |
| refreshInterval | Interval in seconds to refresh the state of the light panels settings. Default is 60. |

## Channels

| Channel             | Item Type | Description                                                            | Read Only |
|---------------------|-----------|------------------------------------------------------------------------|-----------|
| power               | Switch    | Power state of the light panels                                        | No        |
| brightness          | Dimmer    | Brightness of the light panels (0 to 100)                              | No        |
| hue                 | Number    | Hue of the light panels (0 to 360)                                     | No        |
| saturation          | Dimmer    | Saturation of the light panels (0 to 100)                              | No        |
| color               | Color     | Color of the light panels                                              | No        |
| colorTemperature    | Number    | Color temperature (in Kelvin, 1200 to 6500)) of the light panels       | No        |
| colorMode           | String    | Color mode of the light panels                                         | Yes       |
| effect              | String    | Selected effect of the light panels                                    | No        |
| rhythmState         | Switch    | Connection state of the rhythm module                                  | Yes       |
| rhythmActive        | Switch    | Activity state of the rhythm module                                    | Yes       |
| rhythmMode          | Number    | Sound source for the rhythm module. 1=Microphone, 2=Aux cable          | No        |

## Full Example

Below is a full example for a configuration (using things file instead of automatic discovery):

### nanoleaf.things
```
nanoleaf:lightpanels:MyLightPanels [ ipAddress="192.168.1.100", port=16021, authToken="AbcDefGhiJk879LmNopqRstUv1234WxyZ", refreshInterval=60 ]
```

### nanoleaf.items
```
Switch NanoleafPower "Nanoleaf" { channel="nanoleaf:lightpanels:MyLightPanels:power" }
Dimmer NanoleafBrightness "Helligkeit [%.0f]" { channel="nanoleaf:lightpanels:MyLightPanels:brightness" }
Number NanoleafHue "Farbton [%.00f]" { channel="nanoleaf:lightpanels:MyLightPanels:hue" }
Dimmer NanoleafSaturation "Sättigung [%.0f]" { channel="nanoleaf:lightpanels:MyLightPanels:saturation" }
Color NanoleafColor "Farbe" { channel="nanoleaf:lightpanels:MyLightPanels:color" }
Number NanoleafColorTemp "Farbtemperatur [%.000f]" { channel="nanoleaf:lightpanels:MyLightPanels:colorTemperature" }
String NanoleafColorMode "Farbmodus [MAP(nanoleaf.map):%s]" { channel="nanoleaf:lightpanels:MyLightPanels:colorMode" }
String NanoleafEffect "Effekt" { channel="nanoleaf:lightpanels:MyLightPanels:effect" }
Switch NanoleafRhythmState "Rhythm verbunden [MAP(nanoleaf.map):%s]" { channel="nanoleaf:lightpanels:MyLightPanels:rhythmState" }
Switch NanoleafRhythmActive "Rhythm aktiv [MAP(nanoleaf.map):%s]" { channel="nanoleaf:lightpanels:MyLightPanels:rhythmActive" }
Number NanoleafRhythmSource  "Rhythm Quelle" { channel="nanoleaf:lightpanels:MyLightPanels:rhythmMode" }
```

### nanoleaf.sitemap
```
sitemap nanoleaf label="Nanoleaf"
{
    Frame label="Nanoleaf" {
            Switch item=NanoleafPower
            Slider item=NanoleafBrightness 
            Colorpicker item=NanoleafColor           
            Slider item=NanoleafSaturation
            Setpoint item=NanoleafColorTemp step=100 minValue=1200 maxValue=6500
            Setpoint item=NanoleafHue step=10 minValue=0 maxValue=360
            Text item=NanoleafColorMode
            Selection item=NanoleafEffect mappings=["Color Burst"="Color Burst", "Fireworks" = "Feuerwerk", "Flames" = "Flammen", "Forest" = "Wald", "Inner Peace" = "Innerer Frieden", "Meteor Shower" = "Meteorregen", "Nemo" = "Nemo", "Northern Lights" = "Nordlichter", "Paint Splatter" = "Farbspritzer", "Pulse Pop Beats" = "Pop Beats", "Rhythmic Northern Lights" = "Rhytmische Nordlichter", "Ripple" = "Welle", "Romantic" = "Romantik", "Snowfall" = "Schneefall", "Sound Bar" = "Sound Bar", "Streaking Notes" = "Streaking Notes", "moonlight" = "Mondlicht" ]
            Text item=NanoleafRhythmState
            Text item=NanoleafRhythmActive
            Selection item=NanoleafRhythmSource mappings=[0="Mikrofon", 1="Aux"]
	}
}
```

### nanoleaf.map
```
ON=Ja
OFF=Nein
effects=Effekte
```
