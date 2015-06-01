package com.craam.wellington.statics;

public final class Constants {
	
	// Constantes Matemáticas
	public final static Double PI = Math.PI;
	
	// Constantes específicas do algoritmo
	public final static Double RAZIN_FFB_RATIO = 1.5; // Constante de proporção de Razin
	public final static Integer GYROFREQUENCY_TRESHOULD = 100; // Fronteira para cálculo de frequência
	
	// Conversão de unidades
	public final static Double ARCSEC_TO_RAD = 57.3*3600; // Constante de conversão de arcsecond to rads 
	public final static Double JOULE_TO_EV = 6.2415e18;
	public final static Double EV_TO_MEV = 1e-6;
	public final static Double MEV_TO_EV = 1e6;
	public final static Double FFB_TO_FH = 2.80e6;
	public final static Double ERGCMSHZ_TO_SFU = 1.e-3/1.e-22;
	public final static Double FFB_TO_HZ = 2.80e6;
	public final static Double DEGREE_TO_RAD = PI/180;
	public final static Double ARC_TO_CM = (DEGREE_TO_RAD/3600)*1.496e13;
	public final static Double ERG_TO_JOULE = 1.0e-7;
	public final static Double MS_TO_CMS = 1e2;
	public final static Double KG_TO_G = 1e3;
	
	// Constantes físicas
	public final static Double ELECTRON_MASS = 9.1094e-31; // Massa do Electron KG
	public final static Double LIGHT_SPEED = 2.998e8; // Velocidade da luz no vácuo m/s
	public final static Double ELECTRON_CHARGE = 4.803e-10; // Carga do electron (statcoloumb)
	public final static Double ELECTRON_REST_ENERGY = (ELECTRON_MASS * Math.pow(LIGHT_SPEED, 2)) * JOULE_TO_EV * EV_TO_MEV; // Energia do electron em repouso  
	public final static Double EMISSIVITY_FACTOR = Math.pow(ELECTRON_CHARGE, 3) / ((ELECTRON_MASS/1e-3) * Math.pow((LIGHT_SPEED/1e-2), 2)); 
	public final static Double ABSORPTION_FACTOR = 4 * Math.pow(Math.PI, 2) * ELECTRON_CHARGE; 
	public final static Double UA = 1.496e13;
	
	//Constantes do Synchrotron
	public final static Double[] SSY_XD = {0.001,0.005,0.01,0.025,0.05,0.075,0.1,0.15,0.2,0.25,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.,1.2,1.4,1.6,1.8,2.,2.5,3.,3.5,4.,4.5,5.,6.,7.,8.,9.,10.};
	public final static Double[] SSY_FD = {0.213,0.358,0.445,0.583,0.702,0.722,0.818,0.874,0.904,0.917,0.919,0.901,0.872,0.832,0.788,0.742,0.694,0.655,0.566,0.486,0.414,0.354,0.301,0.2,0.13,0.0845,0.0541,0.0339,0.0214,0.0085,0.0033,0.0013,0.0005,0.00019};
	
	//Constantes de Output - Strings
	public final static String DATA_TABLE_REPORT_HEADER = "\t\t\tUniversidade Presbiteriana Mackenzie\n\t\t\tCRAAM - Centro de Radio-Astronomia e Astrofísica Mackenzie\t\t\tWellington Cruz - TIA: 7135004-7";
	public final static String DATA_TABLE_REPORT_LINE = "========================================================================================================================================================================================================";
	public final static String DATA_TABLE_REPORT_PARAMS = "Parametros de entrada: ";
	public final static String DATA_TABLE_REPORT_COLUMNS1 = "ffb\t\t\t\t\t\temissivity o-mode\t\t\temissivity x-mode\t\t\tabsorption o-mode\t\t\tabsorption x-mode\t\t\tpolarization\t\t\tindex";
	public final static String DATA_TABLE_REPORT_COLUMNS2 = "f(hz)\t\t\t\t\t\tphi o-mode\t\t\t\t\tphi x-mode\t\t\t\t\tphi total (erg/cm/s/Hz)\t\t\t\t\tphi total (SFU)\t\t\t\t\tpolarization\t\t\t\tindex";
	public final static String DATA_TABLE_REPORT_UNITS = "Unidades: erg/(cm^3 s^1 sr^1 Hz^1)      cm^-1";

	//Constantes de Output - Dados
	public final static String OUTPUT_FOLDER = "output/";
	
	
}
