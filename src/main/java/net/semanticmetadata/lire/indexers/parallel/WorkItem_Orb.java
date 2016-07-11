package net.semanticmetadata.lire.indexers.parallel;

import java.util.List;

import org.apache.lucene.document.Field;

import net.semanticmetadata.lire.imageanalysis.features.LocalFeature;

public class WorkItem_Orb {
    private byte[] buffer;
    private String fileName;
    private List<Field[]> listOfFeatures;
	public byte[] getBuffer() {
		return buffer;
	}
	public WorkItem_Orb(String path, byte[] buffer) {
        this.fileName = path;
        this.buffer = buffer;
    }

    public WorkItem_Orb(String path, List<Field[]> listOfFeatures) {
        this.fileName = path;
        this.listOfFeatures = listOfFeatures;
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
	public List<Field[]> getListOfFeatures() {
		return listOfFeatures;
	}
	public void setListOfFeatures(List<Field[]> listOfFeatures) {
		this.listOfFeatures = listOfFeatures;
	}
}
