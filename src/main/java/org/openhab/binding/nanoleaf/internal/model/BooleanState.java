/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nanoleaf.internal.model;

/**
 * Interface for states with a boolean value
 *
 * @author Martin Raepple - Initial contribution
 */

public interface BooleanState {

    public Boolean getValue();

    public void setValue(Boolean value);
}
