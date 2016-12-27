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
 * Lux Mathias, Savvas A. Chatzichristofis. Lire: Lucene Image Retrieval –
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
 *
 * Copyright statement:
 * ====================
 * (c) 2002-2013 by Mathias Lux (mathias@juggle.at)
 *  http://www.semanticmetadata.net/lire, http://www.lire-project.net
 *
 * Updated: 20.04.13 09:05
 */

package net.semanticmetadata.lire.imageanalysis.features.global.mpeg7;

import net.semanticmetadata.lire.imageanalysis.features.LireFeature;
import net.semanticmetadata.lire.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * This class implements the EdgeHistogram descriptor from the MPEG-7 standard.
 * <p/>
 * This file is part of the Caliph and Emir project: http://www.SemanticMetadata.net
 * <br>Date: 31.01.2006
 * <br>Time: 23:07:39
 *
 * @author Mathias Lux, mathias@juggle.at
 */

public class BlockedEdgeHistogramImplementation {
    public static final int BIN_COUNT = 80;
    private int[] bins = new int[80];
    private int[] blockedBins = new int[45];
    private int treshold = 11;
    private double width;
    private double height;
    private int num_block = 1100;

    /**
     * Constant used in case there is no edge in the image.
     */
    private static final int NoEdge = 0;

    /**
     * Constant used in case there is a vertical edge.
     */
    private static final int vertical_edge = 1;

    /**
     * Constant used in case there is a horizontal edge.
     */
    private static final int horizontal_edge = 2;

    /**
     * Constant used in case there is no directional edge.
     */
    private static final int non_directional_edge = 3;

    /**
     * Constant used in case there is a diagonal of 45 degree.
     */
    private static final int diagonal_45_degree_edge = 4;

    /**
     * Constant used in case there is a digaonal of 135 degree.
     */
    private static final int diagonal_135_degree_edge = 5;

    /**
     * The grey level that has been found after converting from RGB.
     */
    private double[][] grey_level;

    /**
     * The bins have to be quantized with help of this quantization table.
     * The first row is used for the vertical bins,
     * the second for the horizontal bins,
     * the third for the 45 degree bins,
     * the fourth for the 135 degree and the last for the non-directional edges.
     */

    private static double[][] QuantTable =
            {{0.010867, 0.057915, 0.099526, 0.144849, 0.195573, 0.260504, 0.358031, 0.530128},
                    {0.012266, 0.069934, 0.125879, 0.182307, 0.243396, 0.314563, 0.411728, 0.564319},
                    {0.004193, 0.025852, 0.046860, 0.068519, 0.093286, 0.123490, 0.161505, 0.228960},
                    {0.004174, 0.025924, 0.046232, 0.067163, 0.089655, 0.115391, 0.151904, 0.217745},
                    {0.006778, 0.051667, 0.108650, 0.166257, 0.224226, 0.285691, 0.356375, 0.450972}};

    /**
     * Array, where the bins are saved before they have been quantized.
     */


    private double[] Local_Edge_Histogram = new double[80];
    private double[] Local_Blocked_Histogram = new double[45];
    private int blockSize = -1;
    private BufferedImage image;

    /**
     * The actual edge histogram.
     */
    protected int[] edgeHistogram = new int[80];
    protected int[] blockedEdgeHistogram = new int[45];
    /**
     * Allocates a new <code>EdgeHistogramDescriptor</code> so that it represents the
     * media located in the file.
     */

    public BlockedEdgeHistogramImplementation(BufferedImage image) {
        this.image = image;
        width = image.getWidth();
        height = image.getHeight();

        extractFeature();
        edgeHistogram = setEdgeHistogram();
        blockedEdgeHistogram = setBlockedEdgeHistogram();
    }

    public void extract(BufferedImage image) {
        bins = new int[80];
        blockedBins = new int[45];
        treshold = 11;
        num_block = 1100;
        Local_Edge_Histogram = new double[80];
        blockSize = -1;
        this.image = ImageUtils.get8BitRGBImage(image);
        width = image.getWidth();
        height = image.getHeight();
        extractFeature();
        edgeHistogram = setEdgeHistogram();
        blockedEdgeHistogram = setBlockedEdgeHistogram();
    }

//    public EdgeHistogramImplementation(String descriptor) {
//        setStringRepresentation(descriptor);
//    }

    /**
     * Allocates a new <code>EdgeHistogramDescriptor</code>
     */


