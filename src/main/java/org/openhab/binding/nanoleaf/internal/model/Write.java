/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nanoleaf.internal.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Represents write command to set solid color effect
 *
 * @author Martin Raepple - Initial contribution
 */

public class Write {

    @SerializedName("command")
    @Expose
    private String command;
    @SerializedName("animType")
    @Expose
    private String animType;
    @SerializedName("palette")
    @Expose
    private List<Palette> palette = null;
    @SerializedName("colorType")
    @Expose
    private String colorType;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getAnimType() {
        return animType;
    }

    public void setAnimType(String animType) {
        this.animType = animType;
    }

    public List<Palette> getPalette() {
        return palette;
    }

    public void setPalette(List<Palette> palette) {
        this.palette = palette;
    }

    public String getColorType() {
        return colorType;
    }

    public void setColorType(String colorType) {
        this.colorType = colorType;
    }

}
