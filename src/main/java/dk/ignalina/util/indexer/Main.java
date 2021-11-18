package dk.ignalina.util.indexer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;


import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {


        String schemaFile = args[0];
        String inputDir = args[1];
        String outputDirectory = args[2];
        int cores = Integer.parseInt(args[3]);

        Schema schema = Util.loadSchema(schemaFile);
        StandardAnalyzer analyzer = new StandardAnalyzer();

        IndexWriter wroot = Util.createRootIndex(schema, analyzer, outputDirectory);


        Directory indexes[] = new Directory[cores];
        Thread threads[] = new Thread[cores];


        List<String> fileNamesInDir = Util.getFileNamesInDir(inputDir);
        Iterator<String> fileIterator = fileNamesInDir.iterator();
        while (fileIterator.hasNext()) {

            for (int i = 0; i < indexes.length; i++) {

                if (!fileIterator.hasNext())
                    break;

                String fileName = fileIterator.next();
                Directory index = new ByteBuffersDirectory();
                indexes[i] = index;
                threads[i] = new Thread(() -> Util.createIndex(fileName, schema, analyzer, index));
                threads[i].start();

            }

            // Wait until all threads are done.

            for (Thread thread : threads) {
                    thread.join();
            }

            // Merge in sub index
                wroot.addIndexes(indexes);
                wroot.commit();
        }

            DirectoryReader dr = DirectoryReader.open(wroot);
            System.out.println("Num of docs in index = " + dr.numDocs());
            wroot.close();
            dr.close();


    }

}
