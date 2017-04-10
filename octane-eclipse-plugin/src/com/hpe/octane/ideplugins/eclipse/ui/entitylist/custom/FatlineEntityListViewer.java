package com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.octane.ideplugins.eclipse.ui.entitylist.EntityListViewer;
import com.hpe.octane.ideplugins.eclipse.util.SWTResourceManager;

public class FatlineEntityListViewer extends Composite implements EntityListViewer{
	
	private static final EntityModelRenderer entityModelRenderer = new DefaultEntityModelRenderer();

	private List<EntityModelRow> rows = new ArrayList<>();
	private Collection<EntityModel> entityModels = new ArrayList<>();
	
	private Composite rowComposite;
	private ScrolledComposite rowScrollComposite;

	private int topMargins = 2;
	private int sideMargin = 2;
	private int rowMargins = 2;
	
	public static MouseListener debugMouseListener = new MouseListener() {
		@Override
		public void mouseUp(MouseEvent e) {
			System.out.println(e.getSource());
		}
		@Override
		public void mouseDown(MouseEvent e) {
			System.out.println(e.getSource());
			
		}
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			System.out.println(e.getSource());
		}
	};

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
		
		rowComposite.getDisplay().addFilter(SWT.MouseDown, new Listener() {
	        @Override
	        public void handleEvent(Event event) {
	        	 if (event.widget instanceof Control) {
	        		System.out.println(event.widget.toString());	        		
	        		//System.out.println(rowComposite.getDisplay().map((Control)rowComposite, (Control) event.widget, new Point(0, 0)));

	        	 }
	        }
	    });
		
		rowScrollComposite.setContent(rowComposite);
		rowScrollComposite.setMinSize(rowComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		//rowComposite.addMouseListener(debugMouseListener);
	}
	
	public void setEntityModels(Collection<EntityModel> entityModels){
		this.entityModels = entityModels;	
		clearRowComposite();
		convertToRows();
		paint();
	}
	
	private void clearRowComposite(){
		Arrays.stream(rowComposite.getChildren()).forEach(control -> control.dispose());
	}
	
	private void paint(){
		System.out.println("Source entities size: " + entityModels.size());
		System.out.println("Row size: " + rows.size());
				
		int scrollContainerHeight = rowScrollComposite.getBounds().height;
		int containerHeight = rowComposite.getBounds().height;
		
		int rowWidth = rowScrollComposite.getBounds().width - (sideMargin * 2);
		int rowHeight = 50;
	
		if(scrollContainerHeight < containerHeight){
			//needs a scrollbar, needs more space for it
			rowWidth -= 20;
		}
		
		int x = sideMargin;
		int y = topMargins;
		
		for(EntityModelRow row : rows){
			row.setBounds(x, y, rowWidth , rowHeight);
			
			y += rowHeight + rowMargins;
		}
		
		rowScrollComposite.setMinSize(rowComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	private void convertToRows(){
		rows.clear();
		for(EntityModel entityModel : entityModels){
			EntityModelRow row = entityModelRenderer.createRow(rowComposite, entityModel);			
			rows.add(row);
		}
	}	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
