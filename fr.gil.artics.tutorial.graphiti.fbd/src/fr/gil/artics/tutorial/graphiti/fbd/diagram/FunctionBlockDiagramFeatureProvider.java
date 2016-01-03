package fr.gil.artics.tutorial.graphiti.fbd.diagram;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

import fr.gil.artics.tutorial.fbd.AbstractConnectionPoint;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlock;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlockInput;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramBlockOutput;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramInput;
import fr.gil.artics.tutorial.fbd.FunctionBlockDiagramOutput;
import fr.gil.artics.tutorial.graphiti.fbd.features.AddFunctionBlockDiagramBlockFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.AddFunctionBlockDiagramBlockInputFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.AddFunctionBlockDiagramConnectionFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.AddFunctionBlockDiagramInputFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.CreateFunctionBlockDiagramBlockFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.DisabledRemoveFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.DisabledResizeShapeFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.CreateFunctionBlockDiagramConnectionFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.CreateFunctionBlockDiagramInputFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.DeleteFunctionBlockDiagramBlockFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.DeleteFunctionBlockDiagramConnectionFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.DeleteFunctionBlockDiagramInputFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.DisabledMoveShapeFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.LayoutFunctionBlockDiagramBlockFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.LayoutFunctionBlockDiagramInterfaceElementFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.MoveFunctionBlockDiagramBlockFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.MoveFunctionBlockDiagramInterfaceElement;
import fr.gil.artics.tutorial.graphiti.fbd.features.ReconnectionFunctionBlockDiagramConnectionFeature;
import fr.gil.artics.tutorial.graphiti.fbd.features.RemoveFunctionBlockDiagramConnectionFeature;

public class FunctionBlockDiagramFeatureProvider extends DefaultFeatureProvider {

	public FunctionBlockDiagramFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		return new ICreateFeature[] {
				new CreateFunctionBlockDiagramBlockFeature(this),
				new CreateFunctionBlockDiagramInputFeature(this) };
	}

	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return new ICreateConnectionFeature[] {
				new CreateFunctionBlockDiagramConnectionFeature(this) };
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {

		if (context instanceof IAddConnectionContext) {
			return new AddFunctionBlockDiagramConnectionFeature(this);
		} else if (context instanceof IAddContext) {
			Object newObject = context.getNewObject();

			if (newObject instanceof FunctionBlockDiagramBlock) {
				return new AddFunctionBlockDiagramBlockFeature(this);
			} else if (newObject instanceof FunctionBlockDiagramBlockInput) {
				return new AddFunctionBlockDiagramBlockInputFeature(this);
			} else if (newObject instanceof FunctionBlockDiagramInput) {
				return new AddFunctionBlockDiagramInputFeature(this);
			}
		}
		return super.getAddFeature(context);
	}

	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		Object businessObject =
				getBusinessObjectForPictogramElement(pictogramElement);
		if (businessObject instanceof FunctionBlockDiagramBlock) {
			return new LayoutFunctionBlockDiagramBlockFeature(this);
		} else if (businessObject instanceof FunctionBlockDiagramInput) {
			return new LayoutFunctionBlockDiagramInterfaceElementFeature(this);
		} else if (businessObject instanceof FunctionBlockDiagramOutput) {
			return new LayoutFunctionBlockDiagramInterfaceElementFeature(this);
		}
		return super.getLayoutFeature(context);
	}

	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		Shape shape = context.getShape();
		Object businessObject = getBusinessObjectForPictogramElement(shape);
		if (businessObject instanceof FunctionBlockDiagramBlock) {
			return new MoveFunctionBlockDiagramBlockFeature(this);
		} else if (businessObject instanceof FunctionBlockDiagramBlockInput) {
			return new DisabledMoveShapeFeature(this);
		} else if (businessObject instanceof FunctionBlockDiagramBlockOutput) {
			return new DisabledMoveShapeFeature(this);
		} else if (businessObject instanceof FunctionBlockDiagramInput) {
			return new MoveFunctionBlockDiagramInterfaceElement(this);
		} else if (businessObject instanceof FunctionBlockDiagramOutput) {
			return new MoveFunctionBlockDiagramInterfaceElement(this);
		}
		return super.getMoveShapeFeature(context);
	}

	@Override
	public IResizeShapeFeature getResizeShapeFeature(
			IResizeShapeContext context) {
		Shape shape = context.getShape();
		Object businessObject = getBusinessObjectForPictogramElement(shape);
		if (businessObject instanceof FunctionBlockDiagramBlockInput) {
			return new DisabledResizeShapeFeature(this);
		} else if (businessObject instanceof FunctionBlockDiagramBlockOutput) {
			return new DisabledResizeShapeFeature(this);
		} else if (businessObject instanceof FunctionBlockDiagramInput) {
			return new DisabledResizeShapeFeature(this);
		} else if (businessObject instanceof FunctionBlockDiagramOutput) {
			return new DisabledResizeShapeFeature(this);
		}
		return super.getResizeShapeFeature(context);
	}

	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		Object businessObject =
				getBusinessObjectForPictogramElement(pictogramElement);
		if (businessObject instanceof FunctionBlockDiagramBlock) {
			return new DisabledRemoveFeature(this, false, true);
		} else if (businessObject instanceof FunctionBlockDiagramBlockInput) {
			if (pictogramElement instanceof Connection) {
				return new RemoveFunctionBlockDiagramConnectionFeature(this);
			} else {
				return new DisabledRemoveFeature(this);
			}
		} else if (businessObject instanceof FunctionBlockDiagramBlockOutput) {
			return new DisabledRemoveFeature(this);
		} else if (businessObject instanceof FunctionBlockDiagramInput) {
			return new DisabledRemoveFeature(this);
		} else if (businessObject instanceof FunctionBlockDiagramOutput) {
			return new DisabledRemoveFeature(this);
		}
		return super.getRemoveFeature(context);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		Object businessObject =
				getBusinessObjectForPictogramElement(pictogramElement);
		if (businessObject instanceof FunctionBlockDiagramBlockInput) {
			if (pictogramElement instanceof Connection) {
				return new DeleteFunctionBlockDiagramConnectionFeature(this);
			}
		} else if (businessObject instanceof FunctionBlockDiagramBlock) {
			return new DeleteFunctionBlockDiagramBlockFeature(this);
		} else if (businessObject instanceof FunctionBlockDiagramInput) {
			return new DeleteFunctionBlockDiagramInputFeature(this);
		}
		return super.getDeleteFeature(context);
	}

	@Override
	public IReconnectionFeature getReconnectionFeature(
			IReconnectionContext context) {
		Anchor anchor = context.getConnection().getEnd();
		PictogramElement pictogramElement = anchor.getParent();
		Object businessObject =
				getBusinessObjectForPictogramElement(pictogramElement);
		if (businessObject instanceof AbstractConnectionPoint) {
			return new ReconnectionFunctionBlockDiagramConnectionFeature(this);
		}
		return super.getReconnectionFeature(context);
	}
}
