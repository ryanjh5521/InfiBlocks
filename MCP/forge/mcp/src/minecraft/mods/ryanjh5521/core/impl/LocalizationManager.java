package mods.ryanjh5521.core.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.common.base.Charsets;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.util.StringTranslate;
import mods.ryanjh5521.core.api.language.ILocalizationManager;

public class LocalizationManager implements ILocalizationManager {
	
	private static final boolean DEBUG = Boolean.getBoolean("mods.ryanjh5521.core.debug.localization");

	@SuppressWarnings("unchecked")
	private Collection<String> getLanguages() {
		return (Set<String>)StringTranslate.getInstance().getLanguageList().keySet();
	}
	
	@Override
	public void loadLanguageFiles(Object mod, String modpath) {
		Map<String, Properties> byLanguage = new HashMap<String, Properties>();
		Map<String, Properties> byLanguageGroup = new HashMap<String, Properties>();
		
		for(String langID : getLanguages()) {
			InputStream stream = mod.getClass().getResourceAsStream("/mods/"+modpath+"/lang/"+langID+".lang");
			if(stream != null) {
				Properties props = loadLanguageData(modpath, stream);
				
				byLanguage.put(langID, props);
				
				if(langID.indexOf('_') >= 0) {
					String group = langID.substring(0, langID.indexOf('_')); 
					if(!byLanguageGroup.containsKey(group))
						byLanguageGroup.put(group, props);
				}
			}
		}
		
		for(String langID : getLanguages()) {
			Properties data = byLanguage.get(langID);
			
			if(data == null)
				if(langID.indexOf('_') >= 0)
					data = byLanguageGroup.get(langID.substring(0, langID.indexOf('_'))); 
			
			if(data != null) {
				//if(DEBUG)
					//ryanjh5521Core.LOGGER.log(Level.INFO, "Loading localization for "+modpath+" "+langID+" from "+url);
				LanguageRegistry.instance().addStringLocalization(data, langID);
			}
		}
	}
	
	private int findFirstOf(String s, char[] chars) {
		for(int k = 0; k < s.length(); k++)
			for(char c : chars)
				if(s.charAt(k) == c)
					return k;
		return -1;
	}

	private Properties loadLanguageData(String modpath, InputStream stream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream,Charsets.UTF_8));
		String line;
		
		Properties rv = new Properties();
		
		char[] delims = new char[] {' ', '\t'};
		
		try {
			while((line = reader.readLine()) != null) {
				int i = findFirstOf(line, delims);
				if(i < 0 || line.startsWith("#"))
					continue;
				
				String name = line.substring(0, i).trim();
				String val = line.substring(i).trim();
				if(name.equals("") || val.equals(""))
					continue;
				
				if(name.startsWith("item:")) {
					name = "item."+modpath+":"+name.substring(5)+".name";
				} else if(name.startsWith("block:")) {
					name = "tile."+modpath+":"+name.substring(6)+".name";
				} else if(name.startsWith("raw:")) {
					name = name.substring(4);
				}
				
				rv.setProperty(name, val);
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {reader.close();} catch(IOException e) {}
		}
		
		return rv;
	}

}
