package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.examples.common.ExampleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import fr.gil.artics.tutorial.fbd.FunctionBlockDiagram;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramInput;
import fr.gil.artics.tutorial.fbd.impl.FunctionBlockDiagramFactoryImpl;
import fr.gil.artics.tutorial.graphiti.fbd.diagram.DomainModel;

public class CreateFunctionBlockDiagramInputFeature extends AbstractCreateFeature {
	private static final String TITLE = "Create Input";

	private static final String USER_QUESTION = "Enter new input name";

	public CreateFunctionBlockDiagramInputFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "Input", "Create Input");
	}

	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	public Object[] create(ICreateContext context) {
		// ask user for Block name
		String newInputName = ExampleUtil.askString(TITLE, USER_QUESTION, "");
		if (newInputName == null || newInputName.trim().length() == 0) {
			return EMPTY;
		}

		// create Input
		FunctionBlockDiagramInput input = FunctionBlockDiagramFactoryImpl.eINSTANCE.createFunctionBlockDiagramInput();
		input.setName(newInputName);

		FunctionBlockDiagram diagram = DomainModel.getInstance().getBusinessDiagram(getFeatureProvider(), getDiagram());
		diagram.getInputs().add(input);

		// do the add
		PictogramElement element = addGraphicalRepresentation(context, input);

		// call the layout feature
		layoutPictogramElement(element);

		// return newly created business object(s)
		return new Object[] { input };
	}

}
