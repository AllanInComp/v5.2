/**
 * NMA - Number of Methods A******  Ask Yann
 * 
 * @author Moha N. & Huynh D.L.
 * @since  2005/08/18
 * Copy of NAD by Aminata Saban�
 * 04/06/2012 for the study of testability of patterns by metrics (Bruntink paper)
 * 
 * Modifications made to fit the new architecture
 */
package pom.metrics.repository;

import java.util.List;
import padl.kernel.IAbstractModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class NOF extends AbstractMetric implements IMetric, IUnaryMetric {
	protected double concretelyCompute(
		final IAbstractModel anAbstractModel,
		final IFirstClassEntity firstClassEntity) {

		return this.listOfElements(firstClassEntity).size();
	}
	private List listOfElements(final IFirstClassEntity firstClassEntity) {
		return super.classPrimitives.listOfImplementedFields(firstClassEntity);
	}
	public String getDefinition() {
		return "Number of attributes declared by an entity.";
	}
}
