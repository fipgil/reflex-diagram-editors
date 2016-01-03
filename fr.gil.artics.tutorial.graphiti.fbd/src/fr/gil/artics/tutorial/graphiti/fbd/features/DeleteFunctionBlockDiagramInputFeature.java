package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class DeleteFunctionBlockDiagramInputFeature extends DefaultDeleteFeature {

	public DeleteFunctionBlockDiagramInputFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void delete(IDeleteContext context) {
		super.delete(context);
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		return true;
	}


}
