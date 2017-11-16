package com.hpe.octane.ideplugins.eclipse.ui.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.hpe.octane.ideplugins.eclipse.util.DelayedModifyListener;

public class MultiSelectComboBox<T> {

	private static final String BTN_DATA_CONSTANT = "option";
	private static final int MAX_HEIGHT = 300;

	private List<T> options = new ArrayList<>();
	private List<T> selection = new ArrayList<>();
	private LabelProvider labelProvider;

	private List<Button> buttons = new ArrayList<>();

	private List<ModifyListener> modifyListeners = new ArrayList<ModifyListener>();
	private List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();
	private List<VerifyListener> verifyListeners = new ArrayList<VerifyListener>();

	private Shell floatShell;

	private Runnable resetRunnable;

	public MultiSelectComboBox(LabelProvider labelProvider) {
		this.labelProvider = labelProvider;      
	}

	public void showFloatShell(Control positionControl) {
		floatShell = new Shell(positionControl.getShell(), SWT.BORDER);

		buttons = new ArrayList<>();

		Point p = positionControl.getParent().toDisplay(positionControl.getLocation());
		Point size = positionControl.getSize();
		floatShell.setLayout(new FillLayout());

		ScrolledComposite scrolledComposite = new ScrolledComposite(floatShell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);

		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout());
		scrolledComposite.setContent(composite);

