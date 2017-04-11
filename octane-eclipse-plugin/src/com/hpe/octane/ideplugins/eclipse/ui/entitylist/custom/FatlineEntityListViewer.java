package com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom;

import java.text.Bidi;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.BidiMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityListViewer;
import com.hpe.octane.ideplugins.eclipse.util.SWTResourceManager;

public class FatlineEntityListViewer extends Composite implements EntityListViewer{
	
	private static final EntityModelRenderer entityModelRenderer = new DefaultEntityModelRenderer();
	private static final Color selectionColor = SWTResourceManager.getColor(255, 105, 180);
	private static final Color foregroundColor = SWTResourceManager.getColor(255, 255, 255);
	
	//Keep insertion order
	private BiMap<EntityModel, EntityModelRow> entities = HashBiMap.create();
	
	private EntityModel previousSelection;
	private EntityModel selection;
	
	private Composite rowComposite;
	private ScrolledComposite rowScrollComposite;

	private int topMargins = 2;
	private int sideMargin = 2;
	private int rowMargins = 2;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public FatlineEntityListViewer(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		rowScrollComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		rowScrollComposite.setExpandHorizontal(true);
		rowScrollComposite.setExpandVertical(true);
		
		rowComposite = new Composite(rowScrollComposite, SWT.NONE);
		rowComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		rowComposite.addControlListener(new ControlListener() {
			@Override
			public void controlResized(ControlEvent e) {
				paint();
			}
			@Override
			public void controlMoved(ControlEvent e) {
				//paint();	
			}
		});
		
		//Selection
		rowComposite.getDisplay().addFilter(SWT.MouseDown, new Listener() {
	        @Override
	        public void handleEvent(Event event) {
	        	 if (event.widget instanceof Control && event.button == 1) {
	        		for(EntityModelRow row : entities.values()){
	        			if(containsControl(row, (Control) event.widget)){				
	        				changeSelection(entities.inverse().get(row));
	        			}
	        		}
	        	 }
	        }
	    });
		
		rowScrollComposite.setContent(rowComposite);
		rowScrollComposite.setMinSize(rowComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		//rowComposite.addMouseListener(debugMouseListener);
	}
	
	private void changeSelection(EntityModel entityModel){
		this.previousSelection = selection;
		this.selection = entityModel;
		paint();
	}
	
	public void setEntityModels(Collection<EntityModel> entityModels){
		clearRowComposite();
		entities.clear();
		entityModels.forEach(entityModel -> entities.put(entityModel, entityModelRenderer.createRow(rowComposite, entityModel)));
		paint();
	}
	
	private void clearRowComposite(){
		Arrays.stream(rowComposite.getChildren()).forEach(control -> control.dispose());
	}
	
	private void paint(){				
		int scrollContainerHeight = rowScrollComposite.getBounds().height;
		int containerHeight = rowComposite.getBounds().height;
		
		int rowWidth = rowScrollComposite.getBounds().width - (sideMargin * 2);
		int rowHeight = 53;
	
		if(scrollContainerHeight < containerHeight){
			//needs a scrollbar, needs more space for it
			rowWidth -= 20;
		}
		
		int x = sideMargin;
		int y = topMargins;
		
		for(EntityModelRow row : entities.values()){
			row.setBounds(x, y, rowWidth , rowHeight);
			
			y += rowHeight + rowMargins;
		}
		
		rowScrollComposite.setMinSize(rowComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		paintSelected();
	}
	
	private void paintSelected(){
		if(entities.containsKey(selection)){
			EntityModelRow row = entities.get(selection);
			if(!row.isDisposed()){
				row.setBackground(selectionColor);
			}
		} else {
			selection = null;
		}
		if(entities.containsKey(previousSelection)){
			EntityModelRow row = entities.get(previousSelection);
			if(!row.isDisposed()){
				row.setBackground(foregroundColor);
			}
		} else {
			previousSelection = null;
		}
	}
	
	private static boolean containsControl(Control source, Control target){
		//System.out.println("Looking inside " + source);
		if(source == target){
			return true;
		} else if(source instanceof Composite){
			for(Control control : ((Composite) source).getChildren()){
				if(containsControl(control, target)){
					return true;
				};
			}
		}
		return false;
	}	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
