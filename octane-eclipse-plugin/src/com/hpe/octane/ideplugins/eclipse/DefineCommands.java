package com.hpe.octane.ideplugins.eclipse;

import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;

public class DefineCommands extends ExtensionContributionFactory {
	 
    @Override
    public void createContributionItems(IServiceLocator serviceLocator,
            IContributionRoot additions) {
    	
    	ToolBarContributionItem toolbar = new ToolBarContributionItem( );
        additions.addContributionItem( toolbar, null );
        CommandContributionItemParameter p = new CommandContributionItemParameter( serviceLocator, "", "org.eclipse.ui.file.exit",
                SWT.PUSH );
        p.label = "Exit";
        p.icon = Activator.getImageDescriptor( "icons/mywork.png" );
        CommandContributionItem item = new CommandContributionItem( p );
        item.setVisible( true );
        
        toolbar.getToolBarManager().add( item );
    }
 
}