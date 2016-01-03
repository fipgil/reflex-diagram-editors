package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import fr.gil.artics.tutorial.fbd.AbstractFunctionBlockDiagramGraphicsRectangle;
import fr.gil.artics.tutorial.fbd.AbstractFunctionBlockDiagramInterfaceElement;

public class LayoutFunctionBlockDiagramInterfaceElementFeature extends AbstractLayoutFeature {

	public LayoutFunctionBlockDiagramInterfaceElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		// return true, if pictogram element is linked to an EClass
		PictogramElement pictogramElement = context.getPictogramElement();
		if (!(pictogramElement instanceof ContainerShape))
			return false;
		Object businessObject = getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement);
		if (businessObject instanceof AbstractFunctionBlockDiagramInterfaceElement) {
			return true;
		}
		return false;
	}

	@Override
	public boolean layout(ILayoutContext context) {
		boolean anythingChanged = false;

		PictogramElement pictogramElement = context.getPictogramElement();
		Object businessObject = getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement);
		if (businessObject instanceof AbstractFunctionBlockDiagramGraphicsRectangle) {
			AbstractFunctionBlockDiagramGraphicsRectangle rectangle = (AbstractFunctionBlockDiagramGraphicsRectangle) businessObject;
			GraphicsAlgorithm blockGraphicsAlgorithm = pictogramElement.getGraphicsAlgorithm();
			rectangle.setX(blockGraphicsAlgorithm.getX());
			rectangle.setY(blockGraphicsAlgorithm.getY());
			rectangle.setWidth(blockGraphicsAlgorithm.getWidth());
			rectangle.setHeight(blockGraphicsAlgorithm.getHeight());
			anythingChanged = true;
		}

		return anythingChanged;
	}

}
