package com.hpe.octane.ideplugins.eclipse.ui.entitydetail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.entitydetail.job.GetPossiblePhasesJob;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.SWTResourceManager;
import com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants;

public class EntityPhaseComposite extends Composite {

    private static final String CURRENT_PHASE_PLACE_HOLDER = "CURRENT_PHASE";
    private static final String NEXT_PHASE_PLACE_HOLDER = "NEXT_PHASE";
    private static final String TOOLTIP_BLOCKED_PHASE = "You must save first before doing any more changes to phase";
    private static final String TOOLTIP_CLICKABLE_PHASE = "Click here to choose you desired next phase";

    private Label lblPhase;
    private Label lblCurrentPhase;
    private Label lblMoveTo;
    private Button btnSelectPhase;
    private EntityModel entityModel;
    private Menu phaseSelectionMenu;
    private Label lblNextPhase;

    public EntityPhaseComposite(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(5, false));

        lblPhase = new Label(this, SWT.NONE);
        lblPhase.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
        lblPhase.setAlignment(SWT.CENTER);
        lblPhase.setText("Phase:");

        lblCurrentPhase = new Label(this, SWT.NONE);
        lblCurrentPhase.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
        lblCurrentPhase.setFont(SWTResourceManager.getBoldFont(lblPhase.getFont()));
        lblCurrentPhase.setText(CURRENT_PHASE_PLACE_HOLDER);

        lblMoveTo = new Label(this, SWT.NONE);
        lblMoveTo.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
        lblMoveTo.setText("Move to:");

        lblNextPhase = new Label(this, SWT.NONE);
        lblNextPhase.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
        lblNextPhase.setFont(SWTResourceManager.getBoldFont(lblNextPhase.getFont()));
        lblNextPhase.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
        lblNextPhase.setText(NEXT_PHASE_PLACE_HOLDER);
        lblNextPhase.setToolTipText(TOOLTIP_CLICKABLE_PHASE);

        lblNextPhase.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                btnSelectPhase.setEnabled(false);
                lblNextPhase.setToolTipText(TOOLTIP_BLOCKED_PHASE);
                lblNextPhase.setForeground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
                btnSelectPhase.setEnabled(false);
                lblCurrentPhase.setText(lblNextPhase.getText());
            }
        });

        btnSelectPhase = new Button(this, SWT.NONE);
        btnSelectPhase.setText("Target Phases Button");

        phaseSelectionMenu = new Menu(btnSelectPhase);
        btnSelectPhase.setMenu(phaseSelectionMenu);

    }

    public void setEntityModel(EntityModel entityModel) {
        this.entityModel = entityModel;
        getPossiblePhaseTransitions();
    }

    private void getPossiblePhaseTransitions() {
        if (GetPossiblePhasesJob.hasPhases(Entity.getEntityType(entityModel))) {
            String currentPhaseName = Util.getUiDataFromModel(entityModel.getValue(EntityFieldsConstants.FIELD_PHASE));
            lblCurrentPhase.setText(currentPhaseName);

            // load possible phases
            GetPossiblePhasesJob getPossiblePhasesJob = new GetPossiblePhasesJob("Loading possible phases", entityModel);
            getPossiblePhasesJob.addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(IJobChangeEvent event) {
                    Display.getDefault().asyncExec(() -> {
                        Collection<EntityModel> possibleTransitions = getPossiblePhasesJob.getPossibleTransitions();
                        if (possibleTransitions.isEmpty()) {
                            lblNextPhase.setText("No transition");
                            lblNextPhase.setEnabled(false);
                            btnSelectPhase.setEnabled(false);
                        } else {
                            List<EntityModel> myList = new ArrayList<>(getPossiblePhasesJob.getPossibleTransitions());
                            lblNextPhase.setText(Util.getUiDataFromModel((myList.get(0)).getValue("target_phase")));
                            for (int i = 0; i < myList.size(); i++) {
                                EntityModel em = myList.get(i);
                                String stringulet = Util.getUiDataFromModel(em.getValue("target_phase"));
                                MenuItem mi = new MenuItem(phaseSelectionMenu, SWT.NONE);
                                mi.setText(stringulet);
                                mi.addListener(SWT.Selection, new Listener() {
                                    @Override
                                    public void handleEvent(Event e) {
                                        System.out.println("I did something");
                                        lblNextPhase.setText(mi.getText());
                                    }
                                });
                            }
                        }
                    });
                }
            });
            getPossiblePhasesJob.schedule();
        }
    }
}
