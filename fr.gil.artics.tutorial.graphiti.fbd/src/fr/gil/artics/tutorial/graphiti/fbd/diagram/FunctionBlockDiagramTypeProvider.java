package fr.gil.artics.tutorial.graphiti.fbd.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;

public class FunctionBlockDiagramTypeProvider extends AbstractDiagramTypeProvider {

	public FunctionBlockDiagramTypeProvider() {
		super();
		setFeatureProvider(new FunctionBlockDiagramFeatureProvider(this));
	}
}
