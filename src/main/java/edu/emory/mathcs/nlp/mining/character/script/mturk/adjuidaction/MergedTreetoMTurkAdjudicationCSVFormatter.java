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
package edu.emory.mathcs.nlp.mining.character.script.mturk.adjuidaction;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.mining.character.script.mturk.annotation.StatementTreetoMTurkCSVFormatter;
import edu.emory.mathcs.nlp.mining.character.structure.Episode;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.Season;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;
import edu.emory.mathcs.nlp.mining.character.structure.Utterance;
import edu.emory.mathcs.nlp.mining.character.util.DataLoaderUtil;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 20, 2016
 */
public class MergedTreetoMTurkAdjudicationCSVFormatter {
	private static final String 
		Q_FORMAT 				= StatementTreetoMTurkCSVFormatter.HEADER_QUESTION_FROMAT, 
		EXTRA_CHARACTER_FORMAT 	= StatementTreetoMTurkCSVFormatter.HEADER_CHARACTER_FROMAT,
		OPT_FORMAT 				= "Q%d_Option%d";
	
	private static final String ENTRY_QUESTION_FROMAT = StatementTreetoMTurkCSVFormatter.ENTRY_QUESTION_FROMAT;
	private static final String REGEX_NONUTF = StatementTreetoMTurkCSVFormatter.REGEX_NONUTF;
	private static final String REGEX_PARATHESE = StatementTreetoMTurkCSVFormatter.REGEX_PARATHESE;
	private static final String HR_DELIM = StatementTreetoMTurkCSVFormatter.HR_DELIM;
	private static final String EMPTY_CHARACTER = StatementTreetoMTurkCSVFormatter.EMPTY_CHARACTER;
	private static final String EMPTY_QUESTION = StatementTreetoMTurkCSVFormatter.EMPTY_QUESTION;
	private static final String EMPTY_OPTION = StatementTreetoMTurkCSVFormatter.EMPTY_CHARACTER;

	private int scene_offset, question_count, option_count, extra_character_count;
	private Set<String> ignore_spkeakers, ignore_characters;
	
	public MergedTreetoMTurkAdjudicationCSVFormatter(int scene_offset, int question_count, int option_count, int extra_character_count, Set<String> ignore_spkeakers, Set<String> ignore_characters){
		this.scene_offset = scene_offset;
		this.question_count = question_count;
		this.option_count = option_count;
		this.extra_character_count = extra_character_count;
		this.ignore_spkeakers = ignore_spkeakers;
		this.ignore_characters = ignore_characters;
	}
	
	public String getCSVHeader(){
		StringJoiner joiner = new StringJoiner(StringConst.COMMA);

		joiner.add("Season_Id,Episode_Id,Scene_Id,Scene_html");
		
		for(int i = 1; i <= extra_character_count; i++)
			joiner.add(String.format(EXTRA_CHARACTER_FORMAT, i));
		
		for(int i = 1; i <= question_count; i++){
			joiner.add(String.format(Q_FORMAT, i));
			for(int j = 1; j <= option_count; j++)
				joiner.add(String.format(OPT_FORMAT, i, j));
		}
		return joiner.toString();
	}
	
	public void format(List<Season> seasons, List<String> metadata_selected, String out_path){
		PrintWriter writer = new PrintWriter(IOUtils.createBufferedPrintStream(out_path));
		writer.println(getCSVHeader());
		
		Map<Integer, Season> m_seasons = new HashMap<>(); String hit;
		for(Season season : seasons) m_seasons.put(season.getID(), season);
		
		int[] metadata; Season season; Episode episode;
		for(String m : metadata_selected){
			metadata = DataLoaderUtil.getSceneMetaData(m);
			
			season = m_seasons.get(metadata[0]); 		if(season == null) continue;
			episode = season.getEpisode(metadata[1]);	if(episode == null) continue;

			hit = formatScene(metadata[0], episode, metadata[2]);
			if(hit != null)	writer.println(hit);
		}
		
		writer.close();
	}
	
