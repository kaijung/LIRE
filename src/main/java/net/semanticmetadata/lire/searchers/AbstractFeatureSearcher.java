package net.semanticmetadata.lire.searchers;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
public abstract class AbstractFeatureSearcher implements FestureSearcher{
	   public FeatureSearchHits search(byte[] feature, IndexReader reader) throws IOException {
	        //BufferedImage bufferedImage = ImageIO.read(image);
	        return search(feature, reader);
	    }

	    public FeatureSearchHits relevanceFeedback(ImageSearchHits originalSearch, Set<Document> positives, Set<Document> negatives) {
	        throw new UnsupportedOperationException("Not implemented yet for this kind of searcher!");
	    }


}