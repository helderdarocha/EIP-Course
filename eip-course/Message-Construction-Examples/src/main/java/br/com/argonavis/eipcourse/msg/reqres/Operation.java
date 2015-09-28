package br.com.argonavis.eipcourse.msg.reqres;

public class Operation {
    public Double add(Double arg1, Double arg2) {
    	return arg1 + arg2;
    }
    
    public Double subtract(Double arg1, Double arg2) {
    	return arg1 - arg2;
    }
    
    public Double multiply(Double arg1, Double arg2) {
    	return arg1 * arg2;
    }
    
    public Double divide(Double arg1, Double arg2) {
    	return arg1 / arg2;
    }
}
