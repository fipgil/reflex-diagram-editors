package fr.gil.artics.tutorial.graphiti.fbd.editor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;

import fr.gil.artics.tutorial.graphiti.fbd.Activator;

public class TempDiagramFilesManager implements IWorkbenchListener{

	private static final TempDiagramFilesManager instance =
			new TempDiagramFilesManager();

	private static boolean preShutdownReceived = false;
	private static boolean cleanUpDone = false;
	
	private static final List<URI> disposedURI = new Vector<URI>();
	private static final List<String> openedURI = new Vector<String>();
	
	private TempDiagramFilesManager() {
		PlatformUI.getWorkbench().addWorkbenchListener(this);
	}

	public static void addDiagramFile(URI diagramFile) {
		synchronized (instance) {
			openedURI.add(diagramFile.toFileString());
		}		
	}

	public static void disposeDiagramFile(URI diagramFile) {
		boolean doCleanUp = false;
		
		synchronized (instance) {
			if (!cleanUpDone) {
				doCleanUp = true;
				cleanUpDone = true;
			}
			if (preShutdownReceived) {
				disposedURI.add(diagramFile);
				diagramFile = null;
			}
			else {
				if (openedURI.contains(diagramFile.toFileString())) {
					openedURI.remove(diagramFile.toFileString());
				}
			}
		}
		if (diagramFile != null) {
			try {
				URIConverter.INSTANCE.delete(diagramFile, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (doCleanUp) {
			IPath path = Activator.getDefault().getStateLocation();
			File stateLocation = new File(path.toOSString());
			for (File file : stateLocation.listFiles()) {
				URI uri = URI.createFileURI(file.getAbsolutePath());
				synchronized (instance) {
					String uriPath = uri.toFileString();
					if (openedURI.contains(uriPath)) {
						uri = null;
					}
					if (!uriPath.endsWith(".diagram")) {
						uri = null;
					}

				}
				if (uri != null) {
					try {
						URIConverter.INSTANCE.delete(uri, null);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		List<URI> uriToDelete = new Vector<URI>();
		synchronized (instance) {
			if (preShutdownReceived) {
				uriToDelete.addAll(disposedURI);
				disposedURI.clear();
			}
			else {
				preShutdownReceived = true;
			}
		}
		for (URI uri : uriToDelete) {
			try {
				URIConverter.INSTANCE.delete(uri, null);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		return true;
	}

	@Override
	public void postShutdown(IWorkbench workbench) {
	}

}