	private String formatScene(int seasonId, Episode episode, int sceneId){
		Set<String> extra_speakers; String html; StringJoiner joiner; int i, j, start, end;
		List<String> list, options, questions = new ArrayList<>(); List<List<String>> l_options = new ArrayList<>();
		
		if(episode.getScene(sceneId) != null){
			// Get HTML
			joiner = new StringJoiner(StatementTreetoMTurkCSVFormatter.CSV_DELIM);
			html = constructHtml(episode, sceneId, questions, l_options);
			
			if(!questions.isEmpty()){
				// Get extra characters
				start = Math.max(episode.getFirstSceneId(), sceneId - scene_offset);
				end = Math.min(episode.getLastSceneId(), sceneId + scene_offset);
				extra_speakers = getSpeakerSet(episode, start, end); 
				extra_speakers.removeAll(ignore_characters);
				
				// Construct a hit entry
				joiner.add(Integer.toString(seasonId)); 
				joiner.add(Integer.toString(episode.getID())); 
				joiner.add(Integer.toString(sceneId));
				joiner.add(String.format("\"%s\"", html));
				
				list = new ArrayList<>(extra_speakers);
				for(i = 0; i < list.size() && i < extra_character_count; i++) 
					joiner.add(list.get(i));
				for(; i < extra_character_count; i++) 	joiner.add(EMPTY_CHARACTER);
				
				for(i = 0; i < questions.size(); i++){
					joiner.add(String.format(ENTRY_QUESTION_FROMAT, i+1, questions.get(i)));
					options = l_options.get(i);
					for(j = 0; j < option_count; j++) joiner.add(options.get(j));
				}
				for(i = questions.size(); i < question_count; i++){
					joiner.add(EMPTY_QUESTION);
					for(j = 0; j < option_count; j++) joiner.add(EMPTY_OPTION);
				}
				
				return joiner.toString().replaceAll(REGEX_NONUTF, StringConst.EMPTY);
			}
		}
		return null;
	}
	
	private String constructHtml(Episode episode, int sceneId, List<String> questions, List<List<String>> options){
		int i, start = Math.max(episode.getFirstSceneId(), sceneId - scene_offset),
				end = Math.min(episode.getLastSceneId(), sceneId + scene_offset);
		
		// Construct html of the range
		StringJoiner joiner = new StringJoiner(HR_DELIM);
		for(i = start; i < sceneId; i++) joiner.add(constructHtmlAux(episode.getScene(i), false, questions, options));
		joiner.add(constructHtmlAux(episode.getScene(sceneId), true, questions, options));
		for(i = sceneId+1; i <= end; i++) joiner.add(constructHtmlAux(episode.getScene(i), false, questions, options));
		
		return joiner.toString();
	}
	
	private String constructHtmlAux(Scene scene, boolean markQuestions, List<String> questions, List<List<String>> options){
		if(scene == null) return null;
		StringBuilder html = new StringBuilder();
		
		int i, len; char bilou; StatementNode node; boolean validQuestion;
		String word, speaker, label; StringJoiner line, question = null;
		
		for(Utterance utterance : scene){
			for(StatementNode[] tree : utterance.getStatementTrees()){
				if( (len = tree.length) > 1){
					line = new StringJoiner(StringConst.SPACE);
					
					for(i = 1; i < len; i++){
						if(markQuestions){
							node = tree[i]; word = node.getWordForm().replace('’', '\'');
							label = node.getReferantLabel(); bilou = label.charAt(0);
							validQuestion = !label.isEmpty() && label.indexOf(MTurkAnnoataionToTreeMerger.DELIM) >= 0;
							
							switch (bilou) {
							case 'U': 
								if(validQuestion){
									questions.add(word);
									options.add(Arrays.stream(Splitter.splitPipes(label.substring(2))).collect(Collectors.toList()));
									word = String.format("<mark>%s</mark><sup>%d</sup>", word, questions.size());
								}
								break;
								
							case 'B':
								if(validQuestion){
									question = new StringJoiner(StringConst.SPACE);
									question.add(word); word = String.format("<mark>%s", word);
								}
								break;
								
							case 'I':
								if(validQuestion && question != null) 	
									question.add(word);
								break;
								
							case 'L':
								if(validQuestion && question != null) {
									question.add(word); questions.add(question.toString()); question = null;
									options.add(Arrays.stream(Splitter.splitPipes(label.substring(2))).collect(Collectors.toList()));
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
	
	private Set<String> getSpeakerSet(Episode episode, int fromScene, int toScene){
		Set<String> speakers = new HashSet<>();
		for(int i = fromScene; i <= toScene; i++)
			speakers.addAll(getSpeakerSet(episode.getScene(i)));
		return speakers;
	}
	
	private Set<String> getSpeakerSet(Scene scene){
		Set<String> speakers, set = new HashSet<>();
		speakers = scene.getUtterances().stream().map(u->u.getSpeaker()).collect(Collectors.toSet());
		
		speakers.removeAll(ignore_spkeakers); speakers.remove(StringConst.EMPTY);
		
		String speaker_low;
		for(String speaker : speakers){
			speaker_low = StringUtils.toLowerCase(speaker);
			if(!speaker_low.contains(" and ") && !speaker_low.contains(StringConst.COMMA) &&  
				!speaker_low.contains(StringConst.FW_SLASH) && !speaker_low.contains(StringConst.AMPERSAND))
				set.add(speaker.replaceAll(REGEX_PARATHESE, StringConst.EMPTY).trim());
		}
		return set;
	}
}
