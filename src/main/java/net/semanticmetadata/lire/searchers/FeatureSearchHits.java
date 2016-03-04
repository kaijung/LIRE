package net.semanticmetadata.lire.searchers;

public interface FeatureSearchHits {
    /**
     * Returns the size of the result list.
     *
     * @return the size of the result list.
     */
    public int length();

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
    public double score(int position);

//    /**
//     * Returns the document at given position
//     *
//     * @param position defines the position.
//     * @return the document at given position.
//     */
//    public Document doc(int position);

    /**
     * This returns the actual document number within the IndexReader. You can get the document from indexReader.document(hits.documentID(pos))
     * @param position
     * @return
     */
    public int documentID(int position);

//    public String path(int position);
}
