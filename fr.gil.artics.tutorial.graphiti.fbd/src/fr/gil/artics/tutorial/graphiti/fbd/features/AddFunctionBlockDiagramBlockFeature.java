package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
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

import fr.gil.artics.tutorial.fbd.AbstractWritableConnectionPoint;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlock;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlockInput;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlockOutput;

public class AddFunctionBlockDiagramBlockFeature extends AbstractAddShapeFeature {

	private static final int ANCHOR_SIZE = 20;

	private static final int LINE_WIDTH = 2;

	private static final IColorConstant BLOCK_TEXT_FOREGROUND = IColorConstant.BLACK;

	private static final IColorConstant BLOCK_FOREGROUND = IColorConstant.BLACK;

	private static final IColorConstant BLOCK_BACKGROUND = IColorConstant.WHITE;

	private static final int MIN_HEIGHT = 3 * ANCHOR_SIZE;

	private static final int MIN_WIDTH = 3 * ANCHOR_SIZE;
		
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

	private GraphicsAlgorithm addInnerRectangle (
			IPeCreateService peCreateService,
			IGaService gaService,
			ContainerShape parentContainerShape,
			FunctionBlockDiagramBlock block,
			int width,
			int height) {

		Shape innerRectangleShape = peCreateService.createShape(
				parentContainerShape, false);

		RoundedRectangle innerRectangle =
			gaService.createRoundedRectangle(innerRectangleShape, 5, 5);
		innerRectangle.setForeground(manageColor(BLOCK_FOREGROUND));
		innerRectangle.setBackground(manageColor(BLOCK_BACKGROUND));
		innerRectangle.setLineWidth(LINE_WIDTH);
		gaService.setLocationAndSize(innerRectangle,
				 ANCHOR_SIZE/2, 0, width - ANCHOR_SIZE, height);
		return innerRectangle;	
	}

	private GraphicsAlgorithm addSeparatorLine (
			IPeCreateService peCreateService,
			IGaService gaService,
			ContainerShape parentContainerShape,
			FunctionBlockDiagramBlock block,
			int width,
			int height) {
		// create shape for line
		Shape shape = peCreateService.createShape(parentContainerShape, false);

		// create and set graphics algorithm
		Polyline polyline = gaService.createPolyline(shape,
				new int[] {  ANCHOR_SIZE/2, ANCHOR_SIZE, width - ANCHOR_SIZE/2,
						ANCHOR_SIZE});
		polyline.setForeground(manageColor(BLOCK_FOREGROUND));
		polyline.setLineWidth(LINE_WIDTH);	
		return polyline;
	}

	private Text addTitle (
			IPeCreateService peCreateService,
			IGaService gaService,
			ContainerShape parentContainerShape,
			FunctionBlockDiagramBlock block,
			int width,
			int height) {
		// create shape for text
		Shape shape = peCreateService.createShape(parentContainerShape, false);

		// create and set text graphics algorithm
		Text text = gaService.createText(shape, block.getName());
		text.setForeground(manageColor(BLOCK_TEXT_FOREGROUND));
		text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
		text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
		 System.out.println("text:"+text.getValue()+" "+text.getWidth()+
		 +GraphitiUi.getUiLayoutService().calculateTextSize(text).getWidth());

		gaService.setLocationAndSize(text,  ANCHOR_SIZE/2, 0,
				width - ANCHOR_SIZE, getTextHeight(text, null));
		return text;
	}

	private IDimension getTextSize (AbstractText abstractText, String text) {
		String oldText = abstractText.getValue();
		if (text != null) {
			abstractText.setValue(text);
		}
		IDimension textSize =
			GraphitiUi.getUiLayoutService().calculateTextSize(abstractText);
		abstractText.setValue(oldText);

		return textSize;
	}

	private int getTextWidth (AbstractText abstractText, String text) {
		return getTextSize(abstractText, text).getWidth();
	}

	private int getTextHeight (AbstractText abstractText, String text) {
		return getTextSize(abstractText, text).getHeight();
	}

	private int max (int value1, int value2) {
		if (value1 > value2) {
			return value1;
		}
		return value2;
	}

