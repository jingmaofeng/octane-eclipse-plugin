package com.hpe.octane.ideplugins.eclipse.ui.editor2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.ideplugins.services.util.Util;
import com.hpe.octane.ideplugins.eclipse.ui.combobox.CustomEntityComboBox;
import com.hpe.octane.ideplugins.eclipse.ui.combobox.CustomEntityComboBoxLabelProvider;
import com.hpe.octane.ideplugins.eclipse.ui.util.TruncatingStyledText;
import com.hpe.octane.ideplugins.eclipse.ui.util.icon.EntityIconFactory;
import com.hpe.octane.ideplugins.eclipse.ui.util.resource.ImageResources;

public class EntityHeaderComposite extends Composite {

	private static final EntityIconFactory entityIconFactory = new EntityIconFactory(25, 25, 7);

	private static final String TOOLTIP_REFRESH = "Refresh entity details";
	private static final String TOOLTIP_PHASE = "Save changes to entity phase";
	private static final String TOOLTIP_PHASE_COMBO = "Available entity phases";


	private ToolTip truncatedLabelTooltip;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public EntityHeaderComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(5, false));

		truncatedLabelTooltip = new ToolTip(parent.getShell(), SWT.ICON_INFORMATION);

		Label lblEntityIcon = new Label(this, SWT.NONE);
		lblEntityIcon.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

		TruncatingStyledText lblEntityName = new TruncatingStyledText(this, SWT.NONE, truncatedLabelTooltip);
		lblEntityName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblEntityName.setText("ENTITY_NAME");

		Composite phaseComposite = new Composite(this, SWT.NONE);
		phaseComposite.setLayout(new GridLayout(4, false));

		Label lblPhase = new Label(phaseComposite, SWT.NONE);
		lblPhase.setText("Phase");

		Label lblCurrentPhase = new Label(phaseComposite, SWT.NONE);
		lblCurrentPhase.setText("CURRENT_PHASE");

		Label lblMoveTo = new Label(phaseComposite, SWT.NONE);
		lblMoveTo.setText("Move to:");

		CustomEntityComboBox<EntityModel> nextPhasesComboBox = new CustomEntityComboBox<EntityModel>(phaseComposite);
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
		nextPhasesComboBox.selectFirstItem();
		nextPhasesComboBox.setTooltipText(TOOLTIP_PHASE_COMBO);

		Button btnSave = new Button(this, SWT.NONE);
		btnSave.setToolTipText(TOOLTIP_PHASE);
		btnSave.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_SAVE_EDIT));

		Button btnRefresh = new Button(this, SWT.NONE);
		btnRefresh.setImage(ImageResources.REFRESH_16X16.getImage());
		btnRefresh.setToolTipText(TOOLTIP_REFRESH);
	}
	
	public void setEntityModel(EntityModel entityModel) {
		
	}

}
