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

public class DataTableOutput implements IOutput {
	
	String filename;
	ISRCSR isrcsr;
	InputData params;
	List<Frequency> frequencies;
	
	public DataTableOutput(String filename){
		this.filename = filename;
	}
	
	public Boolean generateOutput(IAlgorithm isrcsr){
		this.isrcsr = (ISRCSR) isrcsr;
		this.frequencies = this.isrcsr.getFrequencies();
		this.params = this.isrcsr.getInputParams();
		
		PrintWriter printer;
		Iterator<Frequency> i;
		
		try {
			printer = new PrintWriter(this.filename, "UTF-8");
			printer.println(Constants.DATA_TABLE_REPORT_LINE);
			printer.println(Constants.DATA_TABLE_REPORT_HEADER);
			printer.println(Constants.DATA_TABLE_REPORT_LINE);			
			printer.println(Constants.DATA_TABLE_REPORT_PARAMS+"\n");
			printer.println(Constants.DATA_TABLE_REPORT_PARAMS_GLOBALS);

			printer.print("ex=      " + String.format("%.16e", params.getEnergyEspectrum()) + "    ");
			printer.print("\t\tEmin=    " + String.format("%.16e", Math.pow(10, (0.05 * (params.getEnergyLower()-1)-2))) + " MeV");
			printer.print("\t\tEmax=    " + String.format("%.16e", Math.pow(10, (0.05 * params.getEnergyUpper()-2))) + " MeV");
			printer.println("\t\tkf=      " + String.format("%16d", params.getHighestFrequency()) + "    ");
			printer.print("etr=     " + String.format("%.16e", params.getEnergyTreshould()) + "    ");
			printer.print("\t\txnisrex= " + String.format("%.16e", params.getElectronFractionISRExtendedSource()) + "    ");
			printer.print("\t\txncsr=   " + String.format("%.16e", params.getElectronFractionCSR()) + "    ");
			printer.println("\t\tNISR=    " + String.format("%.16e", (params.getCompactSource().getTotalElectronISR() +  params.getExtendedSource().getTotalElectronISR())) + "    ");
			printer.print("NCSR=    " + String.format("%.16e", this.isrcsr.getCSRElectronNumber()) + "    ");
			printer.print("\t\tNTotal=  " + String.format("%.16e", params.getElectronNumber()) + "    ");
			printer.print("\t\tNLow=    " + String.format("%.16e", this.isrcsr.getLowEnergyElectron()) + "    ");
			printer.println("\t\tNHigh=   " + String.format("%.16e", this.isrcsr.getHighEnergyElectron()) + "    ");

			printer.println("\n" + Constants.DATA_TABLE_REPORT_PARAMS_CSOURCE);
			printer.print("size=    " + String.format("%.16e", params.getCompactSource().getSize()) + "    ");
			printer.print("\t\theight=  " + String.format("%.16e", params.getCompactSource().getHeight()) + "    ");
			printer.print("\t\tbmag=    " + String.format("%.16e", params.getCompactSource().getMagneticField()) + "G   ");
			printer.println("\t\tnp=      " + String.format("%.16e", params.getCompactSource().getPlasmaDensity()) + "    ");
			printer.print("NTotal=  " + String.format("%.16e", params.getCompactSource().getTotalElectronISR()) + "    ");
			printer.print("\t\tNLow=    " + String.format("%.16e", params.getCompactSource().getLowEnergyElectronISR()) + "    ");
			printer.println("\t\tNHigh=   " + String.format("%.16e", params.getCompactSource().getHighEnergyElectronISR()) + "    ");

			printer.println("\n" + Constants.DATA_TABLE_REPORT_PARAMS_ESOURCE);
			printer.print("size=    " + String.format("%.16e", params.getExtendedSource().getSize()) + "    ");
			printer.print("\t\theight=  " + String.format("%.16e", params.getExtendedSource().getHeight()) + "    ");
			printer.print("\t\tbmag=    " + String.format("%.16e", params.getExtendedSource().getMagneticField()) + "G   ");
			printer.println("\t\tnp=      " + String.format("%.16e", params.getExtendedSource().getPlasmaDensity()) + "    ");
			printer.print("NTotal=  " + String.format("%.16e", params.getExtendedSource().getTotalElectronISR()) + "    ");
			printer.print("\t\tNLow=    " + String.format("%.16e", params.getExtendedSource().getLowEnergyElectronISR()) + "    ");
			printer.println("\t\tNHigh=   " + String.format("%.16e", params.getExtendedSource().getHighEnergyElectronISR()) + "    ");
			
			printer.println("\n" + Constants.DATA_TABLE_REPORT_PARAMS_MICROBUNCH + "\t");
			printer.print("size=    " + String.format("%.16e", params.getMicrobunchingWidth()) + "    ");
			printer.println("\t\tecsr=    " + String.format("%.16e", params.getMicrobunchingTreshould())+"    ");

			blockSourceISR(params.getCompactSource(), printer, frequencies, "Compact Source");
			blockSourceISR(params.getExtendedSource(), printer, frequencies,"Extended Source");
			blockISR(printer, frequencies,"Compact + Extended Source");
			blockCSR(printer, frequencies,"");
			
			printer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	
		return true;
	}
	
	private void blockSourceISR(Source s, PrintWriter p, List<Frequency> frequencies, String title){

		Iterator<Frequency> i;
		
		i=frequencies.iterator();
		int k=1;

		p.println(Constants.DATA_TABLE_REPORT_LINE);			
		p.println("ISR coefficients" + "\t\t"+ title+ "\t\t" + Constants.DATA_TABLE_REPORT_UNITS);
		p.println(Constants.DATA_TABLE_REPORT_LINE);
		p.println(Constants.DATA_TABLE_REPORT_COLUMNS1);
		p.println(Constants.DATA_TABLE_REPORT_LINE);

		
		while(i.hasNext()){
			Frequency freq = (Frequency) i.next();
			p.print(String.format("%.16e", s.getFrequency(freq.getFrequencyIndex()).getFrequencyValue()) + "\t\t");
			p.print(String.format("%.16e", s.getFrequency(freq.getFrequencyIndex()).getOrdISREmissivity()) + "\t\t");
			p.print(String.format("%.16e", s.getFrequency(freq.getFrequencyIndex()).getExtraordISREmissivity()) + "\t\t");
			p.print(String.format("%.16e", s.getFrequency(freq.getFrequencyIndex()).getOrdISRAbsorption()) + "\t\t");
			p.print(String.format("%.16e", s.getFrequency(freq.getFrequencyIndex()).getExtraordISRAbsorption()) + "\t\t");
			p.print(String.format("%.16e", s.getFrequency(freq.getFrequencyIndex()).getStokesAndPolarization().getEmissivityISRPolarization()) + "\t\t");
			p.println(k++);
		}
		
		p.println(Constants.DATA_TABLE_REPORT_LINE);			
		p.println("ISR Fluxes" + "\t\t"+ title + "\t\tSFU (Solar Flux Units)");
		p.println(Constants.DATA_TABLE_REPORT_LINE);
		p.println(Constants.DATA_TABLE_REPORT_COLUMNS2_ISRCSR);
		p.println(Constants.DATA_TABLE_REPORT_LINE);
		
		i=frequencies.iterator();
		k =1;

		while(i.hasNext()){
			Frequency freq = (Frequency) i.next();
			p.print(String.format("%.16e", s.getFrequency(freq.getFrequencyIndex()).getFrequencyValue()) + "\t\t");
			p.print(String.format("%.16e", s.getFrequency(freq.getFrequencyIndex()).getStokesAndPolarization().getPhiISROrd() * Constants.ERGCMSHZ_TO_SFU+1e-23) + "\t\t");
			p.print(String.format("%.16e", s.getFrequency(freq.getFrequencyIndex()).getStokesAndPolarization().getPhiISRExtraord() * Constants.ERGCMSHZ_TO_SFU+1e-23) + "\t\t");
			p.print(String.format("%.16e", s.getFrequency(freq.getFrequencyIndex()).getStokesAndPolarization().getPhiISRTotal() * Constants.ERGCMSHZ_TO_SFU+1e-23) + "\t\t");
			p.print(String.format("%.16e", s.getFrequency(freq.getFrequencyIndex()).getStokesAndPolarization().getFluxPolarizationISR()) + "\t\t");
			p.println(k++);
		}

		
	}

	private void blockISR(PrintWriter p, List<Frequency> frequencies, String title){

		Iterator<Frequency> i;
		
		i=frequencies.iterator();
		int k=1;

		p.println(Constants.DATA_TABLE_REPORT_LINE);			
		p.println("ISR coefficients" + "\t\t"+ title+ "\t\t" + Constants.DATA_TABLE_REPORT_UNITS);
		p.println(Constants.DATA_TABLE_REPORT_LINE);
		p.println(Constants.DATA_TABLE_REPORT_COLUMNS1);
		p.println(Constants.DATA_TABLE_REPORT_LINE);

		
		while(i.hasNext()){
			Frequency freq = (Frequency) i.next();
			p.print(String.format("%.16e", freq.getFrequencyValue()) + "\t\t");
			p.print(String.format("%.16e", freq.getOrdISREmissivity()) + "\t\t");
			p.print(String.format("%.16e", freq.getExtraordISREmissivity()) + "\t\t");
			p.print(String.format("%.16e", freq.getOrdISRAbsorption()) + "\t\t");
			p.print(String.format("%.16e", freq.getExtraordISRAbsorption()) + "\t\t");
			p.print(String.format("%.16e", freq.getEmissivityISRPolarization()) + "\t\t");
			p.println(k++);
		}
		
		p.println(Constants.DATA_TABLE_REPORT_LINE);			
		p.println("ISR Fluxes" + "\t\t"+ title + "\t\tSFU (Solar Flux Units)");
		p.println(Constants.DATA_TABLE_REPORT_LINE);
		p.println(Constants.DATA_TABLE_REPORT_COLUMNS2_ISRCSR);
		p.println(Constants.DATA_TABLE_REPORT_LINE);		

		i=frequencies.iterator();
		k =1;

		
		while(i.hasNext()){
			Frequency freq = (Frequency) i.next();
			p.print(String.format("%.16e", freq.getFrequencyValue()) + "\t\t");
			p.print(String.format("%.16e", freq.getPhiOrdISR() * Constants.ERGCMSHZ_TO_SFU+1e-23) + "\t\t");
			p.print(String.format("%.16e", freq.getPhiExtraordISR() * Constants.ERGCMSHZ_TO_SFU+1e-23) + "\t\t");
			p.print(String.format("%.16e", freq.getPhiISR() * Constants.ERGCMSHZ_TO_SFU+1e-23) + "\t\t");
			p.print(String.format("%.16e", freq.getFluxPolarizationISR()) + "\t\t");
			p.println(k++);
		}
		
	}

	private void blockCSR(PrintWriter p, List<Frequency> frequencies, String title){

		Iterator<Frequency> i;
		
		i=frequencies.iterator();
		int k=1;

		p.println(Constants.DATA_TABLE_REPORT_LINE);			
		p.println("CSR coefficients" + "\t\t"+ title+ "\t\t" + Constants.DATA_TABLE_REPORT_UNITS);
		p.println(Constants.DATA_TABLE_REPORT_LINE);
		p.println(Constants.DATA_TABLE_REPORT_COLUMNS1);
		p.println(Constants.DATA_TABLE_REPORT_LINE);

		
		while(i.hasNext()){
			Frequency freq = (Frequency) i.next();
			p.print(String.format("%.16e", freq.getFrequencyValue()) + "\t\t");
			p.print(String.format("%.16e", freq.getOrdCSREmissivity()) + "\t\t");
			p.print(String.format("%.16e", freq.getExtraordCSREmissivity()) + "\t\t");
			p.print(String.format("%.16e", freq.getOrdCSRAbsorption()) + "\t\t");
			p.print(String.format("%.16e", freq.getExtraordCSRAbsorption()) + "\t\t");
			p.print(String.format("%.16e", freq.getEmissivityCSRPolarization()) + "\t\t");
			p.println(k++);
		}
		
		p.println(Constants.DATA_TABLE_REPORT_LINE);			
		p.println("CSR Fluxes" + "\t\t"+ title + "\t\tSFU (Solar Flux Units)");
		p.println(Constants.DATA_TABLE_REPORT_LINE);
		p.println(Constants.DATA_TABLE_REPORT_COLUMNS1);

		i=frequencies.iterator();
		k =1;
		
		p.println(Constants.DATA_TABLE_REPORT_LINE);
		p.println(Constants.DATA_TABLE_REPORT_COLUMNS2_ISRCSR);

		while(i.hasNext()){
			Frequency freq = (Frequency) i.next();
			p.print(String.format("%.16e", freq.getFrequencyValue()) + "\t\t");
			p.print(String.format("%.16e", freq.getPhiOrdCSR() * Constants.ERGCMSHZ_TO_SFU+1e-23) + "\t\t");
			p.print(String.format("%.16e", freq.getPhiExtraordCSR() * Constants.ERGCMSHZ_TO_SFU+1e-23) + "\t\t");
			p.print(String.format("%.16e", freq.getPhiCSR() * Constants.ERGCMSHZ_TO_SFU+1e-23) + "\t\t");
			p.print(String.format("%.16e", freq.getFluxPolarizationCSR()) + "\t\t");
			p.println(k++);
		}
		
	}

}
