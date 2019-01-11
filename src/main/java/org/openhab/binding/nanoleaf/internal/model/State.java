/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nanoleaf.internal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Represents overall state settings of the light panels
 *
 * @author Martin Raepple - Initial contribution
 */
public class State {

    @SerializedName("on")
    @Expose
    private On on;
    @SerializedName("brightness")
    @Expose
    private Brightness brightness;
    @SerializedName("hue")
    @Expose
    private Hue hue;
    @SerializedName("sat")
    @Expose
    private Sat sat;
    @SerializedName("ct")
    @Expose
    private Ct ct;
    @SerializedName("colorMode")
    @Expose
    private String colorMode;

    public On getOn() {
        return on;
    }

    public void setOn(On on) {
        this.on = on;
    }

    public Brightness getBrightness() {
        return brightness;
    }

    public void setBrightness(Brightness brightness) {
        this.brightness = brightness;
    }

    public Hue getHue() {
        return hue;
    }

    public void setHue(Hue hue) {
        this.hue = hue;
    }

    public Sat getSat() {
        return sat;
    }

    public void setSat(Sat sat) {
        this.sat = sat;
    }

    public Ct getCt() {
        return ct;
    }

    public void setCt(Ct ct) {
        this.ct = ct;
    }

    public String getColorMode() {
        return colorMode;
    }

    public void setColorMode(String colorMode) {
        this.colorMode = colorMode;
    }

}
