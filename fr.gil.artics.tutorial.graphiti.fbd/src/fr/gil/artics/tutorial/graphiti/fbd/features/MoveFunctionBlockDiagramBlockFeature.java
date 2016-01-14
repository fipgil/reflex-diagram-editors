package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlock;

public class MoveFunctionBlockDiagramBlockFeature extends DefaultMoveShapeFeature {

	public MoveFunctionBlockDiagramBlockFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void moveShape(IMoveShapeContext context) {
		super.moveShape(context);

		// call the layout feature
		layoutPictogramElement(context.getShape());

		PictogramElement pictogramElement = context.getPictogramElement();
		Object businessObject = getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement);
		if (businessObject instanceof FunctionBlockDiagramBlock) {
			FunctionBlockDiagramBlock block =
					(FunctionBlockDiagramBlock) businessObject;
			GraphicsAlgorithm blockGraphicsAlgorithm = pictogramElement.getGraphicsAlgorithm();
			block.setX(blockGraphicsAlgorithm.getX());
			block.setY(blockGraphicsAlgorithm.getY());
			block.setWidth(blockGraphicsAlgorithm.getWidth());
			block.setHeight(blockGraphicsAlgorithm.getHeight());
		}

	}

}
