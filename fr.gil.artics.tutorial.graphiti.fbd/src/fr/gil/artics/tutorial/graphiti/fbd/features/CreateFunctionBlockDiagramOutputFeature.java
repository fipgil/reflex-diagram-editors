package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.examples.common.ExampleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import fr.gil.artics.tutorial.fbd.FunctionBlockDiagram;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramOutput;
import fr.gil.artics.tutorial.fbd.impl.FunctionBlockDiagramFactoryImpl;
import fr.gil.artics.tutorial.graphiti.fbd.diagram.DomainModel;

public class CreateFunctionBlockDiagramOutputFeature extends AbstractCreateFeature {
	private static final String TITLE = "Create Output";

	private static final String USER_QUESTION = "Enter new output name";

	public CreateFunctionBlockDiagramOutputFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "Output", "Create Output");
	}

	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	public Object[] create(ICreateContext context) {
		// ask user for Block name
		String newOutputName = ExampleUtil.askString(TITLE, USER_QUESTION, "");
		if (newOutputName == null || newOutputName.trim().length() == 0) {
			return EMPTY;
		}

		// create Output
		FunctionBlockDiagramOutput output = FunctionBlockDiagramFactoryImpl.eINSTANCE.createFunctionBlockDiagramOutput();
		output.setName(newOutputName);

		FunctionBlockDiagram diagram = DomainModel.getInstance().getBusinessDiagram(getFeatureProvider(), getDiagram());
		diagram.getOutputs().add(output);

		// do the add
		PictogramElement element = addGraphicalRepresentation(context, output);

		// call the layout feature
		layoutPictogramElement(element);

		// return newly created business object(s)
		return new Object[] { output };
	}

}
