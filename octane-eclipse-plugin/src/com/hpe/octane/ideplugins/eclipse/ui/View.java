package com.hpe.octane.ideplugins.eclipse.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class View extends ViewPart{
	public static final String ID	    = "TreeTableDemo.view";
	private TreeViewer	      m_treeViewer;

	class ViewLabelProvider extends LabelProvider implements ILabelProvider{
		public String getColumnText(Object obj, int index){
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index){
			return getImage(obj);
		}

		public Image getImage(Object obj){
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	public void createPartControl(Composite parent){
		Tree addressTree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		addressTree.setHeaderVisible(true);
		m_treeViewer = new TreeViewer(addressTree);

		TreeColumn column1 = new TreeColumn(addressTree, SWT.LEFT);
		addressTree.setLinesVisible(true);
		column1.setAlignment(SWT.LEFT);
		column1.setText("Land/Stadt");
		column1.setWidth(160);
		TreeColumn column2 = new TreeColumn(addressTree, SWT.RIGHT);
		column2.setAlignment(SWT.LEFT);
		column2.setText("Person");
		column2.setWidth(100);
		TreeColumn column3 = new TreeColumn(addressTree, SWT.RIGHT);
		column3.setAlignment(SWT.LEFT);
		column3.setText("m/w");
		column3.setWidth(35);

		m_treeViewer.setContentProvider(new AddressContentProvider());
		m_treeViewer.setLabelProvider(new TableLabelProvider());
		List<City> cities = new ArrayList<City>();
		cities.add(new City());
		m_treeViewer.setInput(cities);
		m_treeViewer.expandAll();
	}

	public void setFocus(){
		m_treeViewer.getControl().setFocus();
	}

	class City{
		Street[]	streets	= new Street[2];

		public City(){
			for (int i = 0; i < streets.length; i++)
				streets[i] = new Street(this, i);
		}

		public Street[] getStreets(){
			return streets;
		}

		public String toString(){
			return "Küchenhausen";
		}
	}

	class Street{
		City	city;
		House[]	houses	= new House[2];
		int		indx;

		public Street(City city, int index){
			this.city = city;
			indx = index + 1;
			for (int i = 0; i < houses.length; i++)
				houses[i] = new House(this, i);
		}

		public House[] getHouses(){
			return houses;
		}

		public String toString(){
			return "Topfdeckelstraße " + indx;
		}
	}


	class House{
		Street	street;
		int	indx;

		public House(Street street, int i){
			this.street = street;
			indx = i + 1;
		}

		public String toString(){
			return "Haus " + indx;
		}

		public String getPerson(){
			if (street.toString().equals("Topfdeckelstraße 1")){
				if (indx == 1)
					return "Hugo Hüpfer";
				return "Sabine Springer";
			}
			if (indx == 1)
				return "Leo Löffel";
			return "Marta Messer";
		}

		public String getSex(){
			if (indx == 1)
				return "m";
			return "w";
		}
	}


	class AddressContentProvider implements ITreeContentProvider{
		public Object[] getChildren(Object parentElement){
			if (parentElement instanceof List)
				return ((List<?>) parentElement).toArray();
			if (parentElement instanceof City)
				return ((City) parentElement).getStreets();
			if (parentElement instanceof Street)
				return ((Street) parentElement).getHouses();
			return new Object[0];
		}

		public Object getParent(Object element){
			if (element instanceof Street)
				return ((Street) element).city;
			if (element instanceof House)
				return ((House) element).street;
			return null;
		}

		public boolean hasChildren(Object element){
			if (element instanceof List)
				return ((List<?>) element).size() > 0;
				if (element instanceof City)
					return ((City) element).getStreets().length > 0;
					if (element instanceof Street)
						return ((Street) element).getHouses().length > 0;
						return false;
		}

		public Object[] getElements(Object cities){
			// cities ist das, was oben in setInput(..) gesetzt wurde.
			return getChildren(cities);
		}

		public void dispose(){
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		}
	}


	class TableLabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex){
			return null;
		}

		public String getColumnText(Object element, int columnIndex){
			switch (columnIndex){
			case 0: return element.toString();
			case 1:
				if (element instanceof House)
					return ((House)element).getPerson();
			case 2: 
				if (element instanceof House)
					return ((House)element).getSex();
			}
			return null;
		}

		public void addListener(ILabelProviderListener listener){
		}

		public void dispose(){
		}

		public boolean isLabelProperty(Object element, String property){
			return false;
		}

		public void removeListener(ILabelProviderListener listener){
		}
	}		
}

