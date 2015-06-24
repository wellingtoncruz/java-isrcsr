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
		
		this.inputData.setEnviromentCompactSource(
					  	map.get("bmagco"),
					  	map.get("npco"));
		
		this.inputData.setEnviromentExtendedSource(
			  			map.get("bmagex"),
			  			map.get("npex"));
		
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
				      .setHeight(map.get("scheight"));
		
		this.inputData.getCompactSource().setSize(map.get("scsize"));
				      
		this.inputData.getExtendedSource()
					  .setViewAngle(map.get("angle").intValue())
					  .setHeight(map.get("seheight"));
		
		this.inputData.getExtendedSource().setSize(map.get("sesize"));
		
		this.inputData.setMicrobunch(
					   map.get("tb"),
					   this.inputData.getCompactSource());
		
		this.inputData.getMicrobunch()
					  .setSize(map.get("tb"))
					  .setViewAngle(map.get("angle").intValue());
		
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
