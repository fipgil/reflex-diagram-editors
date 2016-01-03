package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;

public class DisabledRemoveFeature extends DefaultRemoveFeature {

	private boolean canDelete = false;
	private boolean canRemove = false;
	
	public DisabledRemoveFeature(IFeatureProvider fp) {
		super(fp);
	}

	public DisabledRemoveFeature(IFeatureProvider fp, boolean canDelete, boolean canRemove) {
		super(fp);
		this.canDelete = canDelete;
		this.canRemove = canRemove;
	}

	@Override
	public boolean canRemove(IRemoveContext context) {
		return canRemove;
	}

	@Override
	public boolean canExecute(IContext context) {
		return canDelete;
	}

}
