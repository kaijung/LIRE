/*
 * This file is part of the LIRE project: http://lire-project.net
 * LIRE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * LIRE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LIRE; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * We kindly ask you to refer the any or one of the following publications in
 * any publication mentioning or employing Lire:
 *
 * Lux Mathias, Savvas A. Chatzichristofis. Lire: Lucene Image Retrieval -
 * An Extensible Java CBIR Library. In proceedings of the 16th ACM International
 * Conference on Multimedia, pp. 1085-1088, Vancouver, Canada, 2008
 * URL: http://doi.acm.org/10.1145/1459359.1459577
 *
 * Lux Mathias. Content Based Image Retrieval with LIRE. In proceedings of the
 * 19th ACM International Conference on Multimedia, pp. 735-738, Scottsdale,
 * Arizona, USA, 2011
 * URL: http://dl.acm.org/citation.cfm?id=2072432
 *
 * Mathias Lux, Oge Marques. Visual Information Retrieval using Java and LIRE
 * Morgan & Claypool, 2013
 * URL: http://www.morganclaypool.com/doi/abs/10.2200/S00468ED1V01Y201301ICR025
 */

package net.semanticmetadata.lire.imageanalysis.features.local.opencvfeatures;

import net.semanticmetadata.lire.imageanalysis.features.LocalFeature;
import net.semanticmetadata.lire.imageanalysis.features.LocalFeatureExtractor;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CvOrbFreakExtractor implements LocalFeatureExtractor {

	// Default: int nfeatures=0, int nOctaveLayers=3, double
	// contrastThreshold=0.04, double edgeThreshold=10, double sigma=1.6
	private int nfeatures = 3500;
	private int nLevels = 8;
	private int nOctaveLayers = 4;
	private double patternScale = 22.0;


	LinkedList<CvOrbFreakFeature> features = null;

	FeatureDetector detector;
	DescriptorExtractor extractor;

	// private boolean passingParams = false;

	public CvOrbFreakExtractor() {
		init();
	}

	public CvOrbFreakExtractor(int features, int levels,int OctaveLayers, double patternScale) {
		this.nfeatures = features;
		this.nOctaveLayers = OctaveLayers;
		this.patternScale = patternScale;
		// this.passingParams = true;
		init();
	}

	private void init() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		detector = FeatureDetector.create(FeatureDetector.ORB);
		extractor = DescriptorExtractor.create(DescriptorExtractor.FREAK);
		try {
			File temp = File.createTempFile("tempFile", ".tmp");
			
			String orbSettings = "%YAML:1.0\nname: \"Feature2D.ORB\"\nWTA_K: 2\nedgeThreshold: 31\nfirstLevel: 0\nnFeatures: "
					+nfeatures+"\nnLevels: "
					+nLevels+"\npatchSize: 31\nscaleFactor: 1.20\nscoreType: 0\n";

			FileWriter writer = new FileWriter(temp, false);
			writer.write(orbSettings);
			writer.close();
			detector.read(temp.getPath());
			
			
			String freakSettings = "%YAML:1.0" +
            "\npatternScale: "+patternScale+
            "\nnOctaves: "+nOctaveLayers+
            "\norientationNormalized : True"+
            "\nscaleNormalized : True\n";

			writer = new FileWriter(temp, false);
			writer.write(freakSettings);
			writer.close();			
			extractor.read(temp.getPath());
			temp.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public LinkedList<CvOrbFreakFeature> getFeatures() {
		return features;
	}

	@Override
	public Class<? extends LocalFeature> getClassOfFeatures() {
		return CvOrbFreakFeature.class;
	}

	@Override
	public void extract(BufferedImage img) {
		MatOfKeyPoint keypoints = new MatOfKeyPoint();
		Mat descriptors = new Mat();
		List<KeyPoint> myKeys;
		// Mat img_object = Highgui.imread(image, 0); //0 =
		// CV_LOAD_IMAGE_GRAYSCALE
		// detector.detect(img_object, keypoints);
		byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		//Lock lock = new ReentrantLock();
		Mat matRGB = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
		matRGB.put(0, 0, data);
		Mat matGray = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC1);
		Imgproc.cvtColor(matRGB, matGray, Imgproc.COLOR_BGR2GRAY); // TODO: RGB
																	// or BGR?
		byte[] dataGray = new byte[matGray.rows() * matGray.cols() * (int) (matGray.elemSize())];
		matGray.get(0, 0, dataGray);
		//try{
			//lock.lock();
		detector.detect(matGray, keypoints);
		if (keypoints.total() == 0) {

			descriptors.release();
			keypoints.release();
			matRGB.release();
			matGray.release();
			descriptors = null;
			keypoints = null;
			matRGB = null;
			matGray = null;
			return;
		}
		
		extractor.compute(matGray, keypoints, descriptors);
		if (keypoints.total() == 0) {

			descriptors.release();
			keypoints.release();
			matRGB.release();
			matGray.release();
			descriptors = null;
			keypoints = null;
			matRGB = null;
			matGray = null;
			return;
		}
		myKeys = keypoints.toList();
	/*} finally {
		lock.unlock();
		//System.out.println("errer image" + path);

	}*/
		features = new LinkedList<CvOrbFreakFeature>();
		KeyPoint key;
		CvOrbFreakFeature feat;
		double[] desc;
		int cols, rows = myKeys.size();
		for (int i = 0; i < rows; i++) {
			cols = (descriptors.row(i)).cols();
			desc = new double[cols];
			key = myKeys.get(i);
			for (int j = 0; j < cols; j++) {
				desc[j] = descriptors.get(i, j)[0];
			}
			feat = new CvOrbFreakFeature(key.pt.x, key.pt.y, key.size, desc);
			features.add(feat);
		}
	}

	public LinkedList<CvOrbFreakFeature> computeFreakKeypoints(BufferedImage img) {
		MatOfKeyPoint keypoints = new MatOfKeyPoint();
		List<KeyPoint> myKeys;
		// Mat img_object = Highgui.imread(image, 0); //0 =
		// CV_LOAD_IMAGE_GRAYSCALE
		// detector.detect(img_object, keypoints);
		byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		Mat matRGB = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
		matRGB.put(0, 0, data);
		Mat matGray = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC1);
		Imgproc.cvtColor(matRGB, matGray, Imgproc.COLOR_BGR2GRAY); // TODO: RGB
																	// or BGR?
		//Lock lock = new ReentrantLock();
		byte[] dataGray = new byte[matGray.rows() * matGray.cols() * (int) (matGray.elemSize())];
		matGray.get(0, 0, dataGray);
       // try{
        	//lock.lock();
		detector.detect(matGray, keypoints);
		myKeys = keypoints.toList();
       /* }
        finally{
        	lock.unlock();	
        }*/
		LinkedList<CvOrbFreakFeature> myKeypoints = new LinkedList<CvOrbFreakFeature>();
		KeyPoint key;
		CvOrbFreakFeature feat;
		for (Iterator<KeyPoint> iterator = myKeys.iterator(); iterator.hasNext();) {
			key = iterator.next();
			feat = new CvOrbFreakFeature(key.pt.x, key.pt.y, key.size, null);
			myKeypoints.add(feat);
		}

		return myKeypoints;
	}

	public String getParameters() {
		return "%YAML:1.0\nname: \"Feature2D.ORB\"\nWTA_K: 2\nedgeThreshold: 31\nfirstLevel: 0\nnFeatures: "+nfeatures+
				"\nnLevels: "+nLevels+"\npatchSize: 31\nscaleFactor: 1.2000000476837158e+00\nscoreType: 0"+
		        "\npatternScale: "+patternScale+"f"+
		        "\nnOctaves: "+nOctaveLayers+
		        "\norientationNormalized : True"+
		        "\nscaleNormalized : True\n";
	}

}
