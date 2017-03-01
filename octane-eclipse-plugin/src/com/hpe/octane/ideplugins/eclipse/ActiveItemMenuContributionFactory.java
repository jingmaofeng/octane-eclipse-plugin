package com.hpe.octane.ideplugins.eclipse;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.menus.AbstractContributionFactory;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;

public class ActiveItemMenuContributionFactory extends ExtensionContributionFactory {


	@Override
	public void createContributionItems(IServiceLocator serviceLocator, IContributionRoot additions) {
		ToolBarManager toolBarManager = new ToolBarManager();
		CommandContributionItemParameter p = 
				new CommandContributionItemParameter(serviceLocator, "", "octane-eclipse-plugin.openActiveItem",SWT.PUSH);
        p.label = "Test Item";
        p.icon = Activator.getImageDescriptor("path of the image file");
        CommandContributionItem item = new CommandContributionItem( p );
        item.setVisible( true );
        toolBarManager.add( item );		
	}
		

}
