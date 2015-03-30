package assignment2;

//task 1
//packages used
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;



public class EasySearch {
	public static void main(String args[]) throws Exception{
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("TEXT", analyzer);
		//query string
		String queryString="new world";
		Query query = parser.parse(queryString);
		Set<Term> queryTerms = new LinkedHashSet<Term>();
		query.extractTerms(queryTerms);
		Map<Integer , Float> map=new HashMap<Integer, Float>();
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File("C:\\Users\\shree\\Desktop\\Fall-14\\IR\\Assignment-2\\default")));
		DefaultSimilarity dSimi=new DefaultSimilarity();
		int sum=0;
		List<AtomicReaderContext> leafContexts = reader.getContext().reader().leaves();
		int NumberOfDocs=0;
		//iterating through leafles
		for (int i = 0; i < leafContexts.size(); i++) {
			AtomicReaderContext leafContext=leafContexts.get(i);
			int startDocNo=leafContext.docBase;
			int numberOfDoc=leafContext.reader().maxDoc();
			
					int doc;
					//iterating through query terms 
					for(Term t: queryTerms){
						DocsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(),
								MultiFields.getLiveDocs(leafContext.reader()),
								"TEXT", new BytesRef(t.text()));
						//traversing through documents in the leaflet i
			while (de!=null && ((doc = de.nextDoc()) != DocsEnum.NO_MORE_DOCS) ) 
			{
				
				int docno=de.docID()+startDocNo;
				float normLength=dSimi.decodeNormValue(leafContext.reader().getNormValues("TEXT").get(doc));	
				float TF=(de.freq()/normLength);
				float IDF=(float) Math.log10((1 + (reader.maxDoc()/reader.docFreq(new Term("TEXT",t.text())))));
				//calculating relevance frequency
				float freq=TF*IDF;
				System.out.println("The relevance for term " + t.text() + " in doc "+ docno + " is "+ freq + " occurs for " + de.freq() + " times ");
				float temp_freq=(float) 0.0;
				//Storing the relevance frequency in hashmap
				if(map.containsKey((docno))){
				temp_freq=map.get(docno);
				}
				map.put(docno,(freq + temp_freq));
			}
			}
	}
		System.out.println(reader.maxDoc());
		Set key=map.keySet();
		//printing relevance frequency for documents
		for(Map.Entry<Integer, Float> entry : map.entrySet()){
			System.out.println("Doc id "+ entry.getKey() + ", value " + entry.getValue());
		}
		reader.close();
	}
}
