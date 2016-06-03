package net.semanticmetadata.lire.searchers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SimpleFeatureSearchHits implements FeatureSearchHits{
	 ArrayList<SimpleResult> results;

	    public SimpleFeatureSearchHits(Collection<SimpleResult> results, double maxDistance) {
	        this.results = new ArrayList<SimpleResult>(results.size());
	        this.results.addAll(results);
	        // this step normalizes and inverts the distance ...
	        // although its now a score or similarity like measure its further called distance
//	        for (Iterator<SimpleResult> iterator = this.results.iterator(); iterator.hasNext(); ) {
//	            SimpleResult result = iterator.next();
//	            // result.setDistance(1f - result.getDistance() / maxDistance);
//	        }
	    }

	    /**
	     * Basic constructor to create results.
	     *
	     * @param results
	     * @param maxDistance
	     * @param useSimilarityScore set to true is you want similarity scores, otherwise distances will be used. Note that using distance is faster in terms of runtime.
	     */
	    public SimpleFeatureSearchHits(Collection<SimpleResult> results, double maxDistance, boolean useSimilarityScore) {
	        this.results = new ArrayList<SimpleResult>(results.size());
	        this.results.addAll(results);
	        // this step normalizes and inverts the distance ...
	        // although its now a score or similarity like measure its further called distance
	        for (Iterator<SimpleResult> iterator = this.results.iterator(); iterator.hasNext(); ) {
	            SimpleResult result = iterator.next();
	            if (useSimilarityScore) result.setDistance(1d - result.getDistance() / maxDistance);
	        }
	    }

	    /**
	     * Returns the size of the result list.
	     *
	     * @return the size of the result list.
	     */
	    public int length() {
	        return results.size();
	    }

	    /**
	     * Returns the score of the document at given position.
	     * Please note that the score in this case is a distance,
	     * which means a score of 0 denotes the best possible hit.
	     * The result list starts with position 0 as everything
	     * in computer science does.
	     *
	     * @param position defines the position
	     * @return the score of the document at given position. The lower the better (its a distance measure).
	     */
	    public double score(int position) {
	        return results.get(position).getDistance();
	    }

//	    /**
//	     * Returns the document at given position
//	     *
//	     * @param position defines the position.
//	     * @return the document at given position.
//	     */
//	    public Document doc(int position) {
//	        return results.get(position).getDocument();
//	    }

	    /**
	     * Returns the id of the document within the respective Lucene IndexReader
	     *
	     * @param position position in the result list
	     * @return the id in the IndexReader.
	     */
	    public int documentID(int position) {
	        return results.get(position).getIndexNumber();
	    }

//	    public String path(int position) {
//	        return results.get(position).getPath();
//	    }

	    @SuppressWarnings("unused")
	    private double sigmoid(float f) {
	        double result = 0d;
	        result = -1d + 2d / (1d + Math.exp(-2d * f / 0.6));
	        return (1d - result);
	    }
}
