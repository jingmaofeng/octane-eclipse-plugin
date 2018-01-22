package com.hpe.octane.ideplugins.eclipse.ui.editor;

import java.util.ArrayList;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.EntityService;
import com.hpe.adm.octane.ideplugins.services.filtering.Entity;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.Activator;
import com.hpe.octane.ideplugins.eclipse.ui.combobox.CustomEntityComboBox;
import com.hpe.octane.ideplugins.eclipse.ui.combobox.CustomEntityComboBoxLabelProvider;
import com.hpe.octane.ideplugins.eclipse.ui.editor.job.GetPossiblePhasesJob;
import com.hpe.octane.ideplugins.eclipse.ui.util.TruncatingStyledText;
import com.hpe.octane.ideplugins.eclipse.ui.util.icon.EntityIconFactory;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.ImageResources;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.SWTResourceManager;
import com.hpe.octane.ideplugins.eclipse.util.EntityFieldsConstants;

public class EntityHeaderComposite extends Composite {

	private static final EntityIconFactory entityIconFactory = new EntityIconFactory(25, 25, 7);

	private static final String TOOLTIP_REFRESH = "Refresh entity details";
	private static final String TOOLTIP_PHASE = "Save changes to entity phase";
	private static final String TOOLTIP_PHASE_COMBO = "Available entity phases";

	private ToolTip truncatedLabelTooltip;
	
	private Label lblEntityIcon;
	private TruncatingStyledText linkEntityName;
	
	private EntityModel entityModel;

	private Composite phaseComposite;
	private CustomEntityComboBox<EntityModel> nextPhasesComboBox;
	private Label lblCurrentPhase;

	private Button btnRefresh;
	private Button btnSave;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public EntityHeaderComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(5, false));

		Font boldFont = new Font(getDisplay(), new FontData(JFaceResources.DEFAULT_FONT, 12, SWT.BOLD));

		truncatedLabelTooltip = new ToolTip(parent.getShell(), SWT.ICON_INFORMATION);

		lblEntityIcon = new Label(this, SWT.NONE);
		lblEntityIcon.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

		linkEntityName = new TruncatingStyledText(this, SWT.NONE, truncatedLabelTooltip);
		linkEntityName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		linkEntityName.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		linkEntityName.addListener(SWT.MouseDown, event -> Activator.getInstance(EntityService.class).openInBrowser(entityModel));

		linkEntityName.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		linkEntityName.setFont(boldFont);
		linkEntityName.setText("ENTITY_NAME");

		phaseComposite = new Composite(this, SWT.NONE);
		phaseComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		phaseComposite.setLayout(new GridLayout(4, false));

		Label lblPhase = new Label(phaseComposite, SWT.NONE);
		lblPhase.setText("Phase:");

		lblCurrentPhase = new Label(phaseComposite, SWT.NONE);
		lblCurrentPhase.setFont(SWTResourceManager.getBoldFont(lblPhase.getFont()));
		lblCurrentPhase.setText("CURRENT_PHASE");

		Label lblMoveTo = new Label(phaseComposite, SWT.NONE);
		lblMoveTo.setText("Move to:");

		nextPhasesComboBox = new CustomEntityComboBox<EntityModel>(phaseComposite);
		new Label(phaseComposite, SWT.NONE);
		nextPhasesComboBox.setLabelProvider(new CustomEntityComboBoxLabelProvider<EntityModel>() {
			@Override
			public String getSelectedLabel(EntityModel entityModelElement) {
				return Util.getUiDataFromModel(entityModelElement.getValue("target_phase"), "name");
			}
			@Override
			public String getListLabel(EntityModel entityModelElement) {
				return Util.getUiDataFromModel(entityModelElement.getValue("target_phase"), "name");
			}
		});
		nextPhasesComboBox.setTooltipText(TOOLTIP_PHASE_COMBO);
		nextPhasesComboBox.selectFirstItem();

		btnSave = new Button(this, SWT.NONE);
		btnSave.setToolTipText(TOOLTIP_PHASE);
		btnSave.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_SAVE_EDIT));

		btnRefresh = new Button(this, SWT.NONE);
		btnRefresh.setImage(ImageResources.REFRESH_16X16.getImage());
		btnRefresh.setToolTipText(TOOLTIP_REFRESH);
	}

	public void setEntityModel(EntityModel entityModel) {
		this.entityModel = entityModel;		
		lblEntityIcon.setImage(entityIconFactory.getImageIcon(Entity.getEntityType(entityModel)));
		linkEntityName.setText(entityModel.getValue(EntityFieldsConstants.FIELD_NAME).getValue().toString());
		showOrHidePhase(entityModel);
	}

	private void showOrHidePhase(EntityModel entityModel) {
		if(GetPossiblePhasesJob.hasPhases(Entity.getEntityType(entityModel))) {
			
            String currentPhaseName = Util.getUiDataFromModel(entityModel.getValue(EntityFieldsConstants.FIELD_PHASE));
            lblCurrentPhase.setText(currentPhaseName);

			//load possible phases
			GetPossiblePhasesJob getPossiblePhasesJob = new GetPossiblePhasesJob("Loading possible phases", entityModel);
			getPossiblePhasesJob.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {      	
                	Display.getDefault().asyncExec(() -> {
        				nextPhasesComboBox.setContent(new ArrayList<>(getPossiblePhasesJob.getPossibleTransitions()));	
    					nextPhasesComboBox.selectFirstItem();
    					setChildVisibility(phaseComposite, true);
                	});
				}
			});
			getPossiblePhasesJob.schedule();

		} else {						
			setChildVisibility(phaseComposite, false);
		}
	}

	private void setChildVisibility(Control control, boolean isVisible) {
		control.setVisible(isVisible);
	}
	
	public void addSaveSelectionListener(Listener listener) {
		btnSave.addListener(SWT.Selection, listener);
	}
	
	public void addRefreshSelectionListener(Listener listener) {
		btnRefresh.addListener(SWT.Selection, listener);
	}
	
}
