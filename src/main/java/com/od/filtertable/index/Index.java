package com.od.filtertable.index;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * 
 * Represents an index to create for a IndexingBridgeSink
 * 
 * More than one index may be supported for each source
 * e.g. we may index a MapBridgeRecord by both masterfilesId and instrumentCode
 */
public class Index {

    /**
     * Field name on which to build index
     */
    private String indexField;
    
    
    /**
     * if true, the first letter of the search term does not have to be the first letter of the 
     * indexed value
     * 
     * e.g. if we index the value 'ABCDE'
     * then a search term 'BC' will only match if we index substrings
     * otherwise search term would have to start with 'A'
     * 
     * Indexing including substrings does require a lot more memory than without
     * 
     * @param indexSubstrings
     */
    private boolean indexSubstrings;
    
    /**
     * Sets the maximum depth for the initial index creation
     * 
     * e.g. if we index the value 'ABCDE' with substrings enabled but max depth is 3,
     * then we only build an initial index structure for the first 3 characters of any search
     * e.g. ABC, BCD, CDE
     * 
     * If a user searches using a longer search term, we then build the index to a greater depth 
     * on the fly - but only for substrings of the search term specified
     * 
     * Provided the initial length is great enough to reduce the number of values which need to 
     * be dynamically indexed sufficiently, this can be a useful memory optimisation with only a small
     * reduction in response times.
     * 
     * @param initialDepth
     */
    private int initialDepth = Integer.MAX_VALUE;


    /**
     * Whether index is case sensitive
     * false consumes less memory
     */
    private boolean caseSensitive = false;

    public String getIndexField() {
        return indexField;
    }

    public void setIndexField(String indexField) {
        this.indexField = indexField;
    }

    public boolean isIndexSubstrings() {
        return indexSubstrings;
    }

    public void setIndexSubstrings(boolean indexSubstrings) {
        this.indexSubstrings = indexSubstrings;
    }

    public int getInitialDepth() {
        return initialDepth;
    }

    public void setInitialDepth(int initialDepth) {
        this.initialDepth = initialDepth;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
}
