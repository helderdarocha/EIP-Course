package br.com.argonavis.eipcourse.translator.enricher;

import java.io.Serializable;

public class Product implements Serializable {
	private long id;
    private double price;
    private String code;
    
    public Product(long id) {
    	this.id = id;
    }
    
    public Product(long id, String code, double price) {
    	this.id = id;
    	this.code = code;
    	this.price = price;
    }
    
    public long getId() {
    	return id;
    }
	public double getPrice() {
		return price;
	}
	public void setPreco(double preco) {
		this.price = preco;
	}
	public String getCode() {
		return code;
	}
	public void setCodigo(String codigo) {
		this.code = codigo;
	}
    
    public String toString() {
    	return "("+id+") " + code + " $" + price;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
    
    
}
