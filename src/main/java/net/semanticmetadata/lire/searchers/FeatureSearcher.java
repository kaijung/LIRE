package net.semanticmetadata.lire.searchers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

public interface FestureSearcher {
	 /**
     * Searches for images similar to the given image.
     *
     * @param image  the example image to search for.
     * @param reader the IndexReader which is used to search through the images.
     * @return a sorted list of hits.
     * @throws java.io.IOException in case exceptions in the reader occurs
     */
    public FeatureSearchHits search(byte[] feature, IndexReader reader) throws IOException;

    /**
     * Searches for images similar to the given image, defined by the Document from the index.
     *
     * @param doc    the example image to search for.
     * @param reader the IndexReader which is used to dsearch through the images.
     * @return a sorted list of hits.
     * @throws java.io.IOException in case exceptions in the reader occurs
     */
    public FeatureSearchHits search(Document doc, IndexReader reader) throws IOException;

    /**
     * Searches for images similar to the given image.
     *
     * @param image  the example image to search for.
     * @param reader the IndexReader which is used to dsearch through the images.
     * @return a sorted list of hits.
     * @throws IOException in case the image could not be read from stream.
     */
//    public FeatureSearchHits search(InputStream image, IndexReader reader) throws IOException;

    /**
     * Identifies duplicates in the database.
     *
     * @param reader the IndexReader which is used to dsearch through the images.
     * @return a sorted list of hits.
     * @throws IOException in case the image could not be read from stream.
     */
//    public FeatureSearchHits findDuplicates(IndexReader reader) throws IOException;

    /**
     * Modifies the given search by the provided positive and negative examples. This process follows the idea
     * of relevance feedback.
     *
     * @param originalSearch
     * @param positives
     * @param negatives
     * @return
     */
    public FeatureSearchHits relevanceFeedback(ImageSearchHits originalSearch,
                                             Set<Document> positives, Set<Document> negatives);
}


