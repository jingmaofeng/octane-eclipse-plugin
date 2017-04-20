package com.hpe.octane.ideplugins.eclipse.ui.activeitem;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;

public class ImageDataImageDescriptor extends ImageDescriptor {

    private ImageData imageData;

    public ImageDataImageDescriptor(ImageData imageData) {
        this.imageData = imageData;
    }

    @Override
    public ImageData getImageData() {
        return imageData;
    }
}