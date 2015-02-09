package com.rollerhockeyfrance.manager.core.resultat;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.KeyVal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Méthodes utilitaires pour parser les pages HTML du 
 * module de résultats de la FFRS.
 * 
 * @author fabiensaulnier
 */
public class ParseurUtils {
	
	// TODO : mettre dans le fichier de config yaml
	public static int TIME_OUT = 10000; // 10s

	// TODO : mettre dans le fichier de config yaml
	public static String AUTHENTICITY_TOKEN = "e4752a824a152118492cbc21db2518a15599e3b7";
	
	public static Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).timeout(TIME_OUT).get();
	}
	
	public static Document postDocument(String url, Collection<KeyVal> data) throws IOException {
		KeyVal kv = org.jsoup.helper.HttpConnection.KeyVal.create("authenticity_token", AUTHENTICITY_TOKEN);
		data.add(kv);
		return Jsoup.connect(url).timeout(TIME_OUT).data(data).post();	
	}

	public static int extractId(String lien) {
		Pattern p = Pattern.compile(".*/(\\d+)?.*");
		Matcher m = p.matcher(lien);
		return m.find() ? Integer.parseInt(m.group(1)) : -1;
	}

	public static String getString(Elements td, int i) {
		return td.get(i).text();
	}

	public static int getInt(Elements td, int i) {
		String s = getString(td, i);
		String n = isNullOrEmpty(s) ? "-1" : s;
		return Integer.parseInt(n);
	}

}