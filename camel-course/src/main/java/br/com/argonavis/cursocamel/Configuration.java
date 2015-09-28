/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.argonavis.cursocamel;

/**
 *
 * @author helderdarocha
 */
public interface Configuration {
    public static String WORKDIR = "lab";
    public static String INBOX   = "file:" + WORKDIR + "/inbox";
    public static String OUTBOX  = "file:" + WORKDIR + "/outbox";
    public static String WIRETAP  = "file:" + WORKDIR + "/wiretap";
    
    public static String FTP_SERVER = "localhost";
    public static String FTP_USER = "helderdarocha";
    public static String FTP_PASS = "helder";
    public static String FTP_PATH = "/Code/NetBeansProjects/Camel/cursocamel/lab/ftpdata";
    
    public static String ACTIVEMQ_URL = "tcp://localhost:61616";
    public static String FTP_URL      = "ftp://" + FTP_SERVER + FTP_PATH + "?username=" +  FTP_USER + "&password=" + FTP_PASS;
    
}
