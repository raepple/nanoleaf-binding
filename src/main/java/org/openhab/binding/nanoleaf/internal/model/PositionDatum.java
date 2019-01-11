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
 * Represents panel position
 *
 * @author Martin Raepple - Initial contribution
 */
public class PositionDatum {

    @SerializedName("panelId")
    @Expose
    private Integer panelId;
    @SerializedName("x")
    @Expose
    private Integer x;
    @SerializedName("y")
    @Expose
    private Integer y;
    @SerializedName("o")
    @Expose
    private Integer o;

    public Integer getPanelId() {
        return panelId;
    }

    public void setPanelId(Integer panelId) {
        this.panelId = panelId;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getO() {
        return o;
    }

    public void setO(Integer o) {
        this.o = o;
    }

}
