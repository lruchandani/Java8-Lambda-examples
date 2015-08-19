package com.lalit.partialfunction.examples;

import java.util.function.Function;


/**
 * Partial Function example
 *
 */
public class Tagger 
{
	/**
	 *  
	 * @param tag
	 * @return  function that takes text as parameter and adds  the specified tag to it.
	 * 			This is also example of closure as the specified "tag" remains cached and is used during 
	 * 			the execution of applyTags
	 */
	public static Function<String,String> getTagger(final String tag){
		return (text) -> "<" + tag + ">" + text + "</"+ tag + ">";
	}
	/**
	 * 
	 * @param text - text to be tagged
	 * @param tagger - Tag function  that is applied on the text 
	 * @return - tagged text
	 * 
	 */
	public static String applyTag(String text,Function<String,String>  tagger){
		return tagger.apply(text);
	}
	
	
  
}
