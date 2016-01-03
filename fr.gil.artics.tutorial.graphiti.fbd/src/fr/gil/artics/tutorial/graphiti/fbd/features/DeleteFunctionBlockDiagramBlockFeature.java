package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

import fr.gil.artics.tutorial.fbd.AbstractConnectionPoint;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlock;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlockInput;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlockOutput;

public class DeleteFunctionBlockDiagramBlockFeature extends DefaultDeleteFeature {

	public DeleteFunctionBlockDiagramBlockFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void delete(IDeleteContext context) {
		super.delete(context);
	}
	
	private void removeInputOutputPictogramElement(
			AbstractConnectionPoint connectionPoint) {
		for (PictogramElement pe : getFeatureProvider().getAllPictogramElementsForBusinessObject(connectionPoint)) {
			IRemoveContext rc = new RemoveContext(pe);
			IFeatureProvider featureProvider = getFeatureProvider();
			IRemoveFeature removeFeature = featureProvider.getRemoveFeature(rc);
			if (removeFeature != null) {
				removeFeature.remove(rc);
			}
		}
	}
	@Override
	public void preDelete(IDeleteContext context) {
		PictogramElement pictogramElement= context.getPictogramElement();
		FunctionBlockDiagramBlock block = (FunctionBlockDiagramBlock) getBusinessObjectForPictogramElement(pictogramElement);
		for (FunctionBlockDiagramBlockInput input : block.getInputs()) {
			removeInputOutputPictogramElement(input);
		}
		for (FunctionBlockDiagramBlockOutput output : block.getOutputs()) {
			removeInputOutputPictogramElement(output);
		}
	}
}
