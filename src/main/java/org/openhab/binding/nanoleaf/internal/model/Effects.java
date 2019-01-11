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
 * Represents selected effect and effect list of the light panels
 *
 * @author Martin Raepple - Initial contribution
 */
public class Effects {

    @SerializedName("select")
    @Expose
    private String select;
    @SerializedName("effectsList")
    @Expose
    private List<String> effectsList = null;

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public List<String> getEffectsList() {
        return effectsList;
    }

    public void setEffectsList(List<String> effectsList) {
        this.effectsList = effectsList;
    }

}