		Text text = new Text(composite, SWT.BORDER);
		text.setMessage("Filter");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		text.addModifyListener(new DelayedModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Arrays.stream(composite.getChildren())
				.filter(child -> child.getData(BTN_DATA_CONSTANT) != null)
				.forEach(child -> {
					
					String lbl = labelProvider.getText(child.getData(BTN_DATA_CONSTANT));
					lbl = lbl.toLowerCase();
					lbl = lbl.replaceAll("\\s+","");
					String query = text.getText();
					query = query.toLowerCase();
					query = query.replaceAll("\\s+","");
					
					if (lbl.contains(query) || query.contains(lbl)) {
						child.setVisible(true);
						((GridData) child.getLayoutData()).exclude = false;
					} else {
						child.setVisible(false);
						((GridData) child.getLayoutData()).exclude = true;
					}
				});

				// sizing
				Point contentSize = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				composite.setSize(contentSize);
				int shellHeight = contentSize.y > MAX_HEIGHT ? MAX_HEIGHT : contentSize.y;
				Rectangle shellRect = new Rectangle(p.x, p.y + size.y, scrolledComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 10, shellHeight);
				floatShell.setBounds(shellRect);
				floatShell.open();
			}
		}));

		Composite ctrlComposite = new Composite(composite, SWT.NONE);
		ctrlComposite.setLayout(new FillLayout());

		Button all = new Button(ctrlComposite, SWT.BUTTON1);
		all.setText("All");
		all.addListener(SWT.MouseDown, event -> selectAll());

		Button none = new Button(ctrlComposite, SWT.BUTTON1);
		none.setText("None");
		none.addListener(SWT.MouseDown, event -> clearSelection());

		if(resetRunnable != null) {
			Button reset = new Button(ctrlComposite, SWT.BUTTON1);
			reset.setText("Reset");
			reset.addListener(SWT.MouseDown,event -> {
				resetRunnable.run();
				selectionListeners.forEach(l -> l.widgetSelected(null));
			});
		}

		for (T option : options) {
			Button button = new Button(composite, SWT.CHECK);
			button.setText(labelProvider.getText(option));
			button.setSelection(selection.contains(option));
			button.setData(BTN_DATA_CONSTANT, option);
			button.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 1, 1));

			button.addListener(SWT.Selection, e -> {
				if (button.getSelection()) {
					selection.add(option);
				} else {
					selection.remove(option);
				}

				selectionListeners.forEach(l -> l.widgetSelected(new SelectionEvent(e)));
			});
			button.pack();
			buttons.add(button);
		}

		floatShell.addListener(SWT.Deactivate, e -> {
			if (floatShell != null && !floatShell.isDisposed()) {
				floatShell.setVisible(false);

				for (VerifyListener l : verifyListeners) {
					VerifyEvent v = new VerifyEvent(e);
					l.verifyText(v);
				}

				for (ModifyListener l : modifyListeners) {
					l.modifyText(new ModifyEvent(e));
				}

				if (buttons != null) {
					for (Button b : buttons) {
						if (b != null) {
							b.dispose();
						}
					}
				}
				buttons = null;
				floatShell.dispose();
			}
		});

		// sizing
		Point contentSize = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		composite.setSize(contentSize);
		int shellHeight = contentSize.y > MAX_HEIGHT ? MAX_HEIGHT : contentSize.y;
		Rectangle shellRect = new Rectangle(p.x, p.y + size.y, scrolledComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 10, shellHeight);
		floatShell.setBounds(shellRect);
		floatShell.open();
	}

	public void setResetRunnable(Runnable r) {
		resetRunnable = r;
	}

	public boolean isVisible() {
		return floatShell != null && !floatShell.isDisposed() && floatShell.isVisible();
	}

	public void setSelected(T option) {
		setSelected(option, true, true);
	}

	public void setSelected(Collection<T> selectOptions) {
		for (T selectOption : selectOptions) {
			if (options.contains(selectOption)) {
				setSelected(selectOption, true, false);
			}
		}
		selectionListeners.forEach(l -> l.widgetSelected(null));
	}
	
	public void setSelection(Collection<T> selectOptions) {
		clearSelection();
		setSelected(selectOptions);
	}
	
	@SuppressWarnings("unchecked")
	public void clearSelection() {
		selection.clear();
		if(buttons != null) {
			buttons.stream()
				.filter(btn -> !btn.isDisposed())
				.filter(btn -> btn.getVisible())
				.forEach(btn -> setSelected((T) btn.getData(BTN_DATA_CONSTANT), false, false));
		}
		selectionListeners.forEach(l -> l.widgetSelected(null));
	}
	
	@SuppressWarnings("unchecked")
	public void selectAll() {
		selection.addAll(options);
		if(buttons != null) {
			buttons.stream()
				.filter(btn -> !btn.isDisposed())
				.filter(btn -> btn.getVisible())
				.forEach(btn -> setSelected((T) btn.getData(BTN_DATA_CONSTANT), true, false));
		}
		selectionListeners.forEach(l -> l.widgetSelected(null));
	}

	public void setSelected(T option, boolean isSelected, boolean fireSelectionListeners) {
		if (isSelected) {
			selection.add(option);
			Button btn = findButton(option);
			if (btn != null && !btn.isDisposed()) {
				btn.setSelection(true);
			}
		} else {
			selection.remove(option);
			Button btn = findButton(option);
			if (btn != null && !btn.isDisposed()) {
				btn.setSelection(false);
			}
		}
		
		if(fireSelectionListeners) { 
			selectionListeners.forEach(l -> l.widgetSelected(null));
		}
	}

	private Button findButton(T option) {
		Optional<Button> button;

		button = buttons.stream()
				.filter(btn -> option.equals(btn.getData(BTN_DATA_CONSTANT)))
				.findFirst();

		if (button.isPresent()) {
			return button.get();
		} else {
			return null;
		}
	}

	public void add(T option) {
		options.add(option);
	}

	public void addAll(Collection<T> options) {
		this.options.addAll(options);
	}

	public void add(int index, T t, boolean isSelected) {
		options.add(index, t);
		setSelected(t, isSelected, false);
		selectionListeners.forEach(l -> l.widgetSelected(null));
	}

	public int getItemCount() {
		return options.size();
	}

	public List<T> getSelections() {
		return options.stream().filter(op -> selection.contains(op)).collect(Collectors.toList());
	}

	public int getTextHeight() {
		return 20;
	}

	public int getTextLimit() {
		return 20;
	}

	public void addModifyListener(ModifyListener listener) {
		modifyListeners.add(listener);
	}

	public void addSelectionListener(SelectionListener listener) {
		selectionListeners.add(listener);
	}

	public void addVerifyListener(VerifyListener listener) {
		verifyListeners.add(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		selectionListeners.remove(listener);
	}

	public void removeVerifyListener(VerifyListener listener) {
		verifyListeners.remove(listener);
	}

	public void removeModifyListener(ModifyListener listener) {
		modifyListeners.remove(listener);
	}

}