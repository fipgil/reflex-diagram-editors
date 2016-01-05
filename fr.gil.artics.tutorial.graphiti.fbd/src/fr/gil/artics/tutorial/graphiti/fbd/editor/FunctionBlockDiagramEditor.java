package fr.gil.artics.tutorial.graphiti.fbd.editor;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.examples.common.FileService;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;

import fr.gil.artics.tutorial.fbd.AbstractFunctionBlockDiagramBody;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagram;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlock;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlockInput;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBody;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramInput;
import fr.gil.artics.tutorial.graphiti.fbd.diagram.DomainModel;

public class FunctionBlockDiagramEditor extends DiagramEditor {
	
	/**
	 * see diagramType extension point
	 */
	private static final String FDB_DIAGRAM_TYPE_ID = "fbd";

	/**
	 * The *.fbd file currently edited
	 * The DiagramEditor input is the *.diagram file.
	 */
	private IFileEditorInput fileEditorInput;

	private void loadDiagram(IFileEditorInput fileEditorInput) {
		final URI modelFile = URI.createPlatformResourceURI(
				fileEditorInput.getFile().getFullPath().toString(), true);
		Resource model;
		try {
			// Load the resource through the editing domain.
			model = getDiagramBehavior().getEditingDomain().getResourceSet(
					).getResource(modelFile, true);
		} catch (WrappedException e) {
			// FIXME
			// The content of the model file was invalid. => reset it.
			model = getDiagramBehavior().getEditingDomain().getResourceSet(
					).createResource(modelFile);
		}
		final Diagram diagram = getDiagramTypeProvider().getDiagram();
		for (final EObject object : model.getContents()) {
			// FIXME check that there is only one FunctionBlockDiagram object
			if (object instanceof FunctionBlockDiagram) {
				FunctionBlockDiagram fbd = (FunctionBlockDiagram) object;
				// TODO should also create UI for outputs block calls & 
				// connnections.
				for (FunctionBlockDiagramInput input : fbd.getInputs()) {
			        CreateContext createContext = new CreateContext();
			        createContext.setX(input.getX());
			        createContext.setY(input.getY());
			        createContext.setTargetContainer(diagram);
			        
					getDiagramTypeProvider().getFeatureProvider().addIfPossible(
							new AddContext(createContext, input));
				}
				AbstractFunctionBlockDiagramBody body = fbd.getBody();
				if (body instanceof FunctionBlockDiagramBody) {
					for (FunctionBlockDiagramBlock block :
						((FunctionBlockDiagramBody) body).getBlocks()) {
				        CreateContext createContext = new CreateContext();
				        createContext.setX(block.getX());
				        createContext.setY(block.getY());
				        createContext.setWidth(block.getWidth());
				        createContext.setHeight(block.getHeight());
				        createContext.setTargetContainer(diagram);
				        
						PictogramElement element = getDiagramTypeProvider(
							).getFeatureProvider().addIfPossible(
								new AddContext(createContext, block));
						for (FunctionBlockDiagramBlockInput input : block.getInputs()) {
					        createContext = new CreateContext();
					        createContext.setX(input.getRelativeX());
					        createContext.setY(input.getRelativeY());
					        createContext.setTargetContainer(diagram);
					        
							getDiagramTypeProvider().getFeatureProvider().addIfPossible(
									new AddContext(createContext, input));
						}
						LayoutContext layoutContext = new LayoutContext(element);
						getDiagramTypeProvider().getFeatureProvider().layoutIfPossible(layoutContext);

					}
				}
				System.out.println("PGI +"+body);
			}
		}
		// FIXME needed to reset dirty bit at load time
		// this save to disk the diagram file
		// the model file is not saved because it is still unchanged.
		doSave(null);

	}
	private IDiagramEditorInput init (IFileEditorInput fileEditorInput) {
		
		final String diagramName = fileEditorInput.getFile().getLocation(
				).removeFileExtension().lastSegment();
		final URI modelFile = URI.createPlatformResourceURI(
				fileEditorInput.getFile().getFullPath().toString(), true);
		final URI diagramFile = DomainModel.getInstance().getTempURIFile(
				diagramName, modelFile);
		final Diagram diagram = Graphiti.getPeCreateService().createDiagram(
				FDB_DIAGRAM_TYPE_ID, diagramName, true);
		FileService.createEmfFileForDiagram(diagramFile, diagram);
		
		final String providerId = GraphitiUi.getExtensionManager(
				).getDiagramTypeProviderId(FDB_DIAGRAM_TYPE_ID);
		final DiagramEditorInput editorInput = new DiagramEditorInput(
				diagramFile, providerId);
		
		return editorInput;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if (input instanceof IFileEditorInput) {
			fileEditorInput = (IFileEditorInput) input;
			IDiagramEditorInput diagramEditorInput = init(fileEditorInput);
			TempDiagramFilesManager.addDiagramFile(diagramEditorInput.getUri());
			super.init(site, (IEditorInput) diagramEditorInput);
			loadDiagram(fileEditorInput);
		}
		else {
			if (input instanceof IDiagramEditorInput) {
				TempDiagramFilesManager.addDiagramFile(
						((IDiagramEditorInput)input).getUri());
			}
			super.init(site, input);
		}
	}

	@Override
	protected void setInput(IEditorInput input) {
		if (input instanceof IFileEditorInput) {
			fileEditorInput = (IFileEditorInput) input;
			IDiagramEditorInput diagramEditorInput = init(fileEditorInput);
			TempDiagramFilesManager.addDiagramFile(diagramEditorInput.getUri());
			super.setInput((IEditorInput) diagramEditorInput);
			loadDiagram(fileEditorInput);
		}
		else {
			super.setInput(input);
		}
	}
	@Override
	public String getTitleToolTip() {
		if (fileEditorInput != null) {
			return fileEditorInput.getToolTipText();
		}
		else {
			for (Resource resource : getDiagramBehavior().getEditingDomain(
					).getResourceSet().getResources()) {
				System.out.println(resource.getURI().lastSegment());
			}
		}
		return super.getTitleToolTip();
	}
	@Override
	public void dispose() {
		URI uri = getDiagramEditorInput().getUri();
		super.dispose();
		TempDiagramFilesManager.disposeDiagramFile(uri);
	}	
}
