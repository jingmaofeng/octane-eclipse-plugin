package com.hpe.octane.ideplugins.eclipse.ui.entitylist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.util.Util;

/**
 * Util class for client side filtering of the data
 */
public class EntityListData {
	
	public static interface DataChangedHandler {
		public void dataChanged(Collection<EntityModel> newData);
	}
	private List<DataChangedHandler> dataChangedHandlers = new ArrayList<>();
	
	//Source data
	private Collection<EntityModel> entityList = new ArrayList<>();
	
	//Filtering
	private Collection<EntityModel> filteredEntityList = new ArrayList<>();
	private Set<Entity> entityTypeFilter = new HashSet<>();
	private String query;
	private Set<String> queryFields = new HashSet<>();
	
	public void setEntityList(Collection<EntityModel> entityList){
		this.entityList = entityList;
		applyFilter();
	}
	
	public Collection<EntityModel> getEntityList(){
		return filteredEntityList;
	}
	
	public void setTypeFilter(Set<Entity> entityTypeFilter){
		this.entityTypeFilter = entityTypeFilter;
		applyFilter();
	}
	
	public void setStringFilter(String query){
		this.query = query;
		applyFilter();
	}
	
	public void setStringFilterFields(Set<String> queryFields){
		this.queryFields = queryFields;
		applyFilter();
	}
	
	private void applyFilter(){
		Collection<EntityModel> filteredEntities;
		
		//Type filtering first
		filteredEntities = filterByEntityType(entityList, entityTypeFilter);
		
		if(StringUtils.isNotBlank(query)){
			filteredEntities = filterByStringQuery(filteredEntities, query, queryFields);
		}
		
		filteredEntityList = filteredEntities;
		fireDataChangedHandlers();
	}
	
	private Collection<EntityModel> filterByEntityType(Collection<EntityModel> entityModelList, Set<Entity> entityTypeFilter){
		//Type filtering first
		return entityModelList
					.stream()
					.filter(entityModel -> entityTypeFilter.contains(Entity.getEntityType(entityModel)))
					.collect(Collectors.toList());
	}
	
	private Collection<EntityModel> filterByStringQuery(Collection<EntityModel> entityModelList, String queryString, Set<String> affectedFields){
		Collection<EntityModel> result = new ArrayList<>();
		
		for(EntityModel entityModel : entityModelList){
			for(String field : affectedFields) {
				if(entityModel.getValue(field)!=null){
					String fieldValue = Util.getUiDataFromModel(entityModel.getValue(field));
					if(fieldValue.contains(queryString)){
						result.add(entityModel);
						break;
					}
				}
			}
		}
		
		return result;
	}
	
	public void addDataChangedHandler(DataChangedHandler dataChangedHandler){
		dataChangedHandlers.add(dataChangedHandler);
	}
	
	public void removeDataChangedHandler(DataChangedHandler dataChangedHandler){
		dataChangedHandlers.remove(dataChangedHandler);
	}
	
	private void fireDataChangedHandlers(){
		dataChangedHandlers.forEach(dataChangedHandler -> dataChangedHandler.dataChanged(filteredEntityList));
	}
	
}