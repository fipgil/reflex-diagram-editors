package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import fr.gil.artics.tutorial.fbd.AbstractReadableConnectionPoint;
import fr.gil.artics.tutorial.fbd.AbstractWritableConnectionPoint;

public class ReconnectionFunctionBlockDiagramConnectionFeature extends DefaultReconnectionFeature {

	public ReconnectionFunctionBlockDiagramConnectionFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void execute(IContext context) {
		if (context instanceof IReconnectionContext) {
			IReconnectionContext reconnectionContext = (IReconnectionContext) context;
			if (reconnectionContext.getReconnectType().equals(ReconnectionContext.RECONNECT_TARGET)) {
				Anchor anchor = reconnectionContext.getOldAnchor();
				PictogramElement parent = anchor.getParent();
				AbstractWritableConnectionPoint input = (AbstractWritableConnectionPoint) getBusinessObjectForPictogramElement(parent);
				AbstractReadableConnectionPoint output = input.getLink();
				input.setLink(null);
				anchor = reconnectionContext.getNewAnchor();
				parent = anchor.getParent();
				input = (AbstractWritableConnectionPoint) getBusinessObjectForPictogramElement(parent);
				input.setLink(output);
			}
			else {
				Anchor anchor = reconnectionContext.getNewAnchor();
				PictogramElement parent = anchor.getParent();
				AbstractReadableConnectionPoint output = (AbstractReadableConnectionPoint) getBusinessObjectForPictogramElement(parent);
				anchor = reconnectionContext.getConnection().getEnd();
				parent = anchor.getParent();
				AbstractWritableConnectionPoint input = (AbstractWritableConnectionPoint) getBusinessObjectForPictogramElement(parent);
				input.setLink(output);
			}
		}
		super.execute(context);
	}

}
