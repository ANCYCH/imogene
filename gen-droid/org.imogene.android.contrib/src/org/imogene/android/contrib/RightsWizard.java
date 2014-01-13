package org.imogene.android.contrib;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.imogene.studio.contrib.ImogeneStudioPlugin;
import org.imogene.studio.contrib.ui.generation.GenerationWizardPage;


public class RightsWizard extends WizardPage implements GenerationWizardPage {

	private Button multiloginButton;
	
	private Text sntpAddressText;
	
	private Text codeText;
	
	protected RightsWizard() {
		super("AndroidProject");
		setPageComplete(false);
		setTitle("Android application properties.");
		setDescription("Set the user rights of the future android generated project.");
		setImageDescriptor(ImogeneStudioPlugin.getImageDescriptor("icons/gears.png"));
	}

	@Override
	public Map<String, String> getProperties() {
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("Android_multilogin", Boolean.toString(multiloginButton.getSelection()));
		result.put("Android_sntpServer", sntpAddressText.getText());
		result.put("Android_codeHidden", codeText.getText());
		return result;
	}

	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		
		createMultiloginGroup(mainComposite);
		createSntpGroup(mainComposite);
		createHiddenPreferencesCode(mainComposite);
		
		setControl(mainComposite);
		
		setPageComplete(false);
	}
	
	private void createMultiloginGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout());
		group.setText("Multilogin");
		
		multiloginButton = new Button(group, SWT.CHECK);
		multiloginButton.setText("Allow multilogin on the device");
		multiloginButton.setSelection(false);
	}
	
	private void createSntpGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(2, false));
		group.setText("NTP server properties");
		
		Label sntpLabel = new Label(group, SWT.NONE);
		sntpLabel.setText("The NTP server address: ");
		
		sntpAddressText = new Text(group, SWT.BORDER);
		sntpAddressText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		sntpAddressText.setText("europe.pool.ntp.org");
		sntpAddressText.setMessage("ex: europe.pool.ntp.org");
		sntpAddressText.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
	}
	
	private void createHiddenPreferencesCode(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(2, false));
		group.setText("Hidden preferences");
		
		Label sntpLabel = new Label(group, SWT.NONE);
		sntpLabel.setText("Hidden preferences activation code: ");
		
		codeText = new Text(group, SWT.BORDER);
		codeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		codeText.setMessage("ex: 1234");
		codeText.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
	}
	
	private void validate() {
		String sntpRegex = "^\\w+[\\w-\\.-:]*\\w$";
		String codeRegex = "^\\d{4}$";
		String sntpAddress = sntpAddressText.getText();
		String code = codeText.getText();
		
		String error = null;
		if (sntpAddress == null || !sntpAddress.matches(sntpRegex))
			error = "The sntp address is not valid.";
		if (code == null || !code.matches(codeRegex))
			error = "The activation code is not valid.";
		
		setErrorMessage(error);
		setPageComplete(error == null);
	}

}
