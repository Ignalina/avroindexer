package dk.ignalina.util.indexer;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Util {
    static List<String> getFileNamesInDir(String inputDir) {
        List<String> res = new ArrayList<>();
        File[] files = new File(inputDir).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                res.add(file.getAbsoluteFile().toString());
            }
        }
        return res;
    }

    public static Schema loadSchema(String schemaFile) {
        Schema schema = null;
        try {
            schema = new Schema.Parser().parse(new File(schemaFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return schema;
    }

    public static IndexWriter createRootIndex(Schema schema, org.apache.lucene.analysis.Analyzer analyzer, String outDir) {


        Path p = Paths.get(outDir);
        Directory index = null;
        try {
            index = new MMapDirectory(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(index, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }

    public static IndexWriter createIndex(String inputFile, Schema schema, org.apache.lucene.analysis.Analyzer analyzer, Directory index) {


        //
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(index, config);
        } catch (IOException e) {
            e.printStackTrace();
        }


        BufferedInputStream input = null;

        try {
            input = new BufferedInputStream(new FileInputStream(new File(inputFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        try {
            DatumReader<GenericRecord> reader = new GenericDatumReader<>();

            BinaryDecoder binaryDecoder = DecoderFactory.get().binaryDecoder(input, null);


            GenericRecord datum = null;
            Document doc = createDoc();

            try (DataFileStream<GenericRecord> streamReader = new DataFileStream<GenericRecord>(input, reader)) {
                 schema =  streamReader.getSchema();
                for (long recordCount = 0; streamReader.hasNext() ; recordCount++) {
                    datum = (GenericRecord) streamReader.next();
                    addDoc(writer, datum, doc);
                }
            }



        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.commit();

            DirectoryReader dr = DirectoryReader.open(writer);
            System.out.println("Num of docs in index in subindex  = " + dr.numDocs());

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return writer;
    }

    private static void addDoc(IndexWriter w, GenericRecord datum, Document document) throws IOException {

        java.util.List<IndexableField> fields = document.getFields();

        ((NumericDocValuesField) fields.get(0)).setLongValue((long) datum.get(0));
        ((StoredField) fields.get(1)).setLongValue((long) datum.get(0));

        ((TextField) fields.get(2)).setStringValue(datum.get(23).toString());
        ((TextField) fields.get(3)).setStringValue(datum.get(24).toString());
        ((TextField) fields.get(4)).setStringValue(datum.get(25).toString());
        ((TextField) fields.get(5)).setStringValue(datum.get(26).toString());
        ((TextField) fields.get(6)).setStringValue(datum.get(27).toString());
        ((TextField) fields.get(7)).setStringValue(datum.get(28).toString());
        w.addDocument(document);

    }

    private static Document createDoc() throws IOException {

        NumericDocValuesField l1 = new NumericDocValuesField("ref", (long) 0);
        StoredField l1_store = new StoredField("ref_stored", (long) 0);

        TextField fields1 = new TextField("s1", "", Field.Store.NO);
        TextField fields2 = new TextField("s2", "", Field.Store.NO);
        TextField fields3 = new TextField("s3", "", Field.Store.NO);
        TextField fields4 = new TextField("s4", "", Field.Store.NO);
        TextField fields5 = new TextField("s5", "", Field.Store.NO);
        TextField fields6 = new TextField("s6", "", Field.Store.NO);
        Document document = new Document();
        document.add(l1);
        document.add(l1_store);
        document.add(fields1);
        document.add(fields2);
        document.add(fields3);
        document.add(fields4);
        document.add(fields5);
        document.add(fields6);

        return document;
    }

}
