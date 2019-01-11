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
