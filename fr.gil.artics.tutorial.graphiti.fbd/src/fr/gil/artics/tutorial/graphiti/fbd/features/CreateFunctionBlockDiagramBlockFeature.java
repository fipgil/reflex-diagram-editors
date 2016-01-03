package fr.gil.artics.tutorial.graphiti.fbd.features;


import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

import fr.gil.artics.tutorial.fbd.FunctionBlockDiagram;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlock;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlockInput;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBody;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramInput;
import fr.gil.artics.tutorial.fbd.impl.FunctionBlockDiagramFactoryImpl;
import fr.gil.artics.tutorial.graphiti.fbd.diagram.DomainModel;

public class CreateFunctionBlockDiagramBlockFeature extends AbstractCreateFeature {
//	private static final String TITLE = "Create class";
//
//	private static final String USER_QUESTION = "Enter new class name";

	public CreateFunctionBlockDiagramBlockFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "Block", "Create Block");
	}

	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	private FunctionBlockDiagram getFunctionBlockDiagram (){
		IContainer rootContainer = ResourcesPlugin.getWorkspace().getRoot();
		PictogramLink pictogramLink = getDiagram().getLink();
		if (pictogramLink != null) {
			Resource modelResource =
				pictogramLink.getBusinessObjects().get(0).eResource();
			if (modelResource != null) {
				IResource modelFile = ResourcesPlugin.getWorkspace().getRoot()
						.findMember(modelResource.getURI().toPlatformString(true));
				if (modelFile != null) {
					rootContainer = modelFile.getProject();
				}
			}
		}
		ResourceListSelectionDialog dialog  = new ResourceListSelectionDialog(
				Display.getDefault().getActiveShell(),
				rootContainer,
				IResource.FILE);
		dialog.open();
		Object [] objects = dialog.getResult();
		if (objects != null) {
			if (objects.length > 0) {
				return DomainModel.getInstance().getFunctionBlockDiagram(
						(IFile) objects[0]);
			}
		}
		return null;
	}

	public Object[] create(ICreateContext context) {
		// ask user for Block name
		FunctionBlockDiagram fbd = getFunctionBlockDiagram ();

		if (fbd == null) {
			return EMPTY;
		}

//		String newClassName = ExampleUtil.askString(TITLE, USER_QUESTION, "");
//		if (newClassName == null || newClassName.trim().length() == 0) {
//			return EMPTY;
//		}

		// create Block
		FunctionBlockDiagramBlock block = FunctionBlockDiagramFactoryImpl.eINSTANCE.createFunctionBlockDiagramBlock();
		block.setName(fbd.getName());

		FunctionBlockDiagram diagram = DomainModel.getInstance().getBusinessDiagram(getFeatureProvider(), getDiagram());
		FunctionBlockDiagramBody body = (FunctionBlockDiagramBody) diagram.getBody();
		body.getBlocks().add(block);

		// do the add
		PictogramElement element = addGraphicalRepresentation(context, block);

		// create Inputs
		int index = 0;
		for (FunctionBlockDiagramInput input : fbd.getInputs()) {
			FunctionBlockDiagramBlockInput blockInput = FunctionBlockDiagramFactoryImpl.eINSTANCE
					.createFunctionBlockDiagramBlockInput();
			blockInput.setName(input.getName());
			block.getInputs().add(blockInput);
			CreateContext createContext = new CreateContext();
			createContext.setX(context.getX());
			// TODO change block input position
			createContext.setY(context.getY() + 30 + 40 * index);
			createContext.setWidth(context.getTargetContainer().getGraphicsAlgorithm().getWidth());
			createContext.setHeight(20);
			createContext.setTargetContainer(context.getTargetContainer());
			addGraphicalRepresentation(createContext, blockInput);
			index = index + 1;
		}

		// call the layout feature
		layoutPictogramElement(element);

		// return newly created business object(s)
		return new Object[] { block };
	}

}
