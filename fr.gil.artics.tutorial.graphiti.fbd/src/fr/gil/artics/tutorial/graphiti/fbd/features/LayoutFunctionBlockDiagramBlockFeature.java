package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

import fr.gil.artics.tutorial.fbd.AbstractFunctionBlockDiagramGraphicsRectangle;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlock;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlockInput;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlockOutput;

public class LayoutFunctionBlockDiagramBlockFeature extends AbstractLayoutFeature {

	public LayoutFunctionBlockDiagramBlockFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		// return true, if pictogram element is linked to an EClass
		PictogramElement pictogramElement = context.getPictogramElement();
		if (!(pictogramElement instanceof ContainerShape))
			return false;
		Object businessObject = getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement);
		if (businessObject instanceof FunctionBlockDiagramBlock) {
			return true;
		}
		return false;
	}

	private void moveConnectionPoint(PictogramElement pictogramElement, int x, int y) {
		Shape shape = null;
		if (pictogramElement instanceof Anchor) {
			Anchor anchor = (Anchor) pictogramElement;
			shape = (Shape) anchor.getParent();
		} else {
			shape = (Shape) pictogramElement;
		}
		if (shape != null) {
			MoveShapeContext context = new MoveShapeContext(shape);
			context.setLocation(x, y);
			getFeatureProvider().getMoveShapeFeature(context).moveShape(context);
		}
	}

	@Override
	public boolean layout(ILayoutContext context) {
		boolean anythingChanged = false;
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();

		int containerWidth = containerGa.getWidth();

		for (Shape shape : containerShape.getChildren()) {
			GraphicsAlgorithm graphicsAlgorithm = shape.getGraphicsAlgorithm();
			IGaService gaService = Graphiti.getGaService();
			IDimension size = gaService.calculateSize(graphicsAlgorithm);
			if (containerWidth != size.getWidth()) {
				if (graphicsAlgorithm instanceof Polyline) {
					Polyline polyline = (Polyline) graphicsAlgorithm;
					Point secondPoint = polyline.getPoints().get(1);
					Point newSecondPoint = gaService.createPoint(containerWidth, secondPoint.getY());
					polyline.getPoints().set(1, newSecondPoint);
					anythingChanged = true;
				} else {
					gaService.setWidth(graphicsAlgorithm, containerWidth);
					anythingChanged = true;
				}
			}
		}
		PictogramElement blockPictogramElement = context.getPictogramElement();
		Object businessObject = getFeatureProvider().getBusinessObjectForPictogramElement(blockPictogramElement);
		if (businessObject instanceof FunctionBlockDiagramBlock) {
			GraphicsAlgorithm blockGraphicsAlgorithm = blockPictogramElement.getGraphicsAlgorithm();
			int blockX = blockGraphicsAlgorithm.getX();
			int blockY = blockGraphicsAlgorithm.getY();
			int blockWidth = blockGraphicsAlgorithm.getWidth();
			int blockHeight = blockGraphicsAlgorithm.getHeight();
			FunctionBlockDiagramBlock block = (FunctionBlockDiagramBlock) businessObject;
			int deltaY = blockHeight / (block.getInputs().size() + 1);
			blockY = blockY + deltaY;
			for (FunctionBlockDiagramBlockInput input : block.getInputs()) {
				PictogramElement inputPictogramElement = getFeatureProvider()
						.getPictogramElementForBusinessObject(input);
				moveConnectionPoint(inputPictogramElement, blockX - 10, blockY);
				blockY = blockY + deltaY;

			}
			deltaY = blockHeight / (block.getOutputs().size() + 1);
			blockY = blockGraphicsAlgorithm.getY() + deltaY;
			for (FunctionBlockDiagramBlockOutput output : block.getOutputs()) {
				PictogramElement outputPictogramElement = getFeatureProvider()
						.getPictogramElementForBusinessObject(output);
				moveConnectionPoint(outputPictogramElement, blockX + blockWidth, blockY);
				blockY = blockY + deltaY;
			}
		}
		if (businessObject instanceof AbstractFunctionBlockDiagramGraphicsRectangle) {
			AbstractFunctionBlockDiagramGraphicsRectangle rectangle = (AbstractFunctionBlockDiagramGraphicsRectangle) businessObject;
			GraphicsAlgorithm blockGraphicsAlgorithm = blockPictogramElement.getGraphicsAlgorithm();
			rectangle.setX(blockGraphicsAlgorithm.getX());
			rectangle.setY(blockGraphicsAlgorithm.getY());
			rectangle.setWidth(blockGraphicsAlgorithm.getWidth());
			rectangle.setHeight(blockGraphicsAlgorithm.getHeight());
		}

		return anythingChanged;
	}

}
