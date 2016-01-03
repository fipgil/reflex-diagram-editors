package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlock;

public class AddFunctionBlockDiagramBlockFeature extends AbstractAddShapeFeature {
	private static final IColorConstant BLOCK_TEXT_FOREGROUND = IColorConstant.BLACK;

	private static final IColorConstant BLOCK_FOREGROUND = IColorConstant.BLACK;

	private static final IColorConstant BLOCK_BACKGROUND = IColorConstant.WHITE;

	public AddFunctionBlockDiagramBlockFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canAdd(IAddContext context) {
		// check if user wants to add a Block
		if (context.getNewObject() instanceof FunctionBlockDiagramBlock) {
			// check if user wants to add to a diagram
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
	}

	public PictogramElement add(IAddContext context) {
		FunctionBlockDiagramBlock block = (FunctionBlockDiagramBlock) context.getNewObject();
		Diagram targetDiagram = (Diagram) context.getTargetContainer();

		// CONTAINER SHAPE WITH ROUNDED RECTANGLE
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

		// define a default size for the shape
		int width = context.getWidth();
		int height = context.getHeight();
		IGaService gaService = Graphiti.getGaService();

		// create and set graphics algorithm
		RoundedRectangle roundedRectangle = gaService.createRoundedRectangle(containerShape, 5, 5);
		roundedRectangle.setForeground(manageColor(BLOCK_FOREGROUND));
		roundedRectangle.setBackground(manageColor(BLOCK_BACKGROUND));
		roundedRectangle.setLineWidth(2);
		gaService.setLocationAndSize(roundedRectangle, context.getX(), context.getY(), width, height);

		// create link and wire it
		link(containerShape, block);

		// SHAPE WITH LINE
		{
			// create shape for line
			Shape shape = peCreateService.createShape(containerShape, false);

			// create and set graphics algorithm
			Polyline polyline = gaService.createPolyline(shape, new int[] { 0, 20, width, 20 });
			polyline.setForeground(manageColor(BLOCK_FOREGROUND));
			polyline.setLineWidth(2);
		}

		// SHAPE WITH TEXT
		{
			// create shape for text
			Shape shape = peCreateService.createShape(containerShape, false);

			// create and set text graphics algorithm
			Text text = gaService.createText(shape, block.getName());
			text.setForeground(manageColor(BLOCK_TEXT_FOREGROUND));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
			// System.out.println("text:"+text.getValue()+" "+text.getWidth()+"
			// "+GraphitiUi.getUiLayoutService().calculateTextSize(text).getWidth());

			gaService.setLocationAndSize(text, 0, 0, width, 20);
		}

		// call the layout feature
		layoutPictogramElement(containerShape);

		return containerShape;
	}
}
