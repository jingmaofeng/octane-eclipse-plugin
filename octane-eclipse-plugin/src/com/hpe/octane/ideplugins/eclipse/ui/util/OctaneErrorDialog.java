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

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;

import com.hpe.adm.nga.sdk.exception.OctaneException;
import com.hpe.adm.nga.sdk.model.ErrorModel;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.PlatformResourcesManager;

public class OctaneErrorDialog extends Dialog {

    private Shell shell;
    private Composite compositeExceptionData;
    private Composite compositeBtns;

    public OctaneErrorDialog(Shell parent) {
        super(parent);
        shell = new Shell(getParent(), SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.PRIMARY_MODAL);
        shell.setSize(600, 300);
        shell.setLayout(new GridLayout(1, false));
        shell.setImage(PlatformResourcesManager.getPlatformImage(ISharedImages.IMG_DEC_FIELD_ERROR));

        compositeExceptionData = new Composite(shell, SWT.NONE);
        compositeExceptionData.setLayout(new GridLayout(1, false));
        compositeExceptionData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1)); 
        
        Label separator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1)); 
        
        compositeBtns = new Composite(shell, SWT.NONE);
        FillLayout fl_compositeBtns = new FillLayout(SWT.HORIZONTAL);
        fl_compositeBtns.spacing = 10;
        compositeBtns.setLayout(fl_compositeBtns);
        compositeBtns.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    }
    
    private static void positionShell(Shell shell) {
        Monitor primary = shell.getMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);
    }

    public void openException(Exception exception, String title) {
        positionShell(shell);
        shell.setText(title);
        
        //Specific data for OctaneExceptions
        if(exception instanceof OctaneException) {
            ErrorModel errorModel = ((OctaneException) exception).getError();
            Label lbl = new Label(compositeExceptionData, SWT.NONE);
            lbl.setText(Util.getUiDataFromModel(errorModel.getValue("correlationId")));
        } else {
            //Generic handling
            Text exceptionText = new Text(compositeExceptionData, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
            exceptionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            exceptionText.setText(exception.getMessage());   
        }
        
        shell.open();
        
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }
    
    public void addButton(String btnText, Runnable clickedRunnable) {
        Button btn = new Button(compositeBtns, SWT.NONE);
        btn.setText(btnText);
        btn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                clickedRunnable.run();
            }
        });
    }
    
    public void clearButtons() {
        Arrays.stream(compositeBtns.getChildren()).forEach(Control::dispose);
    }
    
    public void close() {
        shell.close();
    }
}
