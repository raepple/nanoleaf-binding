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
 * Represents layout of the light panels
 *
 * @author Martin Raepple - Initial contribution
 */
public class Layout {

    @SerializedName("numPanels")
    @Expose
    private Integer numPanels;
    @SerializedName("sideLength")
    @Expose
    private Integer sideLength;
    @SerializedName("positionData")
    @Expose
    private List<PositionDatum> positionData = null;

    public Integer getNumPanels() {
        return numPanels;
    }

    public void setNumPanels(Integer numPanels) {
        this.numPanels = numPanels;
    }

    public Integer getSideLength() {
        return sideLength;
    }

    public void setSideLength(Integer sideLength) {
        this.sideLength = sideLength;
    }

    public List<PositionDatum> getPositionData() {
        return positionData;
    }

    public void setPositionData(List<PositionDatum> positionData) {
        this.positionData = positionData;
    }

}
