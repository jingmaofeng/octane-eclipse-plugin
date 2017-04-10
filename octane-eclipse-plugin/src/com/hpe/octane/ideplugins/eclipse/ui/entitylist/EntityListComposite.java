package com.hpe.octane.ideplugins.eclipse.ui.entitylist;

import java.util.Collection;
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

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.EntityService;
import com.hpe.adm.octane.services.connection.ConnectionSettings;
import com.hpe.adm.octane.services.connection.ConnectionSettingsProvider;
import com.hpe.adm.octane.services.di.ServiceModule;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom.FatlineEntityListViewer;

public class EntityListComposite extends Composite {
	
	private EntityListData entityListData = new EntityListData();
	private Text textFilter;
	private EntityTypeSelectorComposite entityTypeSelectorComposite;
	
	private static final Set<Entity> defaultFilterTypes = DefaultRowEntityFields.entityFields.keySet();
	private static final Set<String> clientSideQueryFields = DefaultRowEntityFields.entityFields
																							.values()
																							.stream()
																							.flatMap(coll -> coll.stream())
																							.collect(Collectors.toSet());
	//Currently only fatlines
	private EntityListViewer entityListViewer;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public EntityListComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		//UI
		init();
		
		//Data
		entityListData.setTypeFilter(defaultFilterTypes);
		entityListData.setStringFilterFields(clientSideQueryFields);
		entityListData.setEntityList(getOctaneData());
		entityListViewer.setEntityModels(entityListData.getEntityList());
	}
	
	private void init(){
		
		entityTypeSelectorComposite = new EntityTypeSelectorComposite(this, SWT.NONE, defaultFilterTypes.toArray(new Entity[]{}));
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
				fireEventTimer.schedule( createTask(), 500);				
			}
			
			private TimerTask createTask(){
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
		
		//Just a placeholder for the viewer
		Composite compositeEntityList = new Composite(this, SWT.NONE);
		compositeEntityList.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeEntityList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		
		entityListViewer = new FatlineEntityListViewer(compositeEntityList, SWT.NONE);
		entityListData.addDataChangedHandler(entityList -> entityListViewer.setEntityModels(entityList));
	}
	
	private Collection<EntityModel> getOctaneData(){
		ServiceModule serviceModule = new ServiceModule(new ConnectionSettingsProvider() {
			@Override
			public void setConnectionSettings(ConnectionSettings connectionSettings) {}
			
			@Override
			public ConnectionSettings getConnectionSettings() {
				ConnectionSettings connectionSettings = new ConnectionSettings();
				connectionSettings.setBaseUrl("http://myd-vm19852.hpeswlab.net:8080");
				connectionSettings.setSharedSpaceId(1001L);
				connectionSettings.setWorkspaceId(1002L);
				connectionSettings.setUserName("sa@nga");
				connectionSettings.setPassword("Welcome1");
				
				// TODO Auto-generated method stub
				return connectionSettings;
			}
			
			@Override
			public void addChangeHandler(Runnable observer) {}
		});
		
		return serviceModule.getInstance(EntityService.class).findEntities(Entity.WORK_ITEM);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}