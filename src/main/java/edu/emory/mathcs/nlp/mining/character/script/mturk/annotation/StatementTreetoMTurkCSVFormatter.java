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
package edu.emory.mathcs.nlp.mining.character.script.mturk.annotation;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.mining.character.reader.SceneTSVReader;
import edu.emory.mathcs.nlp.mining.character.structure.Episode;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.Season;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;
import edu.emory.mathcs.nlp.mining.character.structure.Utterance;
import edu.emory.mathcs.nlp.mining.character.util.DataLoaderUtil;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 14, 2016
 */
public class StatementTreetoMTurkCSVFormatter {
	
	/* Constants */
	public static final String CSV_DELIM = StringConst.COMMA;
	public static final String EMPTY_CHARACTER = "-N/A-";
	public static final String EMPTY_QUESTION = "-DO NOT ANSWER-";
	public static final String HR_DELIM = "<hr style='margin: 2em 0 2em 0;'/>";
	
	public static final String HEADER_QUESTION_FROMAT = "Question_%d";
	public static final String HEADER_CHARACTER_FROMAT = "Extra_Character_%d";

	public static final String ENTRY_QUESTION_FROMAT = "%d. '%s' refers to?";
	
	public static final String REGEX_NONUTF = "[^\\x00-\\x7e]";
	public static final String REGEX_PARATHESE = "\\(.*\\)"; 
	/* ********* */
	
	private int scene_offset, q_lb, q_ub, extra_character_count;
	private Set<String> ign_referants, ign_speakers, ign_characters;
	
	public StatementTreetoMTurkCSVFormatter(int sceneOffset, int question_lowerbound, int question_upperbound, 
			int extra_character_limit, Set<String> ignore_referants, Set<String> ignore_spkeakers, Set<String> ignore_characters){
		scene_offset = sceneOffset;
		q_lb = question_lowerbound;
		q_ub = question_upperbound;
		extra_character_count = extra_character_limit;
		ign_referants = ignore_referants;
		ign_speakers = ignore_spkeakers;
		ign_characters = ignore_characters;
	}

	public String getCSVHeader(){
		StringJoiner joiner = new StringJoiner(StringConst.COMMA);

		joiner.add("Season_Id,Episode_Id,Scene_Id,Scene_html");
		for(int i = 1; i <= extra_character_count; i++)	joiner.add(String.format(HEADER_CHARACTER_FROMAT, i));
		for(int i = 1; i <= q_ub; i++)					joiner.add(String.format(HEADER_QUESTION_FROMAT, i));
		
		return joiner.toString();
	}
	
	public String formatScene(int season_id, Episode episode, int sceneId){
		Scene scene = episode.getScene(sceneId);
		if(scene == null) return null;
		
		List<String> list; Set<String> extra_speakers, ignore_words = getSpeakerSet(scene);
		ignore_words = ignore_words.stream().map(spk->StringUtils.toLowerCase(spk)).collect(Collectors.toSet());
		ignore_words.addAll(ign_referants);
		
		if(isQualifiedScene(scene, ignore_words)){
			StringJoiner joiner = new StringJoiner(CSV_DELIM);
			int i; List<String> questions = new ArrayList<>();
			
			// Get html
			String html = constructHtml(episode, sceneId, questions);
			
			// Get extra characters
			int start = Math.max(episode.getFirstSceneId(), sceneId - scene_offset),
				end = Math.min(episode.getLastSceneId(), sceneId + scene_offset);
			extra_speakers = getSpeakerSet(episode, start, end);
			extra_speakers.removeAll(ign_characters);
			
			// Construct a hit entry
			joiner.add(Integer.toString(season_id)); 
			joiner.add(Integer.toString(episode.getID())); 
			joiner.add(Integer.toString(sceneId));
			joiner.add(String.format("\"%s\"", html));
			
			list = new ArrayList<>(extra_speakers);
			for(i = 0; i < list.size() && i < extra_character_count; i++) 
				joiner.add(list.get(i));
			for(; i < extra_character_count; i++) 	joiner.add(EMPTY_CHARACTER);
			
			for(i = 0; i < Math.min(questions.size(), q_ub); i++)
				joiner.add(String.format(ENTRY_QUESTION_FROMAT, i+1, questions.get(i)));
			for(i = questions.size(); i < q_ub; i++) joiner.add(EMPTY_QUESTION);
			
			return joiner.toString().replaceAll(REGEX_NONUTF, StringConst.EMPTY);
		}
		return null;
	}
	
	public void formatScenes(SceneTSVReader reader, String in_path, String in_tree_ext, String in_speaker_ext, String out_path) throws Exception{
		List<Season> seasons = DataLoaderUtil.loadFromFiles(in_path, in_tree_ext, in_speaker_ext, reader);
		PrintWriter writer = new PrintWriter(IOUtils.createBufferedPrintStream(out_path));
		writer.println(getCSVHeader());
		
		int i, start, end; String html;
		for(Season seaon : seasons){
			for(Episode episode : seaon.getEpisodes()){
				start = episode.getFirstSceneId();
				end = episode.getLastSceneId();
			
				for(i = start; i <= end; i++){
					html = formatScene(seaon.getID(), episode, i);
					if(html != null) writer.println(html);
				}
			}
		}
		writer.close();
	}
	
