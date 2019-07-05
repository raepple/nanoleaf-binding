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

import org.apache.commons.lang.StringUtils;

/**
 * Represents rhythm module settings
 *
 * @author Martin Raepple - Initial contribution
 */
public class Rhythm {

    private Boolean rhythmConnected;
    private Boolean rhythmActive;
    private Integer rhythmId;
    private String hardwareVersion;
    private String firmwareVersion;
    private Boolean auxAvailable;
    private Integer rhythmMode;
    private RhythmPos rhythmPos;

    public Boolean getRhythmConnected() {
        return rhythmConnected == null ? Boolean.FALSE : rhythmConnected;
    }

    public void setRhythmConnected(Boolean rhythmConnected) {
        this.rhythmConnected = rhythmConnected;
    }

    public Boolean getRhythmActive() {
        return rhythmActive == null ? Boolean.FALSE : rhythmActive;
    }

    public void setRhythmActive(Boolean rhythmActive) {
        this.rhythmActive = rhythmActive;
    }

    public Integer getRhythmId() {
        return rhythmId == null ? new Integer(0) : this.rhythmId;
    }

    public void setRhythmId(Integer rhythmId) {
        this.rhythmId = rhythmId;
    }

    public String getHardwareVersion() {
        return hardwareVersion == null ? StringUtils.EMPTY : this.hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion == null ? StringUtils.EMPTY : this.firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public Boolean getAuxAvailable() {
        return auxAvailable == null ? Boolean.FALSE : this.auxAvailable;
    }

    public void setAuxAvailable(Boolean auxAvailable) {
        this.auxAvailable = auxAvailable;
    }

    public Integer getRhythmMode() {
        return rhythmMode == null ? new Integer(-1) : rhythmMode;
    }

    public void setRhythmMode(Integer rhythmMode) {
        this.rhythmMode = rhythmMode;
    }

    public RhythmPos getRhythmPos() {
        return rhythmPos;
    }

    public void setRhythmPos(RhythmPos rhythmPos) {
        this.rhythmPos = rhythmPos;
    }

}
