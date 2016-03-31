/**
 * Copyright 2016, Emory University
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
package edu.emory.mathcs.nlp.component.template.node;

import edu.emory.mathcs.nlp.component.template.util.BILOU;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 8, 2016
 */
public class StatementNode extends NLPNode{
	private static final long serialVersionUID = 7476507663839944246L;
	static final String ROOT_TAG = NLPNode.ROOT_TAG;
	
	protected String referant_label;
	
	public StatementNode(){ }
	
	public StatementNode(NLPNode node){
		super(node.getID(), node.getWordForm(), node.getLemma(), node.getPartOfSpeechTag(), 
			node.getNamedEntityTag(), node.getFeatMap(), node.getDependencyHead(), node.getDependencyLabel());
		setReferantLabel(BILOU.O.toString());
	}
	
	public StatementNode(NLPNode node, String referant_label){
		super(node.getID(), node.getWordForm(), node.getLemma(), node.getPartOfSpeechTag(), 
			node.getNamedEntityTag(), node.getFeatMap(), node.getDependencyHead(), node.getDependencyLabel());
		setReferantLabel((referant_label == null)? BILOU.O.toString() : referant_label);
	}
	
	public StatementNode toRoot(){
		set(0, ROOT_TAG, ROOT_TAG, ROOT_TAG, ROOT_TAG, new FeatMap(), null, null);
		setReferantLabel(ROOT_TAG);
		return this;
	}
	
	public String getReferantLabel(){
		return referant_label;
	}
	
	public void setReferantLabel(String label){
		this.referant_label = label;
	}
	
	@Override
	public String toString(){
		return String.format("%s\t%s", super.toString(), referant_label);
	}
}
