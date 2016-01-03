package fr.gil.artics.tutorial.graphiti.fbd.diagram;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;

import fr.gil.artics.tutorial.fbd.FunctionBlockDiagram;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramFactory;
import fr.gil.artics.tutorial.graphiti.fbd.Activator;

/**
 * This singleton class is use:
 * 
 * 1. to get the temporary (*.diagram) file located in this plugin state
 * location eg: workspace/.metadata/.plugins/fr.gil.artics.tutorial.
 * 
 * 2- to create the businessObjects model file (*.fbd)
 * 
 * TODO delete obsolete diagram temporary files.
 */
public class DomainModel {

	/**
	 * The shared instance.
	 */
	private static final DomainModel instance = new DomainModel();

	/**
	 * cf fr.gil.artics.tutorial.graphiti.fbd.file-content-type extension point
	 */
	private static final String FBD = "fbd";

	/**
	 * the diagram files extension.
	 */
	private static final String DIAGRAM = "diagram";

	/**
	 * The key=diagramURI value=modelURI map used to get from a Diagram object
	 * the FunctionBlockDiagram business model diagram object.
	 */
	private final Map<URI, URI> diagramToModelMap = new HashMap<URI, URI>();

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DomainModel getInstance() {
		return instance;
	}

	/**
	 * If needed create model file and set its content to fbd root
	 * businessObject
	 * 
	 * @param fbd: root businessObject
	 * @param diagram: root graphiti object
	 */
	private void setModelContent (final FunctionBlockDiagram fbd,
			final Diagram diagram) {

		final URI diagramUri = diagram.eResource().getURI();
		URI modelUri = diagramToModelMap.get(diagramUri);
		
		// FIXME to be removed if fbd *.diagram file not supported.
		// modelUri is null when editing a diagram file located in the
		// workspace.
		if (modelUri == null) {
			// In this case a *.fbd file sibling of the *.diagram file is
			// created.
			modelUri = diagramUri.trimFileExtension();
			modelUri = modelUri.appendFileExtension(FBD);
		}

		// the model file resource to use to set the root businessObject
		Resource resource = null;

		final IResource file = ResourcesPlugin.getWorkspace().getRoot().
				findMember(modelUri.toPlatformString(true));
		if (file == null || !file.exists()) {
			// create the modelUri file.
			resource =
				diagram.eResource().getResourceSet().createResource(modelUri);
		} else {
			try {
				// load the model file into resource object..
				resource = diagram.eResource().getResourceSet().getResource(
						modelUri, true);
				// remove its previous content.
				resource.getContents().clear();
			} catch (WrappedException e) {
				// FIXME
				// The content of the model file was invalid.
				// reset it.
				resource = diagram.eResource().getResourceSet().createResource(
						modelUri);
			}
		}
		// set the new model content. 
		resource.getContents().add(fbd);
	}

	/**
	 * @param featureProvider to link Diagram & FunctionBlockDiagram
	 * @param diagram: root graphiti object
	 * @return the root businessObject
	 */
	public FunctionBlockDiagram getBusinessDiagram(
			IFeatureProvider featureProvider, Diagram diagram) {

		PictogramLink link = diagram.getLink();

		if (link == null) {

			// create business object
			FunctionBlockDiagram fbd = FunctionBlockDiagramFactory.eINSTANCE.
					createFunctionBlockDiagram();
			fbd.setBody(FunctionBlockDiagramFactory.eINSTANCE.
					createFunctionBlockDiagramBody());

			setModelContent (fbd, diagram);

			featureProvider.link(diagram, fbd);

			link = diagram.getLink();
		}

		if (link.getBusinessObjects().size() == 1 && 
			link.getBusinessObjects().get(0) instanceof FunctionBlockDiagram) {
			// Return the automaton object
			return (FunctionBlockDiagram) link.getBusinessObjects().get(0);
		} else {
			throw new IllegalStateException();
		}
	}

	/**
	 * @param diagramName
	 * @param modelUri if not null the returned diagramFile URI is added to
	 * diagramToModelMap
	 * 
	 * @return the URI to use to create the diagram file.
	 */
	public URI getTempURIFile(String diagramName, URI modelUri) {
		
		// build the temporary diagram URI absolute path
		IPath path = Activator.getDefault().getStateLocation();
		path = path.append(
			diagramName + "-" + new Random().nextLong() + "." + DIAGRAM);
		
		URI diagramUri = URI.createFileURI(path.toString());
		if (modelUri != null) {
			diagramToModelMap.put(diagramUri, modelUri);
		}
		return diagramUri;
	}

	public FunctionBlockDiagram getFunctionBlockDiagram (IFile file) {
		final URI modelFile = URI.createPlatformResourceURI(
				file.getFullPath().toString(), true);
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource model = resourceSet.getResource(modelFile, true);
		EList<EObject> contents = model.getContents();
		if (contents.size() == 1) {
			EObject object = contents.get(0);
			if (object instanceof FunctionBlockDiagram) {
				return (FunctionBlockDiagram) object;
			}
		}
		return null;
	}
}
