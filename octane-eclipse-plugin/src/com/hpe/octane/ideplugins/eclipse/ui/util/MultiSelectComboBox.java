package com.hpe.octane.ideplugins.eclipse.ui.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.hpe.octane.ideplugins.eclipse.util.DelayedModifyListener;

public class MultiSelectComboBox<T> extends Composite {
	
	private List<T> options = new ArrayList<>();
	private List<T> selection = new ArrayList<>();
	private LabelProvider labelProvider;
	
	private List<Button> buttons;
	
	private List<ModifyListener> modifyListeners = new ArrayList<ModifyListener>();
	private List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();
	private List<VerifyListener> verifyListeners = new ArrayList<VerifyListener>();

	private String defaultText = "options";
	private Button display;
	private Shell floatShell;

	public MultiSelectComboBox(Composite parent, int style, LabelProvider labelProvider) {
		super(parent, style);
		init();
		this.labelProvider = labelProvider;
	}

	private void init() {
		GridLayout layout = new GridLayout();
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		display = new Button(this, SWT.NONE);
		display.setLayoutData(new GridData(GridData.FILL_BOTH));
		display.setText(defaultText);
		display.addListener(SWT.MouseDown, e -> {
			showFloatShell(display);
		});
	}

	private void showFloatShell(Button display) {

		Point p = display.getParent().toDisplay(display.getLocation());
		Point size = display.getSize();

		floatShell = new Shell(MultiSelectComboBox.this.getShell(), SWT.BORDER);
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
				.filter(child -> child.getData("combo") != null)
				.forEach(child -> {
					if (child.getData("combo").toString().contains(text.getText())) {
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
				int shellHeight = contentSize.y > 500 ? 500 : contentSize.y;
				Rectangle shellRect = new Rectangle(p.x, p.y + size.y, scrolledComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 10, shellHeight);
				floatShell.setBounds(shellRect);
				floatShell.open();
			}
		}));

		Button toggle = new Button(composite, SWT.BUTTON1);
		toggle.setText("Reset");
		toggle.addListener(SWT.MouseDown, e -> {
		});

		buttons = new ArrayList<>();
		
		for(T option : options) {
			Button button = new Button(composite, SWT.CHECK);
			button.setText(labelProvider.getText(option));
			button.setData("combo", labelProvider.getText(option));
			
			button.addListener(SWT.Selection, e -> {

				if(button.getSelection()) {
					selection.add(option);
				} else {
					selection.remove(option);
				}
				
				for (SelectionListener l : selectionListeners) {
					l.widgetSelected(new SelectionEvent(e));
				}
			});
			button.pack();
			buttons.add(button);
		}
	
		floatShell.addListener(SWT.Deactivate, e -> {
			if (floatShell != null && !floatShell.isDisposed()) {
				floatShell.setVisible(false);
				
				for (SelectionListener l : selectionListeners) {
					l.widgetDefaultSelected(new SelectionEvent(e));
				}

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
		int shellHeight = contentSize.y > 500 ? 500 : contentSize.y;
		Rectangle shellRect = new Rectangle(p.x, p.y + size.y, scrolledComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 10, shellHeight);
		floatShell.setBounds(shellRect);
		floatShell.open();
	}

	public void add(T t) {
		options.add(t);
	}

	public void add(int index, T t) {
		options.add(index, t);
	}

	public void add(int index, T t, boolean isSelected) {
		options.add(index, t);
		if(isSelected) {
			
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return display.computeSize(wHint, hHint);
	}

	public int getItemCount() {
		return options.size();
	}

	public List<T> getSelections() {
		return selection;
	}

	public String getText() {
		return display.getText();
	}

	public int getTextHeight() {
		return 20;
	}

	public int getTextLimit() {
		return 20;
	}
	
	@Override
	public void setFont(Font font) {
		display.setFont(font);
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