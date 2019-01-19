/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.nanoleaf.internal.config;

/**
 * The {@link NanoleafLightPanelsConfig} class contains fields mapping thing configuration parameters.
 *
 * @author Martin Raepple - Initial contribution
 */
public class NanoleafLightPanelsConfig {

    /** IP address of the light panels controller */
    public static final String IP_ADDRESS = "ipAddress";
    public String ipAddress = "";
    /** Port number of the light panels controller */
    public static final String PORT = "port";
    public int port;
    /** Authorization token for controller API */
    public static final String AUTH_TOKEN = "authToken";
    public String authToken;
    /** Light panels status refresh interval */
    public static final String REFRESH_INTERVAL = "refreshInterval";
    public int refreshInterval;
}
