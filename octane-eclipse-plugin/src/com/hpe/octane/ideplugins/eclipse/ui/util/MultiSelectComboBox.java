package com.hpe.octane.ideplugins.eclipse.ui.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.hpe.octane.ideplugins.eclipse.util.DelayedModifyListener;

public class MultiSelectComboBox extends Composite {

	private List<Option> options = new ArrayList<Option>();
	private Button[] buttons;
	private List<ModifyListener> modifyListeners = new ArrayList<ModifyListener>();
	private List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();
	private List<VerifyListener> verifyListeners = new ArrayList<VerifyListener>();

	private String defaultText = "options";
	private Button display;
	private Shell floatShell;

	private class Option {
		String text;
		boolean selection = false;

		Option(String text) {
			if (text == null)
				throw new IllegalArgumentException();
			this.text = text;
		}

		Option(String text, boolean selection) {
			if (text == null)
				throw new IllegalArgumentException();
			this.text = text;
			this.selection = selection;
		}
	}

	public MultiSelectComboBox(Composite parent, int style) {
		super(parent, style);
		init();
	}

	public MultiSelectComboBox(Composite parent, int style, String defaultText) {
		super(parent, style);
		if (defaultText == null)
			throw new IllegalArgumentException("Default Text cannot be null");
		this.defaultText = defaultText;
		init();
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
			toggleAll();
			for (SelectionListener l : selectionListeners) {
				l.widgetSelected(new SelectionEvent(e));
			}
		});

		buttons = new Button[options.size()];
		for (int i = 0; i < options.size(); i++) {
			Button b = new Button(composite, SWT.CHECK);
			Option o = options.get(i);
			b.setText(o.text);
			b.setData("combo", o.text);
			b.setSelection(o.selection);
			b.addListener(SWT.Selection, e -> {
				o.selection = b.getSelection();
				for (SelectionListener l : selectionListeners) {
					l.widgetSelected(new SelectionEvent(e));
				}
			});
			b.pack();
			buttons[i] = b;
		}

		floatShell.addListener(SWT.Deactivate, e -> {
			if (floatShell != null && !floatShell.isDisposed()) {
				floatShell.setVisible(false);
				for (SelectionListener l : selectionListeners) {
					l.widgetDefaultSelected(new SelectionEvent(e));
				}

				for (VerifyListener l : verifyListeners) {
					VerifyEvent v = new VerifyEvent(e);
					//v.doit = false;
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

	//location and sizing
	private void resizeAndOpenShell(Shell shell, Control parentControl, Point contentSize) {
		Point p = parentControl.getParent().toDisplay(display.getLocation());
		Point size = parentControl.getSize();



		int shellHeight = contentSize.y > 500 ? 500 : contentSize.y;
		Rectangle shellBounds = new Rectangle(p.x, p.y + size.y, contentSize.x + 10, shellHeight);
		floatShell.setBounds(shellBounds);
		floatShell.open();
	}

	public void add(String string) {
		options.add(new Option(string));
	}

	public void add(String string, boolean selection) {
		options.add(new Option(string, selection));
	}

	public void add(String string, int index) {
		if (index < 0 || index > options.size())
			throw new IllegalArgumentException("ERROR_INVALID_RANGE");
		options.add(index, new Option(string));
	}

	public void add(String string, boolean selection, int index) {
		if (index < 0 || index > options.size())
			throw new IllegalArgumentException("ERROR_INVALID_RANGE");
		options.add(index, new Option(string, selection));
	}

	public void addModifyListener(ModifyListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		modifyListeners.add(listener);
	}

	public void addSelectionListener(SelectionListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		selectionListeners.add(listener);
	}

	public void addVerifyListener(VerifyListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		verifyListeners.add(listener);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return display.computeSize(wHint, hHint);
	}

	/**
	 *
	 * Deselects the item at the given zero-relative index in the receiver's
	 * list. If the item at the index was already deselected, it remains
	 * deselected. Indices that are out of range are ignored.
	 *
	 * @param index
	 *            the index of the item to deselect
	 * @since version 1.0.0.0
	 */
	public void deselect(int index) {
		if (index >= 0 && index < options.size()) {
			options.get(index).selection = false;
			if (buttons != null) {
				buttons[index].setSelection(false);
			}
		}
	}

	public void deselectAll() {
		for (Option o : options) {
			o.selection = false;
		}
		if (buttons != null) {
			for (int i = 0; i < options.size(); i++) {
				buttons[i].setSelection(false);
			}
		}
	}

	public String getItem(int index) {
		checkrange(index);
		return options.get(index).text;
	}

	public int getItemCount() {
		return options.size();
	}

	public int[] getSelectionIndices() {
		ArrayDeque<Integer> selections = new ArrayDeque<Integer>();
		for (int i = 0; i < options.size(); i++) {
			if (options.get(i).selection) {
				selections.add(i);
			}
		}
		return selections.stream().mapToInt(i -> i).toArray();
	}

	public String[] getSelections() {
		ArrayDeque<String> selections = new ArrayDeque<String>();
		for (int i = 0; i < options.size(); i++) {
			Option o = options.get(i);
			if (o.selection) {
				selections.add(o.text);
			}
		}
		return selections.toArray(new String[selections.size()]);
	}

	public String getText() {
		return display.getText();
	}

	public int getTextHeight() {
		return 20;
		// return display.getLineHeight();
	}

	public int getTextLimit() {
		return 20;
		// return display.getTextLimit();
	}

	public int indexOf(String string) {
		if (string == null)
			throw new IllegalArgumentException();
		for (int i = 0; i < options.size(); i++) {
			if (options.get(i).text.equals(string)) {
				return i;
			}
		}
		return -1;
	}

	public int indexOf(String string, int start) {
		if (string == null)
			throw new IllegalArgumentException();
		if (start < 0 || start >= options.size())
			return -1;
		for (int i = start; i < options.size(); i++) {
			if (options.get(i).text.equals(string)) {
				return i;
			}
		}
		return -1;
	}

	public void remove(int index) {
		checkrange(index);
		options.remove(index);
		if (buttons != null) {
			buttons[index].setEnabled(false);
		}
	}

	public void remove(int start, int end) {
		checkrange(start);
		checkrange(end);
		assert start <= end;
		for (int i = start; i <= end; i++) {
			options.remove(i);
			if (buttons != null) {
				buttons[i].setEnabled(false);
			}
		}
	}

	public void remove(String string) {
		if (string != null) {
			for (int i = 0; i < options.size(); i++) {
				if (options.get(i).text.equals(string)) {
					options.remove(i);
					if (buttons != null) {
						buttons[i].setEnabled(false);
					}
					return;
				}
			}
		}
		throw new IllegalArgumentException();
	}

	public void removeAll() {
		options.clear();
		if (buttons != null) {
			for (Button b : buttons) {
				b.setEnabled(false);
			}
		}
		display.setText(defaultText);
		display.pack();
	}

	public void removeSelectionListener(SelectionListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		selectionListeners.remove(listener);
	}

	public void removeVerifyListener(VerifyListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		verifyListeners.remove(listener);
	}

	public void removeModifyListener(ModifyListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		modifyListeners.remove(listener);
	}

	public void select(int index) {
		if (index >= 0 && index < options.size()) {
			options.get(index).selection = true;
			if (buttons != null) {
				buttons[index].setSelection(true);
			}
		}
	}

	public void select(int[] indices) {
		for (int i : indices) {
			select(i);
		}
	}

	@Override
	public void setFont(Font font) {
		display.setFont(font);
	}

	public void setItem(int index, String string) {
		checkrange(index);
		if (string == null)
			throw new IllegalArgumentException();
		options.get(index).text = string;
		if (buttons != null) {
			buttons[index].setText(string);
			buttons[index].pack();
		}
	}

	public void setItems(String[] items) {
		options = new ArrayList<Option>(items.length);
		for (String s : items) {
			add(s);
		}
	}

	public void toggleAll() {
		for (Option o : options) {
			o.selection = !o.selection;
		}
		if (buttons != null) {
			for (Button b : buttons) {
				b.setSelection(!b.getSelection());
			}
		}
	}

	private void checkrange(int index) {
		if (index < 0 || index >= options.size())
			throw new IllegalArgumentException("ERROR_INVALID_RANGE");
	}

}