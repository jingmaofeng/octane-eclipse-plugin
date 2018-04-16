/*******************************************************************************
 * © 2017 EntIT Software LLC, a Micro Focus company, L.P.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.hpe.octane.ideplugins.eclipse.ui.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;

/**
 * Intercept location events on swt {@link Browser} control
 */
public class LinkInterceptListener implements LocationListener {

    /**
     * method called when the user clicks a link but before the link is opened
     */
    @Override
    public void changing(LocationEvent event) {
        URI externalUrl = null;
        try {
            new URL(event.location); // make sure it's a valid URL, which is a
                                     // subset of a URI
            externalUrl = new URI(event.location);
            Desktop.getDesktop().browse(externalUrl);
            event.doit = false; // stop propagation
        } catch (URISyntaxException | IOException e) {
            // tough luck, continue propagation, it's better than nothing
            event.doit = true;
        }

    }

    // method called after the link has been opened in place.
    @Override
    public void changed(LocationEvent event) {
        // Not used
    }
}