	private int getWidth (Text titleText,
			FunctionBlockDiagramBlock block,
			int minWidth) {
		int width = minWidth;

		int titleWidth = getTextWidth(titleText, null);
		int widthFromTitle = titleWidth + 3 * ANCHOR_SIZE;
		width = max(width, widthFromTitle);

		int maxInputWidth = 0;
		for (FunctionBlockDiagramBlockInput input : block.getInputs()) {
			maxInputWidth = max
					(maxInputWidth, getTextWidth(titleText, input.getName()));
		}

		int maxOutputWidth = 0;
		for (FunctionBlockDiagramBlockOutput output : block.getOutputs()) {
			maxOutputWidth = max
					(maxOutputWidth, getTextWidth(titleText, output.getName()));
		}

		int widthFromInterface =
			2 * ANCHOR_SIZE + maxInputWidth + maxOutputWidth;
		width = max (width, widthFromInterface);
		return width;
	}

	private int getHeight(Text titleText, FunctionBlockDiagramBlock block, int height) {
		int inputsCount = block.getInputs().size();
		int outputsCount = block.getOutputs().size();
		
		height = max (height, ANCHOR_SIZE * (2 * inputsCount + 1));
		height = max (height, ANCHOR_SIZE * (2 * outputsCount + 1));
		
		if (inputsCount == outputsCount) { 
			height = height + ANCHOR_SIZE;
		}
		return height;
	}

	private void addInput (
			IPeCreateService peCreateService,
			IGaService gaService,
			ContainerShape parentContainerShape,
			Rectangle invisibleRectangle,
			FunctionBlockDiagramBlockInput input,
			int width,
			int inputY,
			int height) {

		double y = inputY;
		double blockHeight = height;
		
		// create an box relative anchor at middle-left
		final BoxRelativeAnchor boxAnchor =
				peCreateService.createBoxRelativeAnchor(parentContainerShape);

		boxAnchor.setRelativeWidth(0.0);
		boxAnchor.setRelativeHeight(y/blockHeight);

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

		// create shape for text
		Shape shape = peCreateService.createShape(parentContainerShape, false);

		// create and set text graphics algorithm
		Text text = gaService.createText(shape, input.getName());
		text.setForeground(manageColor(BLOCK_TEXT_FOREGROUND));
		text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);

