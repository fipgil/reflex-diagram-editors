package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;

import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlockInput;

public class AddFunctionBlockDiagramBlockInputFeature extends AbstractAddShapeFeature {

	private static final int ANCHOR_SIZE = 20;

	private static final IColorConstant BLOCK_TEXT_FOREGROUND = IColorConstant.BLACK;

	public AddFunctionBlockDiagramBlockInputFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canAdd(IAddContext context) {
		// check if user wants to add a Block
		if (context.getNewObject() instanceof FunctionBlockDiagramBlockInput) {
			// check if user wants to add to a diagram
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
	}

	public PictogramElement add(IAddContext context) {

		// get associated businessObject & diagram
		FunctionBlockDiagramBlockInput input = (FunctionBlockDiagramBlockInput) context.getNewObject();
		Diagram targetDiagram = (Diagram) context.getTargetContainer();

		// CONTAINER SHAPE WITH RECTANGLE
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

		IGaService gaService = Graphiti.getGaService();

		// create invisible outer rectangle expanded by
		// the width needed for the anchor
		Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);

		// create shape for text
		Shape shape = peCreateService.createShape(containerShape, false);

		// create and set text graphics algorithm
		Text text = gaService.createText(shape, input.getName());
		text.setForeground(manageColor(BLOCK_TEXT_FOREGROUND));
		text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);

		// vertical alignment has as default value "center"
		text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));

		int width = GraphitiUi.getUiLayoutService().calculateTextSize(text).getWidth();
		int height = GraphitiUi.getUiLayoutService().calculateTextSize(text).getHeight();
		if (height < ANCHOR_SIZE)
			height = ANCHOR_SIZE;

		gaService.setLocationAndSize(invisibleRectangle, context.getX(), context.getY(), width + ANCHOR_SIZE, height);
		gaService.setLocationAndSize(text, ANCHOR_SIZE, 0, width, height);

		// create an box relative anchor at middle-left
		final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);

		boxAnchor.setRelativeWidth(0.0);
		boxAnchor.setRelativeHeight(0.50); // use golden section

		// anchor references visible rectangle instead of invisible rectangle
		boxAnchor.setReferencedGraphicsAlgorithm(invisibleRectangle);

		// assign a rectangle graphics algorithm for the box relative anchor
		// note, that the rectangle is inside the border of the rectangle shape
		final Rectangle rectangle = gaService.createPlainRectangle(boxAnchor);
		rectangle.setForeground(manageColor(ColorConstant.BLACK));
		rectangle.setBackground(manageColor(ColorConstant.WHITE));
		rectangle.setLineWidth(2);

		// anchor is located on the right border of the visible rectangle
		// and touches the border of the invisible rectangle
		gaService.setLocationAndSize(rectangle, 0, -ANCHOR_SIZE / 2, ANCHOR_SIZE, ANCHOR_SIZE);

		// create link and wire it
		link(containerShape, input);
		link(boxAnchor, input);

		// call the layout feature
		layoutPictogramElement(containerShape);

		if (input.getLink() != null) {
			CreateConnectionContext ccc = new CreateConnectionContext();
			for (PictogramElement element : getFeatureProvider(
				).getAllPictogramElementsForBusinessObject(input.getLink())) {
				if (element instanceof Anchor) {
					ccc.setSourceAnchor((Anchor) element);
				}
			}
			ccc.setTargetAnchor(boxAnchor);
			new CreateFunctionBlockDiagramConnectionFeature(
					getFeatureProvider()).create(ccc);
		}
		return containerShape;
	}
}
