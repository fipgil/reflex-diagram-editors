package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IMoveAnchorFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IMoveAnchorContext;
import org.eclipse.graphiti.features.impl.DefaultMoveAnchorFeature;

public class DisabledMoveAnchorFeature extends DefaultMoveAnchorFeature implements IMoveAnchorFeature {

	public DisabledMoveAnchorFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canMoveAnchor(IMoveAnchorContext context) {
		return false;
	}

	@Override
	public boolean canExecute(IContext context) {
		return false;
	}

	
}
