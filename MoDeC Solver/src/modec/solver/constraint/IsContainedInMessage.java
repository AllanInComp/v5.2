/*
 * (c) Copyright 2001-2004 Yann-Ga�l Gu�h�neuc,
 * University of Montr�al.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the author, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHOR IS ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package modec.solver.constraint;

import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import metamodel.scenarioDiagram.Message;
import metamodel.scenarioDiagram.ScenarioDiagram;
import modec.util.ExecutionTraceParser;
import choco.Constraint;
import choco.ContradictionException;
import choco.integer.IntVar;
import choco.palm.PalmProblem;
import choco.palm.explain.PalmConstraintPlugin;
import choco.palm.integer.AbstractPalmBinIntConstraint;
import choco.palm.integer.PalmIntDomain;
import choco.palm.integer.PalmIntVar;
import choco.palm.search.PalmSolution;
import choco.util.IntIterator;

/**
 * @author Janice Ng
 */
public class IsContainedInMessage extends AbstractPalmBinIntConstraint
		implements Constraint {

	private ScenarioDiagram sd;
	public static void main(final String[] args) {
		final PalmProblem problem = new PalmProblem();
		final ExecutionTraceParser etp =
			new ExecutionTraceParser(
				"../MoDeC Bytecode Instrumentation Tests/TraceNumero2.trace");
		//new ExecutionTraceParser("../MoDeC Bytecode Instrumentation Tests/Builder_SearchManager.trace");
		//new ExecutionTraceParser("../MoDeC Bytecode Instrumentation Tests/TestFilms.trace");
		//new ExecutionTraceParser("../MoDeC Bytecode Instrumentation Tests/Memento_DCClient.trace");
		//new ExecutionTraceParser("../MoDeC Bytecode Instrumentation Tests/Evaluation_JHotDraw_Visitor_CutAndPasteRectangle.trace");

		final ScenarioDiagram sd = etp.getScenarioDiagram();
		final List componentsMessages = sd.visitComponentMessages();
		final List allClassifiers = sd.getAllClassifiers(componentsMessages);
		final int nbMessages = sd.countNbMessages(componentsMessages);
		final int nbClassifiers = sd.countNbClassifiers(allClassifiers);
		System.out.println(nbMessages + " " + nbClassifiers);

		sd.determineSourceCalledMessages(componentsMessages, allClassifiers);
		sd.determineDestinationCalledMessages(
			componentsMessages,
			allClassifiers);
		sd.determineClassifierIdx(allClassifiers);
		sd.determineIdxClassifier(allClassifiers);
		sd.determineMessageContainer(componentsMessages);

		final IntVar v_message1 =
			problem.makeBoundIntVar("message1", 0, nbMessages);
		final IntVar v_message2 =
			problem.makeBoundIntVar("message2", 0, nbMessages);

		problem.post(new IsContainedInMessage(
			v_message1,
			v_message2,
			sd,
			componentsMessages));

		// Yann 2013/08/12: Needed?
		//	problem.logger.setLevel(Level.INFO);
		problem.logger.addHandler(new Handler() {
			public void close() throws SecurityException {
			}
			public void flush() {
			}
			public void publish(LogRecord record) {
				if (record.getMessage().equals("A solution was found.")) {
					//					System.out.println(variable1.isInstantiated());
					//					System.out.println(variable2.isInstantiated());

					final List solutions = problem.getPalmSolver().solutions;
					System.out.println(solutions.size());
					final PalmSolution solution =
						(PalmSolution) solutions.get(solutions.size() - 1);

					System.out.println("OPERATION [1]: "
							+ sd.getIdxMessage(solution.getValue(0))
							+ "OPERATION [2]: "
							+ sd.getIdxMessage(solution.getValue(1)));
				}
			}
		});

		problem.solve(true);
		//System.out.println(problem.getPalmSolver().solutions);

	}

	public IsContainedInMessage(
		IntVar v0,
		IntVar v1,
		ScenarioDiagram sd,
		List componentsMessages) {
		this.v0 = v0;
		this.v1 = v1;
		this.sd = sd;
		this.hook = new PalmConstraintPlugin(this);
	}

	public void propagate() {

		if (this.v0.getDomain().getSize() > 0) {

			IntIterator iterator0 = this.v0.getDomain().getIterator();
			boolean toBeRemoved = true;

			while (iterator0.hasNext() /* && toBeRemoved*/
			) {

				int index_e0 = iterator0.next();
				IntIterator iterator1 = this.v1.getDomain().getIterator();
				toBeRemoved = true;

				while (iterator1.hasNext() && toBeRemoved) {

					final int index_e1 = iterator1.next();
					Message msg0 = this.sd.getIdxMessage(index_e0);
					Message msg1 = this.sd.getIdxMessage(index_e1);

					//System.out.println(msg0.getIndex() + "asjkf;sadf  " + msg0);
					List container0 = this.sd.getMessageContainer(msg0);
					//					System.out.print("msg0:" + msg1.getIndex() + " " + msg0);
					//					System.out.print("msg1:" + msg1.getIndex() + " " + msg1);
					//					if (container0 != null)
					//						System.out.println("diff null");
					//					else
					//						System.out.println("null");

					if (container0.contains(msg1) && index_e0 != index_e1)
						toBeRemoved = false;

					//					if (toBeRemoved) {
					//						choco.palm.explain.Explanation expl =
					//							((PalmProblem) this.getProblem()).makeExplanation();
					//						((PalmConstraintPlugin) this.getPlugIn()).self_explain(
					//							expl);
					//						((PalmIntVar) this.v1).self_explain(
					//							PalmIntDomain.DOM,
					//							expl);
					//						((PalmIntVar) this.v0).removeVal(
					//							index_e0,
					//							this.cIdx0,
					//							expl);
					//					}

				}

				if (toBeRemoved) {
					choco.palm.explain.Explanation expl =
						((PalmProblem) this.getProblem()).makeExplanation();
					((PalmConstraintPlugin) this.getPlugIn())
						.self_explain(expl);
					((PalmIntVar) this.v1)
						.self_explain(PalmIntDomain.DOM, expl);
					((PalmIntVar) this.v0)
						.removeVal(index_e0, this.cIdx0, expl);
				}

			}

		}

		if (this.v1.getDomain().getSize() > 0) {
			IntIterator iterator1 = this.v1.getDomain().getIterator();
			boolean toBeRemoved = true;

			while (iterator1.hasNext() /*&& toBeRemoved*/
			) {

				int index_e1 = iterator1.next();
				IntIterator iterator0 = this.v0.getDomain().getIterator();
				toBeRemoved = true;

				while (iterator0.hasNext() && toBeRemoved) {

					final int index_e0 = iterator0.next();

					Message msg0 = this.sd.getIdxMessage(index_e0);
					Message msg1 = this.sd.getIdxMessage(index_e1);
					List container0 = this.sd.getMessageContainer(msg0);

					//System.out.println(msg0 + "\t" + container0);

					if (container0.contains(msg1) && index_e0 != index_e1)
						toBeRemoved = false;

					//					if (toBeRemoved) {
					//						choco.palm.explain.Explanation expl =
					//							((PalmProblem) this.getProblem()).makeExplanation();
					//						((PalmConstraintPlugin) this.getPlugIn()).self_explain(
					//							expl);
					//						((PalmIntVar) this.v0).self_explain(
					//							PalmIntDomain.DOM,
					//							expl);
					//						((PalmIntVar) this.v1).removeVal(
					//							index_e1,
					//							this.cIdx1,
					//							expl);
					//					}
					//index_e1--;

				}
				if (toBeRemoved) {
					choco.palm.explain.Explanation expl =
						((PalmProblem) this.getProblem()).makeExplanation();
					((PalmConstraintPlugin) this.getPlugIn())
						.self_explain(expl);
					((PalmIntVar) this.v0)
						.self_explain(PalmIntDomain.DOM, expl);
					((PalmIntVar) this.v1)
						.removeVal(index_e1, this.cIdx1, expl);
				}

			}
		}
	}

	//		public void propagate() {
	//	
	//			if (v0.getDomain().getSize() > 0) {
	//				IntIterator iterator0 = v0.getDomain().getIterator();
	//				boolean toBeRemoved = true;
	//	
	//				while (iterator0.hasNext() && toBeRemoved) {
	//					int index_e0 = iterator0.next();
	//	
	//					if (index_e0 > -1) {
	//						Message msg0 =
	//							this.sd.getIdxMessage(componentsMessages, index_e0);
	//	
	//						IntIterator iterator1 = v1.getDomain().getIterator();
	//	
	//						while (iterator1.hasNext() && toBeRemoved) {
	//							int index_e1 = iterator1.next();
	//	
	//							if (index_e1 > -1) {
	//								Message msg1 =
	//									this.sd.getIdxMessage(
	//										componentsMessages,
	//										index_e1);
	//	
	//								if (componentsMessages.indexOf(msg1)
	//									> componentsMessages.indexOf(msg0)
	//									&& !msg1.equals(msg0))
	//									toBeRemoved = false;
	//							}
	//						}
	//	
	//						if (toBeRemoved) {
	//							choco.palm.explain.Explanation expl =
	//								((PalmProblem) this.getProblem()).makeExplanation();
	//							((PalmConstraintPlugin) this.getPlugIn()).self_explain(
	//								expl);
	//							((PalmIntVar) this.v1).self_explain(
	//								PalmIntDomain.DOM,
	//								expl);
	//							((PalmIntVar) this.v0).removeVal(
	//								index_e0,
	//								this.cIdx0,
	//								expl);
	//						}
	//					}
	//				}
	//			}
	//	
	//			if (v1.getDomain().getSize() > 0) {
	//				IntIterator iterator1 = v1.getDomain().getIterator();
	//				boolean toBeRemoved = true;
	//	
	//				while (iterator1.hasNext() && toBeRemoved) {
	//					int index_e1 = iterator1.next();
	//	
	//					if (index_e1 > -1) {
	//						Message msg1 =
	//							this.sd.getIdxMessage(componentsMessages, index_e1);
	//	
	//						if (componentsMessages.indexOf(msg1)
	//							< v0.getDomain().getSup())
	//							toBeRemoved = false;
	//	
	//						IntIterator iterator0 = v0.getDomain().getIterator();
	//	
	//						while (iterator0.hasNext() && toBeRemoved) {
	//							int index_e0 = iterator0.next();
	//	
	//							if (index_e0 > -1) {
	//								Message msg0 =
	//									this.sd.getIdxMessage(
	//										componentsMessages,
	//										index_e0);
	//								if (componentsMessages.indexOf(msg1)
	//									> componentsMessages.indexOf(msg0)
	//									&& !msg1.equals(msg0)) {
	//									toBeRemoved = false;
	//								}
	//							}
	//						}
	//	
	//						if (toBeRemoved) {
	//							choco.palm.explain.Explanation expl =
	//								((PalmProblem) this.getProblem()).makeExplanation();
	//							((PalmConstraintPlugin) this.getPlugIn()).self_explain(
	//								expl);
	//							((PalmIntVar) this.v0).self_explain(
	//								PalmIntDomain.DOM,
	//								expl);
	//							((PalmIntVar) this.v1).removeVal(
	//								index_e1,
	//								this.cIdx1,
	//								expl);
	//						}
	//					}
	//				}
	//			}
	//		}

	/* (non-Javadoc)
	 * @see choco.palm.integer.PalmIntVarListener#awakeOnRestoreVal(int, int)
	 */
	public void awakeOnRestoreVal(int idx, int val)
			throws ContradictionException {
		propagate();
	}

	/* (non-Javadoc)
	 * @see choco.palm.integer.PalmIntVarListener#whyIsTrue()
	 */
	public Set whyIsTrue() {

		return null;
	}

	/* (non-Javadoc)
	 * @see choco.palm.integer.PalmIntVarListener#whyIsFalse()
	 */
	public Set whyIsFalse() {

		return null;
	}

	/* (non-Javadoc)
	 * @see choco.Constraint#isSatisfied()
	 */
	public boolean isSatisfied() {

		return false;
	}

}
