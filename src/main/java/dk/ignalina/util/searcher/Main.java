package dk.ignalina.util.searcher;

import dk.ignalina.util.searcher.Util.*;
import org.apache.avro.Schema;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldableNode;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException, QueryNodeException {

        String indexDir = args[0];
        String responseDir = args[1];

        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexReader indexReader = DirectoryReader.open(dir);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        StandardQueryParser queryParserHelper = new StandardQueryParser();
        boolean keepGoing=true;

        String banner=Util.getNiceBanner(indexReader);

        while(keepGoing) {
            TotalHitCollector collector = new TotalHitCollector();

            System.out.println(banner);
            String queryString = System.console().readLine();
            Query query = queryParserHelper.parse(queryString, "texten");

            long startTime = System.currentTimeMillis();
            indexSearcher.search(query,collector);
            long endTime = System.currentTimeMillis();

            if(collector.getTotalHits()==0) {
                System.out.println("Found ZERO documents took " + (endTime - startTime) + " milliseconds");
                continue;
            }
            long searchTime=endTime - startTime;


            long[] res=new long[collector.getTotalHits()];

            startTime = System.currentTimeMillis();
            if (collector.getTotalHits() != 0) {
                for (int i = 0; i < collector.getTotalHits(); i++) {
                    Document doc = indexSearcher.doc(collector.getDoc(i));
                    res[i]=doc.getField("ref_stored").numericValue().longValue();
                }
            }
            endTime = System.currentTimeMillis();
            long gatherTime=endTime-startTime;

            System.out.println("Found "+ collector.getTotalHits()+" hits in " + searchTime + " ms. Saved in " +gatherTime +" ms.");
            Util.saveRes(res,responseDir);
        }

        indexReader.close();
        dir.close();

    }



}
