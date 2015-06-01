package com.craam.wellington.isrcsr.io;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.craam.wellington.interfaces.IAlgorithm;
import com.craam.wellington.interfaces.IOutput;
import com.craam.wellington.isrcsr.ISRCSR;
import com.craam.wellington.synclab.*;
import com.craam.wellington.synclab.SyncLab.Frequency;
import com.craam.wellington.statics.Constants;
import com.craam.wellington.synclab.SyncLab;

public class CSVOutput implements IOutput {
	
	String filename;
	ISRCSR isrcsr;
	InputData params;
	List<Frequency> frequencies;
	
	//Data Flags
	Boolean exportFrequency=true, exportIntensity=true, exportPotency=true;
	
	public CSVOutput(String filename){
		this.filename = filename;
	}
	
	public Boolean generateOutput(IAlgorithm isrcsr){
		this.isrcsr = (ISRCSR) isrcsr;
/*		this.frequencies = this.isrcsr.getFrequencies();
		this.params = this.isrcsr.getInputParams();
		
		PrintWriter printer;
		Iterator<Frequency> i;
		
		try {
			printer = new PrintWriter(this.filename, "UTF-8");
			String parameters = String.format("bmag=%.2e, ne=%.2e, tp=%.2e, ek=%.2e, kf=%d, ny=%d, xnisr=%.2e", params.getMagneticField(), params.getElectronNumber(), params.getMicrobunchingTimeWidth(), params.getElectronKinectyEnergy(), params.getHighestFrequency(), params.getNY(), params.getElectronFractionISR());
			printer.println(parameters);
			
			String header = "";
			if(exportFrequency) header += "Frequency;";
			if(exportIntensity) header += "Intensity;";
			if(exportPotency) header += "Potency";
			printer.println(header);

			i = frequencies.iterator();

			while(i.hasNext()){
				String row = "";
				Frequency freq = (Frequency) i.next();

				if(exportFrequency) row += String.format("%.16e;\t", freq.getFrequency());
				if(exportIntensity) row += String.format("%.16e;\t", freq.getD2WdvdO());
				if(exportPotency) row += String.format("%.16e", freq.getdPdv());
				printer.println(row);
			}
			printer.close();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
*/		
		return true;
	}
	
	public CSVOutput setExportFrequency(Boolean exportFrequency) {
		this.exportFrequency = exportFrequency;
		return this;
	}

	public CSVOutput setExportIntensity(Boolean exportIntensity) {
		this.exportIntensity = exportIntensity;
		return this;
	}

	public CSVOutput setExportPotency(Boolean exportPotency) {
		this.exportPotency = exportPotency;
		return this;
	}

}
