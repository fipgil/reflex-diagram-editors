package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
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

import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramOutput;

public class AddFunctionBlockDiagramOutputFeature extends AbstractAddShapeFeature {

	private static final int ANCHOR_SIZE = 20;
	private static final int MARGIN_SIZE = 10;

	public AddFunctionBlockDiagramOutputFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canAdd(IAddContext context) {
		// check if user wants to add an output
		if (context.getNewObject() instanceof FunctionBlockDiagramOutput) {
			// check if user wants to add to a diagram
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
	}

	public PictogramElement add(IAddContext context) {

		// get associated businessObject & diagram
		FunctionBlockDiagramOutput output = (FunctionBlockDiagramOutput) context.getNewObject();
		Diagram targetDiagram = (Diagram) context.getTargetContainer();

		getFeatureProvider().link(targetDiagram, output.getParent());

		// CONTAINER SHAPE WITH INVISIBLE RECTANGLE
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

		IGaService gaService = Graphiti.getGaService();

		// create invisible outer rectangle expanded by
		// the width needed for the anchor
		Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);

		// create and set visible rectangle inside invisible rectangle
		RoundedRectangle roundedRectangle = gaService.createPlainRoundedRectangle(invisibleRectangle, 10, 10);
		roundedRectangle.setBackground(manageColor(ColorConstant.WHITE));
		roundedRectangle.setForeground(manageColor(ColorConstant.BLACK));
		// roundedRectangle.setFilled(false);
		roundedRectangle.setLineWidth(2);

		// create shape for text
		final Shape shape = peCreateService.createShape(containerShape, false);

		// create and set text graphics algorithm
		final Text text = gaService.createPlainText(shape, output.getName());
		text.setForeground(manageColor(ColorConstant.BLACK));
		// text.setFilled(false);
		text.setBackground(manageColor(ColorConstant.WHITE));
		// text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
		text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));

		int width = GraphitiUi.getUiLayoutService().calculateTextSize(text).getWidth();
		int height = GraphitiUi.getUiLayoutService().calculateTextSize(text).getHeight();
		if (height < ANCHOR_SIZE)
			height = ANCHOR_SIZE;
		gaService.setLocationAndSize(invisibleRectangle, context.getX(), context.getY(),
				width + ANCHOR_SIZE + 2 * MARGIN_SIZE, height + 2 * MARGIN_SIZE);
		gaService.setLocationAndSize(roundedRectangle, ANCHOR_SIZE/2, 0, width + ANCHOR_SIZE / 2 + 2 * MARGIN_SIZE,
				height + 2 * MARGIN_SIZE);
		gaService.setLocationAndSize(text, ANCHOR_SIZE + MARGIN_SIZE, MARGIN_SIZE, width, height);

		// provide information to support direct-editing directly
		// after object creation (must be activated additionally)
		final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
		// set container shape for direct editing after object creation
		directEditingInfo.setMainPictogramElement(containerShape);
		// set shape and graphics algorithm where the editor for
		// direct editing shall be opened after object creation
		directEditingInfo.setPictogramElement(shape);
		directEditingInfo.setGraphicsAlgorithm(text);

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
		link(containerShape, output);
		link(boxAnchor, output);

		if (output.getLink() != null) {
			for (PictogramElement element : getFeatureProvider(
				).getAllPictogramElementsForBusinessObject(output.getLink())) {
				if (element instanceof Anchor) {
					CreateConnectionContext ccc = new CreateConnectionContext();
					ccc.setSourceAnchor((Anchor) element);
					ccc.setTargetAnchor(boxAnchor);
					new CreateFunctionBlockDiagramConnectionFeature(
							getFeatureProvider()).create(ccc);
				}
			}
		}

		// call the layout feature
		layoutPictogramElement(containerShape);

		return containerShape;
	}
}
