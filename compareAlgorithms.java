package assignment2;


//Task 3
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.FSDirectory;

public class compareAlgorithms {
	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File("C:\\Users\\shree\\Desktop\\Fall-14\\IR\\Assignment-2\\default")));
				 //You need to explicitly specify the
				//ranking algorithm using the respective Similarity class
				Analyzer analyzer = new StandardAnalyzer();
				QueryParser parser = new QueryParser("TEXT", analyzer);
				Path path=Paths.get("C:\\Users\\shree\\Desktop\\Fall-14\\IR\\Assignment-2\\topics.51-100");
				String content= new String(Files.readAllBytes(path));
				String[] documents = StringUtils.substringsBetween(content, "<top>","</top>");
				List<String> title=new ArrayList<>();
				List<String> desc=new ArrayList<>();
				//adding short query and long query in array list
				for(String str: documents){
					title.add("&&"+StringUtils.substringBetween(str,"Number:", "<").trim().replaceAll("[^\\w_]"," " )+"&&&&"+StringUtils.substringBetween(str,"Topic:", "<").trim().replaceAll("[^\\w_]", " ")+"&&");
					desc.add("&&"+StringUtils.substringBetween(str,"Number:", "<").trim().replaceAll("[^\\w_]"," " )+"&&&&"+StringUtils.substringBetween(str,"Description:", "<").trim().replaceAll("[^\\w_]", " ")+"&&");
				}		
				
				//creating printwriter for each algorithm
				PrintWriter writer=new PrintWriter("C:\\Users\\shree\\Desktop\\IR-OutputFiles\\BM25shortQuery.txt","UTF-8");
				PrintWriter writer1=new PrintWriter("C:\\Users\\shree\\Desktop\\IR-OutputFiles\\BM25longQuery.txt","UTF-8");
				PrintWriter writer2=new PrintWriter("C:\\Users\\shree\\Desktop\\IR-OutputFiles\\DefaultshortQuery.txt","UTF-8");
				PrintWriter writer3=new PrintWriter("C:\\Users\\shree\\Desktop\\IR-OutputFiles\\DefaultlongQuery.txt","UTF-8");
				PrintWriter writer4=new PrintWriter("C:\\Users\\shree\\Desktop\\IR-OutputFiles\\LMDirichletshortQuery.txt","UTF-8");
				PrintWriter writer5=new PrintWriter("C:\\Users\\shree\\Desktop\\IR-OutputFiles\\LMDirichletlongQuery.txt","UTF-8");
				PrintWriter writer6=new PrintWriter("C:\\Users\\shree\\Desktop\\IR-OutputFiles\\LMJelinekshortQuery.txt","UTF-8");
				PrintWriter writer7=new PrintWriter("C:\\Users\\shree\\Desktop\\IR-OutputFiles\\LMJelineklongQuery.txt","UTF-8");
				
				//short query-BM25
				for(String str:title){
				int rank=1;
				IndexSearcher searcher = new IndexSearcher(reader);
				searcher.setSimilarity(new BM25Similarity());
				String[] arr=new String[3];
				String queryNumber,queryString;
				arr=StringUtils.substringsBetween(str,"&&","&&");
				
				if(arr[0].equals("100")){	
						queryNumber=arr[0];
				}
					else{
						queryNumber=arr[0].replaceFirst("0", "");
					}
				queryString=arr[1];
				Query query = parser.parse(queryString);
				TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);
				searcher.search(query, collector);
				ScoreDoc[] docs = collector.topDocs().scoreDocs;
				for (int i = 0; i < docs.length; i++) {
					if(rank>1000){
						break;}
				Document doc = searcher.doc(docs[i].doc);
				writer.println(queryNumber+"  "+"Q0  "+doc.get("DOCNO")+"  "+rank+"  "+docs[i].score+"  "+"run-l-BM25-short");
				System.out.println(doc.get("DOCNO")+" "+docs[i].score);
				rank=rank+1;
				}
				}
				writer.close();
				
				//Long query BM25
				for(String str:desc){
					int rank=1;
					IndexSearcher searcher = new IndexSearcher(reader);
					searcher.setSimilarity(new BM25Similarity());
					String[] arr=new String[3];
					String queryNumber,queryString;
					arr=StringUtils.substringsBetween(str,"&&","&&");
					
					if(arr[0].equals("100")){	
							queryNumber=arr[0];
					}
						else{
							queryNumber=arr[0].replaceFirst("0", "");
						}
					queryString=arr[1];
					Query query = parser.parse(queryString);
					TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);
					searcher.search(query, collector);
					ScoreDoc[] docs = collector.topDocs().scoreDocs;
					for (int i = 0; i < docs.length; i++) {
						if(rank>1000){
							break;}
					Document doc = searcher.doc(docs[i].doc);
					writer1.println(queryNumber+"  "+"Q0  "+doc.get("DOCNO")+"  "+rank+"  "+docs[i].score+"  "+"run-l-BM25-long");
					System.out.println(doc.get("DOCNO")+" "+docs[i].score);
					rank=rank+1;
					}
					}
				writer1.close();
				
				//short query Vector Space Model
				for(String str:title){
					int rank=1;
					IndexSearcher searcher = new IndexSearcher(reader);
					searcher.setSimilarity(new DefaultSimilarity());
					String[] arr=new String[3];
					String queryNumber,queryString;
					arr=StringUtils.substringsBetween(str,"&&","&&");
					
					if(arr[0].equals("100")){	
							queryNumber=arr[0];
					}
						else{
							queryNumber=arr[0].replaceFirst("0", "");
						}
					queryString=arr[1];
					Query query = parser.parse(queryString);
					TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);
					searcher.search(query, collector);
					ScoreDoc[] docs = collector.topDocs().scoreDocs;
					for (int i = 0; i < docs.length; i++) {
						if(rank>1000){
							break;}
					Document doc = searcher.doc(docs[i].doc);
					writer2.println(queryNumber+"  "+"Q0  "+doc.get("DOCNO")+"  "+rank+"  "+docs[i].score+"  "+"run-l-Default-short");
					System.out.println(doc.get("DOCNO")+" "+docs[i].score);
					rank=rank+1;
					}
					}
				writer2.close();
				
				//Long query Vector space model
					for(String str:desc){
						int rank=1;
						IndexSearcher searcher = new IndexSearcher(reader);
						searcher.setSimilarity(new DefaultSimilarity());
						String[] arr=new String[3];
						String queryNumber,queryString;
						arr=StringUtils.substringsBetween(str,"&&","&&");
						
						if(arr[0].equals("100")){	
								queryNumber=arr[0];
						}
							else{
								queryNumber=arr[0].replaceFirst("0", "");
							}
						queryString=arr[1];
						Query query = parser.parse(queryString);
						TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);
						searcher.search(query, collector);
						ScoreDoc[] docs = collector.topDocs().scoreDocs;
						for (int i = 0; i < docs.length; i++) {
							if(rank>1000){
								break;}
						Document doc = searcher.doc(docs[i].doc);
						writer3.println(queryNumber+"  "+"Q0  "+doc.get("DOCNO")+"  "+rank+"  "+docs[i].score+"  "+"run-l-Default-long");
						System.out.println(doc.get("DOCNO")+" "+docs[i].score);
						rank=rank+1;
						}
						}
					writer3.close();
					
					//short query LMDirichlet 
					for(String str:title){
						int rank=1;
						IndexSearcher searcher = new IndexSearcher(reader);
						searcher.setSimilarity(new LMDirichletSimilarity());
						String[] arr=new String[3];
						String queryNumber,queryString;
						arr=StringUtils.substringsBetween(str,"&&","&&");
						
						if(arr[0].equals("100")){	
								queryNumber=arr[0];
						}
							else{
								queryNumber=arr[0].replaceFirst("0", "");
							}
						queryString=arr[1];
						Query query = parser.parse(queryString);
						TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);
						searcher.search(query, collector);
						ScoreDoc[] docs = collector.topDocs().scoreDocs;
						for (int i = 0; i < docs.length; i++) {
							if(rank>1000){
								break;}
						Document doc = searcher.doc(docs[i].doc);
						writer4.println(queryNumber+"  "+"Q0  "+doc.get("DOCNO")+"  "+rank+"  "+docs[i].score+"  "+"run-l-LMDirichlet-short");
						System.out.println(doc.get("DOCNO")+" "+docs[i].score);
						rank=rank+1;
						}
						}
					writer4.close();
					
					//long query LMDirichlet 
						for(String str:desc){
							int rank=1;
							IndexSearcher searcher = new IndexSearcher(reader);
							searcher.setSimilarity(new LMDirichletSimilarity());
							String[] arr=new String[3];
							String queryNumber,queryString;
							arr=StringUtils.substringsBetween(str,"&&","&&");
							
							if(arr[0].equals("100")){	
									queryNumber=arr[0];
							}
								else{
									queryNumber=arr[0].replaceFirst("0", "");
								}
							queryString=arr[1];
							Query query = parser.parse(queryString);
							TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);
							searcher.search(query, collector);
							ScoreDoc[] docs = collector.topDocs().scoreDocs;
							for (int i = 0; i < docs.length; i++) {
								if(rank>1000){
									break;}
							Document doc = searcher.doc(docs[i].doc);
							writer5.println(queryNumber+"  "+"Q0  "+doc.get("DOCNO")+"  "+rank+"  "+docs[i].score+"  "+"run-l-LMDirichlet-long");
							System.out.println(doc.get("DOCNO")+" "+docs[i].score);
							rank=rank+1;
							}
							}
				
						writer5.close();
						
						//short query LMJelinek
						for(String str:title){
							int rank=1;
							IndexSearcher searcher = new IndexSearcher(reader);
							searcher.setSimilarity(new LMJelinekMercerSimilarity(0.7f));
							String[] arr=new String[3];
							String queryNumber,queryString;
							arr=StringUtils.substringsBetween(str,"&&","&&");
							
							if(arr[0].equals("100")){	
									queryNumber=arr[0];
							}
								else{
									queryNumber=arr[0].replaceFirst("0", "");
								}
							queryString=arr[1];
							Query query = parser.parse(queryString);
							TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);
							searcher.search(query, collector);
							ScoreDoc[] docs = collector.topDocs().scoreDocs;
							for (int i = 0; i < docs.length; i++) {
								if(rank>1000){
									break;}
							Document doc = searcher.doc(docs[i].doc);
							writer6.println(queryNumber+"  "+"Q0  "+doc.get("DOCNO")+"  "+rank+"  "+docs[i].score+"  "+"run-l-LMJelinekMercer-short");
							System.out.println(doc.get("DOCNO")+" "+docs[i].score);
							rank=rank+1;
							}
							}
						writer6.close();
						//long query LMJelinek
							for(String str:desc){
								int rank=1;
								IndexSearcher searcher = new IndexSearcher(reader);
								searcher.setSimilarity(new LMJelinekMercerSimilarity(0.7f));
								String[] arr=new String[3];
								String queryNumber,queryString;
								arr=StringUtils.substringsBetween(str,"&&","&&");
								
								if(arr[0].equals("100")){	
										queryNumber=arr[0];
								}
									else{
										queryNumber=arr[0].replaceFirst("0", "");
									}
								queryString=arr[1];
								Query query = parser.parse(queryString);
								TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);
								searcher.search(query, collector);
								ScoreDoc[] docs = collector.topDocs().scoreDocs;
								for (int i = 0; i < docs.length; i++) {
									if(rank>1000){
										break;}
								Document doc = searcher.doc(docs[i].doc);
								writer7.println(queryNumber+"  "+"Q0  "+doc.get("DOCNO")+"  "+rank+"  "+docs[i].score+"  "+"run-l-LMJelinekMercer-long");
								System.out.println(doc.get("DOCNO")+" "+docs[i].score);
								rank=rank+1;
								}
								}
							writer7.close();
				reader.close();
}
}
