package com.hpe.octane.ideplugins.eclipse.ui.entitylist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.octane.ideplugins.eclipse.util.EntityIconFactory;
import com.hpe.octane.ideplugins.eclipse.util.resource.SWTResourceManager;

public class EntityTypeSelectorComposite extends Composite {

    private static final EntityIconFactory entityIconFactory = new EntityIconFactory(20, 20, 7);
    private List<Button> checkBoxes = new ArrayList<>();
    private List<Runnable> selectionListeners = new ArrayList<>();
    private Label totalCountLbl;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public EntityTypeSelectorComposite(Composite parent, int style, Entity... supportedEntityTypes) {
        super(parent, style);
        RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
        rowLayout.center = true;
        rowLayout.spacing = 7;
        setLayout(rowLayout);

        for (Entity entity : supportedEntityTypes) {
            Button btnCheckButton = new Button(this, SWT.CHECK);

            btnCheckButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    fireAllSelectionListeners();
                }
            });

            btnCheckButton.setFont(SWTResourceManager.getBoldFont(btnCheckButton.getFont()));
            btnCheckButton.setData(entity);
            btnCheckButton.setImage(entityIconFactory.getImageIcon(entity));
            checkBoxes.add(btnCheckButton);
        }

        totalCountLbl = new Label(this, SWT.NONE);
        totalCountLbl.setFont(SWTResourceManager.getBoldFont(totalCountLbl.getFont()));
    }

    public void setEntityTypeCount(Map<Entity, Integer> entityTypeCount) {
        checkBoxes.forEach(checkBox -> {
            Integer count = entityTypeCount.get(checkBox.getData());
            if (count != null) {
                checkBox.setText("" + count);
            } else {
                checkBox.setText("0");
            }
        });
        totalCountLbl.setText("Total: " + entityTypeCount.values().stream().mapToInt(i -> i.intValue()).sum());
    }

    public Set<Entity> getCheckedEntityTypes() {
        Set<Entity> result = new HashSet<>();
        for (Button checkBox : checkBoxes) {
            if (checkBox.getSelection()) {
                result.add((Entity) checkBox.getData());
            }
        }
        return result;
    }

    public void addSelectionListener(Runnable listener) {
        selectionListeners.add(listener);
    }

    public void checkAll() {
        checkBoxes.forEach(checkBox -> checkBox.setSelection(true));
        fireAllSelectionListeners();
    }

    public void checkNone() {
        checkBoxes.forEach(checkBox -> checkBox.setSelection(false));
        fireAllSelectionListeners();
    }

    private void fireAllSelectionListeners() {
        selectionListeners.forEach(listener -> listener.run());
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

}