	private String constructHtml(Episode episode, int sceneId, List<String> questions){
		int i, start = Math.max(episode.getFirstSceneId(), sceneId - scene_offset),
				end = Math.min(episode.getLastSceneId(), sceneId + scene_offset);
		
		// Construct speaker set of the range
		Set<String> ignored_words = getSpeakerSet(episode, start, end);
		ignored_words = ignored_words.stream().map(spk->StringUtils.toLowerCase(spk)).collect(Collectors.toSet());
		ignored_words.addAll(ign_referants); ignored_words.addAll(ign_characters);
		
		// Construct html of the range
		StringJoiner joiner = new StringJoiner(HR_DELIM);
		for(i = start; i < sceneId; i++) joiner.add(constructHtmlAux(episode.getScene(i), ignored_words, false, questions));
		joiner.add(constructHtmlAux(episode.getScene(sceneId), ignored_words, true, questions));
		for(i = sceneId+1; i <= end; i++) joiner.add(constructHtmlAux(episode.getScene(i), ignored_words, false, questions));
		
		return joiner.toString();
	}
	
	private String constructHtmlAux(Scene scene, Set<String> speakers, boolean markQuestions, List<String> questions){
		if(scene == null) return null;
		StringBuilder html = new StringBuilder();
		
		int i, len; char bilou; StatementNode node;
		String word, speaker; StringJoiner line, question = null;
		
		for(Utterance utterance : scene){
			for(StatementNode[] tree : utterance.getStatementTrees()){
				if( (len = tree.length) > 1){
					line = new StringJoiner(StringConst.SPACE);
					
					for(i = 1; i < len; i++){
						if(markQuestions){
							node = tree[i]; word = node.getWordForm().replace('’', '\''); 
							bilou = node.getReferantLabel().charAt(0);
							
							switch (bilou) {
							case 'U': 
								if(isQualifiedQuestion(node, speakers)){
									questions.add(word);
									word = String.format("<mark>%s</mark><sup>%d</sup>", word, questions.size());
								}
								break;
								
							case 'B':
								if(isQualifiedQuestion(node, speakers)){
									question = new StringJoiner(StringConst.SPACE);
									question.add(word); word = String.format("<mark>%s", word);
								}
								break;
								
							case 'I':
								if(question != null) 	
									question.add(word);
								break;
								
							case 'L':
								if(question != null) {
									question.add(word); questions.add(question.toString()); question = null;
									word = String.format("%s</mark><sup>%d</sup>", word, questions.size());
								}
								break;
							}
							line.add(word);
						}
						else line.add(tree[i].getWordForm());
					}
					
					speaker = utterance.getSpeaker();
					if(speaker == null) html.append("<tr><td><b></b></td><td>");
					else html.append(String.format("<tr><td><b>%s:</b></td><td>", speaker));
					
					html.append(String.format("%s</td></tr>", line.toString()));
				}
			}
		}
		
		return String.format("<table>%s</table>", html.toString().replace('"', '\''));
	}
	
	private Set<String> getSpeakerSet(Scene scene){
		Set<String> speakers, set = new HashSet<>();
		speakers = scene.getUtterances().stream().map(u->u.getSpeaker()).collect(Collectors.toSet());
		
		speakers.removeAll(ign_speakers);
		speakers.remove(StringConst.EMPTY);
		
		String speaker_low;
		for(String speaker : speakers){
			speaker_low = StringUtils.toLowerCase(speaker);
			if(!speaker_low.contains(" and ") && !speaker_low.contains(StringConst.COMMA) &&  
				!speaker_low.contains(StringConst.FW_SLASH) && !speaker_low.contains(StringConst.AMPERSAND))
				set.add(speaker.replaceAll(REGEX_PARATHESE, StringConst.EMPTY).trim());
		}
		return set;
	}
	
	private Set<String> getSpeakerSet(Episode episode, int fromScene, int toScene){
		Set<String> speakers = new HashSet<>();
		for(int i = fromScene; i <= toScene; i++)
			speakers.addAll(getSpeakerSet(episode.getScene(i)));
		return speakers;
	}
	
	private boolean isQualifiedQuestion(StatementNode node, Set<String> ignore_words){
		String low_wordform = node.getWordFormSimplifiedLowercase();
		char bilou = node.getReferantLabel().charAt(0);
		
		return !ignore_words.contains(low_wordform) && (bilou == 'B' || bilou == 'U');
	}
	
	private boolean isQualifiedScene(Scene scene, Set<String> ignore_words){
		int i, count = 0;
		for(Utterance utterance : scene){
			for(StatementNode[] tree : utterance.getStatementTrees()){
				for(i = 1; i < tree.length; i++){
					if(isQualifiedQuestion(tree[i], ignore_words)) count++;
				}
			}
		}
		return count >= q_lb && count <= q_ub;
	}
}
