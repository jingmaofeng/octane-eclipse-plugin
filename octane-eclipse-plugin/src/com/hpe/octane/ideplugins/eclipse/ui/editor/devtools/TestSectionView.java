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
package com.hpe.octane.ideplugins.eclipse.ui.editor.devtools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TestSectionView extends Composite {

    public TestSectionView(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(4, false));

        Label lblNewLabel = new Label(this, SWT.NONE);
        lblNewLabel.setText("LabelColumnOne");

        Label lblNewLabel_1 = new Label(this, SWT.NONE);
        lblNewLabel_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        lblNewLabel_1.setText("valueColumnOne");

        Label lblNewLabel_2 = new Label(this, SWT.NONE);
        lblNewLabel_2.setText("LabelColumnTwo");

        Label lblNewLabel_3 = new Label(this, SWT.NONE);
        lblNewLabel_3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        lblNewLabel_3.setText("valueColum nTwovalueColum nTwovalueColum nTwovalueColum nTwovalueColum nTwovalueColum nTwovalueColum nTwo");
        // TODO Auto-generated constructor stub
    }

}
