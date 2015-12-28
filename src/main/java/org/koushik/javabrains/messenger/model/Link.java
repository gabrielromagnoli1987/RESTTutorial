package org.koushik.javabrains.messenger.model;

// clase utilizada para armar un link que sera utilizado en los responses de los request a la api 
// para hacerla navegable (HATEOAS) 

public class Link {
	private String link;
	private String rel;
	
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	
	
}