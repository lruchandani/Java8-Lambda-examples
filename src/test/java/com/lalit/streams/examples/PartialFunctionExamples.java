package com.lalit.streams.examples;

import static com.lalit.partialfunction.examples.Tagger.applyTag;
import static com.lalit.partialfunction.examples.Tagger.getTagger;
import static org.junit.Assert.assertEquals;

import java.util.function.Function;

import org.junit.Test;

public class PartialFunctionExamples {

	@Test
	public void makeBold() {
		String text = "Hello World";
		assertEquals("<B>"+text+"</B>", applyTag(text, getTagger("B")));
	
	}
	
	@Test
	public void makeBoldAndItalic() {
		String text = "Hello World";
		Function<String,String> boldAndItalicFun = getTagger("B").andThen(getTagger("I"));
		assertEquals("<I><B>"+text+"</B></I>", applyTag(text,boldAndItalicFun));
	
	}
}
