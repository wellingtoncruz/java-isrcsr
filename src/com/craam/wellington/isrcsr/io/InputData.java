package com.craam.wellington.isrcsr.io;

import com.craam.wellington.isrcsr.Microbunch;
import com.craam.wellington.isrcsr.Source;
import com.craam.wellington.statics.Constants;
import com.craam.wellington.statics.Messages;

/*
 *  Essa classe é o objeto que carrega todas as informações
 *  de input de configuração para execução do algoritmo.
 *  
*/
public final class InputData {

	private Double energyEspectrum; // Espectro de energia;
	private Double electronNumber; // Número de electrons
	private int viewAngle; // Angulo formado entre o campo magnético e a linha de visada (em graus)
	private Double viewAngleCs, viewAngleSin; // Cosseno e Seno de viewAngle;
	private int energyLower; // Menor nível de energia usado na integração
	private int energyUpper; // Maior nível de energia usado na integração
	private int highestFrequency; // Valor da maior frequência
	private Double energyTreshould; // Valor de transição de energia
	private Double microbunchingTreshould; // Fronteira de energia para Microbunching

	private Double electronFractionISRExtendedSource; // Fração de eletrons no processo CSR fonte compacta 
	private Double electronFractionCSR; // Fração de eletrons no processo CSR 
	private Double microbunchingWidth; // Tempo em microbunching
	
	private Source extendedSource;
	private Source compactSource;
	private Microbunch microbunch;


	public InputData() {
		extendedSource = new Source();
		compactSource = new Source();
	}
	
	public Double getEnergyEspectrum() {
		return energyEspectrum;
	}
	
	public Double getElectronNumber() {
		return electronNumber;
	}
	
	public int getViewAngle() {
		return viewAngle;
	}
	public Double getViewAngleCs() {
		return viewAngleCs;
	}
	
	public Double getViewAngleSin() {
		return viewAngleSin;
	}
	
	public int getEnergyLower() {
		return energyLower;
	}
	
	public int getEnergyUpper() {
		return energyUpper;
	}
	
	public int getHighestFrequency() {
		return highestFrequency;
	}
	
	public Double getEnergyTreshould() {
		return energyTreshould;
	}
	
	public Double getMicrobunchingTreshould() {
		return microbunchingTreshould;
	}
	
	public Double getElectronFractionISRExtendedSource() {
		return electronFractionISRExtendedSource;
	}
	public Double getElectronFractionCSR() {
		return electronFractionCSR;
	}
	public Double getMicrobunchingWidth() {
		return microbunchingWidth;
	}
	public InputData setEnergyEspectrum(Double energyEspectrum) {
		this.energyEspectrum = energyEspectrum;
		return this;
	}
	public InputData setElectronNumber(Double electronNumber) {
		this.electronNumber = electronNumber;
		return this;
	}
	
	public InputData setViewAngle(int viewAngle) throws Exception {
		if(viewAngle > 0 && viewAngle < 90){
			this.viewAngle = viewAngle;
			this.viewAngleCs = Math.cos(Math.toRadians(this.viewAngle));
			this.viewAngleSin = Math.sin(Math.toRadians(this.viewAngle));
			
		} else throw new Exception(Messages.VIEWANGLE_OUT_RANGE);
		return this;
	}

	public InputData setEnergyLower(int energyLower) {
		this.energyLower = energyLower;
		return this;
	}
	public InputData setEnergyUpper(int energyUpper) {
		this.energyUpper = energyUpper;
		return this;
	}
	public InputData setHighestFrequency(int highestFrequency) {
		this.highestFrequency = highestFrequency;
		return this;
	}
	public InputData setEnergyTreshould(Double energyTreshould) {
		this.energyTreshould = energyTreshould;
		return this;
	}
	public InputData setMicrobunchingTreshould(Double microbunchingTreshould) {
		this.microbunchingTreshould = microbunchingTreshould;
		return this;
	}
	
	public InputData setElectronFractionISRExtendedSource(Double electronFractionISRExtendedSource) {
		this.electronFractionISRExtendedSource = electronFractionISRExtendedSource;
		return this;
	}
	public InputData setElectronFractionCSR(Double electronFractionCSR) {
		this.electronFractionCSR = electronFractionCSR;
		return this;
	}
	public InputData setMicrobunchingWidth(Double microbunchingWidth) {
		this.microbunchingWidth = microbunchingWidth;
		this.microbunch = new Microbunch(microbunchingWidth);
		return this;
	}
	
	// Sources

	public Source getCompactSource(){
		return this.compactSource;
	}
	
	public Source getExtendedSource(){
		return this.extendedSource;
	}
	
	public Microbunch getMicrobunch(){
		return this.microbunch;
	}

}
