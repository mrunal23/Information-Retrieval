package assignment2;


//Task 2
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;


public class searchTRECtopics {

	public static void main(String[] args) throws IOException, ParseException {
		
		//File corpus=new File("C:\\Users\\shree\\Desktop\\Fall-14\\IR\\Assignment-2\\topics.51-100");
		Path path=Paths.get("C:\\Users\\shree\\Desktop\\Fall-14\\IR\\Assignment-2\\topics.51-100");
		String content= new String(Files.readAllBytes(path));
		String[] docs = StringUtils.substringsBetween(content, "<top>","</top>");
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("TEXT", analyzer);
		Map<String , Float> short_query=new HashMap<String, Float>();
		Map<String , Float> long_query=new HashMap<String, Float>();
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File("C:\\Users\\shree\\Desktop\\Fall-14\\IR\\Assignment-2\\default")));
		IndexSearcher searcher = new IndexSearcher(reader);
		DefaultSimilarity dSimi=new DefaultSimilarity();
		List<AtomicReaderContext> leafContexts = reader.getContext().reader().leaves();
		//List<String> title=new ArrayList<>();
		List<String> desc=new ArrayList<>();
		List<String> number=new ArrayList<>();
		
		//for loop for adding short and long queries to arraylist 
		//appending it with the querynumber
		for(String str: docs){
			//query number + short query
			number.add("&&"+StringUtils.substringBetween(str,"Number:", "<").trim().replaceAll("[^\\w_]"," " )+"&&&&"+StringUtils.substringBetween(str,"Topic:", "<").trim().replaceAll("[^\\w_]", " ")+"&&");
			//query number + long query
			desc.add("&&"+StringUtils.substringBetween(str,"Number:", "<").trim().replaceAll("[^\\w_]"," " )+"&&&&"+StringUtils.substringBetween(str,"Description:", "<").trim().replaceAll("[^\\w_]", " ")+"&&");
		}
		
		//Finding relevance for short query
		//for loop to traverse through query strings
		for(String temp:number){
			String queryNo,queryDoc;
			String[] arr=new String[3];
			arr=StringUtils.substringsBetween(temp,"&&","&&");
			//separating query number and query
			if(arr[0].equals("100")){	
				queryNo=arr[0];
				}
				else{
					queryNo=arr[0].replaceFirst("0", "");
					}
			queryDoc=arr[1];
			
			//for loop for traversing leafContexts
			for (int i = 0; i < leafContexts.size(); i++) {
				AtomicReaderContext leafContext=leafContexts.get(i);
				int startDocNo=leafContext.docBase;
				int doc;
				//System.out.println(temp);
				Query query = parser.parse(queryDoc);	
				Set<Term> queryTerms = new LinkedHashSet<Term>();
				query.extractTerms(queryTerms);
				//for loop for traversing through query terms
				for(Term t: queryTerms){
					DocsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(),
					MultiFields.getLiveDocs(leafContext.reader()),
					"TEXT", new BytesRef(t.text()));
					//while loop for traversing through documents for each leaflet
				while (de!=null && ((doc = de.nextDoc()) != DocsEnum.NO_MORE_DOCS) ) 
				{			
					int docno=de.docID()+startDocNo;
					Document doc1=searcher.doc(docno);
					String newDocNo=doc1.get("DOCNO");
					float normLength=dSimi.decodeNormValue(leafContext.reader().getNormValues("TEXT").get(doc));	
					float TF=(de.freq()/normLength);
					float IDF=(float) Math.log10((1 + (reader.maxDoc()/reader.docFreq(new Term("TEXT",t.text())))));
					float freq=TF*IDF;
					System.out.println("The relevance for term " + t.text() + " in doc "+ docno + " is "+ freq + " occurs for " + de.freq() + " times ");
					float temp_freq=(float) 0.0;
					//storing key as query number and document number
					String key=("&&"+queryNo+"&&&&"+newDocNo+"&&");
					//to check if the key already exists, if so then updating the value of relevance score
					//else just adding a new entry in the map
					if(short_query.containsKey((key))){
					temp_freq=short_query.get(key);
					}
					short_query.put((key),(freq + temp_freq));
				}//end of while
				}//end of term for
		}//end of leaflet for			
	}//end of number's for 
		
		//for loop traversing through long query
		for(String temp:desc){
			String queryNo,queryDoc;
			String[] arr=new String[3];
			arr=StringUtils.substringsBetween(temp,"&&","&&");
			//separating query number and query 
			if(arr[0].equals("100")){	
				queryNo=arr[0];//query number
				}
				else{
					queryNo=arr[0].replaceFirst("0", "");
					}
			queryDoc=arr[1];//query
			
			//for loop for traversing through leafContexts
			for (int i = 0; i < leafContexts.size(); i++) {
				AtomicReaderContext leafContext=leafContexts.get(i);
				int startDocNo=leafContext.docBase;
				int doc;
				Query query = parser.parse(queryDoc);	
				Set<Term> queryTerms = new LinkedHashSet<Term>();
				query.extractTerms(queryTerms);
				//for loop for traversing through query terms
				for(Term t: queryTerms){
					DocsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(),
					MultiFields.getLiveDocs(leafContext.reader()),
					"TEXT", new BytesRef(t.text()));
					//while loop for traversing through documents in each leaflet 
				while (de!=null && ((doc = de.nextDoc()) != DocsEnum.NO_MORE_DOCS) ) 
				{			
					int docno=de.docID()+startDocNo;
					Document doc1=searcher.doc(docno);
					String newDocNo=doc1.get("DOCNO");
					float normLength=dSimi.decodeNormValue(leafContext.reader().getNormValues("TEXT").get(doc));	
					float TF=(de.freq()/normLength);
					float IDF=(float) Math.log10((1 + (reader.maxDoc()/reader.docFreq(new Term("TEXT",t.text())))));
					float freq=TF*IDF;
					System.out.println("The relevance for term " + t.text() + " in doc "+ docno + " is "+ freq + " occurs for " + de.freq() + " times ");
					float temp_freq=(float) 0.0;
					//key is query number and document number
					String key=("&&"+queryNo+"&&&&"+newDocNo+"&&");
					//to check if key already exists in the map
					if(long_query.containsKey((key))){
					temp_freq=long_query.get(key);
					}
					long_query.put((key),(freq + temp_freq));
				}//end of while
				}//end of term for
		}//end of leaflet for			
	}//end of desc's for
				
		//Map<String,Float> sortedLongQuery=sortByComparator(long_query);
		String[] arr1=new String[3];
		int rank;
		PrintWriter writer=new PrintWriter("C:\\Users\\shree\\Desktop\\IR-OutputFiles\\myalgo-small.txt","UTF-8");
		PrintWriter writer1=new PrintWriter("C:\\Users\\shree\\Desktop\\IR-OutputFiles\\myalgo-long.txt","UTF-8");
		//for loop for sorting short queries and
		//adding first 1000 relevance scores for each query in files
		for(int i=51;i<=100;i++){
			//initialising rank as 1
			rank=1;
			//creating a temporary hashmap for storing 
			Map<String,Float> temporaryMap=new HashMap<String,Float>();		
			for(Map.Entry<String, Float> entry: short_query.entrySet()){
				arr1=StringUtils.substringsBetween(entry.getKey(),"&&","&&");
				int querynumber=Integer.parseInt(arr1[0]);
				if(i==querynumber){
					temporaryMap.put(entry.getKey(),entry.getValue());
				}
			}			
			Map<String,Float> sortedShortQuery=sortByComparator(temporaryMap);
			for(Map.Entry<String, Float> entry: sortedShortQuery.entrySet()){
				if(rank<1001){
				arr1=StringUtils.substringsBetween(entry.getKey(),"&&","&&");
				String Query1=arr1[0];
				String DocID=arr1[1];
				writer.println(Query1+"  "+" Q0 "+"  "+DocID+"  "+rank+"  "+entry.getValue()+"  "+" run-l-short");
				rank=rank+1;
				}
				else
					break;
			}			
		}		
		//for loop for sorting short queries and
				//adding first 1000 relevance scores for each query in files
		for(int i=51;i<=100;i++){
			rank=1;
			Map<String,Float> temporaryMap=new HashMap<String,Float>();			
			for(Map.Entry<String, Float> entry: long_query.entrySet()){
				arr1=StringUtils.substringsBetween(entry.getKey(),"&&","&&");
				int querynumber=Integer.parseInt(arr1[0]);
				if(i==querynumber){
					temporaryMap.put(entry.getKey(),entry.getValue());
				}
			}			
			Map<String,Float> sortedLongQuery=sortByComparator(temporaryMap);
			for(Map.Entry<String, Float> entry: sortedLongQuery.entrySet()){
				if(rank<1001){
				arr1=StringUtils.substringsBetween(entry.getKey(),"&&","&&");
				String Query1=arr1[0];
				String DocID=arr1[1];
				writer1.println(Query1+"  "+" Q0 "+"  "+DocID+"  "+rank+"  "+entry.getValue()+"  "+" run-l-long");
				rank=rank+1;
				}
				else
					break;
			}			
		}		
		writer.close();
		writer1.close();
		reader.close();		
	}//end of main
	
	//for this method referred stackoverflow
	private static Map<String, Float> sortByComparator(Map<String, Float> unsortMap)
    {
        List<Entry<String, Float>> list = new LinkedList<Entry<String, Float>>(unsortMap.entrySet());
        
        Collections.sort(list, new Comparator<Entry<String, Float>>()
        {
            public int compare(Entry<String, Float> o1,
                    Entry<String, Float> o2)
            {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        // Maintaining insertion order with the help of LinkedList
        Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
        for (Entry<String, Float> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
