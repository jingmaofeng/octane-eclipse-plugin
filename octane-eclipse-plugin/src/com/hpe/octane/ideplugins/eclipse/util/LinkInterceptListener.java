package com.hpe.octane.ideplugins.eclipse.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
            externalUrl = new URI(event.location);
            Desktop.getDesktop().browse(externalUrl);
        } catch (URISyntaxException | IOException e) {

        }
        // stop propagation
        event.doit = false;
    }

    // method called after the link has been opened in place.
    @Override
    public void changed(LocationEvent event) {
        // Not used
    }
}