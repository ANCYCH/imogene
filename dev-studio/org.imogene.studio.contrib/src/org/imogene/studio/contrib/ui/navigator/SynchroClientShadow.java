package org.imogene.studio.contrib.ui.navigator;

import org.eclipse.core.resources.IProject;
import org.imogene.studio.contrib.ImogeneStudioPlugin;


public class SynchroClientShadow extends AbstractShadow {

	public static final String TYPE = "sync"; //$NON-NLS-1$
	
	public static final String NATURE="org.imogene.nature.gen.client"; //$NON-NLS-1$
	
	public SynchroClientShadow(IProject parent){
		super(parent, TYPE);
		setLabel(Messages.SynchroShadow_2);
		setIcon(ImogeneStudioPlugin.getImageDescriptor(
			"icons/serverSynchro.png").createImage()); //$NON-NLS-1$
	}
	
	@Override
	public Object[] getChildren() {
		return getProjects(NATURE).toArray();
	}

	@Override
	public boolean hasChildren() {
		return true;
	}
	
}
