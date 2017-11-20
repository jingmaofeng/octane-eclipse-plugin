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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.hpe.octane.ideplugins.eclipse.ui.util.resource.SWTResourceManager;

/**
 * Composite used to display an error message
 */
public class ErrorComposite extends Composite {

    private Label lblError;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public ErrorComposite(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(1, false));
        lblError = new Label(this, SWT.NONE);
        lblError.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
        lblError.setAlignment(SWT.CENTER);
        lblError.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public void setErrorMessage(String errorMessage) {
        lblError.setText(errorMessage);
    }

}
