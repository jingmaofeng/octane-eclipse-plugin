package com.hpe.octane.ideplugins.eclipse.ui.search;

import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_DESCRIPTION;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_ID;
import static com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants.FIELD_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.nonentity.EntitySearchService;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.filter.ArrayEntityListData;
import com.hpe.octane.ideplugins.eclipse.ui.OctaneViewPart;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityListComposite;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.FatlineEntityListViewer;
import com.hpe.octane.ideplugins.eclipse.ui.util.NoSearchResultsComposite;
import com.hpe.octane.ideplugins.eclipse.util.DelayedModifyListener;
import com.hpe.octane.ideplugins.eclipse.util.PredefinedEntityComparator;

public class SearchView extends OctaneViewPart {

    // private static final ILog logger = Activator.getDefault().getLog();

    public static final String ID = "com.hpe.octane.ideplugins.eclipse.ui.SearchView";

    private static final Set<Entity> searchEntityTypes = new HashSet<>(Arrays.asList(
            Entity.EPIC,
            Entity.FEATURE,
            Entity.USER_STORY,
            Entity.QUALITY_STORY,
            Entity.DEFECT,
            Entity.TASK,
            Entity.MANUAL_TEST,
            Entity.GHERKIN_TEST,
            Entity.TEST_SUITE_RUN,
            Entity.MANUAL_TEST_RUN,
            Entity.COMMENT));

    private static final Set<String> searchEntityFilterFields = new HashSet<>(Arrays.asList(FIELD_ID, FIELD_NAME, FIELD_DESCRIPTION));

    private EntitySearchService searchService = Activator.getInstance(EntitySearchService.class);
    private ArrayEntityListData entityData = new ArrayEntityListData();
    private EntityListComposite entityListComposite;
    private Text textFilter;

    /**
     * Shown when my work service returns an empty list
     */
    private NoSearchResultsComposite noSearchResultsComposite;

    @Override
    public Control createOctanePartControl(Composite parent) {

        entityListComposite = new EntityListComposite(
                parent,
                SWT.NONE,
                entityData,
                (parentControl) -> {
                    return new FatlineEntityListViewer((Composite) parentControl,
                            SWT.NONE,
                            null,
                            new SearchResultRowRenderer());
                },
                searchEntityTypes,
                searchEntityFilterFields);

        noSearchResultsComposite = new NoSearchResultsComposite(parent, SWT.NONE);

        showControl(noSearchResultsComposite);

        // Add refresh action to view
        IActionBars viewToolbar = getViewSite().getActionBars();
        TextContributionItem textContributionItem = new TextContributionItem(ID + ".searchtext") {
            @Override
            protected Text createText(Composite parent) {
                SearchView.this.textFilter = new Text(parent, SWT.BORDER);
                textFilter.setMessage("Search");
                textFilter.addModifyListener(new DelayedModifyListener((e) -> {

                    showLoading();

                    String query = textFilter.getText().trim().toLowerCase();
                    if (query.trim().length() != 0) {
                        entityData.setEntityList(search(query));
                        if (entityData.getEntityList().size() == 0) {
                            showControl(noSearchResultsComposite);
                        } else {
                            showContent();
                        }
                    } else {
                        // TODO: show "search for something"
                    }
                }));
                return textFilter;
            }
        };
        viewToolbar.getToolBarManager().add(textContributionItem);

        Runnable changeHandler = () -> {
            if (!Activator.getConnectionSettings().isEmpty()) {
                if (textFilter != null)
                    textFilter.setEnabled(true);
            } else {
                if (textFilter != null)
                    textFilter.setEnabled(false);
                showWelcome();
            }
        };

        Activator.addConnectionSettingsChangeHandler(changeHandler);
        changeHandler.run(); // Init

        return entityListComposite;
    }

    @Override
    public void setFocus() {
    }

    private List<EntityModel> search(String query) {

        Collection<EntityModel> searchResults = new ArrayList<>();

        searchResults = searchService.searchGlobal(query, Entity.WORK_ITEM);
        searchResults.addAll(searchService.searchGlobal(query, Entity.TASK));
        searchResults.addAll(searchService.searchGlobal(query, Entity.TEST));

        return searchResults
                .stream()
                .sorted((entityLeft, entityRight) -> {
                    Entity entityTypeLeft = Entity.getEntityType(entityLeft);
                    Entity entityTypeRight = Entity.getEntityType(entityRight);
                    if (entityTypeLeft != entityTypeRight) {
                        return new PredefinedEntityComparator().compare(entityTypeLeft, entityTypeRight);
                    } else {
                        Long leftId = Long.parseLong(entityLeft.getValue("id").getValue().toString());
                        Long rightId = Long.parseLong(entityRight.getValue("id").getValue().toString());
                        return leftId.compareTo(rightId);
                    }
                }).collect(Collectors.toList());
    }

}
