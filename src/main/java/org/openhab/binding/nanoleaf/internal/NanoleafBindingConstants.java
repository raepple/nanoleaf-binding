/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nanoleaf.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link NanoleafBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Martin Raepple - Initial contribution
 */
@NonNullByDefault
public class NanoleafBindingConstants {

    private static final String BINDING_ID = "nanoleaf";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_LIGHT_PANEL = new ThingTypeUID(BINDING_ID, "lightpanels");

    // Thing configuration ids
    public static final String CONFIG_IP_ADDRESS = "ipAddress";
    public static final String CONFIG_PORT = "port";
    public static final String CONFIG_AUTH_TOKEN = "authToken";

    // List of all Channel ids
    public static final String CHANNEL_POWER = "power";
    public static final String CHANNEL_BRIGHTNESS = "brightness";
    public static final String CHANNEL_HUE = "hue";
    public static final String CHANNEL_SATURATION = "saturation";
    public static final String CHANNEL_COLOR_TEMPERATURE = "colorTemperature";
    public static final String CHANNEL_COLOR_MODE = "colorMode";
    public static final String CHANNEL_EFFECT = "effect";
    public static final String CHANNEL_RHYTHM_STATE = "rhythmState";
    public static final String CHANNEL_RHYTHM_ACTIVE = "rhythmActive";
    public static final String CHANNEL_RHYTHM_MODE = "rhythmMode";

    // API URLs
    public static final String API_V1_BASE_URL = "/api/v1";
    public static final String API_GET_CONTROLLER_INFO = "/";
    public static final String API_ADD_USER = "/new";
    public static final String API_SET_VALUE = "/state";
    public static final String API_COLOR_MODE = "/state/colorMode";
    public static final String API_SELECT_EFFECT = "/effects";
}
