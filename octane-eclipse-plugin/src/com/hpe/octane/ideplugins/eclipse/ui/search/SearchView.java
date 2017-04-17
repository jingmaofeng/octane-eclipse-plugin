package com.hpe.octane.ideplugins.eclipse.ui.search;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.ILog;
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
import com.hpe.octane.ideplugins.eclipse.ui.util.ErrorComposite;
import com.hpe.octane.ideplugins.eclipse.ui.util.NoSearchResultsComposite;
import com.hpe.octane.ideplugins.eclipse.util.DelayedModifyListener;

public class SearchView extends OctaneViewPart {

    private static final ILog logger = Activator.getDefault().getLog();

    public static final String ID = "com.hpe.octane.ideplugins.eclipse.ui.SearchView";

    private EntitySearchService searchService = Activator.getInstance(EntitySearchService.class);
    private ArrayEntityListData entityData = new ArrayEntityListData();
    private EntityListComposite entityListComposite;
    private Text textFilter;

    /**
     * Shown when my work service returns an empty list
     */
    private NoSearchResultsComposite noSearchResultsComposite;
    private ErrorComposite errorComposite;

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
                });

        noSearchResultsComposite = new NoSearchResultsComposite(parent, SWT.NONE);
        errorComposite = new ErrorComposite(parent, SWT.NONE);

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

                    String query = textFilter.getText().toLowerCase();

                    if (query.trim().length() != 0) {
                        Collection<EntityModel> searchResults = new ArrayList<>();
                        searchResults = searchService.searchGlobal(query, Entity.WORK_ITEM);
                        searchResults.addAll(searchService.searchGlobal(query, Entity.TASK));
                        searchResults.addAll(searchService.searchGlobal(query, Entity.TEST));
                        entityData.setEntityList(searchResults);
                    }

                    showContent();
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

}
