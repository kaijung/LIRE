# LIRE - Lucene Image Retrieval
LIRE (Lucene Image Retrieval) is an open source library for content based image retrieval, which means you can search for images that look similar. Besides providing multiple common and state of the art retrieval mechanisms LIRE allows for easy use on multiple platforms. LIRE is actively used for research, teaching and commercial applications. Due to its modular nature it can be used on process level (e.g. index images and search) as well as on image feature level. Developers and researchers can easily extend and modify LIRE to adapt it to their needs.

An online demo can be found at http://demo-itec.uni-klu.ac.at/liredemo/
Most recent documentation is found at the [github repo] (https://github.com/dermotte/LIRE/blob/master/src/main/docs/developer-docs/docs/index.md).

❗ **Please cite LIRE if you use it!** LIRE is open source and free, the only thing we ask for is that you cite it if you use it in your work. For references see below.

## Downloads ##
Downloads are currently hosted at: http://www.itec.uni-klu.ac.at/~mlux/lire-release/.

Nightly builds are available at http://www.itec.uni-klu.ac.at/~mlux/lire-release/lire-nightly.zip

## Getting Started ##
The developer documentation & blog are currently hosted on http://www.semanticmetadata.net/wiki/. In the developer docs common tasks are described, so take a look there if you are starting to use LIRE.

If you are very new to Lire and just want to try out the image search functionality I recommend to start with _LireDemo_, a GUI application which lets you index and search your own photos. If you want to integrate search functions in your software, then take a look at _Lire-SimpleApplication_, which shows you the most straight forward way to use Lire. Both are available in the Downloads section. Small tutorials are available for creating an index and searching images in the at http://www.semanticmetadata.net/wiki/.

We further highly recommend the book titled “Visual Information Retrieval using Java and LIRE”, written by Mathias Lux and Oge Marques. It’s available from Morgan & Claypool, i.e. as PDF eBook (doi:10.2200/S00468ED1V01Y201301ICR025, see [here](http://www.morganclaypool.com/doi/abs/10.2200/S00468ED1V01Y201301ICR025) or on Kindle [here](http://www.amazon.de/gp/product/B00CDGMPR0/ref=as_li_tl?ie=UTF8&camp=1638&creative=6742&creativeASIN=B00CDGMPR0&linkCode=as2&tag=liluimre-21)).

[![](http://ecx.images-amazon.com/images/I/41Rot9eQLKL._SS400_.jpg)](http://www.amazon.de/gp/product/B00CDGMPR0/ref=as_li_tl?ie=UTF8&camp=1638&creative=6742&creativeASIN=B00CDGMPR0&linkCode=as2&tag=liluimre-21)

Sometimes you’re stuck with the integration of LIRE in your product, or you don’t excactly know which parameters to choose. In this case (i) we are either happy to help on the mailing list so all LIRE user can benefit, or to (ii) offer our services for implementation, benchmarking and consulting if there is need for private conversation on LIRE. In the latter case please contact Mathias Lux.

## LIRE and Solr ##
If you are searching for the Solr plugin of LIRE ... it's still under construction. Four global features are working fine and its based on Solr 4.10.2. It can be found at https://bitbucket.org/dermotte/liresolr

## Citation ##

We kindly ask you to refer to either of the following papers in publications mentioning or employing Lire:

Mathias Lux, Savvas A. Chatzichristofis. _LIRE: Lucene Image Retrieval – An Extensible Java CBIR Library_. In proceedings of the 16th ACM International Conference on Multimedia, pp. 1085-1088, Vancouver, Canada, 2008 - Download paper and BibTeX [here](http://dl.acm.org/citation.cfm?id=1459577)

Mathias Lux. _Content Based Image Retrieval with LIRE_. In proceedings of the 19th ACM International Conference on Multimedia, pp. 735-738, Scottsdale, Arizona, USA, 2011 - Download paper and BibTeX [here](http://dl.acm.org/citation.cfm?id=2072432)

Mathias Lux,  Oge Marques _Visual Information Retrieval using Java and LIRE_, Morgan Claypool, 2013

## Acknowledgements ##

This work is supported by the Faculty of Technical Sciences of the Alpen-Adria-Universität Klagenfurt: http://technik.aau.at/en/
