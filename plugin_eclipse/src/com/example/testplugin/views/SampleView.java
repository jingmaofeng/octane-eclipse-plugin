package com.example.testplugin.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import com.hpe.nga.ide.restclient.Entity;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class SampleView extends ViewPart {
	public SampleView() {
	}

	public static final String ID = "com.example.testplugin.views.SampleView";

	private TableViewer viewer;
	private Action actionSetting;
	private Action actionDefects;
	private Action doubleClickAction;
	private Table table;
	private List<Entity> entities;
	private String[][] defects;
	private List<String> headers;

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return null;
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	class NameSorter extends ViewerSorter {
	}

	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		// viewer.setInput(getViewSite());
		table = viewer.getTable();
		table.setLinesVisible(true);
		table.setVisible(true);
		table.setHeaderVisible(true);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "com.example.testPlugin.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		createTableDefects();
	}

	private void createTableDefects() {

		entities = ApplRestClientConnect.getRestClient();
		if (entities == null) {
			DialogPreferencePage.preferenceDialog("Setting error!");
		} else {
			if (entities.size() > 0) {
				headers = new ArrayList<String>();
				entities.get(0);
				headers.addAll(entities.get(0).fields.keySet());
				for (String header : headers) {
					TableColumn column = new TableColumn(table, SWT.NONE);
					column.setText(header);
				}

				defects = new String[entities.size()][headers.size()];
				int j = 0;
				for (Entity entity : entities) {
					int i = 0;
					for (String header : headers) {
						defects[j][i++] = entity.fields.get(header).toString();
					}
					j++;
				}

				for (int a = 0; a < defects.length; a++) {
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(defects[a]);
				}

				for (int b = 0; b < headers.size(); b++) {
					table.getColumn(b).pack();
				}
			} else {
				showMessage("Number of defects = " + entities.size());
			}
		}
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SampleView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(actionSetting);
		manager.add(new Separator());
		manager.add(actionDefects);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionSetting);
		manager.add(actionDefects);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionSetting);
		manager.add(actionDefects);
	}

	private void makeActions() {
		actionSetting = new Action() {
			public void run() {
				DialogPreferencePage.preferenceDialog("");
			}
		};

		actionSetting.setText("Setting");
		actionSetting.setToolTipText("edit setting");

		// action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages(
		// ). getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		actionDefects = new Action() {
			public void run() {
				table.removeAll();
				createTableDefects();
			}
		};

		actionDefects.setText("Defects");
		actionDefects.setToolTipText("edit defects");

		// action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
		// getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		doubleClickAction = new Action() {
			public void run() {
				int i = 0;
				String[] res = { "" };
				int selection = table.getSelectionIndex();
				for (String[] defect : defects) {
					if (i == selection) {
						res = defect;
					}
					i++;
				}
				i = 0;
				String stringRes = "";
				for (String str : res) {
					stringRes = stringRes + " / " + headers.get(i) + ": " + str;
					i++;
				}
				showMessage("Double-click detected on line: " + selection + stringRes);
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Sample View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}
