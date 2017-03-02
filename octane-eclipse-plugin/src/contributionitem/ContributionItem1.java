package contributionitem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class ContributionItem1 extends WorkbenchWindowControlContribution {
	
	private static Label lbl;

	public ContributionItem1() {
		// TODO Auto-generated constructor stub
	}

	public ContributionItem1(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}
	
	public static void setLblText(String text){
		lbl.setText(text);
	}

	@Override
	protected Control createControl(Composite parent) {		
		lbl = new Label(parent, SWT.NONE);
		lbl.setText("default");
		return lbl;
	}

}
