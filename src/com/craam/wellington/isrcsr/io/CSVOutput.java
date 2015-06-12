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
import com.craam.wellington.isrcsr.ISRCSR.Frequency;
import com.craam.wellington.isrcsr.Source;
import com.craam.wellington.statics.Constants;

public class CSVOutput implements IOutput {
	
	String filename, filenameCompact, filenameExtended, filenameISR, filenameHigh, filenameLow, filenameCSR, filenameTotal;
	ISRCSR isrcsr;
	InputData params;
	List<Frequency> frequencies;
	
	//Data Flags
	Boolean exportFrequency=true;
	
	public CSVOutput(String filename){
		this.filenameCompact = "COMPACT-" + filename;
		this.filenameExtended = "EXTENDED-" + filename;
		this.filenameISR = "ISR-" + filename;
		this.filenameHigh = "HIGH-" + filename;
		this.filenameLow = "LOW-" + filename;
		this.filenameCSR = "CSR-" + filename;
		this.filenameTotal = "TOTAL-" + filename;
	}
	
	public Boolean generateOutput(IAlgorithm isrcsr){
		this.isrcsr = (ISRCSR) isrcsr;
		this.frequencies = this.isrcsr.getFrequencies();
		this.params = this.isrcsr.getInputParams();
		generateSourceOutput(params.getCompactSource(), frequencies, this.filenameCompact);
		generateSourceOutput(params.getExtendedSource(), frequencies, this.filenameExtended);
		generateISROutput(frequencies, this.filenameISR);
		generateCSROutput(frequencies, this.filenameCSR);
		generateHighOutput(frequencies, this.filenameHigh);
		generateLowOutput(frequencies, this.filenameLow);
		generateTotalOutput(frequencies, this.filenameTotal);
		return true;
	}
	
	public CSVOutput setExportFrequency(Boolean exportFrequency) {
		this.exportFrequency = exportFrequency;
		return this;
	}
	
	private void generateSourceOutput(Source s, List<Frequency> frequencies, String filename){
		PrintWriter printer;
		Iterator<Frequency> i = frequencies.iterator();
		
		try {
			printer = new PrintWriter(filename, "UTF-8");
			String header = "";
			if(exportFrequency) header += "Frequency;";
			header += "Flux";
			printer.println(header);

			i = frequencies.iterator();

			while(i.hasNext()){
				String row = "";
				Frequency freq = (Frequency) i.next();

				if(exportFrequency) row += String.format("%.16e;\t", s.getFrequency(freq.getFrequencyIndex()).getFrequencyValue());
				                    row += String.format("%.16e", s.getFrequency(freq.getFrequencyIndex()).getStokesAndPolarization().getPhiISRTotal() * Constants.ERGCMSHZ_TO_SFU+1e-23);
				printer.println(row);
			}
			printer.close();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void generateISROutput(List<Frequency> frequencies, String filename){
		PrintWriter printer;
		Iterator<Frequency> i = frequencies.iterator();
		
		try {
			printer = new PrintWriter(filename, "UTF-8");
			String header = "";
			if(exportFrequency) header += "Frequency;";
			header += "Flux";
			printer.println(header);

			i = frequencies.iterator();

			while(i.hasNext()){
				String row = "";
				Frequency freq = (Frequency) i.next();

				if(exportFrequency) row += String.format("%.16e;\t", freq.getFrequencyValue());
				                    row += String.format("%.16e",  freq.getPhiISR() * Constants.ERGCMSHZ_TO_SFU+1e-23);
				printer.println(row);
			}
			printer.close();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	private void generateCSROutput(List<Frequency> frequencies, String filename){
		PrintWriter printer;
		Iterator<Frequency> i = frequencies.iterator();
		
		try {
			printer = new PrintWriter(filename, "UTF-8");
			String header = "";
			if(exportFrequency) header += "Frequency;";
			header += "Flux";
			printer.println(header);

			i = frequencies.iterator();

			while(i.hasNext()){
				String row = "";
				Frequency freq = (Frequency) i.next();

				if(exportFrequency) row += String.format("%.16e;\t", freq.getFrequencyValue());
				                    row += String.format("%.16e",  freq.getPhiCSR() * Constants.ERGCMSHZ_TO_SFU+1e-23);
				printer.println(row);
			}
			printer.close();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void generateHighOutput(List<Frequency> frequencies, String filename){
		PrintWriter printer;
		Iterator<Frequency> i = frequencies.iterator();
		
		try {
			printer = new PrintWriter(filename, "UTF-8");
			String header = "";
			if(exportFrequency) header += "Frequency;";
			header += "Flux";
			printer.println(header);

			i = frequencies.iterator();

			while(i.hasNext()){
				String row = "";
				Frequency freq = (Frequency) i.next();

				if(exportFrequency) row += String.format("%.16e;\t", freq.getFrequencyValue());
				                    row += String.format("%.16e",  freq.getPhiHigh() * Constants.ERGCMSHZ_TO_SFU+1e-23);
				printer.println(row);
			}
			printer.close();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void generateLowOutput(List<Frequency> frequencies, String filename){
		PrintWriter printer;
		Iterator<Frequency> i = frequencies.iterator();
		
		try {
			printer = new PrintWriter(filename, "UTF-8");
			String header = "";
			if(exportFrequency) header += "Frequency;";
			header += "Flux";
			printer.println(header);

			i = frequencies.iterator();

			while(i.hasNext()){
				String row = "";
				Frequency freq = (Frequency) i.next();

				if(exportFrequency) row += String.format("%.16e;\t", freq.getFrequencyValue());
				                    row += String.format("%.16e",  freq.getPhiLow() * Constants.ERGCMSHZ_TO_SFU+1e-23);
				printer.println(row);
			}
			printer.close();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void generateTotalOutput(List<Frequency> frequencies, String filename){
		PrintWriter printer;
		Iterator<Frequency> i = frequencies.iterator();
		
		try {
			printer = new PrintWriter(filename, "UTF-8");
			String header = "";
			if(exportFrequency) header += "Frequency;";
			header += "Flux";
			printer.println(header);

			i = frequencies.iterator();

			while(i.hasNext()){
				String row = "";
				Frequency freq = (Frequency) i.next();

				if(exportFrequency) row += String.format("%.16e;\t", freq.getFrequencyValue());
				                    row += String.format("%.16e",  freq.getPhiTotal() * Constants.ERGCMSHZ_TO_SFU+1e-23);
				printer.println(row);
			}
			printer.close();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}


	
}
