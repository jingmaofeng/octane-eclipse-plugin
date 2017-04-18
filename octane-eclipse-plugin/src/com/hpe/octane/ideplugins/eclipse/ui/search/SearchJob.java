package com.hpe.octane.ideplugins.eclipse.ui.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
import com.hpe.octane.ideplugins.eclipse.util.PredefinedEntityComparator;

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

        Collection<EntityModel> searchResults = search(query);

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