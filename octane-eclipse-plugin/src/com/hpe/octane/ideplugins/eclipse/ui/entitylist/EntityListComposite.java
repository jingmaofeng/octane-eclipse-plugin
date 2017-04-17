package com.hpe.octane.ideplugins.eclipse.ui.entitylist;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.mywork.MyWorkUtil;
import com.hpe.octane.ideplugins.eclipse.filter.EntityListData;
import com.hpe.octane.ideplugins.eclipse.util.ControlProvider;
import com.hpe.octane.ideplugins.eclipse.util.DelayedModifyListener;
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

    private static final Set<String> defaultClientSideQueryFields = DefaultRowEntityFields.entityFields
            .values()
            .stream()
            .flatMap(coll -> coll.stream())
            .collect(Collectors.toSet());

    private Set<Entity> filterTypes;
    private Set<String> clientSideQueryFields;

    // Currently only fatlines
    private EntityListViewer entityListViewer;
    private ControlProvider<EntityListViewer> controlProvider;

    public EntityListComposite(
            Composite parent,
            int style,
            EntityListData entityListData,
            ControlProvider<EntityListViewer> controlProvider) {

        this(parent, style, entityListData, controlProvider, defaultFilterTypes, defaultClientSideQueryFields);
    }

    public EntityListComposite(
            Composite parent,
            int style,
            EntityListData entityListData,
            ControlProvider<EntityListViewer> controlProvider,
            Set<Entity> filterTypes,
            Set<String> clientSideQueryFields) {

        super(parent, style);
        setLayout(new GridLayout(1, false));

        this.entityListData = entityListData;
        this.controlProvider = controlProvider;

        this.filterTypes = filterTypes;
        this.clientSideQueryFields = clientSideQueryFields;

        entityListData.setTypeFilter(filterTypes);
        entityListData.setStringFilterFields(clientSideQueryFields);

        init();
        entityListViewer.setEntityModels(entityListData.getEntityList());
    }

    private void init() {

        entityTypeSelectorComposite = new EntityTypeSelectorComposite(this, SWT.NONE, filterTypes.toArray(new Entity[] {}));
        entityTypeSelectorComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        entityTypeSelectorComposite.checkAll();
        entityTypeSelectorComposite.addSelectionListener(() -> {
            entityListData.setTypeFilter(entityTypeSelectorComposite.getCheckedEntityTypes());
        });

        textFilter = new Text(this, SWT.BORDER);
        textFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        textFilter.setMessage("Filter");
        textFilter.addModifyListener(new DelayedModifyListener((e) -> {
            String text = textFilter.getText();
            text = text.trim();
            text = text.toLowerCase();
            entityListData.setStringFilter(text);
        }));

        // Just a placeholder for the viewer
        Composite compositeEntityList = new Composite(this, SWT.NONE);
        compositeEntityList.setLayout(new FillLayout(SWT.HORIZONTAL));
        compositeEntityList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        entityListViewer = controlProvider.createControl(compositeEntityList);
        entityListData.addDataChangedHandler(entityList -> entityListViewer.setEntityModels(entityList));
        entityListData.addDataChangedHandler(entityList -> {
            entityTypeSelectorComposite
                    .setEntityTypeCount(
                            countEntitiesByType(entityListData.getOriginalEntityList()));
        });
    }

    private Map<Entity, Integer> countEntitiesByType(Collection<EntityModel> entities) {
        Map<Entity, Integer> result = new HashMap<>();

        entities.forEach(entityModel -> {
            Entity entityType = Entity.getEntityType(entityModel);
            if (entityType == Entity.USER_ITEM) {
                entityType = Entity.getEntityType(MyWorkUtil.getEntityModelFromUserItem(entityModel));
            }
            if (!result.containsKey(entityType)) {
                result.put(entityType, 0);
            }
            result.put(entityType, result.get(entityType) + 1);
        });
        return result;
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