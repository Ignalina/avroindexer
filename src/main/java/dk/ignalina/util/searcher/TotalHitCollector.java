package dk.ignalina.util.searcher;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.SimpleCollector;

import java.util.ArrayList;

public  class TotalHitCollector extends SimpleCollector {

    private int base;
    private final java.util.List<Integer> docs = new ArrayList<>();

    public int getTotalHits() {
        return docs.size();
    }

    public int getDoc(int i) {
        return docs.get(i);
    }

    @Override
    public void collect(int doc) {
        doc += this.base;
        docs.add(doc);
    }

    @Override
    protected void doSetNextReader(LeafReaderContext context) {
        this.base = context.docBase;
    }

    @Override
    public ScoreMode scoreMode() {
        return ScoreMode.COMPLETE_NO_SCORES;
    }
}