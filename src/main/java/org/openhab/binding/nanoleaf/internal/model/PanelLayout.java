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
package org.openhab.binding.nanoleaf.internal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Represents panel layout of the light panels
 *
 * @author Martin Raepple - Initial contribution
 */
public class PanelLayout {

    @SerializedName("layout")
    @Expose
    private Layout layout;
    @SerializedName("globalOrientation")
    @Expose
    private GlobalOrientation globalOrientation;

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public GlobalOrientation getGlobalOrientation() {
        return globalOrientation;
    }

    public void setGlobalOrientation(GlobalOrientation globalOrientation) {
        this.globalOrientation = globalOrientation;
    }

}
