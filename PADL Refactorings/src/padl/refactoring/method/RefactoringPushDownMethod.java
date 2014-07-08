/** (c) Copyright 2001 and following years, Yann-Ga�l Gu�h�neuc,
 * University of Montreal.
 * 
 * /**
 * @author Saliha Bouden
 * @since 2006/03/31
 * 
 */
package padl.refactoring.method;

import padl.kernel.IAbstractLevelModel;
import padl.kernel.IClass;
import padl.kernel.IMethod;
import padl.kernel.exception.ModelDeclarationException;

public class RefactoringPushDownMethod extends RefactoringMethod {

	//private IAbstractLevelModel abstractLevelModel;
	public RefactoringPushDownMethod(
		final IAbstractLevelModel anAbstractLevelModel) {
		//this.abstractLevelModel = anAbstractLevelModel;
		super(anAbstractLevelModel);
	}

	private IMethod checkPreConditionsOfPushDownMethod(
		final String nameMethod,
		final IClass superClass,
		final IClass subClass) {

		final IMethod theMethod =
			this.getMethodToRefactor(superClass.getDisplayName(), nameMethod);
		if (theMethod != null) {
			if (!doesMethodInvoquedInClass(theMethod.getDisplayID(), superClass)) {
				if (doesNewMethodExist(
					subClass.getDisplayID(),
					theMethod.getDisplayName())) {
					throw new ModelDeclarationException(
						"Refactoring Push Down Method  is impossible to apply because the name of the method "
								+ theMethod.getDisplayName()
								+ " already exist in the subclass "
								+ subClass.getDisplayName());
				}

			}
			else {
				throw new ModelDeclarationException(
					"Refactoring Push Down Method  is impossible to apply because "
							+ theMethod.getDisplayName()
							+ " is invoqued in the super class "
							+ superClass.getDisplayName());
			}
		}
		return theMethod;
	}

	public void pushDownMethod(
		final String nameMethod,
		final String superClassName,
		final String subClassName) {

		final IClass superClass =
			(IClass) this.abstractLevelModel
				.getTopLevelEntityFromID(superClassName);
		final IClass subClass =
			(IClass) this.abstractLevelModel
				.getTopLevelEntityFromID(subClassName);

		final IMethod aMethod =
			this.checkPreConditionsOfPushDownMethod(
				nameMethod,
				superClass,
				subClass);
		if (aMethod != null) {
			subClass.addConstituent(aMethod);
			superClass.removeConstituentFromID(aMethod.getID());
		}
	}

	protected IMethod checkPreConditionsOfPushDownMethodAcceptOverloading(
		final String nameMethod,
		final IClass superClass,
		final IClass subClass) {

		final IMethod theMethod =
			this.getMethodToRefactor(superClass.getDisplayName(), nameMethod);
		if (theMethod != null) {
			final String aNewMethod =
				this.createNewMethodSignature(theMethod, nameMethod);
			if (!doesMethodInvoquedInClass(aNewMethod, superClass)) {
				if (doesNewMethodExistWithOverloading(
					subClass.getDisplayID(),
					aNewMethod)) {
					throw new ModelDeclarationException(
						"Refactoring Push Down Method  is impossible to apply because the signature of the method"
								+ theMethod.getDisplayName()
								+ "  already exist in the subclass "
								+ subClass.getDisplayName());
				}

			}
			else {
				throw new ModelDeclarationException(
					"Refactoring Push Down Method  is impossible to apply because the method "
							+ theMethod.getDisplayName()
							+ " is invoqued in the super class "
							+ superClass.getDisplayName());

			}
		}
		return theMethod;

	}

	public void pushDownMethodAcceptOverloading(
		final String nameMethod,
		final String superClassName,
		final String subClassName) {

		final IClass superClass =
			(IClass) this.abstractLevelModel
				.getTopLevelEntityFromID(superClassName);
		final IClass subClass =
			(IClass) this.abstractLevelModel
				.getTopLevelEntityFromID(subClassName);

		final IMethod aMethod =
			this.checkPreConditionsOfPushDownMethodAcceptOverloading(
				nameMethod,
				superClass,
				subClass);
		if (aMethod != null) {
			subClass.addConstituent(aMethod);
			superClass.removeConstituentFromID(aMethod.getID());
		}
	}
}
