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
package edu.emory.mathcs.nlp.component.pos.feature;

import edu.emory.mathcs.nlp.component.pos.POSState;
import edu.emory.mathcs.nlp.component.util.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.util.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.util.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class POSFeatureTemplate<N extends NLPNode> extends FeatureTemplate<N,POSState<N>>
{
	private static final long serialVersionUID = -243334323533999837L;
	
	public POSFeatureTemplate()	
	{
		super();
	}

//	========================= FEATURE EXTRACTORS =========================
	
	@Override
	protected String getFeature(FeatureItem<?> item)
	{
		N node = state.getNode(item);
		if (node == null) return null;
		
		switch (item.field)
		{
		case ambiguity_class: return state.getAmbiguityClass(node);
		default: return getFeature(item, node);
		}
	}
	
	@Override
	protected String[] getFeatures(FeatureItem<?> item)
	{
		N node = state.getNode(item);
		return (node == null) ? null : getFeatures(item, node);
	}
}