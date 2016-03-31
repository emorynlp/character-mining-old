/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.mathcs.nlp.component.ner;

import java.io.InputStream;
import java.util.List;

import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.eval.F1Eval;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERTagger extends OnlineComponent<NERState, NLPNode>
{
	private static final long serialVersionUID = 87807440372806016L;

	public NERTagger() {super(false);}
	
	public NERTagger(InputStream configuration)
	{
		super(false, configuration);
	}
	
//	============================== ABSTRACT ==============================
	
	@Override
	public Eval createEvaluator()
	{
		return new F1Eval();
	}
	
	@Override
	protected NERState initState(NLPNode[] nodes)
	{
		return new NERState(nodes);
	}
	
	@Override
	protected void postProcess(NERState state)
	{
		state.postProcess();
	}

	@Override
	protected NERState initState(List<NLPNode[]> document) {return null;}
}
