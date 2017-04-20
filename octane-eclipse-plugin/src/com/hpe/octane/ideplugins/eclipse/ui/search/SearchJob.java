package com.hpe.octane.ideplugins.eclipse.ui.search;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.nonentity.EntitySearchService;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.filter.EntityListData;

public class SearchJob extends Job {

    private EntitySearchService searchService = Activator.getInstance(EntitySearchService.class);
    private boolean isCancelled = false;
    private EntityListData resultEntityListData;
    private String query;

    public SearchJob(String name, String query, EntityListData resultEntityListData) {
        super(name);
        this.resultEntityListData = resultEntityListData;
        this.query = query;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        isCancelled = false;

        monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);

        Collection<EntityModel> searchResults = searchService.searchGlobal(
                query,
                20,
                SearchEditor.searchEntityTypes.toArray(new Entity[] {}));

        if (!isCancelled) {
            Display.getDefault().asyncExec(() -> {
                resultEntityListData.setEntityList(searchResults);
            });
        }

        monitor.done();
        return Status.OK_STATUS;
    }

    @Override
    protected void canceling() {
        isCancelled = true;
    }

}