    public BlockedEdgeHistogramImplementation() {
        bins = new int[80];
        treshold = 11;
        num_block = 1100;
        Local_Edge_Histogram = new double[80];
        blockSize = -1;
    }


    /**
     * Sets the Bin Counts after they have been quantized.
     *
     * @param bins array of 80 bins
     * @throws Exception indicates conditions that a reasonable application might want to catch.
     */

    @SuppressWarnings("unused")
    private void setBinCounts(int[] bins) throws Exception {
        for (int i = 0; i <= BlockedEdgeHistogramImplementation.BIN_COUNT - 1; i++) {
            this.bins[i] = bins[i];
        }
    }


    /**
     * Responsible for setting the Bin Counts in view of the position of the bins.
     *
     * @param pos   position of the bins
     * @param value value of the bin
     * @throws ArrayIndexOutOfBoundsException is necessary, since the bins are stored in an array
     */

    public void setBinCounts(int pos, int[] value) throws ArrayIndexOutOfBoundsException {
        bins[pos] = value[pos];
    }


    /**
     * Gets the Bins of the array.
     *
     * @throws Exception indicates conditions that a reasonable application might want to catch.
     */

    public int getBinCounts() throws Exception {
        int i = 0;
        return bins[i];
    }

    /**
     * If the maximum of the five edge strengths is greater than a threshold, then the image block is
     * considered to have the corresponding edge in it. Otherwise, the image block contains no edge.
     * The default value is 11.
     */

    public void setThreshold() {
        treshold = 11;
    }

    public int getThreshold() {
        return treshold;
    }

    /**
     * The image is split into 16 local regions, each of them is divided into a fixed number
     * of image_blocks, depending on width and height of the image.
     * The size of this image_block is here computed.
     *
     * @return b_size
     */
    private int getBlockSize() {
        if (blockSize < 0) {
            double a = (int) (Math.sqrt((width * height) / num_block));
            blockSize = (int) (Math.floor((a / 2)) * 2);
            if (blockSize == 0)
                blockSize = 2;
        }
        return blockSize;
    }

    /**
     * The width and height of the media is taken to convert the RGB values into luminance values.
     * returns returns the grey_level of the pixel
     */

