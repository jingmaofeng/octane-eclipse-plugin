package com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityListViewer;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityModelMenuFactory;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityMouseListener;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.rowrenderer.MyWorkEntityModelRowRenderer;
import com.hpe.octane.ideplugins.eclipse.util.resource.SWTResourceManager;

public class FatlineEntityListViewer extends Composite implements EntityListViewer {

    private static final EntityModelRenderer entityModelRenderer = new MyWorkEntityModelRowRenderer();
    private static final Color selectionBackgroundColor = SWTResourceManager.getColor(255, 105, 180);
    private static final Color selectionForegroundColor = SWTResourceManager.getColor(255, 255, 255);
    private static final Color backgroundColor = SWTResourceManager.getColor(255, 255, 255);
    private static final Color foregroundColor = SWTResourceManager.getColor(0, 0, 0);

    // Keep insertion order
    private BiMap<EntityModel, EntityModelRow> entities;
    private List<EntityMouseListener> entityMouseListeners = new ArrayList<>();

    private Composite rowComposite;
    private ScrolledComposite rowScrolledComposite;

    private EntityModel previousSelection;
    private EntityModel selection;

    private EntityModelMenuFactory entityModelMenuFactory;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public FatlineEntityListViewer(Composite parent, int style, EntityModelMenuFactory entityModelMenuFactory) {
        super(parent, SWT.NONE);
        setLayout(new FillLayout(SWT.HORIZONTAL));

        this.entityModelMenuFactory = entityModelMenuFactory;

        rowScrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        rowScrolledComposite.setExpandHorizontal(true);
        rowScrolledComposite.setExpandVertical(true);

        rowComposite = new Composite(rowScrolledComposite, SWT.NONE);
        rowComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginTop = 0;
        layout.marginRight = 0;
        layout.marginBottom = 0;
        layout.marginLeft = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;

        rowComposite.setLayout(layout);

        rowScrolledComposite.setContent(rowComposite);
        rowScrolledComposite.setMinSize(rowComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        // Selection
        rowComposite.getDisplay().addFilter(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                handleMouseFilterEvent(event);
            }
        });
    }

    @Override
    public void setEntityModels(Collection<EntityModel> entityModels) {
        clearRows();

        Map<EntityModel, EntityModelRow> tempMap = new LinkedHashMap<>();

        entityModels.forEach(entityModel -> {

            // Create the row
            EntityModelRow row = entityModelRenderer.createRow(rowComposite, entityModel);
            if (entityModelMenuFactory != null) {
                row.addMenuDetectListener(new EntityModelRowMenuDetectListener(row, entityModel, entityModelMenuFactory));
            }

            row.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
            tempMap.put(entityModel, row);
        });
        rowScrolledComposite.setMinSize(rowComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        rowComposite.pack();

        this.entities = ImmutableBiMap.copyOf(tempMap);
    }

    private void recreateRows() {
        setEntityModels(entities.keySet());
    }

    private void clearRows() {
        Arrays.stream(rowComposite.getChildren()).forEach(control -> control.dispose());
    }

    private void handleMouseFilterEvent(Event event) {
        EntityModelRow row = getEntityModelRowFromMouseFilter(event);
        if (row != null) {
            EntityModel entityModel = entities.inverse().get(row);

            row.setFocus();

            // Selection
            changeSelection(entityModel);

            // Fire listeners
            MouseEvent mouseEvent = new MouseEvent(event);
            for (EntityMouseListener listener : entityMouseListeners) {
                listener.mouseClick(entityModel, mouseEvent);
            }
        } else {
            changeSelection(null);
        }
    }

    private EntityModelRow getEntityModelRowFromMouseFilter(Event event) {
        if (event.widget instanceof Control) {
            for (EntityModelRow row : entities.values()) {
                if (containsControl(row, (Control) event.widget)) {
                    return row;
                }
            }
        }
        return null;
    }

    private void changeSelection(EntityModel entityModel) {
        if (entityModel != selection) {
            this.previousSelection = selection;
            this.selection = entityModel;
            paintSelected();
        }
    }

    private void paintSelected() {
        if (entities.containsKey(selection) && selection != null) {
            EntityModelRow row = entities.get(selection);
            if (!row.isDisposed()) {
                row.setBackground(selectionBackgroundColor);
                row.setLabelFontColor(selectionForegroundColor);
            }
        } else {
            selection = null;
        }
        if (entities.containsKey(previousSelection)) {
            EntityModelRow row = entities.get(previousSelection);
            if (!row.isDisposed()) {
                row.setBackground(backgroundColor);
                row.setLabelFontColor(foregroundColor);
            }
        } else {
            previousSelection = null;
        }
    }

    private static boolean containsControl(Control source, Control target) {
        if (source == target) {
            return true;
        } else if (source instanceof Composite) {
            for (Control control : ((Composite) source).getChildren()) {
                if (containsControl(control, target)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void addEntityMouseListener(EntityMouseListener entityMouseListener) {
        entityMouseListeners.add(entityMouseListener);

    }

    @Override
    public void removeEntityMouseListener(EntityMouseListener entityMouseListener) {
        entityMouseListeners.remove(entityMouseListener);
    }

    @Override
    public void setEntityModelMenuFatory(EntityModelMenuFactory entityModelMenuFactory) {
        this.entityModelMenuFactory = entityModelMenuFactory;
        recreateRows();
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
