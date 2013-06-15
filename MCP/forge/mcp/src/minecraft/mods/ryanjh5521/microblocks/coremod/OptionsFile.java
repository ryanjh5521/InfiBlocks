package mods.ryanjh5521.microblocks.coremod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionsFile {
	public static final boolean DEBUG = false;
	
	public abstract static class Option {
		private final String name;
		public String getName() {
			return name;
		}
		
		public Option(String name) {
			this.name = name;
		}
		
		public abstract void setTo(String s) throws Exception;
		public abstract void finishReading() throws Exception;
		public abstract Collection<String> getValues();
	}
	
	public static class StringListOption extends Option{
		private List<String> values = new ArrayList<String>();
		
		private Collection<String> defaults;
		
		public StringListOption(String name) {
			this(name, Collections.<String>emptyList());
		}
		
		public StringListOption(String name, Collection<String> defaults) {
			super(name);
			this.defaults = defaults;
		}
		
		@Override
		public void setTo(String s) throws Exception {
			values.add(s);
		}
		
		@Override
		public void finishReading() throws Exception {
			if(values.size() == 0)
				values.addAll(defaults);
		}
		
		@Override
		public Collection<String> getValues() {
			return values.size() == 0 ? defaults : values;
		}

		public Collection<? extends String> get() {
			return Collections.unmodifiableList(values);
		}

		public void addValues(List<String> L) {
			values.addAll(L);
		}
	}
	
	public static class BooleanOption extends Option {
		
		private final boolean hasDefault, _default;
		private boolean hasValue;
		private boolean value;
		
		public BooleanOption(String name, boolean _default) {
			super(name);
			this.hasDefault = true;
			this._default = _default;
		}
		
		public BooleanOption(String name) {
			super(name);
			this.hasDefault = false;
			this._default = false;
		}
		
		@Override
		public void finishReading() throws Exception {
			if(!hasDefault && !hasValue)
				throw new Exception("value not specified");
			if(!hasValue)
				value = _default;
		}
		
		@Override
		public Collection<String> getValues() {
			return Arrays.asList(value ? "true" : "false");
		}
		
		@Override
		public void setTo(String s) throws Exception {
			
			if(hasValue)
				throw new Exception("value already specified");
			hasValue = true;
			
			if(s.equals("true"))
				value = true;
			else if(s.equals("false"))
				value = false;
			else
				throw new Exception("must be true or false");
		}

		public void set(boolean v) {
			if(DEBUG)
				System.out.println("[ryanjh5521's Microblocks DEBUG] BooleanOption "+getName()+": setting to "+v+", was "+value); 
			value = v;
		}

		public boolean get() {
			return value;
		}
	}
	
	public static class ItemListOption extends Option {
		public ItemListOption(String name) {
			super(name);
		}

		protected boolean requireMeta; 
		public static class ItemID {
			public int id;
			public int meta;
			public boolean hasMeta;
			
			public ItemID(int id) {
				this.id = id;
			}
			
			public ItemID(int id, int meta) {
				this.id = id;
				this.meta = meta;
				this.hasMeta = true;
			}
			
			public ItemID(String line, boolean requireMeta) throws Exception {
				if(line.contains("#"))
					line = line.substring(0, line.indexOf('#')).trim();
				
				int i = line.indexOf(':');
				if(i <= 0) {
					if(requireMeta)
						throw new Exception("damage value is required, format is <id>:<damage value> eg 5:2");
					try {
						this.id = Integer.parseInt(line);
					} catch(NumberFormatException e) {
						throw new Exception("invalid number. must be in <id> or <id>:<damage value> format, eg 5:2");
					}
				} else {
					try {
						this.id = Integer.parseInt(line.substring(0, i));
						this.meta = Integer.parseInt(line.substring(i+1));
						this.hasMeta = true;
					} catch(NumberFormatException e) {
						throw new Exception("invalid number. must be in <id> or <id>:<damage value> format, eg 5:2");
					}
				}
				
				if(id < 0 || id >= 32000)
					throw new Exception("item ID outside valid range");
				if(hasMeta && (meta < -32768 || meta > 32767))
					throw new Exception("damage value outside valid range");
			}
			
			@Override
			public String toString() {
				String s;
				if(!hasMeta)
					s = String.valueOf(id);
				else
					s = id + ":" + meta;
				
				if(BridgeClass1.isMinecraftLoaded)
					s = s + "    # " + BridgeClass2.getItemName(id, hasMeta ? meta : 0);
				
				return s;
			}
		}
		
		private List<ItemID> values = new ArrayList<ItemID>();
		
		@Override
		public void finishReading() throws Exception {
			
		}
		
		@Override
		public Collection<String> getValues() {
			List<String> rv = new ArrayList<String>(values.size());
			for(ItemID id : values)
				rv.add(id.toString());
			return rv;
		}
		
		@Override
		public void setTo(String s) throws Exception {
			values.add(new ItemID(s, requireMeta));
		}

		public List<ItemID> get() {
			return values;
		}
		
		public void set(List<ItemID> v) {
			v = new ArrayList<ItemID>(v); // in case v == values
			
			if(DEBUG)
				System.out.println("[ryanjh5521's Microblocks DEBUG] ItemListOption "+getName()+": setting to "+v+", was "+values);
			values.clear();
			values.addAll(v);
		}
	}
	
	public static class ItemAndMetaListOption extends ItemListOption {
		public ItemAndMetaListOption(String name) {
			super(name);
		}

		{
			requireMeta = true;
		}
	}
	
	private Map<String, Option> options = new HashMap<String, Option>();
	
	public void addOption(Option option) {
		String name = option.getName();
		
		if(options.containsKey(name))
			throw new IllegalArgumentException("option already added with name: "+name);
		
		options.put(name, option);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getOption(String name) {
		return (T)options.get(name);
	}
	
	public void read(File f) throws IOException {
		
		if(DEBUG)
			System.out.println("[ryanjh5521's Microblocks DEBUG] Reading config file from "+f);
		
		BufferedReader in = new BufferedReader(new FileReader(f));
		try {
			String line;
			int lineNo = 0;
			while((line = in.readLine()) != null) {
				lineNo++;
				
				line = line.trim();
				if(line.startsWith("#") || line.equals(""))
					continue;
				
				int i = line.indexOf(':');
				if(i < 0)
					throw new IOException("config error: malformed line, on line "+lineNo+" of "+f.getName());
				
				String key = line.substring(0, i).trim();
				String value = line.substring(i+1).trim();
				
				Option opt = options.get(key);
				if(opt == null)
					throw new IOException("config error: unknown option, on line "+lineNo+" of "+f.getName());
				
				try {
					opt.setTo(value);
					
				} catch(Exception e) {
					if(e.getClass() == Exception.class)
						throw new IOException("config error: "+e.getMessage()+", on line "+lineNo+" of "+f.getName());
					else
						throw new IOException("config error: internal exception, on line "+lineNo+" of "+f.getName(), e);
				}
			}
			
			for(Map.Entry<String, Option> e : options.entrySet()) {
				try {
					e.getValue().finishReading();
				} catch(Exception ex) {
					if(ex.getClass() == Exception.class)
						throw new IOException("config error: "+ex.getMessage()+", in option "+e.getKey()+" of "+f.getName());
					else
						throw new IOException("config error: internal exception, in option "+e.getKey()+" of "+f.getName(), ex);
				}
			}
			
		} finally {
			in.close();
		}
		
		if(DEBUG)
			System.out.println("[ryanjh5521's Microblocks DEBUG] Finished reading config file");
	}

	public void write(File f) throws IOException {
		
		if(DEBUG)
			System.out.println("[ryanjh5521's Microblocks DEBUG] Writing config file to "+f);
		
		Writer out = new FileWriter(f);
		
		String lineSep = System.getProperty("line.separator");
		
		try {
			for(Map.Entry<String, Option> e : options.entrySet())
				out.write("# "+e.getKey()+": "+e.getValue().getClass().getSimpleName()+lineSep);
			
			for(Map.Entry<String, Option> e : options.entrySet()) {
				Collection<String> values = e.getValue().getValues();
				if(values.size() > 0)
					out.write(lineSep);
				for(String val : values)
					out.write(e.getKey()+": "+val+lineSep);
			}
			
		} finally {
			out.close();
		}
		
		if(DEBUG)
			System.out.println("[ryanjh5521's Microblocks DEBUG] Finished writing config file");
	}
	
}
