/*******************************************************************************
 * Copyright 2017 Hewlett-Packard Enterprise Development Company, L.P.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.hpe.octane.ideplugins.eclipse.ui.util;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.util.resource.ImageResources;
import com.hpe.octane.ideplugins.eclipse.util.resource.SWTResourceManager;

public class LoadingComposite extends Composite {

    private ImageData[] imageDataArray;
    private Thread animateThread;
    private Image image;
    private ImageLoader loader;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public LoadingComposite(Composite parent, int style) {
        super(parent, style);

        setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        Display display = parent.getDisplay();
        GC shellGC = new GC(this);
        Color shellBackground = getBackground();

        loader = new ImageLoader();
        InputStream stream;
        try {
            stream = Platform.getBundle(Activator.PLUGIN_ID).getEntry(ImageResources.OCTANE_PRELOADER.getPluginPath()).openStream();
            imageDataArray = loader.load(stream);
        } catch (IOException ex) {
            // BACKUP PLAN!!!
            setLayout(new GridLayout(1, false));
            Label lblLoading = new Label(this, SWT.NONE);
            lblLoading.setLayoutData(
                    new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
            lblLoading.setAlignment(SWT.CENTER);
            lblLoading.setText("Loading...");
            return;
        }

        animateThread = new Thread() {
            @Override
            public void run() {
                /*
                 * Create an off-screen image to draw on, and fill it with the
                 * shell background.
                 */
                Image offScreenImage = new Image(display, loader.logicalScreenWidth,
                        loader.logicalScreenHeight);
                GC offScreenImageGC = new GC(offScreenImage);
                offScreenImageGC.setBackground(shellBackground);
                offScreenImageGC.fillRectangle(0, 0, loader.logicalScreenWidth,
                        loader.logicalScreenHeight);

                try {
                    /*
                     * Create the first image and draw it on the off-screen
                     * image.
                     */
                    int imageDataIndex = 0;
                    ImageData imageData = imageDataArray[imageDataIndex];
                    if (image != null && !image.isDisposed())
                        image.dispose();
                    image = new Image(display, imageData);
                    offScreenImageGC.drawImage(
                            image,
                            0,
                            0,
                            imageData.width,
                            imageData.height,
                            imageData.x,
                            imageData.y,
                            imageData.width,
                            imageData.height);

                    /*
                     * Now loop through the images, creating and drawing each
                     * one on the off-screen image before drawing it on the
                     * shell.
                     */
                    int repeatCount = loader.repeatCount;
                    while (loader.repeatCount == 0 || repeatCount > 0) {
                        switch (imageData.disposalMethod) {
                            case SWT.DM_FILL_BACKGROUND:
                                /*
                                 * Fill with the background color before
                                 * drawing.
                                 */
                                Color bgColor = null;
                                offScreenImageGC.setBackground(bgColor != null ? bgColor : shellBackground);
                                offScreenImageGC.fillRectangle(imageData.x, imageData.y,
                                        imageData.width, imageData.height);
                                break;
                            case SWT.DM_FILL_PREVIOUS:
                                /*
                                 * Restore the previous image before drawing.
                                 */
                                offScreenImageGC.drawImage(
                                        image,
                                        0,
                                        0,
                                        imageData.width,
                                        imageData.height,
                                        imageData.x,
                                        imageData.y,
                                        imageData.width,
                                        imageData.height);
                                break;
                        }

                        imageDataIndex = (imageDataIndex + 1) % imageDataArray.length;
                        imageData = imageDataArray[imageDataIndex];
                        image.dispose();
                        image = new Image(display, imageData);
                        offScreenImageGC.drawImage(
                                image,
                                0,
                                0,
                                imageData.width,
                                imageData.height,
                                imageData.x,
                                imageData.y,
                                imageData.width,
                                imageData.height);

                        /*
                         * Draw the off-screen image
                         */

                        display.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                if (LoadingComposite.this.isDisposed()) {
                                    return;
                                }

                                Point point = LoadingComposite.this.getSize();
                                int xPos = point.x / 2 - offScreenImage.getBounds().width / 2;
                                int yPos = point.y / 2 - offScreenImage.getBounds().height / 2;
                                shellGC.fillRectangle(0, 0, point.x, point.y);
                                shellGC.drawImage(offScreenImage, xPos, yPos);
                            }
                        });

                        /*
                         * Sleep for the specified delay time (adding
                         * commonly-used slow-down fudge factors).
                         */
                        try {
                            int ms = imageData.delayTime * 10;
                            if (ms < 20)
                                ms += 30;
                            if (ms < 30)
                                ms += 10;
                            Thread.sleep(ms);
                        } catch (InterruptedException e) {
                        }

                        /*
                         * If we have just drawn the last image, decrement the
                         * repeat count and start again.
                         */
                        if (imageDataIndex == imageDataArray.length - 1)
                            repeatCount--;
                    }
                } catch (SWTException ignored) {
                    // Assuming thread was stopped because component was
                    // disposed
                } finally {
                    if (offScreenImage != null && !offScreenImage.isDisposed())
                        offScreenImage.dispose();
                    if (offScreenImageGC != null && !offScreenImageGC.isDisposed())
                        offScreenImageGC.dispose();
                    if (image != null && !image.isDisposed())
                        image.dispose();
                }
            }
        };

        animateThread.setDaemon(true);
        animateThread.start();
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
