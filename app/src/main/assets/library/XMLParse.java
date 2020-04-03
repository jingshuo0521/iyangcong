package com.iyangcong.reader.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.iyangcong.reader.bean.Word;

public class XMLParse extends DefaultHandler {

	private List<Word> words = null;
	private Word currentWord;
	private List<String> word;
	private String tagName = null;// 当前解析的元素标签
	private Map<String,List<String>> category;
	private String tempCat;
	private List<String> descriptions;
	//private List<String> explains;

	public List<Word> getWords() {
		return words;
	}

	// 接收文档开始的通知。当遇到文档的开头的时候，调用这个方法，可以在其中做一些预处理的工作。
	@Override
	public void startDocument() throws SAXException {
		words = new ArrayList<Word>();
	}

	// 接收元素开始的通知。当读到一个开始标签的时候，会触发这个方法。其中namespaceURI表示元素的命名空间；
	// localName表示元素的本地名称（不带前缀）；qName表示元素的限定名（带前缀）；atts 表示元素的属性集合
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (localName.equals("entry")) {
			currentWord = new Word();
			word = new ArrayList<String>();
			category=new HashMap<String,List<String>>();
			descriptions=new ArrayList<String>();
			//explains=new ArrayList<String>();
		}else if(localName.equals("category")){
			tempCat=null;
		}
		this.tagName = localName;
	}

	// 接收字符数据的通知。该方法用来处理在XML文件中读到的内容，第一个参数用于存放文件的内容，
	// 后面两个参数是读到的字符串在这个数组中的起始位置和长度，使用new String(ch,start,length)就可以获取内容。
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		if (tagName != null) {
			String data = new String(ch, start, length);
			if (tagName.equals("word")) {
				word.add(data);
			}
			if (tagName.equals("phonetic")) {
				String mData=data;
				if(mData.contains("/")){
					mData=mData.replace("/", "");
				}
				currentWord.setPhonetic(mData);
				//currentWord.setPhonetic(data);
			}
			if(tagName.equals("variant")){
				currentWord.setVariant(data);
			}
			if(tagName.equals("cat")){
				tempCat=data;
			}
			if(tagName.equals("description")){
				descriptions.add(data);
			}
		}
	}

	// 接收文档的结尾的通知。在遇到结束标签的时候，调用这个方法。其中，uri表示元素的命名空间；
	// localName表示元素的本地名称（不带前缀）；name表示元素的限定名（带前缀）
	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if(localName.equals("head")){
			currentWord.setWord(word);
		}
		if(localName.equals("category")){
			if(descriptions!=null){
				if(tempCat!=null){
					category.put(tempCat, descriptions);
				}else{
					category.put("_TEMP", descriptions);
				}
			}
		}
		if (localName.equals("entry")) {
			currentWord.setCategory(category);
			words.add(currentWord);
			currentWord = null;
			word=null;
		}
		this.tagName = null;
	}
}
