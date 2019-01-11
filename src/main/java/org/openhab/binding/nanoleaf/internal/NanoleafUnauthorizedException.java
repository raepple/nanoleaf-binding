/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nanoleaf.internal;

/**
 * Exception if request to OpenAPI is unauthorized (e.g. invalid auth token)
 *
 * @author Martin Raepple - Initial contribution
 */
public class NanoleafUnauthorizedException extends NanoleafException {

    private static final long serialVersionUID = -6941678941424573257L;

    public NanoleafUnauthorizedException(String message) {
        super(message);
    }
}
