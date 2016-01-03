package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;

public class MoveFunctionBlockDiagramInterfaceElement extends DefaultMoveShapeFeature {

	public MoveFunctionBlockDiagramInterfaceElement(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void moveShape(IMoveShapeContext context) {
		super.moveShape(context);

		// call the layout feature
		layoutPictogramElement(context.getShape());

	}

}
