package dk.ignalina.util.searcher;

import dk.ignalina.util.searcher.Util.*;
import org.apache.avro.Schema;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
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

        while(keepGoing) {
            TotalHitCollector collector = new TotalHitCollector();

            System.out.println("Index has "+indexReader.numDocs()+" of docs,enter a query");
            String queryString = System.console().readLine();
            Query query = queryParserHelper.parse(queryString, "defaultField");

            long startTime = System.currentTimeMillis();
            indexSearcher.search(query,collector);
            long endTime = System.currentTimeMillis();

            if(collector.getTotalHits()==0) {
                System.out.println("Found ZERO documents took " + (endTime - startTime) + " milliseconds");
                continue;
            }

            System.out.println("Found "+ collector.getTotalHits()+" took " + (endTime - startTime) + " milliseconds");


             startTime = System.currentTimeMillis();
            if (collector.getTotalHits() != 0) {
                for (int i = 0; i < collector.getTotalHits(); i++) {
                    Document doc = indexSearcher.doc(collector.getDoc(i));
                }
            }
             endTime = System.currentTimeMillis();
            System.out.println("Collecting them took " + (endTime - startTime) + " milliseconds");

        }

        indexReader.close();
        dir.close();

    }



}
