package com.hpe.octane.ideplugins.eclipse.ui.entitylist;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.filter.EntityListData;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.FatlineEntityListViewer;
import com.hpe.octane.ideplugins.eclipse.util.PredefinedEntityComparator;

public class EntityListComposite extends Composite {

    private EntityListData entityListData;
    private Text textFilter;
    private EntityTypeSelectorComposite entityTypeSelectorComposite;

    private static final Set<Entity> defaultFilterTypes = new LinkedHashSet<>(DefaultRowEntityFields.entityFields
            .keySet()
            .stream()
            .sorted(new PredefinedEntityComparator())
            .collect(Collectors.toList()));

    private static final Set<String> clientSideQueryFields = DefaultRowEntityFields.entityFields
            .values()
            .stream()
            .flatMap(coll -> coll.stream())
            .collect(Collectors.toSet());

    // Currently only fatlines
    private EntityListViewer entityListViewer;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public EntityListComposite(Composite parent, int style, EntityListData entityListData) {
        super(parent, style);
        setLayout(new GridLayout(1, false));

        this.entityListData = entityListData;

        entityListData.setTypeFilter(defaultFilterTypes);
        entityListData.setStringFilterFields(clientSideQueryFields);

        init();
        entityListViewer.setEntityModels(entityListData.getEntityList());
    }

    /**
     * Change viewer implementation
     * 
     * @param entityListViewer
     */
    public void setEntityListViewer(EntityListViewer entityListViewer) {
        this.entityListViewer = entityListViewer;
    }

    private void init() {

        entityTypeSelectorComposite = new EntityTypeSelectorComposite(this, SWT.NONE, defaultFilterTypes.toArray(new Entity[] {}));
        entityTypeSelectorComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        entityTypeSelectorComposite.checkAll();
        entityTypeSelectorComposite.addSelectionListener(() -> {
            entityListData.setTypeFilter(entityTypeSelectorComposite.getCheckedEntityTypes());
        });

        textFilter = new Text(this, SWT.BORDER);
        textFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        textFilter.setMessage("Filter");

        textFilter.addModifyListener(new ModifyListener() {

            private Timer fireEventTimer = new Timer();

            @Override
            public void modifyText(ModifyEvent e) {
                fireEventTimer.cancel();
                fireEventTimer = new Timer();
                fireEventTimer.schedule(createTask(), 500);
            }

            private TimerTask createTask() {
                return new TimerTask() {
                    @Override
                    public void run() {
                        Display.getDefault().asyncExec(() -> {
                            String text = textFilter.getText();
                            text = text.trim();
                            text = text.toLowerCase();
                            entityListData.setStringFilter(text);
                        });
                    }
                };
            }
        });

        // Just a placeholder for the viewer
        Composite compositeEntityList = new Composite(this, SWT.NONE);
        compositeEntityList.setLayout(new FillLayout(SWT.HORIZONTAL));
        compositeEntityList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        entityListViewer = new FatlineEntityListViewer(compositeEntityList, SWT.NONE);
        entityListData.addDataChangedHandler(entityList -> entityListViewer.setEntityModels(entityList));
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public void addEntityMouseListener(EntityMouseListener entityMouseListener) {
        entityListViewer.addEntityMouseListener(entityMouseListener);
    }

    public void removeEntityMouseListener(EntityMouseListener entityMouseListener) {
        entityListViewer.removeEntityMouseListener(entityMouseListener);
    }

}