		// vertical alignment has as default value "center"
		text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));

		gaService.setLocationAndSize(text, ANCHOR_SIZE + ANCHOR_SIZE/2, inputY,
			GraphitiUi.getUiLayoutService().calculateTextSize(text).getWidth(),
			GraphitiUi.getUiLayoutService().calculateTextSize(text).getHeight());

		// create link and wire it
		link(boxAnchor, input);

		if (input.getLink() != null) {
			for (PictogramElement element : getFeatureProvider(
				).getAllPictogramElementsForBusinessObject(input.getLink())) {
				if (element instanceof Anchor) {
					CreateConnectionContext ccc = new CreateConnectionContext();
					ccc.setSourceAnchor((Anchor) element);
					ccc.setTargetAnchor(boxAnchor);
					new CreateFunctionBlockDiagramConnectionFeature(
							getFeatureProvider()).create(ccc);
				}
			}
		}
		
	}

	private void addOutput (
			IPeCreateService peCreateService,
			IGaService gaService,
			ContainerShape parentContainerShape,
			Rectangle invisibleRectangle,
			FunctionBlockDiagramBlockOutput output,
			int width,
			int outputY,
			int height) {

		double y = outputY;
		double blockHeight = height;
		
		// create an box relative anchor at middle-left
		final BoxRelativeAnchor boxAnchor =
				peCreateService.createBoxRelativeAnchor(parentContainerShape);

		boxAnchor.setRelativeWidth(1.0);
		boxAnchor.setRelativeHeight(y/blockHeight);

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
		gaService.setLocationAndSize(rectangle,
				-ANCHOR_SIZE, -ANCHOR_SIZE / 2, ANCHOR_SIZE, ANCHOR_SIZE);

		// create shape for text
		Shape shape = peCreateService.createShape(parentContainerShape, false);

		// create and set text graphics algorithm
		Text text = gaService.createText(shape, output.getName());
		text.setForeground(manageColor(BLOCK_TEXT_FOREGROUND));
		text.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);

		// vertical alignment has as default value "center"
		text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));

		gaService.setLocationAndSize(text, ANCHOR_SIZE, outputY,
			width - 2 * ANCHOR_SIZE - ANCHOR_SIZE/2,
			GraphitiUi.getUiLayoutService().calculateTextSize(text).getHeight());

		// create link and wire it
		link(boxAnchor, output);

		if (output.getLinks() != null) {
			for (AbstractWritableConnectionPoint wcp : output.getLinks()) {
				for (PictogramElement element : getFeatureProvider(
					).getAllPictogramElementsForBusinessObject(wcp)) {
					if (element instanceof Anchor) {
						CreateConnectionContext ccc = new CreateConnectionContext();
						ccc.setTargetAnchor((Anchor) element);
						ccc.setSourceAnchor(boxAnchor);
						new CreateFunctionBlockDiagramConnectionFeature(
								getFeatureProvider()).create(ccc);			
					}
				}
			}
		}
	}

	private void addInputs (
			IPeCreateService peCreateService,
			IGaService gaService,
			ContainerShape parentContainerShape,
			Rectangle invisibleRectangle,
			FunctionBlockDiagramBlock block,
			int width,
			int height) {
		int inputHeight = 2 * ANCHOR_SIZE;
		int inputsCount = block.getInputs().size();
		int outputsCount = block.getOutputs().size();

		if (outputsCount > inputsCount) {
			inputHeight = 3 * ANCHOR_SIZE;
		}
		for (FunctionBlockDiagramBlockInput input : block.getInputs()) {
			addInput(peCreateService, gaService, parentContainerShape,
				invisibleRectangle, input, width, inputHeight, height);
			inputHeight = inputHeight + 2 * ANCHOR_SIZE;
		}
	}

	private void addOutputs (
			IPeCreateService peCreateService,
			IGaService gaService,
			ContainerShape parentContainerShape,
			Rectangle invisibleRectangle,
			FunctionBlockDiagramBlock block,
			int width,
			int height) {
		int outputHeight = 2 * ANCHOR_SIZE;
		int inputsCount = block.getInputs().size();
		int outputsCount = block.getOutputs().size();

		if (outputsCount <= inputsCount) {
			outputHeight = 3 * ANCHOR_SIZE;
		}
		for (FunctionBlockDiagramBlockOutput output : block.getOutputs()) {
			addOutput(peCreateService, gaService, parentContainerShape,
				invisibleRectangle, output, width, outputHeight, height);
			outputHeight = outputHeight + 2 * ANCHOR_SIZE;
		}
	}

	public PictogramElement add(IAddContext context) {

		FunctionBlockDiagramBlock block =
				(FunctionBlockDiagramBlock) context.getNewObject();
		Diagram targetDiagram = (Diagram) context.getTargetContainer();

		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IGaService gaService = Graphiti.getGaService();

		// CONTAINER SHAPE WITH INVISIBLE RECTANGLE
		ContainerShape containerShape = peCreateService.createContainerShape(
				targetDiagram, true);

		// define a default size for the shape
		int width = context.getWidth();
		int height = context.getHeight();
		if (height < MIN_HEIGHT) {
			height = MIN_HEIGHT;
		}
		int inputsCount = block.getInputs().size();
		int outputsCount = block.getOutputs().size();
		int maxOfInputsOrOutputsCount = inputsCount;
		if (inputsCount < outputsCount) {
			maxOfInputsOrOutputsCount = outputsCount;
		}
		height = ANCHOR_SIZE * (2 * maxOfInputsOrOutputsCount + 2);		
		if (width < MIN_WIDTH) {
			width = MIN_WIDTH;
		}
		if (height < MIN_HEIGHT) {
			height = MIN_HEIGHT;
		}
		
		// create invisible outer rectangle expanded by
		// the width needed for the anchors
		Rectangle invisibleRectangle =
			gaService.createInvisibleRectangle(containerShape);
		gaService.setLocationAndSize(
			invisibleRectangle, context.getX(), context.getY(),
			MIN_WIDTH, MIN_HEIGHT);

		// create link and wire it
		link(containerShape, block);

		GraphicsAlgorithm innerRectangle = addInnerRectangle(
			peCreateService, gaService, containerShape, block, width, height);
		
		Text titleText = addTitle(
			peCreateService, gaService, containerShape, block, width, height);

		width = getWidth(titleText, block, width);
		height = getHeight (titleText, block, height);

		gaService.setLocationAndSize(
				invisibleRectangle, context.getX(), context.getY(),
				width, height);
		gaService.setLocationAndSize(
				innerRectangle, ANCHOR_SIZE/2, 0, width - ANCHOR_SIZE, height);

		gaService.setLocationAndSize(
				titleText, ANCHOR_SIZE/2, 0, width - ANCHOR_SIZE,
				getTextHeight(titleText, null));

		addSeparatorLine (
			peCreateService, gaService, containerShape, block, width, height);

		addInputs(peCreateService, gaService, containerShape,
				invisibleRectangle, block, width, height);

		addOutputs(peCreateService, gaService, containerShape,
				invisibleRectangle, block, width, height);

		// call the layout feature
		layoutPictogramElement(containerShape);

		return containerShape;
	}

}
