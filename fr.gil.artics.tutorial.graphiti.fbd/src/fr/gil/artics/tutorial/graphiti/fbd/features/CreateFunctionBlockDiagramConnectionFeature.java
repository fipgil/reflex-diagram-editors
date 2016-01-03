package fr.gil.artics.tutorial.graphiti.fbd.features;

import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import fr.gil.artics.tutorial.fbd.AbstractReadableConnectionPoint;
import fr.gil.artics.tutorial.fbd.AbstractWritableConnectionPoint;

public class CreateFunctionBlockDiagramConnectionFeature extends AbstractCreateConnectionFeature
		implements ICreateConnectionFeature {

	public CreateFunctionBlockDiagramConnectionFeature(IFeatureProvider fp) {
		super(fp, "Connection", "Creates a new Connection between two Function Block Diagram Objects");
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		PictogramElement sourcePictogramElement = context.getSourcePictogramElement();
		Anchor sourceAnchor = context.getSourceAnchor();
		if (sourceAnchor != null) {
			sourcePictogramElement = sourceAnchor.getParent();
		}
		return getBusinessObjectForPictogramElement(sourcePictogramElement) instanceof AbstractReadableConnectionPoint;
	}

	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		PictogramElement sourcePictogramElement = context.getSourcePictogramElement();
		PictogramElement targetPictogramElement = context.getTargetPictogramElement();
		Anchor targetAnchor = context.getTargetAnchor();
		if (targetAnchor != null) {
			targetPictogramElement = targetAnchor.getParent();
		}
		Anchor sourceAnchor = context.getSourceAnchor();
		if (sourceAnchor != null) {
			sourcePictogramElement = sourceAnchor.getParent();
		}

		return getBusinessObjectForPictogramElement(sourcePictogramElement)
									instanceof AbstractReadableConnectionPoint 
			&& getBusinessObjectForPictogramElement(targetPictogramElement)
									instanceof AbstractWritableConnectionPoint;
	}

	@Override
	public Connection create(ICreateConnectionContext context) {
		PictogramElement sourcePictogramElement = context.getSourcePictogramElement();
		PictogramElement targetPictogramElement = context.getTargetPictogramElement();
		Anchor targetAnchor = context.getTargetAnchor();
		if (targetAnchor != null) {
			targetPictogramElement = targetAnchor.getParent();
		}
		Anchor sourceAnchor = context.getSourceAnchor();
		if (sourceAnchor != null) {
			sourcePictogramElement = sourceAnchor.getParent();
		}

		AbstractWritableConnectionPoint target = (AbstractWritableConnectionPoint)
				getBusinessObjectForPictogramElement(targetPictogramElement);
		AbstractReadableConnectionPoint source = (AbstractReadableConnectionPoint) target.getLink();
		if (source != null) {
			for (PictogramElement pe : getFeatureProvider().getAllPictogramElementsForBusinessObject(target)) {
				if (pe instanceof Connection) {
					IRemoveContext rc = new RemoveContext(pe);
					IFeatureProvider featureProvider = getFeatureProvider();
					IRemoveFeature removeFeature = featureProvider.getRemoveFeature(rc);
					if (removeFeature != null) {
						removeFeature.remove(rc);
					}
				}
			}
		}
		
		source = (AbstractReadableConnectionPoint)
		getBusinessObjectForPictogramElement(sourcePictogramElement);
		target.setLink(source);
		
		Connection newConnection = null;

		AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
		addContext.setNewObject(target);
		newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);
		if (newConnection != null) {
			link(newConnection, target);
		}
		return newConnection;
	}
}
