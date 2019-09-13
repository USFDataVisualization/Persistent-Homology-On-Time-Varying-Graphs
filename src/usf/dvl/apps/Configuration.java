//    Persistent Homology on Time Varying Graphs
//    Copyright (C) 2019  Paul Rosen
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.

package usf.dvl.apps;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import usf.dvl.common.SystemX;

public class Configuration {

	Properties props = new Properties();
	String configurationFile;
	boolean loadedOk = false;
	
	Configuration() {
		this("config.properties");
	}
	
	Configuration( String _configurationFile ) {
		
		configurationFile = _configurationFile;
		
		File configFile   = new File(configurationFile);
		System.out.println( configFile.getAbsolutePath() );
	    FileReader reader;
		try {
			reader = new FileReader(configFile);
		    props.load(reader);
		    reader.close();
		    loadedOk = true;
		} catch (IOException e) {
			System.out.println("Config file not found, one will need to be created.");
			loadedOk = false;
			//e.printStackTrace();
		}
	}
	
	void saveConfiguration() throws IOException {
		this.saveConfiguration(this.configurationFile);
	}
	
	void saveConfiguration( String outputConfigurationFile) throws IOException {
		File configFile = new File( outputConfigurationFile );
	    FileWriter writer = new FileWriter(configFile);

	    props.store(writer, "path settings");
		
	    writer.close();
	}

	public boolean isRisperValid() {
		String ripserPath = props.getProperty("ripser");
		if( ripserPath == null ) return false;
		System.out.println("Ripser Path: " + ripserPath);
		return SystemX.fileExists(ripserPath);
	}

	public boolean isHeraBottleneckValid() {
		String heraBottleneckPath = props.getProperty("bottleneck");
		if( heraBottleneckPath == null ) return false;
		System.out.println("Hera/Bottleneck Path: " + heraBottleneckPath);
		return SystemX.fileExists(heraBottleneckPath);
	}

	public boolean isHeraWassersteinValid() {
		String heraWassersteinPath = props.getProperty("wasserstein");
		if( heraWassersteinPath == null ) return false;
		System.out.println("Hera/Wasserstein Path: " + heraWassersteinPath);
		return SystemX.fileExists(heraWassersteinPath);
	}
	

	public String getRisper() {
		return props.getProperty("ripser");
	}

	public String getHeraBottleneck() {
		return props.getProperty("bottleneck");
	}

	public String getHeraWasserstein() {
		return props.getProperty("wasserstein");
	}

	public boolean isValid() {
		return loadedOk;
	}

	public boolean setHeraWasserstein(String path) {
		props.setProperty("wasserstein", path);
		return isHeraWassersteinValid();
	}

	public boolean setHeraBottleneck(String path) {
		props.setProperty("bottleneck", path);
		return isHeraBottleneckValid();
	}

	public boolean setRipser(String path) {
		props.setProperty("ripser", path);
		return isRisperValid();
	}

	public void setPreviousDirectory( String path ) {
		props.setProperty("prevDirectory", path);
	}
	
	public String getPreviousDirectory() {
		return props.getProperty("prevDirectory");
	}
	
	
}
