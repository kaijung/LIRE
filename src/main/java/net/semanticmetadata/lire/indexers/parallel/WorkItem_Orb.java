package net.semanticmetadata.lire.indexers.parallel;

import java.util.List;

import org.apache.lucene.document.Field;

import net.semanticmetadata.lire.imageanalysis.features.LocalFeature;

public class WorkItem_Orb {
    private byte[] buffer;
    private String fileName;
    private Field[] Features;
	public WorkItem_Orb(String path, Field[] features) {
		// TODO Auto-generated constructor stub
		this.fileName=path;
		this.Features=features;
	}
	public byte[] getBuffer() {
		return buffer;
	}
	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Field[] getFeatures() {
		return Features;
	}
	public void setFeatures(Field[] features) {
		Features = features;
	}
	

}
