package com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityListViewer;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityMouseListener;
import com.hpe.octane.ideplugins.eclipse.util.SWTResourceManager;

public class FatlineEntityListViewer extends Composite implements EntityListViewer {

    private static final EntityModelRenderer entityModelRenderer = new DefaultEntityModelRenderer();
    private static final Color selectionColor = SWTResourceManager.getColor(255, 105, 180);
    private static final Color foregroundColor = SWTResourceManager.getColor(255, 255, 255);

    // Keep insertion order
    private BiMap<EntityModel, EntityModelRow> entities;

    private EntityModel previousSelection;
    private EntityModel selection;

    private Composite rowComposite;
    private ScrolledComposite rowScrollComposite;

    private int topMargins = 2;
    private int sideMargin = 2;
    private int rowMargins = 2;

    private List<EntityMouseListener> entityMouseListeners = new ArrayList<>();

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public FatlineEntityListViewer(Composite parent, int style) {
        super(parent, style);
        setLayout(new FillLayout(SWT.HORIZONTAL));

        rowScrollComposite = new ScrolledComposite(this, SWT.BORDER | SWT.V_SCROLL);
        rowScrollComposite.setExpandHorizontal(true);
        rowScrollComposite.setExpandVertical(true);

        rowComposite = new Composite(rowScrollComposite, SWT.NONE);
        rowComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        parent.addControlListener(new ControlListener() {
            @Override
            public void controlResized(ControlEvent e) {
                paint();
            }

            @Override
            public void controlMoved(ControlEvent e) {
            }
        });

        // Selection
        rowComposite.getDisplay().addFilter(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                handleMouseFilterEvent(event);
            }
        });

        rowScrollComposite.setContent(rowComposite);
        rowScrollComposite.setMinSize(rowComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        // rowComposite.addMouseListener(debugMouseListener);
    }

    private void handleMouseFilterEvent(Event event) {
        EntityModelRow row = getEntityModelRowFromMouseFilter(event);
        if (row != null) {
            EntityModel entityModel = entities.inverse().get(row);
            changeSelection(entityModel);
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
            paint();
        }
    }

    @Override
    public void setEntityModels(Collection<EntityModel> entityModels) {

        Map<EntityModel, EntityModelRow> tempMap = new LinkedHashMap<>();

        clearRowComposite();
        entityModels.forEach(entityModel -> {
            EntityModelRow row = entityModelRenderer.createRow(rowComposite, entityModel);
            tempMap.put(entityModel, row);
            Menu popupMenu = new Menu(row);
            MenuItem newItem = new MenuItem(popupMenu, SWT.CASCADE);
            newItem.setText("New");
            MenuItem refreshItem = new MenuItem(popupMenu, SWT.NONE);
            refreshItem.setText("Refresh");
            MenuItem deleteItem = new MenuItem(popupMenu, SWT.NONE);
            deleteItem.setText("Delete");
            row.setMenu(popupMenu);
        });

        this.entities = ImmutableBiMap.copyOf(tempMap);

        paint();
    }

    private void clearRowComposite() {
        Arrays.stream(rowComposite.getChildren()).forEach(control -> control.dispose());
    }

    private void paint() {
        int scrollContainerHeight = rowScrollComposite.getBounds().height;
        int containerHeight = rowComposite.getBounds().height;

        int rowWidth = getParent().getBounds().width - (sideMargin * 2);
        int rowHeight = 53;

        if (scrollContainerHeight < containerHeight) {
            // needs a scrollbar, needs more space for it
            rowWidth -= 20;
        }

        int x = sideMargin;
        int y = topMargins;

        for (EntityModelRow row : entities.values()) {
            // System.out.println(String.format("row bounds: x: %d y: %d width:
            // %d height: %d", x, y, rowWidth, rowHeight));
            row.setBounds(x, y, rowWidth, rowHeight);
            y += rowHeight + rowMargins;
        }

        rowScrollComposite.setMinSize(rowComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        paintSelected();
    }

    private void paintSelected() {
        if (entities.containsKey(selection) && selection != null) {
            EntityModelRow row = entities.get(selection);
            if (!row.isDisposed()) {
                row.setBackground(selectionColor);
            }
        } else {
            selection = null;
        }
        if (entities.containsKey(previousSelection)) {
            EntityModelRow row = entities.get(previousSelection);
            if (!row.isDisposed()) {
                row.setBackground(foregroundColor);
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
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    @Override
    public void addEntityMouseListener(EntityMouseListener entityMouseListener) {
        entityMouseListeners.add(entityMouseListener);
    }

    @Override
    public void removeEntityMouseListener(EntityMouseListener entityMouseListener) {
        entityMouseListeners.remove(entityMouseListener);
    }
}
