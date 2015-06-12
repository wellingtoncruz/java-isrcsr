package com.craam.wellington.isrcsr.io;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.craam.wellington.interfaces.IInput;
import com.craam.wellington.isrcsr.Source;
import com.craam.wellington.isrcsr.io.InputData;

/* Essa classe é utilizada para captar os dados de parâmetros de entrada
 * E carregar no objeto InputData. Implementa a Interface de mesmo nome.
 * Essa arquitetura pode ser utilizada para reutilização do código captando
 * informações de diferentes fontes de dados, basta implementar a interface.
 * Neste caso mais simples, o objeto será carregado com um HashMap; 
 */

public class ManualInput implements IInput{
	
	private InputData inputData;
	
	public ManualInput(HashMap<String, Double> map){
		
		this.inputData = new InputData();
		
		this.inputData.setEnergyEspectrum(map.get("ex"))
				      .setElectronNumber(map.get("ntotal"))
				      .setHighestFrequency(map.get("kf").intValue())
				      .setEnergyLower(map.get("j1").intValue())
				      .setEnergyUpper(map.get("j2").intValue())
				      .setEnergyTreshould(map.get("etr"))
				      .setMicrobunchingTreshould(map.get("ecsr"))
				      .setElectronFractionISRExtendedSource(map.get("xnisrex"))
				      .setElectronFractionCSR(map.get("xncsr"))
				      .setMicrobunchingWidth(map.get("tb"));
				      
		this.inputData.getCompactSource()
					  .setViewAngle(map.get("angle").intValue())
				      .setMagneticField(map.get("bmagco"))
				      .setSize(map.get("scsize"))
				      .setHeight(map.get("scheight"))
				      .setPlasmaDensity(map.get("npco"))
				      .setMicrobunchSize(map.get("tb"));
				      
		this.inputData.getExtendedSource()
					 .setViewAngle(map.get("angle").intValue())
					 .setMagneticField(map.get("bmagex"))
					 .setSize(map.get("sesize"))
					 .setHeight(map.get("seheight"))
					 .setPlasmaDensity(map.get("npex"))
					 .setMicrobunchSize(map.get("tb"));
		

		try{ this.inputData.setViewAngle(map.get("angle").intValue()); }
		catch(Exception e){}
	
	}
	
	public InputData getInputData(){
		return this.inputData;
	}
	
	public ManualInput setUseRazin(Boolean useRazin){
		this.inputData.setUseRazin(useRazin);
		return this;
	}

}
