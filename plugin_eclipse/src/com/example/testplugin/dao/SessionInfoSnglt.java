package com.example.testplugin.dao;

//import com.intellij.util.xmlb.annotations.Transient;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dublier on 16/03/2016.
 */

public class SessionInfoSnglt {
    private static volatile SessionInfoSnglt instance;
    private String login = "";

    //Temporary
    //TODO Remove
    private String password = "";

    private String location = "";
    private String sharedSpace = "";
    private String workSpace = "";
    //String to send in HTTP request
    private String filter = "";
    // Map <filterBy, Map<filterItem, isChecked>
    private Map<String, Map<String, Boolean>> filters = new HashMap<>();

    public Map<String, Map<String, Boolean>> getFilters() {
        return filters;
    }
    // Is used for throw events on datamember changed
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public static SessionInfoSnglt getInstance() {
        if (instance == null) {
            synchronized (SessionInfoSnglt.class) {
                if (instance == null) {
                    instance = new SessionInfoSnglt();
                }
            }
        }
        return instance;
    }

    private SessionInfoSnglt() {
    }

    //The method changes only submap and throw event for building text areas
    //in filterpanel
    public void setFilters(String type, Map<String, Boolean> filters) {
        this.filters.put(type, filters);
        propertyChangeSupport.firePropertyChange(type, 0, 1);
    }


    public String getFilter() {
        return filter;
    }

    public void setFilter(String newFilter) {
        String oldFilter = filter;
        instance.filter = newFilter;
        propertyChangeSupport.firePropertyChange("filter", oldFilter, newFilter);
    }

    public String getLogin() {
        return login;
    }


    public void setLogin(String newLogin) {
        String oldLogin = login;
        instance.login = newLogin;
        propertyChangeSupport.firePropertyChange("login", oldLogin, newLogin);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String newLocation) {
        String oldLocation = location;
        instance.location = newLocation;
        propertyChangeSupport.firePropertyChange("location", oldLocation, newLocation);
    }

    public String getSharedSpace() {
        return sharedSpace;
    }

    public void setSharedSpace(String newSharedSpace) {
        String oldSharedSpace = sharedSpace;
        instance.sharedSpace = newSharedSpace;
        propertyChangeSupport.firePropertyChange("sharedSpace", oldSharedSpace, newSharedSpace);
    }

    public String getWorkSpace() {
        return workSpace;
    }

    public void setWorkSpace(String newWorkSpace) {
        String oldWorkSpace = sharedSpace;
        instance.workSpace = newWorkSpace;
        propertyChangeSupport.firePropertyChange("workSpace", oldWorkSpace, newWorkSpace);
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void reloadTable(){
        propertyChangeSupport.firePropertyChange("reload", 1, 2);
    }
}
