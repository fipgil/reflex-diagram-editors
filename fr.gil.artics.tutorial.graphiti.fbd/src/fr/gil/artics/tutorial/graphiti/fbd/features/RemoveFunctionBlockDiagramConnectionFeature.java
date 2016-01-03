package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlockInput;

public class RemoveFunctionBlockDiagramConnectionFeature extends DefaultRemoveFeature {

	
	public RemoveFunctionBlockDiagramConnectionFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canRemove(IRemoveContext context) {
		return super.canRemove(context);
	}

	@Override
	public boolean canExecute(IContext context) {
		return super.canExecute(context);
	}

	@Override
	public void remove(IRemoveContext context) {
		PictogramElement pictogramElement= context.getPictogramElement();
		FunctionBlockDiagramBlockInput input = (FunctionBlockDiagramBlockInput) getBusinessObjectForPictogramElement(pictogramElement);
		input.setLink(null);
		super.remove(context);
	}

	@Override
	public void execute(IContext context) {
		super.execute(context);
	}

}
