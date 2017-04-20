package com.hpe.octane.ideplugins.eclipse.ui.search;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

import com.hpe.octane.ideplugins.eclipse.util.resource.ImageResources;

public class SearchEditorInput implements IElementFactory, IEditorInput {

    private static final String FACTORY_ID = "com.hpe.octane.ideplugins.eclipse.ui.search.SearchEditorInput";

    private String query = "";
    private static final ImageDescriptor searchImage = ImageDescriptor.createFromImage(ImageResources.SEARCH.getImage());

    public SearchEditorInput() {
    }

    public SearchEditorInput(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        return null;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return searchImage;
    }

    @Override
    public String getName() {
        return "\"" + query + "\"";
    }

    @Override
    public IPersistableElement getPersistable() {
        IPersistableElement persistableElement = new IPersistableElement() {
            @Override
            public void saveState(IMemento memento) {
                memento.putString("query", query);
            }

            @Override
            public String getFactoryId() {
                return FACTORY_ID;
            }
        };
        return persistableElement;
    }

    @Override
    public String getToolTipText() {
        return "\"" + query + "\"";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((query == null) ? 0 : query.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SearchEditorInput other = (SearchEditorInput) obj;
        if (query == null) {
            if (other.query != null)
                return false;
        } else if (!query.equals(other.query))
            return false;
        return true;
    }

    @Override
    public IAdaptable createElement(IMemento memento) {
        return new SearchEditorInput(memento.getString("query"));
    }

}
