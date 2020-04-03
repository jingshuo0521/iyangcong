package com.iyangcong.reader.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyangcong.reader.app.MyApplication;
import com.iyangcong.reader.bean.Word;
import com.iyangcong.reader.utils.L;

public class WordUtils {

	/*
	 * author shaoyousheng 2015 10.11
	 */

	public static Word getWordTranslate(String str) {

		List<Word> words = new ArrayList<Word>();
		int strLen = str.length();
		String first = null;
		String second = null;
		String xmlName = null;
		if (strLen == 1) {
			first = str.substring(0, 1);
			if (first.equalsIgnoreCase("A")) {
				xmlName = "AA";
			} else if (first.equalsIgnoreCase("B")) {
				xmlName = "BA";
			} else if (first.equalsIgnoreCase("C")) {
				xmlName = "CA";
			} else if (first.equalsIgnoreCase("D")) {
				xmlName = "DA";
			} else if (first.equalsIgnoreCase("E")) {
				xmlName = "EA";
			} else if (first.equalsIgnoreCase("F")) {
				xmlName = "FA";
			} else if (first.equalsIgnoreCase("G")) {
				xmlName = "GA";
			} else if (first.equalsIgnoreCase("H")) {
				xmlName = "HA";
			} else if (first.equalsIgnoreCase("I")) {
				xmlName = "IA";
			} else if (first.equalsIgnoreCase("J")) {
				xmlName = "JA";
			} else if (first.equalsIgnoreCase("K")) {
				xmlName = "KA";
			} else if (first.equalsIgnoreCase("L")) {
				xmlName = "LA";
			} else if (first.equalsIgnoreCase("M")) {
				xmlName = "MA";
			} else if (first.equalsIgnoreCase("N")) {
				xmlName = "NA";
			} else if (first.equalsIgnoreCase("O")) {
				xmlName = "OA";
			} else if (first.equalsIgnoreCase("P")) {
				xmlName = "PA";
			} else if (first.equalsIgnoreCase("Q")) {
				xmlName = "QA";
			} else if (first.equalsIgnoreCase("R")) {
				xmlName = "RA";
			} else if (first.equalsIgnoreCase("S")) {
				xmlName = "SA";
			} else if (first.equalsIgnoreCase("T")) {
				xmlName = "TA";
			} else if (first.equalsIgnoreCase("U")) {
				xmlName = "UA";
			} else if (first.equalsIgnoreCase("V")) {
				xmlName = "VA";
			} else if (first.equalsIgnoreCase("W")) {
				xmlName = "WA";
			} else if (first.equalsIgnoreCase("X")) {
				xmlName = "XA";
			} else if (first.equalsIgnoreCase("Y")) {
				xmlName = "YA";
			} else if (first.equalsIgnoreCase("Z")) {
				xmlName = "ZA";
			}

		} else if (strLen >= 2) {
			first = str.substring(0, 1);
			second = str.substring(1, 2);
			if (first.equalsIgnoreCase("A")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'J') {
					xmlName = "AA";
				} else if (secondChar >= 'J' && secondChar < 'P') {
					xmlName = "AJ";
				} else if (secondChar >= 'P') {
					xmlName = "AP";
				}
			} else if (first.equalsIgnoreCase("B")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'B') {
					xmlName = "BA";
				} else if (secondChar >= 'B' && secondChar < 'I') {
					xmlName = "BB";
				} else if (secondChar >= 'I' && secondChar < 'O') {
					xmlName = "BI";
				} else if (secondChar >= 'O' && secondChar < 'R') {
					xmlName = "BO";
				} else if (secondChar >= 'R') {
					xmlName = "BR";
				}
			} else if (first.equalsIgnoreCase("C")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'B') {
					xmlName = "CA";
				} else if (secondChar >= 'B' && secondChar < 'I') {
					xmlName = "CB";
				} else if (secondChar >= 'I' && secondChar < 'O') {
					xmlName = "CI";
				} else if (secondChar >= 'O' && secondChar < 'P') {
					xmlName = "CO";
				} else if (secondChar >= 'P') {
					xmlName = "CP";
				}
			} else if (first.equalsIgnoreCase("D")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'E') {
					xmlName = "DA";
				} else if (secondChar >= 'E' && secondChar < 'I') {
					xmlName = "DE";
				} else if (secondChar >= 'I' && secondChar < 'J') {
					xmlName = "DI";
				} else if (secondChar >= 'J' && secondChar < 'P') {
					xmlName = "DJ";
				} else if (secondChar >= 'P') {
					xmlName = "DP";
				}
			} else if (first.equalsIgnoreCase("E")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'N') {
					xmlName = "EA";
				} else if (secondChar >= 'N') {
					xmlName = "EN";
				}
			} else if (first.equalsIgnoreCase("F")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'L') {
					xmlName = "FA";
				} else if (secondChar >= 'L') {
					xmlName = "FL";
				}
			} else if (first.equalsIgnoreCase("G")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'M') {
					xmlName = "GA";
				} else if (secondChar >= 'M') {
					xmlName = "GM";
				}
			} else if (first.equalsIgnoreCase("H")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'F') {
					xmlName = "HA";
				} else if (secondChar >= 'F') {
					xmlName = "HF";
				}
			} else if (first.equalsIgnoreCase("I")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'N') {
					xmlName = "IA";
				} else if (secondChar >= 'N') {
					xmlName = "IN";
				}
			} else if (first.equalsIgnoreCase("J")) {
				xmlName = "JA";
			} else if (first.equalsIgnoreCase("K")) {
				xmlName = "KA";
			} else if (first.equalsIgnoreCase("L")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'I') {
					xmlName = "LA";
				} else if (secondChar >= 'I') {
					xmlName = "LI";
				}
			} else if (first.equalsIgnoreCase("M")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'B') {
					xmlName = "MA";
				} else if (secondChar >= 'B' && secondChar < 'K') {
					xmlName = "MB";
				} else if (secondChar >= 'K') {
					xmlName = "MK";
				}
			} else if (first.equalsIgnoreCase("N")) {
				xmlName = "NA";
			} else if (first.equalsIgnoreCase("O")) {
				xmlName = "OA";
			} else if (first.equalsIgnoreCase("P")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'E') {
					xmlName = "PA";
				} else if (secondChar >= 'E' && secondChar < 'G') {
					xmlName = "PE";
				} else if (secondChar >= 'G' && secondChar < 'P') {
					xmlName = "PG";
				} else if (secondChar >= 'P' && secondChar < 'R') {
					xmlName = "PP";
				} else if (secondChar >= 'R' && secondChar < 'Y') {
					xmlName = "PR";
				} else if (secondChar >= 'Y') {
					xmlName = "PY";
				}
			} else if (first.equalsIgnoreCase("Q")) {
				xmlName = "QA";
			} else if (first.equalsIgnoreCase("R")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'E') {
					xmlName = "RA";
				} else if (secondChar >= 'E' && secondChar < 'H') {
					xmlName = "RE";
				} else if (secondChar >= 'H' && secondChar < 'Y') {
					xmlName = "RH";
				} else if (secondChar >= 'Y') {
					xmlName = "RY";
				}
			} else if (first.equalsIgnoreCase("S")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'E') {
					xmlName = "SA";
				} else if (secondChar >= 'E' && secondChar < 'G') {
					xmlName = "SE";
				} else if (secondChar >= 'G' && secondChar < 'O') {
					xmlName = "SG";
				} else if (secondChar >= 'O' && secondChar < 'P') {
					xmlName = "SO";
				} else if (secondChar >= 'P' && secondChar < 'T') {
					xmlName = "SP";
				} else if (secondChar >= 'T' && secondChar < 'U') {
					xmlName = "ST";
				} else if (secondChar >= 'U' && secondChar < 'Y') {
					xmlName = "SU";
				} else if (secondChar >= 'Y') {
					xmlName = "SY";
				}
			} else if (first.equalsIgnoreCase("T")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'H') {
					xmlName = "TA";
				} else if (secondChar >= 'H' && secondChar < 'I') {
					xmlName = "TH";
				} else if (secondChar >= 'I' && secondChar < 'Z') {
					xmlName = "TI";
				} else if (secondChar >= 'Z') {
					xmlName = "TZ";
				}
			} else if (first.equalsIgnoreCase("U")) {
				xmlName = "UA";
			} else if (first.equalsIgnoreCase("V")) {
				xmlName = "VA";
			} else if (first.equalsIgnoreCase("W")) {
				second = second.toUpperCase();
				char secondChar = second.charAt(0);
				if (secondChar >= 'A' && secondChar < 'E') {
					xmlName = "WA";
				} else if (secondChar >= 'E' && secondChar < 'H') {
					xmlName = "WE";
				} else if (secondChar >= 'H' && secondChar < 'I') {
					xmlName = "WH";
				} else if (secondChar >= 'I' && secondChar < 'K') {
					xmlName = "WI";
				} else if (secondChar >= 'K' && secondChar < 'Y') {
					xmlName = "WK";
				} else if (secondChar >= 'Y') {
					xmlName = "WY";
				}
			} else if (first.equalsIgnoreCase("X")) {
				xmlName = "XA";
			} else if (first.equalsIgnoreCase("Y")) {
				xmlName = "YA";
			} else if (first.equalsIgnoreCase("Z")) {
				xmlName = "ZA";
			}
		}
		try {

			long begin = System.currentTimeMillis();
			// String filepath="/edu/english/study/library/" + xmlName + ".xml";
			String filepath = "/com/iyangcong/reader/library/" + xmlName
					+ ".xml";
			// String filepath="library/" + xmlName + ".xml";
			// String filepath="library/12D.xml";
			InputStream stream = MyApplication.getInstance().getClass()
					.getResourceAsStream(filepath);
			// InputStream
			// stream=AppContext.getInstance().getResources().getAssets().open(filepath);
			long load = System.currentTimeMillis();
			words = readXML(stream);
			long end = System.currentTimeMillis();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * 得到的单词是可能是多项，所以得合并
		 */
		boolean have = false;
		List<Word> wordlist = new ArrayList<Word>();
		for (Word w : words) {
			String value = w.getWord().get(0);
			if (value.equalsIgnoreCase(str)) {
				have = true;
				wordlist.add(w);
				// return w;
				// return w;
				// 后期修改
			}
		}
		int listsize = wordlist.size();
		if (listsize > 0) {
			Word word = parcelToEntiry(wordlist);
			return word;

			// if (listsize == 1) {
			// Word word=parcelToEntiry(wordlist);
			// return word;
			// } else {
			// Word word=parcelToEntiry(wordlist);
			// return word;
			// Word returnWord=new Word();
			// List<String> explains=new ArrayList<String>();
			// List<String> namelist=new ArrayList<String>();
			// for (int m = 0; m < listsize; m++) {
			// Word word=wordlist.get(m);
			// List<String> tempnamelist=word.getWord();
			// if(tempnamelist.size()>0){
			// namelist.add(word.getWord().get(0));
			// }
			// if(m==listsize-1){
			// returnWord.setWord(namelist);
			// }
			// if(word.getVariant()!=null){
			// explains.add(word.getVariant());
			// }
			// if(word.getPhonetic()!=null){
			// returnWord.setPhonetic(word.getPhonetic());
			// }
			// Map<String, List<String>> tempCategory =
			// wordlist.get(m).getCategory();
			// Iterator entries = tempCategory.entrySet().iterator();
			// while (entries.hasNext()) {
			// Map.Entry entry = (Map.Entry) entries.next();
			// String key = (String) entry.getKey();
			// List<String> value = (List<String>) entry.getValue();
			// String va="";
			// for(int n=0;n<value.size();n++){
			// va+=value.get(n);
			// }
			// String cxp=key+"  "+va;
			// explains.add(cxp);
			// }
			//
			// }
			// returnWord.setExplains(explains);
			// return returnWord;
			// }
		}

		if (!have) {
			InputStream stream;
			Map<String, String> verbMap = new HashMap<String, String>();
			try {
				long begin = System.currentTimeMillis();

				// String filepath="/edu/english/study/library/VERB.txt";
				// InputStream
				// stream1=AppContext.getInstance().getClass().getResourceAsStream(filepath);
				// String filepath="/edu/english/study/library/" + xmlName +
				// ".xml";
				String filepath = "/com/iyangcong/reader/library/VERB.txt";
				// String filepath="library/VERB.txt";
				stream = MyApplication.getInstance().getClass()
						.getResourceAsStream(filepath);
				// stream=AppContext.getInstance().getResources().getAssets().open(filepath);
				InputStreamReader reader = new InputStreamReader(stream);
				BufferedReader inTxt = new BufferedReader(reader);
				String strTmp;
				String arrTmp[];
				while ((strTmp = inTxt.readLine()) != null) {
					arrTmp = strTmp.split(" ");
					verbMap.put(arrTmp[0], arrTmp[1]);
				}
				long end = System.currentTimeMillis();
				long distance = (end - begin);
				if (verbMap.size() > 0) {
					String convertStr = verbMap.get(str);
					String mFirst = null;
					if (convertStr != null) {
						mFirst = convertStr.substring(0, 1);
					}
					if (mFirst != null) {
						if (mFirst.equals(first)) {
							for (Word w : words) {
								String value = w.getWord().get(0);
								if (value.equalsIgnoreCase(convertStr)) {
									have = true;
									wordlist.add(w);
									// return w;
								}
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!have) {
			String lastChar = str.substring(strLen - 1);
			String lastSecondChar = null;
			if (lastChar.equalsIgnoreCase("S")) {
				String tempStr = str.substring(0, strLen - 1);
				for (Word w : words) {
					String value = w.getWord().get(0);
					if (value.equalsIgnoreCase(tempStr)) {
						have = true;
						wordlist.add(w);
						// return w;
					}
				}
			}
			if (!have) {
				if (strLen >= 2) {
					lastSecondChar = str.substring(strLen - 2, strLen);
					if (lastSecondChar.equalsIgnoreCase("ED")) {
						String tempStr = str.substring(0, strLen - 2);
						for (Word w : words) {
							String value = w.getWord().get(0);
							if (value.equalsIgnoreCase(tempStr)) {
								have = true;
								wordlist.add(w);
								// return w;
							}
						}
					}
					if (!have) {
						if (lastSecondChar.equalsIgnoreCase("LY")) {
							String tempStr = str.substring(0, strLen - 2);
							for (Word w : words) {
								String value = w.getWord().get(0);
								if (value.equalsIgnoreCase(tempStr)) {
									have = true;
									wordlist.add(w);
									// return w;
								}
							}
						}
					}
				}
			}

			if (!have) {
				if (strLen >= 3) {
					lastSecondChar = str.substring(strLen - 3, strLen);
					if (lastSecondChar.equalsIgnoreCase("ING")) {
						String tempStr = str.substring(0, strLen - 3);
						for (Word w : words) {
							String value = w.getWord().get(0);
							if (value.equalsIgnoreCase(tempStr)) {
								have = true;
								wordlist.add(w);
								// return w;
							}
						}
						if (!have) {
							tempStr += "e";
							for (Word w : words) {
								String value = w.getWord().get(0);
								if (value.equalsIgnoreCase(tempStr)) {
									have = true;
									wordlist.add(w);
									// return w;
								}
							}
						}
					}
				}
			}
		}

		int listsize2 = wordlist.size();// 此处不能省略，此处是在前面没找到结果的情况下再去查一遍
		if (listsize2 > 0) {
			Word word = parcelToEntiry(wordlist);
			return word;
		}
		return null;
	}

	private static Word parcelToEntiry(List<Word> list) {

		Word rword = null;
		int listsize = list.size();
		if (listsize == 1) {
			Word tempW = list.get(0);
			List<String> explains = new ArrayList<String>();
			if (tempW.getWord() != null) {
				rword = new Word();
				rword.setWord(tempW.getWord());
			}
			if (tempW.getCategory() != null) {
				Map<String, List<String>> tempCategory = tempW.getCategory();
				Iterator entries = tempCategory.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry entry = (Map.Entry) entries.next();
					String key = (String) entry.getKey();
					List<String> value = (List<String>) entry.getValue();
					String va = "";
					int size = value.size();
					for (int n = 0; n < size; n++) {
						if (n != size - 1) {
							va += value.get(n) + "；";
						} else {
							va += value.get(n);
						}

					}
					String cxp = "";
					if ("_TEMP".equals(key)) {
						cxp = "  " + va;
					} else {
						cxp = key + ".  " + va;
					}
					explains.add(cxp);
				}
			}
			if (tempW.getExplains() != null) {
				if (rword != null) {
					rword.setExplains(tempW.getExplains());
				}
			}
			if (tempW.getPhonetic() != null) {
				if (rword != null) {
					rword.setPhonetic(tempW.getPhonetic());
				}

			}
			if (tempW.getVariant() != null) {
				if (rword != null) {
					explains.add(tempW.getVariant());
				}
			}
			if (rword != null) {
				rword.setExplains(explains);
			}
		} else {
			List<String> explains = new ArrayList<String>();
			List<String> namelist = new ArrayList<String>();
			for (int m = 0; m < listsize; m++) {
				Word word = list.get(m);
				List<String> tempnamelist = word.getWord();
				if (tempnamelist.size() > 0) {
					rword = new Word();
					namelist.add(word.getWord().get(0));
				}
				if (m == listsize - 1) {
					if (rword != null) {
						rword.setWord(namelist);
					}

				}
				if (word.getVariant() != null) {
					if (rword != null) {
						explains.add(word.getVariant());
					}

				}
				if (word.getPhonetic() != null) {
					if (rword != null) {
						rword.setPhonetic(word.getPhonetic());
					}

				}
				Map<String, List<String>> tempCategory = list.get(m)
						.getCategory();
				Iterator entries = tempCategory.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry entry = (Map.Entry) entries.next();
					String key = (String) entry.getKey();
					List<String> value = (List<String>) entry.getValue();
					String va = "";
					int size = value.size();
					for (int n = 0; n < size; n++) {
						if (n != size - 1) {
							va += value.get(n) + "；";
						} else {
							va += value.get(n);
						}

					}
					String cxp = "";
					if ("_TEMP".equals(key)) {
						cxp = "" + va;
					} else {
						cxp = key + ".  " + va;
					}
					explains.add(cxp);
				}

			}
			if (rword != null) {
				rword.setExplains(explains);
			}
		}
		return rword;
	}

	public static List<Word> readXML(InputStream inStream) {
		try {
			// 创建解析器
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();
			// 设置解析器的相关特性，true表示开启命名空间特性
			// saxParser.setProperty("http://xml.org/sax/features/namespaces",true);
			XMLParse handler = new XMLParse();
			saxParser.parse(inStream, handler);
			inStream.close();
			return handler.getWords();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public Map<String, String> getVerbs() {
		InputStream stream;

		Map<String, String> verbMap = new HashMap<String, String>();
		try {
			// String filepath="/edu/english/study/library/VERB.txt";
			// InputStream
			// stream2=AppContext.getInstance().getClass().getResourceAsStream(filepath);
			String filepath = "/com/iyangcong/reader/library/VERB.txt";
			// String filepath="library/VERB.txt";
			stream = MyApplication.getInstance().getClass()
					.getResourceAsStream(filepath);
			// stream=AppContext.getInstance().getResources().getAssets().open(filepath);
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader inTxt = new BufferedReader(reader);
			StringTokenizer strToken;
			String strTmp;

			String arrTmp[];
			while ((strTmp = inTxt.readLine()) != null) {
				strToken = new StringTokenizer(strTmp, "，");
				arrTmp = strTmp.split(" ");
				verbMap.put(arrTmp[0], arrTmp[1]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return verbMap;
	}

	public static List<String> getGradeTag(String str) {

		List<String> list=new ArrayList<String>();
		if (str == null || str.length() == 0)
			return null;
		int strLen = str.length();
		String first = null;

		String jsonName = null;
		if (strLen >= 1) {
			first = str.substring(0, 1);
			if (first.equalsIgnoreCase("A")) {
				jsonName = "a";
			} else if (first.equalsIgnoreCase("B")) {
				jsonName = "b";
			} else if (first.equalsIgnoreCase("C")) {
				jsonName = "c";
			} else if (first.equalsIgnoreCase("D")) {
				jsonName = "d";
			} else if (first.equalsIgnoreCase("E")) {
				jsonName = "e";
			} else if (first.equalsIgnoreCase("F")) {
				jsonName = "f";
			} else if (first.equalsIgnoreCase("G")) {
				jsonName = "g";
			} else if (first.equalsIgnoreCase("H")) {
				jsonName = "h";
			} else if (first.equalsIgnoreCase("I")) {
				jsonName = "i";
			} else if (first.equalsIgnoreCase("J")) {
				jsonName = "j";
			} else if (first.equalsIgnoreCase("K")) {
				jsonName = "k";
			} else if (first.equalsIgnoreCase("L")) {
				jsonName = "l";
			} else if (first.equalsIgnoreCase("M")) {
				jsonName = "m";
			} else if (first.equalsIgnoreCase("N")) {
				jsonName = "n";
			} else if (first.equalsIgnoreCase("O")) {
				jsonName = "o";
			} else if (first.equalsIgnoreCase("P")) {
				jsonName = "p";
			} else if (first.equalsIgnoreCase("Q")) {
				jsonName = "q";
			} else if (first.equalsIgnoreCase("R")) {
				jsonName = "r";
			} else if (first.equalsIgnoreCase("S")) {
				jsonName = "s";
			} else if (first.equalsIgnoreCase("T")) {
				jsonName = "t";
			} else if (first.equalsIgnoreCase("U")) {
				jsonName = "u";
			} else if (first.equalsIgnoreCase("V")) {
				jsonName = "v";
			} else if (first.equalsIgnoreCase("W")) {
				jsonName = "w";
			} else if (first.equalsIgnoreCase("X")) {
				jsonName = "x";
			} else if (first.equalsIgnoreCase("Y")) {
				jsonName = "y";
			} else if (first.equalsIgnoreCase("Z")) {
				jsonName = "z";
			}

		}
		try {

			String filepath = "/com/iyangcong/reader/library/" + jsonName
					+ ".json";
			InputStream stream = MyApplication.getInstance().getClass()
					.getResourceAsStream(filepath);
			BufferedReader tBufferedReader = new BufferedReader(
					new InputStreamReader(stream));
			StringBuffer tStringBuffer = new StringBuffer();
			String sTempOneLine = new String("");
			while ((sTempOneLine = tBufferedReader.readLine()) != null) {
				tStringBuffer.append(sTempOneLine);
			}
			String res=tStringBuffer.toString();
			JSONObject jobj=new JSONObject(res);
			try{
				String v=jobj.getString(str);
				String []arr=v.split(",");
				for(String s:arr){
					list.add(s);
				}
			}catch(JSONException e){
				if(list.size()==0)
				{
					list.add(""+4);//默认设置一个四级项
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}

}
