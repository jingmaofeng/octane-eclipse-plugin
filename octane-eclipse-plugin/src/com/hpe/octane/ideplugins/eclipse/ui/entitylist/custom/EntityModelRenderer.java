package com.hpe.octane.ideplugins.eclipse.ui.entitylist.custom;

import org.eclipse.swt.widgets.Composite;

import com.hpe.adm.nga.sdk.model.EntityModel;

/**
 * Convert your entity model into a row for the composite
 */
public interface EntityModelRenderer {
	EntityModelRow createRow(Composite parent, EntityModel entityModel);
}