/*******************************************************************************
 * © 2017 EntIT Software LLC, a Micro Focus company, L.P.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.util.EntityUtil;

public class EntityComboBox extends Composite {

    private static final int MAX_HEIGHT = 400;
    private static final int MIN_HEIGHT = 200;
    private static final int MIN_WIDTH = 200;

    public interface EntityLoader {
        public List<EntityModel> loadEntities(String searchQuery);
    }

    private LabelProvider labelProvider;
    
    private List<EntityModel> entityList;
    private EntityLoader entityLoader;

    private List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();
    private List<EntityModel> selectedEntities = new ArrayList<>();

    private int selectionMode;

    private Shell shell;
    private TruncatingStyledText textSelection;

    private Composite btnComposite;
    
    /**
     * @param parent composite
     * @param style SWT.SINGLE for single selection SWT.MULTI for multi selection
     * @param labelProvider create a string for each entity model to display in the list
     * @param entityLoader lamda used by the control to load the data
     * 
     * @see SWT.SINGLE
     * @see SWT.MULTI
     */
    public EntityComboBox(Composite parent, int style, LabelProvider labelProvider, EntityLoader entityLoader) {
        super(parent, SWT.BORDER);
        
        selectionMode = SWT.SINGLE | style & (SWT.SINGLE | SWT.MULTI);

        this.entityLoader = entityLoader;
        this.labelProvider = labelProvider;
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginHeight = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        setLayout(gridLayout);

        textSelection = new TruncatingStyledText(this, SWT.READ_ONLY | SWT.SINGLE);
        textSelection.setAlwaysShowScrollBars(false);
        textSelection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        textSelection.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                createAndShowShell();
                displayEntities(textSelection.getText());
            }
        });
        
        Button btnArrow = new Button(this, SWT.FLAT | SWT.ARROW | SWT.DOWN);
        btnArrow.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
        btnArrow.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createAndShowShell();
                displayEntities(textSelection.getText());
            }
        });
    } 

    private void displayEntities(String searchTerm) {  
        if(btnComposite == null || btnComposite.isDisposed()) {
            return;
        }

        Job getEntitiesJob = new Job(searchTerm) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                entityList = entityLoader.loadEntities(searchTerm);                
                return Status.OK_STATUS;
            }
        };

        getEntitiesJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void scheduled(IJobChangeEvent event) {
                Display.getDefault().syncExec(() -> {
                    if(btnComposite.isDisposed()) {
                        return;
                    }
                    Arrays.stream(btnComposite.getChildren()).forEach(Control::dispose);
                    
                    LoadingComposite loadingComposite = new LoadingComposite(btnComposite, SWT.NONE);
                    loadingComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                    btnComposite.layout();
                    btnComposite.setSize(btnComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                });
            }
            @Override
            public void done(IJobChangeEvent event) {
                Display.getDefault().syncExec(() -> {
                    
                    //The shell might have been close before the result has been fetched from the server
                    //In this case discard the result
                    if(btnComposite.isDisposed()) {
                        return;
                    }
                    
                    Arrays.stream(btnComposite.getChildren()).forEach(Control::dispose);
                    
                    if(entityList == null || entityList.isEmpty()) {
                        Label lblNoResult = new Label(btnComposite, SWT.NONE);
                        lblNoResult.setText("No results");
                        lblNoResult.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
                    } else {
                        for (EntityModel entityModel : entityList) {
                            Button button = new Button(btnComposite, SWT.CHECK);
                            button.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
                            button.setText(labelProvider.getText(entityModel));
                            button.setSelection(EntityUtil.containsEntityModel(selectedEntities, entityModel));
                            
                            button.addListener(SWT.Selection, e -> {
                                if(selectionMode == SWT.SINGLE) {
                                    selectedEntities.clear();
                                }
                                
                                if (button.getSelection()) {
                                    selectedEntities.add(entityModel);
                                } else {
                                    EntityUtil.removeEntityModel(selectedEntities, entityModel);
                                }
                                selectionListeners.forEach(l -> l.widgetSelected(new SelectionEvent(e)));
                                
                                //Set text to selection
                                textSelection.setText(getLabelForSelection(selectedEntities));
                                textSelection.setSize(textSelection.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                                textSelection.getParent().layout();
                                
                                if(selectionMode == SWT.SINGLE) {
                                    closeAndDisposeShell();
                                }
                            });
                        }
                    }
                   
                    btnComposite.layout();
                    btnComposite.setSize(btnComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                    resizeShellToContent();
                });
            }
        });

        getEntitiesJob.schedule();
    }
    
    public void setSelectedEntity(EntityModel entityModel) {
        selectedEntities.clear();
        selectedEntities.add(entityModel);
    }
    
    public void setSelectedEntities(Collection<EntityModel> entityModel) {
        selectedEntities.clear();
        selectedEntities.addAll(entityModel);
    }
    
    public int getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(int selectionMode) {
        this.selectionMode = selectionMode;
    }

    public Collection<EntityModel> getSelectedEntities() {
        if(selectionMode == SWT.SINGLE) {
            throw new RuntimeException("Unsupported method for selection mode: SWT.SINGLE");
        }
        return selectedEntities;
    }
    
    public EntityModel getSelectedEntity() {
        if(selectionMode == SWT.MULTI) {
            throw new RuntimeException("Unsupported method for selection mode: SWT.MULTI");
        }
        return selectedEntities.size() == 0 ? null : selectedEntities.get(0);
    }

    private String getLabelForSelection(List<EntityModel> selectedEntities) {
        return selectedEntities
                .stream()
                .map(labelProvider::getText)
                .collect(Collectors.joining(" | "));        
    }

    private void createAndShowShell() {
        shell = new Shell(textSelection.getShell(), SWT.BORDER);
        shell.setLayout(new GridLayout());

        Text textSearch = new Text(shell, SWT.BORDER);
        textSearch.setMessage("Search");
        textSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        textSearch.addModifyListener(new DelayedModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                displayEntities(textSearch.getText());
            }
        }));

        ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        btnComposite = new Composite(scrolledComposite, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 5;
        btnComposite.setLayout(gridLayout);
        
        scrolledComposite.setContent(btnComposite);     

        shell.open();
        shell.addListener(SWT.Deactivate, e -> {
            if (shell != null && !shell.isDisposed()) {
                shell.setVisible(false);
                shell.dispose();
            }
        });

        resizeShellToContent();
        poistionShell();
    }
    
    /**
     * Disposed automatically on SWT.Deactivate by listener
     * @see EntityComboBox#createAndShowShell()
     */
    private void closeAndDisposeShell() {
        if(shell != null && !shell.isDisposed()) {
            shell.close();
        }
    }

    private void resizeShellToContent() {
        Point shellSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        shellSize = limitContentSize(shell);
        shell.setSize(shellSize);
    }
    
    private void poistionShell() {
        Point shellSize = shell.getSize();
        Point parentBounds = textSelection.getParent().toDisplay(textSelection.getLocation());
        Point size = textSelection.getSize();
        Rectangle shellRect = new Rectangle(parentBounds.x - shellSize.x + size.x, parentBounds.y + size.y, shellSize.x, shellSize.y);
        shell.setBounds(shellRect);
    }

    private Point limitContentSize(Control control) {
        Point contentSize = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);

        //limit height
        int shellHeight = contentSize.y > MAX_HEIGHT ? MAX_HEIGHT : contentSize.y;
        shellHeight = shellHeight < MIN_HEIGHT ? MIN_HEIGHT : shellHeight;

        //limit width
        int shellWidth = contentSize.x < MIN_WIDTH ? MIN_WIDTH : contentSize.x;

        contentSize.y = shellHeight;
        contentSize.x = shellWidth;
        return contentSize;	
    }

    public boolean isVisible() {
        return shell != null && !shell.isDisposed() && shell.isVisible();
    }
}