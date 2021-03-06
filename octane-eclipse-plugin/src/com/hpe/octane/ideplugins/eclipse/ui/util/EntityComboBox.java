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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.SWTResourceManager;

public class EntityComboBox extends Composite {

    private static final int MAX_HEIGHT = 400;
    private static final int MIN_HEIGHT = 200;
    private static final int MIN_WIDTH = 200;
    
    private static final MouseTrackAdapter focusMouseTrackAdapter = new MouseTrackAdapter() {
        @Override
        public void mouseEnter(MouseEvent mouseEvent) {
            Control control = (Control) mouseEvent.getSource();
            control.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
            control.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION_TEXT));
        }
        @Override
        public void mouseExit(MouseEvent mouseEvent) {
            Control control = (Control) mouseEvent.getSource();
            control.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
            control.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_FOREGROUND));
        }
    };

    public interface EntityLoader {
        public List<EntityModel> loadEntities(String searchQuery);
    }

    private LabelProvider labelProvider;
    
    private List<EntityModel> entityList;
    private EntityLoader entityLoader;

    private List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();
    private List<EntityModel> selectedEntities = new ArrayList<>();

    private int selectionMode;

    /**
     * Floating window that appears 
     */
    private Shell shell;
    private TruncatingStyledText textSelection;
    
    /**
     * Contains the possible entity controls
     */
    private Composite optionsComposite;
    
    /**
     * Cosmetic arrow button, to make it look like a combo box 
     */
    private Button btnArrow;
    
    /**
     * @param parent composite
     * @param style SWT.SINGLE for single selection SWT.MULTI for multi selection
     * @param labelProvider create a string for each entity model to display in the list
     * @param entityLoader lambda used by the control to load the data
     * 
     * @see SWT.SINGLE
     * @see SWT.MULTI
     */
    public EntityComboBox(Composite parent, int style, LabelProvider labelProvider, EntityLoader entityLoader) {
        super(parent, SWT.BORDER);
        
        selectionMode = (style & SWT.MULTI) != 0 ? SWT.MULTI : SWT.SINGLE; 
        this.entityLoader = entityLoader;
        this.labelProvider = labelProvider == null ? new LabelProvider() : labelProvider;
        
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
        
        btnArrow = new Button(this, SWT.FLAT | SWT.ARROW | SWT.DOWN);
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
        if(optionsComposite == null || optionsComposite.isDisposed()) {
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
                    if(optionsComposite.isDisposed()) {
                        return;
                    }
                    Arrays.stream(optionsComposite.getChildren()).forEach(Control::dispose);
                    
                    LoadingComposite loadingComposite = new LoadingComposite(optionsComposite, SWT.NONE);
                    loadingComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                    optionsComposite.setSize(optionsComposite.getParent().getSize());
                    optionsComposite.layout();
                });
            }
            @Override
            public void done(IJobChangeEvent event) {
                Display.getDefault().syncExec(() -> {
                    
                    //The shell might have been close before the result has been fetched from the server
                    //In this case discard the result
                    if(optionsComposite.isDisposed()) {
                        return;
                    }
                    
                    Arrays.stream(optionsComposite.getChildren()).forEach(Control::dispose);
                    
                    if(entityList == null || entityList.isEmpty()) {
                        Label lblNoResult = new Label(optionsComposite, SWT.NONE);
                        lblNoResult.setText("No results");
                        lblNoResult.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
                    } else if(selectionMode == SWT.SINGLE){
                        createSingleSelectionUI();
                    } else if(selectionMode == SWT.MULTI){
                        createMultiSelectionUI();
                    }
                   
                    optionsComposite.layout();
                    optionsComposite.setSize(optionsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                    resizeShellToContent();
                    poistionShell();
                });
            }
        });

        getEntitiesJob.schedule();
    }
    
    private void clearButtons() {
        if(!optionsComposite.isDisposed()) {
            Arrays.stream(optionsComposite.getChildren()).forEach(Control::dispose);
        }
    }
    
    private void createSingleSelectionUI() {
        clearButtons();
        entityList.forEach(entityModel -> {
            CLabel  lbl = new CLabel (optionsComposite, SWT.NONE);
            lbl.setText(labelProvider.getText(entityModel));
            GridData lblGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
            lblGridData.heightHint = 20;
            lbl.setLayoutData(lblGridData);
            
            lbl.addListener(SWT.MouseDown, e -> {
                selectedEntities.clear();
                selectedEntities.add(entityModel);
                selectionListeners.forEach(l -> l.widgetSelected(new SelectionEvent(e)));
                adjustTextToSelection();
                closeAndDisposeShell();
            });
            lbl.addMouseTrackListener(focusMouseTrackAdapter);
        });
    }
    
    private void createMultiSelectionUI() {
        clearButtons();
        entityList.forEach(entityModel -> {
            Button btn = new Button (optionsComposite, SWT.CHECK);
            btn.setText(labelProvider.getText(entityModel));
            btn.setSelection(EntityUtil.containsEntityModel(selectedEntities, entityModel));
            
            GridData lblGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
            lblGridData.heightHint = 20;
            btn.setLayoutData(lblGridData);
            
            btn.addListener(SWT.Selection, e -> {
                
                //it's being selected
                if(btn.getSelection() && !EntityUtil.containsEntityModel(selectedEntities, entityModel)) {
                    selectedEntities.add(entityModel);
                } 
                //it's being de-selected
                else if(!btn.getSelection() && EntityUtil.containsEntityModel(selectedEntities, entityModel)) {
                    EntityUtil.removeEntityModel(selectedEntities, entityModel);
                }
                
                selectionListeners.forEach(l -> l.widgetSelected(new SelectionEvent(e)));
                adjustTextToSelection();
            });
            btn.addMouseTrackListener(focusMouseTrackAdapter);
        });
    }
    
    private void adjustTextToSelection() {
        textSelection.setText(getLabelForSelection(selectedEntities));
        textSelection.setSize(textSelection.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        textSelection.getParent().layout();
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
        shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
        shell.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_FOREGROUND));
        shell.setBackgroundMode(SWT.INHERIT_FORCE);

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
        
        optionsComposite = new Composite(scrolledComposite, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 5;
        gridLayout.marginHeight = 0;
        optionsComposite.setLayout(gridLayout);
        
        scrolledComposite.setContent(optionsComposite);     

        shell.open();
        shell.addListener(SWT.Deactivate, e -> {
            if (shell != null && !shell.isDisposed()) {
                shell.setVisible(false);
                shell.dispose();
            }
            e.doit = true;
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
        Point btnLocation = this.toDisplay(btnArrow.getLocation());
        Rectangle shellRect = new Rectangle(
                btnLocation.x + btnArrow.getSize().x - shellSize.x, 
                btnLocation.y + btnArrow.getSize().y, 
                shellSize.x, 
                shellSize.y);
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