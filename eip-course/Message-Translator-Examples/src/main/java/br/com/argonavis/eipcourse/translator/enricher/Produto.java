package br.com.argonavis.eipcourse.translator.enricher;

import java.io.Serializable;

public class Produto implements Serializable {
	private long id;
    private double preco;
    private String codigo;
    
    public Produto(long id) {
    	this.id = id;
    }
    
    public Produto(long id, String codigo, double preco) {
    	this.id = id;
    	this.codigo = codigo;
    	this.preco = preco;
    }
    
    public long getId() {
    	return id;
    }
	public double getPreco() {
		return preco;
	}
	public void setPreco(double preco) {
		this.preco = preco;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
    
    public String toString() {
    	return "("+id+") " + codigo + " $" + preco;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
		Produto other = (Produto) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
    
    
}