    public void makeGreyLevel() {
        grey_level = new double[(int) width][(int) height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grey_level[x][y] = getYfromRGB(image.getRGB(x, y));
            }
        }

    }


    /**
     * The image_block is divided into four sub-blocks, for these the luminance mean values
     * are used for the edge detection later.
     * This is the first sub-block.
     *
     * @param i indices from the image_block
     * @param j indices from the image_block
     * @return returns the average brightness of the first sub-block.
     */

    private double getFirstBlockAVG(int i, int j) {
        double average_brightness = 0;
        if (grey_level[i][j] != 0) {

            for (int m = 0; m <= (getBlockSize() >> 1) - 1; m++) {
                for (int n = 0; n <= (getBlockSize() >> 1) - 1; n++) {
                    average_brightness = average_brightness + grey_level[i + m][j + n];
                }
            }
        } else {
            System.err.println("Grey level not initialized.");
        }
        double bs = getBlockSize() * getBlockSize();
        double div = 4 / bs;
        average_brightness = average_brightness * div;
        return average_brightness;
    }


    /**
     * The image_block is divided into four sub-blocks, for these the luminance mean values
     * are used for the edge detection later.
     * This is the second sub-block.
     *
     * @param i indices from the image_block
     * @param j indices from the image_block
     * @return returns the average brightness of the seconc sub-block.
     */


    private double getSecondBlockAVG(int i, int j) {
        double average_brightness = 0;
        if (grey_level[i][j] != 0)

            for (int m = (int) (getBlockSize() >> 1); m <= getBlockSize() - 1; m++) {
                for (int n = 0; n <= (getBlockSize() >> 1) - 1; n++) {
                    average_brightness += grey_level[i + m][j + n];


                }
            }
        else {
            System.err.println("Grey level not initialized.");
        }
        double bs = getBlockSize() * getBlockSize();
        double div = 4 / bs;
        average_brightness = average_brightness * div;
        return average_brightness;
    }


    /**
     * The image_block is divided into four sub-blocks, for these the luminance mean values
     * are used for the edge detection later.
     * This is the third sub-block.
     *
     * @param i indices from the image_block
     * @param j indices from the image_block
     * @return returns the average brightness of the third sub-block.
     */

    private double getThirdBlockAVG(int i, int j) {
        double average_brightness = 0;
        if (grey_level[i][j] != 0) {

            for (int m = 0; m <= (getBlockSize() >> 1) - 1; m++) {
                for (int n = (int) (getBlockSize() >> 1); n <= getBlockSize() - 1; n++) {
                    average_brightness += grey_level[i + m][j + n];
                }
            }
        } else {
            System.err.println("Grey level not initialized.");
        }
        double bs = getBlockSize() * getBlockSize();
        double div = 4 / bs;
        average_brightness = average_brightness * div;
        return average_brightness;
    }

    /**
     * The image_block is divided into four sub-blocks, for these the luminance mean values
     * are used for the edge detection later.
     * This is the fourth sub-block.
     *
     * @param i indices from the image_block
     * @param j indices from the image_block
     * @return returns the average brightness of the fourth sub-block.
     */


    private double getFourthBlockAVG(int i, int j) {
        double average_brightness = 0;

        for (int m = (int) (getBlockSize() >> 1); m <= getBlockSize() - 1; m++) {
            for (int n = (int) (getBlockSize() >> 1); n <= getBlockSize() - 1; n++) {
                average_brightness += grey_level[i + m][j + n];
            }
        }
        double bs = getBlockSize() * getBlockSize();
        double div = 4 / bs;
        average_brightness = average_brightness * div;
        return average_brightness;
    }


    /**
     * The four mean values from the sub-blocks are convolved with filter coefficients.
     * Then the maximum of these results is found and compared with a threshold.
     *
     * @param i indices of image_block
     * @param j indices of image_block
     * @return e_index  returns the index, where the maximum edge is found.
     */

    private int getEdgeFeature(int i, int j) {
        double average[] = {getFirstBlockAVG(i, j), getSecondBlockAVG(i, j),
                getThirdBlockAVG(i, j), getFourthBlockAVG(i, j)};
        double th = this.treshold;
        double edge_filter[][] = {{1.0, -1.0, 1.0, -1.0},
                {1.0, 1.0, -1.0, -1.0},
                {Math.sqrt(2), 0.0, 0.0, -Math.sqrt(2)},
                {0.0, Math.sqrt(2), -Math.sqrt(2), 0.0},
                {2.0, -2.0, -2.0, 2.0}};
        double[] strengths = new double[5];
        int e_index;

        for (int e = 0; e < 5; e++) {
            for (int k = 0; k < 4; k++) {
                strengths[e] += average[k] * edge_filter[e][k];
            }
            strengths[e] = Math.abs(strengths[e]);
        }
        double e_max = 0.0;
        e_max = strengths[0];
        e_index = BlockedEdgeHistogramImplementation.vertical_edge;
        if (strengths[1] > e_max) {
            e_max = strengths[1];
            e_index = BlockedEdgeHistogramImplementation.horizontal_edge;
        }
        if (strengths[2] > e_max) {
            e_max = strengths[2];
            e_index = BlockedEdgeHistogramImplementation.diagonal_45_degree_edge;
        }
        if (strengths[3] > e_max) {
            e_max = strengths[3];
            e_index = BlockedEdgeHistogramImplementation.diagonal_135_degree_edge;
        }
        if (strengths[4] > e_max) {
            e_max = strengths[4];
            e_index = BlockedEdgeHistogramImplementation.non_directional_edge;
        }
        if (e_max < th) {
            e_index = BlockedEdgeHistogramImplementation.NoEdge;
        }

        return (e_index);
    }


    /**
     * <code>extractFeature()</code> takes all the information about the types of edge, that
     * have been found in the sub-images and applies the methods getFirstBlockAVG(int i,int j) through
     * getFourthBlockAVG(int i, int j) on the whole image. returns Local_Edge_Histogram returns the 80 bins
     */

    public void extractFeature() {
        Arrays.fill(Local_Edge_Histogram, 0d);
        makeGreyLevel();
        int sub_local_index = 0;
        int EdgeTypeOfBlock = 0;
        int[] count_local = new int[16];

        for (int i = 0; i < 16; i++) {
            count_local[i] = 0;
        }

        for (int j = 0; j <= height - getBlockSize(); j += getBlockSize())
            for (int i = 0; i <= width - getBlockSize(); i += getBlockSize()) {
                sub_local_index = (int) ((i << 2) / width) + ((int) ((j << 2) / height) << 2);
                count_local[sub_local_index]++;

                EdgeTypeOfBlock = getEdgeFeature(i, j);

                switch (EdgeTypeOfBlock) {
                    case BlockedEdgeHistogramImplementation.NoEdge:
                        break;
                    case BlockedEdgeHistogramImplementation.vertical_edge:
                        Local_Edge_Histogram[sub_local_index * 5]++;
                        break;
                    case BlockedEdgeHistogramImplementation.horizontal_edge:
                        Local_Edge_Histogram[sub_local_index * 5 + 1]++;
                        break;
                    case BlockedEdgeHistogramImplementation.diagonal_45_degree_edge:
                        Local_Edge_Histogram[sub_local_index * 5 + 2]++;
                        break;
                    case BlockedEdgeHistogramImplementation.diagonal_135_degree_edge:
                        Local_Edge_Histogram[sub_local_index * 5 + 3]++;
                        break;
                    case BlockedEdgeHistogramImplementation.non_directional_edge:
                        Local_Edge_Histogram[sub_local_index * 5 + 4]++;
                        break;
                }//switch(EdgeTypeOfBlock)

            }//for(i)
        
       
        Local_Blocked_Histogram=calculateBlockedHistogram(Local_Edge_Histogram,count_local);
        
        for (int k = 0; k < 80; k++) {
            Local_Edge_Histogram[k] /= count_local[(int) k / 5];
        }


        
    }
    
    public double[] calculateBlockedHistogram(double[] nonNormalizedHistogram, int[] countLocal) {
    	

        double blocked_histogram[] = new double[45];
        Arrays.fill(blocked_histogram, 0d);
        
        double A[] = new double[5];
        double B[] = new double[5];
        double C[] = new double[5];
        double D[] = new double[5];
        double E[] = new double[5];
        double F[] = new double[5];
        double G[] = new double[5];
        double H[] = new double[5]; 
        double I[] = new double[5]; 
        
        
        A[0] = nonNormalizedHistogram[0]+nonNormalizedHistogram[5]+nonNormalizedHistogram[20]+nonNormalizedHistogram[25];
        A[1] = nonNormalizedHistogram[1]+nonNormalizedHistogram[6]+nonNormalizedHistogram[21]+nonNormalizedHistogram[26];
        A[2] = nonNormalizedHistogram[2]+nonNormalizedHistogram[7]+nonNormalizedHistogram[22]+nonNormalizedHistogram[27];
        A[3] = nonNormalizedHistogram[3]+nonNormalizedHistogram[8]+nonNormalizedHistogram[23]+nonNormalizedHistogram[28];        
        A[4] = nonNormalizedHistogram[4]+nonNormalizedHistogram[9]+nonNormalizedHistogram[24]+nonNormalizedHistogram[29];
        
        B[0] = nonNormalizedHistogram[5]+nonNormalizedHistogram[10]+nonNormalizedHistogram[25]+nonNormalizedHistogram[30];
        B[1] = nonNormalizedHistogram[6]+nonNormalizedHistogram[11]+nonNormalizedHistogram[26]+nonNormalizedHistogram[31];
        B[2] = nonNormalizedHistogram[7]+nonNormalizedHistogram[12]+nonNormalizedHistogram[27]+nonNormalizedHistogram[32];
        B[3] = nonNormalizedHistogram[8]+nonNormalizedHistogram[13]+nonNormalizedHistogram[28]+nonNormalizedHistogram[33];        
        B[4] = nonNormalizedHistogram[9]+nonNormalizedHistogram[14]+nonNormalizedHistogram[29]+nonNormalizedHistogram[34];  
        
        C[0] = nonNormalizedHistogram[10]+nonNormalizedHistogram[15]+nonNormalizedHistogram[30]+nonNormalizedHistogram[35];
        C[1] = nonNormalizedHistogram[11]+nonNormalizedHistogram[16]+nonNormalizedHistogram[31]+nonNormalizedHistogram[36];
        C[2] = nonNormalizedHistogram[12]+nonNormalizedHistogram[17]+nonNormalizedHistogram[32]+nonNormalizedHistogram[37];
        C[3] = nonNormalizedHistogram[13]+nonNormalizedHistogram[18]+nonNormalizedHistogram[33]+nonNormalizedHistogram[38];        
        C[4] = nonNormalizedHistogram[14]+nonNormalizedHistogram[19]+nonNormalizedHistogram[34]+nonNormalizedHistogram[39];
        
        D[0] = nonNormalizedHistogram[20]+nonNormalizedHistogram[25]+nonNormalizedHistogram[40]+nonNormalizedHistogram[45];
        D[1] = nonNormalizedHistogram[21]+nonNormalizedHistogram[26]+nonNormalizedHistogram[41]+nonNormalizedHistogram[46];
        D[2] = nonNormalizedHistogram[22]+nonNormalizedHistogram[27]+nonNormalizedHistogram[42]+nonNormalizedHistogram[47];
        D[3] = nonNormalizedHistogram[23]+nonNormalizedHistogram[28]+nonNormalizedHistogram[43]+nonNormalizedHistogram[48];        
        D[4] = nonNormalizedHistogram[24]+nonNormalizedHistogram[29]+nonNormalizedHistogram[44]+nonNormalizedHistogram[49];

        E[0] = nonNormalizedHistogram[25]+nonNormalizedHistogram[25]+nonNormalizedHistogram[45]+nonNormalizedHistogram[50];
        E[1] = nonNormalizedHistogram[26]+nonNormalizedHistogram[26]+nonNormalizedHistogram[46]+nonNormalizedHistogram[51];
        E[2] = nonNormalizedHistogram[27]+nonNormalizedHistogram[27]+nonNormalizedHistogram[47]+nonNormalizedHistogram[52];
        E[3] = nonNormalizedHistogram[28]+nonNormalizedHistogram[28]+nonNormalizedHistogram[48]+nonNormalizedHistogram[53];        
        E[4] = nonNormalizedHistogram[29]+nonNormalizedHistogram[29]+nonNormalizedHistogram[49]+nonNormalizedHistogram[54];
        
        F[0] = nonNormalizedHistogram[30]+nonNormalizedHistogram[35]+nonNormalizedHistogram[50]+nonNormalizedHistogram[55];
        F[1] = nonNormalizedHistogram[31]+nonNormalizedHistogram[36]+nonNormalizedHistogram[51]+nonNormalizedHistogram[56];
        F[2] = nonNormalizedHistogram[32]+nonNormalizedHistogram[37]+nonNormalizedHistogram[52]+nonNormalizedHistogram[57];
        F[3] = nonNormalizedHistogram[33]+nonNormalizedHistogram[38]+nonNormalizedHistogram[53]+nonNormalizedHistogram[58];        
        F[4] = nonNormalizedHistogram[34]+nonNormalizedHistogram[39]+nonNormalizedHistogram[54]+nonNormalizedHistogram[59];
        
        G[0] = nonNormalizedHistogram[40]+nonNormalizedHistogram[45]+nonNormalizedHistogram[60]+nonNormalizedHistogram[65];
        G[1] = nonNormalizedHistogram[41]+nonNormalizedHistogram[46]+nonNormalizedHistogram[61]+nonNormalizedHistogram[66];
        G[2] = nonNormalizedHistogram[42]+nonNormalizedHistogram[47]+nonNormalizedHistogram[62]+nonNormalizedHistogram[67];
        G[3] = nonNormalizedHistogram[43]+nonNormalizedHistogram[48]+nonNormalizedHistogram[63]+nonNormalizedHistogram[68];        
        G[4] = nonNormalizedHistogram[44]+nonNormalizedHistogram[49]+nonNormalizedHistogram[64]+nonNormalizedHistogram[69];

        H[0] = nonNormalizedHistogram[40]+nonNormalizedHistogram[45]+nonNormalizedHistogram[65]+nonNormalizedHistogram[70];
        H[1] = nonNormalizedHistogram[41]+nonNormalizedHistogram[46]+nonNormalizedHistogram[66]+nonNormalizedHistogram[71];
        H[2] = nonNormalizedHistogram[42]+nonNormalizedHistogram[47]+nonNormalizedHistogram[67]+nonNormalizedHistogram[72];
        H[3] = nonNormalizedHistogram[43]+nonNormalizedHistogram[48]+nonNormalizedHistogram[68]+nonNormalizedHistogram[73];        
        H[4] = nonNormalizedHistogram[44]+nonNormalizedHistogram[49]+nonNormalizedHistogram[69]+nonNormalizedHistogram[74];
        
        I[0] = nonNormalizedHistogram[50]+nonNormalizedHistogram[55]+nonNormalizedHistogram[70]+nonNormalizedHistogram[75];
        I[1] = nonNormalizedHistogram[51]+nonNormalizedHistogram[56]+nonNormalizedHistogram[71]+nonNormalizedHistogram[76];
        I[2] = nonNormalizedHistogram[52]+nonNormalizedHistogram[57]+nonNormalizedHistogram[72]+nonNormalizedHistogram[77];
        I[3] = nonNormalizedHistogram[53]+nonNormalizedHistogram[58]+nonNormalizedHistogram[73]+nonNormalizedHistogram[78];        
        I[4] = nonNormalizedHistogram[54]+nonNormalizedHistogram[59]+nonNormalizedHistogram[74]+nonNormalizedHistogram[79];
        
        for(int i=0;i<5;i++) {
        	blocked_histogram[i]	=A[i]/(countLocal[0]+countLocal[1]+countLocal[4]+countLocal[5]);
        	blocked_histogram[i+5]	=B[i]/(countLocal[1]+countLocal[2]+countLocal[5]+countLocal[6]);
        	blocked_histogram[i+10]	=C[i]/(countLocal[2]+countLocal[4]+countLocal[6]+countLocal[7]);
        	blocked_histogram[i+15]	=D[i]/(countLocal[4]+countLocal[5]+countLocal[8]+countLocal[9]);
        	blocked_histogram[i+20]	=E[i]/(countLocal[5]+countLocal[6]+countLocal[9]+countLocal[10]);
        	blocked_histogram[i+25]	=F[i]/(countLocal[6]+countLocal[7]+countLocal[10]+countLocal[11]);
        	blocked_histogram[i+30]	=G[i]/(countLocal[8]+countLocal[9]+countLocal[12]+countLocal[13]);
        	blocked_histogram[i+35]	=H[i]/(countLocal[9]+countLocal[10]+countLocal[13]+countLocal[14]);        	
        	blocked_histogram[i+40]	=I[i]/(countLocal[10]+countLocal[11]+countLocal[14]+countLocal[15]);         	
        }
        return blocked_histogram;
    }
    
    public int[] setBlockedEdgeHistogram() {
        double iQuantValue = 0;
        double value[] = Local_Blocked_Histogram;

        for (int i = 0; i < 45; i++) {
            for (int j = 0; j < 8; j++) {
            	blockedBins[i] = j;
                if (j < 7)
                    iQuantValue = (BlockedEdgeHistogramImplementation.QuantTable[i % 5][j] + BlockedEdgeHistogramImplementation.QuantTable[i % 5][j + 1]) / 2.0;
                else
                    iQuantValue = 1.0;
                if (value[i] <= iQuantValue) {
                    break;
                }
            }
        }
        return blockedBins;
    }
    /**
     * In the <code>setEdgeHistogram()</code> method the value for each edgeHistogram bin are quantized by
     * quantization tables. Five quantisation tables for five different edge types are existing.
     *
     * @return returns 80 bins.
     */


    public int[] setEdgeHistogram() {
        int Edge_HistogramElement[] = new int[80];
        double iQuantValue = 0;
        double value[] = Local_Edge_Histogram;

        for (int i = 0; i < 80; i++) {
            for (int j = 0; j < 8; j++) {
                bins[i] = j;
                if (j < 7)
                    iQuantValue = (BlockedEdgeHistogramImplementation.QuantTable[i % 5][j] + BlockedEdgeHistogramImplementation.QuantTable[i % 5][j + 1]) / 2.0;
                else
                    iQuantValue = 1.0;
                if (value[i] <= iQuantValue) {
                    break;
                }
            }
        }
        return bins;
    }

    /**
     * Calculates the distance between two images taking the edge edgeHistogram into account. take 480 as soft upper
     * border, 0 is hard lower border because there are 80 bins <= 7.
     *
     * @param edgeHistogramA defines the first point
     * @param edgeHistogramB defines the second point
     * @return the distance from [0, 480]
     */
    public static double calculateDistance(int[] edgeHistogramA, int[] edgeHistogramB) {
//        if (edgeHistogramA == null) System.err.println("Input edgeHistogram a is null!");
//        if (edgeHistogramB == null) System.err.println("Input edgeHistogram b is null!");
        double result = 0d;
        // Todo: this first for loop should sum up the differences of the non quantized edges. Check if this code is right!
        for (int i = 0; i < edgeHistogramA.length; i++) {
            // first version is the un-quantized version, according to the MPEG-7 docs part 8 this version is quite okay as though its nearly linear quantization
            // result += Math.abs((float) edgeHistogramA[i] - (float) edgeHistogramB[i]);
            result += Math.abs(QuantTable[i % 5][edgeHistogramA[i]] - QuantTable[i % 5][edgeHistogramB[i]]);
        }
        for (int i = 0; i < 5; i++) {
            result +=  5d*Math.abs(edgeHistogramA[i] - edgeHistogramB[i]);
        }
        for (int i = 5; i < 10; i++) {
            result +=  3d*Math.abs(edgeHistogramA[i] - edgeHistogramB[i]);
        }
        for (int i = 10; i <= 15; i++) {
            result +=  5d*Math.abs(edgeHistogramA[i] - edgeHistogramB[i]);
        }
        for (int i = 15; i <= 20; i++) {
            result +=  1d * Math.abs(edgeHistogramA[i] - edgeHistogramB[i]);
        }   
        for (int i = 20; i <= 25; i++) {
            result +=  1d * Math.abs(edgeHistogramA[i] - edgeHistogramB[i]);
        }  
        for (int i = 25; i < 30; i++) {
            result += 1d * Math.abs(edgeHistogramA[i] - edgeHistogramB[i]);
        }
        for (int i = 30; i < 35; i++) {
            result += 5d*Math.abs(edgeHistogramA[i] - edgeHistogramB[i]);
        }
        for (int i = 35; i < 40; i++) {
            result += 3d*Math.abs(edgeHistogramA[i] - edgeHistogramB[i]);
        } 
        for (int i = 40; i < 45; i++) {
            result += 5d*Math.abs(edgeHistogramA[i] - edgeHistogramB[i]);
        } 
        return result;
    }

    @SuppressWarnings("unused")
    private static int[] RGB2YCRCB(int[] pixel, int[] result) {
        double yy = (0.299 * pixel[0] + 0.587 * pixel[1] + 0.114 * pixel[2]) / 256.0;
        result[0] = (int) (219.0 * yy + 16.5);
        result[1] = (int) (224.0 * 0.564 * (pixel[2] / 256.0 * 1.0 - yy) + 128.5);
        result[2] = (int) (224.0 * 0.713 * (pixel[0] / 256.0 * 1.0 - yy) + 128.5);
        return result;
    }

    private static int getYfromRGB(int rgb) {
        int b = rgb & 255;
        int g = rgb >> 8 & 255;
        int r = rgb >> 16 & 255;
        double yy = (0.299 * r + 0.587 * g + 0.114 * b) / 256.0;
        return (int) (219.0 * yy + 16.5);
    }

    /**
     * Compares one descriptor to another.
     *
     * @param descriptor
     * @return the distance from [0,infinite) or -1 if descriptor type does not match
     */
    public double getDistance(LireFeature descriptor) {
        if (!(descriptor instanceof BlockedEdgeHistogramImplementation)) return -1d;
        BlockedEdgeHistogramImplementation e = (BlockedEdgeHistogramImplementation) descriptor;
        return calculateDistance(e.blockedEdgeHistogram, blockedEdgeHistogram);
    }

//    /**
//     * Creates a String representation from the descriptor.
//     *
//     * @return the descriptor as String.
//     */
//    public String getStringRepresentation() {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("edgehistogram;");
//        stringBuilder.append(edgeHistogram[0]);
//        for (int i = 1; i < edgeHistogram.length; i++) {
//            stringBuilder.append(' ');
//            stringBuilder.append(edgeHistogram[i]);
//        }
//        return stringBuilder.toString();
//    }
//
//    /**
//     * Sets the descriptor value from a String.
//     *
//     * @param descriptor the descriptor as String.
//     */
//    public void setStringRepresentation(String descriptor) {
//        String[] parts = descriptor.split(";");
//        if (!parts[0].equals("edgehistogram")) {
//            throw new UnsupportedOperationException("This is no valid representation of a EdgeHistogram descriptor!");
//        }
//        int[] bins = new int[80];
//        StringTokenizer st = new StringTokenizer(parts[1], " ");
//        int count = 0;
//        while (st.hasMoreElements()) {
//            bins[count] = Integer.parseInt(st.nextToken());
//            count++;
//        }
//        edgeHistogram = bins;
//    }
}
