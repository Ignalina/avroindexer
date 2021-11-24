package dk.ignalina.util.searcher;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;

public class Util {
    public static IndexSearcher createSearcher(String indexDir) throws IOException
    {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexReader reader = DirectoryReader.open(dir);
        System.out.println("Index cointains "+reader.numDocs()+" of docs");
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }

    public static TopDocs searchById(Integer id, IndexSearcher searcher) throws Exception
    {


        QueryParser qp = new QueryParser("id", new StandardAnalyzer());
        Query idQuery = qp.parse(id.toString());
        TopDocs hits = searcher.search(idQuery, 10);
        return hits;
    }


    public static String getNiceBanner(IndexReader indexReader) throws IOException {

        Document doc1 = indexReader.document(1);
        java.util.List<IndexableField> fields= doc1.getFields();
        String fieldNames="";
        String pre="";
        for(IndexableField f: fields) {
            fieldNames+=pre+f.name();
            pre=",";
        }

        return "\n\nIndex has "+indexReader.numDocs()+" of docs,Enter a query:";
    }

    public static void saveRes(long[] res, String responseDir) throws IOException {
        int endIndex = responseDir.lastIndexOf("/");
        if (endIndex != -1)
        {
            responseDir = responseDir.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
        }

        String filename=responseDir+"/response_"+Instant.now().getEpochSecond();
        System.out.println("Saving to "+filename);

        BufferedWriter out = new BufferedWriter(new FileWriter(filename));
        for (int i = 0; i < res.length; ++i) {
            out.write(res[i] + "\n");
        }
        out.close();
    }
}


