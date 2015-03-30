package assignment1;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
//import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.apache.commons.lang.StringUtils;


public class IndexComparision{
	public static void main(String[] args) throws IOException{
		Analyzer analyzer1 = new StandardAnalyzer();
		Analyzer analyzer2 = new StopAnalyzer();
		Analyzer analyzer3 = new KeywordAnalyzer();
		Analyzer analyzer4 = new SimpleAnalyzer();
		
	IndexComparision compare = new IndexComparision();
	System.out.println("***************Standard Analyser**********************");
	compare.indexG(analyzer1);
	System.out.println("***************Stop Analyzer**************************");
	compare.indexG(analyzer2);
	System.out.println("***************Keyword Analyzer************************");
	compare.indexG(analyzer3);
	System.out.println("****************Simple Analyser***********************");
	compare.indexG(analyzer4);
	
		
	}
	
	public void indexG(Analyzer analyzer) throws IOException{
		
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_0, analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		Directory dir = FSDirectory.open(new File("C:\\Users\\shree\\Desktop\\Fall-14\\IR\\Assignment1\\index"));
		IndexWriter writer = new IndexWriter(dir, iwc);		
		File corpus=new File("C:\\Users\\shree\\Desktop\\Fall-14\\IR\\Assignment1\\corpus");
		
		for (File corp : corpus.listFiles()) {

			Path path = Paths.get("C:\\Users\\shree\\Desktop\\Fall-14\\IR\\Assignment1\\corpus\\"+corp.getName());
			String content = new String(Files.readAllBytes(path));			
			String[] docs = StringUtils.substringsBetween(content, "<DOC>","</DOC>");
			
			for(String str: docs)
			{
				Document luceneDoc = new Document();
				//String[] doc=StringUtils.substringsBetween(str, "<DOCNO>", "</DOCNO>");
				String[] tex=StringUtils.substringsBetween(str, "<TEXT>", "</TEXT>");
				//String[] line=StringUtils.substringsBetween(str, "<BYLINE>", "</BYLINE>");
				//String[] date=StringUtils.substringsBetween(str, "<DATELINE>", "</DATELINE>");
				//String[] header=StringUtils.substringsBetween(str, "<HEAD>", "</HEAD>");
				//String byline=null;
				//String head=null;
				//String docno=null;
				String text=null;
				//String dateline=null;
				/*if(line!=null){
					byline=Arrays.toString(line);
						}
								
				if(header!=null){
					head=Arrays.toString(header);
						}
				
				if(doc!=null){
					docno=Arrays.toString(doc);
				}*/
				if(tex!=null){
					text=Arrays.toString(tex);
				}
				/*if(date!=null){
					dateline=Arrays.toString(date);
				}*/
				/*if(docno!=null){
				luceneDoc.add(new StringField("DOCNO",docno,Field.Store.YES));}*/
				if(text!=null){
				luceneDoc.add(new TextField("TEXT",text,Field.Store.YES));}
				/*if(!(byline==null)){
				luceneDoc.add(new TextField("BYLINE",byline,Field.Store.YES));
				}
				if(!(dateline==null)){
				luceneDoc.add(new TextField("DATELINE",dateline,Field.Store.YES));
				}
				if(!(head==null)){
				luceneDoc.add(new TextField("HEAD",head,Field.Store.YES));
				}*/
				writer.addDocument(luceneDoc);
			}	
		}
		writer.forceMerge(1);
		writer.commit();
		writer.close();
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File("C:\\Users\\shree\\Desktop\\Fall-14\\IR\\Assignment1\\index")));
		System.out.println("Total number of documents in the corpus:"+reader.maxDoc());
		System.out.println("Number of documents containing the term \"new\" for field \"TEXT\": "+reader.docFreq(new Term("TEXT", "new")));
		System.out.println("Number of occurences of \"new\" in the field\"TEXT\": "+reader.totalTermFreq(new Term("TEXT","new")));
		Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
		System.out.println("Size of the vocabulary for this field:"+vocabulary.size());
		System.out.println("Number of documents that have at least one term for this field: "+vocabulary.getDocCount());
		System.out.println("Number of tokens for this field:"+vocabulary.getSumTotalTermFreq());
		System.out.println("Number of postings for this field:"+vocabulary.getSumDocFreq());
		TermsEnum iterator = vocabulary.iterator(null);
		BytesRef byteRef = null;
		System.out.println("\n*******Vocabulary-Start**********");
		while((byteRef = iterator.next()) != null) {
			//String term = byteRef.utf8ToString();
			//System.out.print(term+"\t");
			}
		System.out.println("\n*******Vocabulary-End**********");
		reader.close();
		
		
	}
}
