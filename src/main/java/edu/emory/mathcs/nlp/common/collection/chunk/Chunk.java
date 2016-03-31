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
package edu.emory.mathcs.nlp.common.collection.chunk;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.emory.mathcs.nlp.common.collection.tuple.IntIntPair;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 9, 2016
 */
public class Chunk<N extends NLPNode> implements Serializable, Iterable<N>, Comparable<Chunk<N>>{
	private static final long serialVersionUID = -3742195733519671641L;
	
	private String chunk_tag;
	private N[] nodes;
	
	public Chunk(String chunk_tag, N[] nodes){
		this.chunk_tag = chunk_tag;
		this.nodes = nodes;
	}
	
	public String getChunkTag(){
		return chunk_tag;
	}
	
	public N getFirstNode(){
		return (nodes != null)? nodes[0] : null;
	}
	
	public N getHeadNode(){
		Set<N> ns = Arrays.stream(nodes).collect(Collectors.toSet());
		for(N n : nodes)
			if(!ns.contains(n.getDependencyHead()))
				return n;
		return getFirstNode();
	}
	
	public N[] getNodes(){
		return nodes;
	}
	
	public IntIntPair getChunkSpan(){
		return new IntIntPair(nodes[0].getID(), nodes[nodes.length-1].getID());
	}
	
	public List<NLPNode> getCompoundChunk(){
		return getHeadNode().getSubNodeList();
	}
	
	public void setChunkTag(String chunk_tag){
		this.chunk_tag = chunk_tag;
	}
	
	public void setNodes(N[] nodes){
		this.nodes = nodes;
	}

	@Override
	public int compareTo(Chunk<N> o) {
		int id1 = nodes[0].getID(), id2 = o.getNodes()[0].getID(), diff = id1-id2; 
		
		if(diff == 0)
			return o.getNodes().length - nodes.length;
		return diff;
	}

	@Override
	public Iterator<N> iterator() {
		return new Iterator<N>() {
			int i = 0, size = nodes.length;
			
			@Override
			public N next() { return nodes[i++]; }
			
			@Override
			public boolean hasNext() { return i < size; }
		};
	}
}
