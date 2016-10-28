package com.it.train.base.index;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

/**
 *  
 */
public class IndexSearch {

	@Test
	public void search() throws Exception {
		// 1、创建查询对象
		// 第一个参数：默认搜索域的域名
		QueryParser parser = new QueryParser(Version.LUCENE_47,"description",
				new StandardAnalyzer());
		// queryparser可以通过查询语法来创建query对象
		Query query = parser.parse("java");

		Directory directory = FSDirectory.open(new File("E:\\Lucene Index"));
		IndexReader reader = DirectoryReader.open(directory);
		// 2、创建搜索器
		IndexSearcher searcher = new IndexSearcher(reader);

		// 3、通过searcher的search方法来操作query对象进行查询
		// 第二个参数：要查询的条数，匹配度高的10条
		TopDocs topDocs = searcher.search(query, 10);
		// 匹配出的所有的记录总数
		int count = topDocs.totalHits;

		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		System.out.println("查询的文档总数为：" + count);
		for (ScoreDoc scoreDoc : scoreDocs) {
			// 得到document的id
			int docId = scoreDoc.doc;
			Document doc = searcher.doc(docId);
			System.out.println("图书ID：" + doc.get("id"));
			System.out.println("图书名称：" + doc.get("name"));
			System.out.println("图书价格：" + doc.get("price"));
			System.out.println("图书图片地址：" + doc.get("pic"));
			// System.out.println("图书描述："+ doc.get("description"));
		}

		// 关闭reader
		reader.close();
	}

	private void doSearch(Query query) {
		try {
			Directory directory = FSDirectory.open(new File(
					"E:\\11-index\\hm17"));
			IndexReader reader = DirectoryReader.open(directory);
			// 2、创建搜索器
			IndexSearcher searcher = new IndexSearcher(reader);

			// 3、通过searcher的search方法来操作query对象进行查询
			// 第二个参数：要查询的条数，匹配度高的10条
			TopDocs topDocs = searcher.search(query, 10);
			// 匹配出的所有的记录总数
			int count = topDocs.totalHits;

			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			System.out.println("查询的文档总数为：" + count);
			for (ScoreDoc scoreDoc : scoreDocs) {
				// 得到document的id
				int docId = scoreDoc.doc;
				Document doc = searcher.doc(docId);
				System.out.println("图书ID：" + doc.get("id"));
				System.out.println("图书名称：" + doc.get("name"));
				System.out.println("图书价格：" + doc.get("price"));
				System.out.println("图书图片地址：" + doc.get("pic"));
				// System.out.println("图书描述："+ doc.get("description"));
			}

			// 关闭reader
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void termQueryTest() {

		// 创建TermQuery
		Query query = new TermQuery(new Term("description", "java"));

		doSearch(query);
	}

	@Test
	public void numericRangeQueryTest() {
		// 创建NumericRangeQuery
		// 参数：域的名称、范围最小值、范围最大值、是否包含最小值、是否包含最大值
		Query query = NumericRangeQuery.newFloatRange("price", 10f, 56f, true,
				true);

		doSearch(query);
	}

	@Test
	public void booleanQueryTest() {
		// 创建booleanQuery
		BooleanQuery query = new BooleanQuery();
		// 创建TermQuery
		Query q1 = new TermQuery(new Term("description", "java"));

		Query q2 = NumericRangeQuery.newFloatRange("price", 10f, 56f, true,
				true);

		// 组合关系代表的意思如下:
		// 1、MUST和MUST表示“与”的关系，即“交集”。
		// 2、MUST和MUST_NOT前者包含后者不包含。
		// 3、MUST_NOT和MUST_NOT没意义
		// 4、SHOULD与MUST表示MUST，SHOULD失去意义；
		// 5、SHOUlD与MUST_NOT相当于MUST与MUST_NOT。
		// 6、SHOULD与SHOULD表示“或”的概念。

		query.add(q1, Occur.SHOULD);
		query.add(q2, Occur.MUST);

		doSearch(query);
	}

	@Test
	public void multiFieldQueryParserTest() throws Exception {
		// 创建查询对象
		// 参数：默认搜索的域的集合、分词器
		String[] fields = { "name", "description" };
		MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_47,fields,
				new StandardAnalyzer());
		// 通过parser可以指定查询表达式来进行创建查询对象
		Query query = parser.parse("java");
		System.out.println(query);

		doSearch(query);
	}

	@Test
	public void multiFieldQueryParserTest2() throws Exception {
		// 创建查询对象
		// 参数：默认搜索的域的集合、分词器
		String[] fields = { "name", "description" };

		// 通过map集合对field域进行加权
		Map<String, Float> boosts = new HashMap<>();
		boosts.put("name", 100f);
		MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_47,fields,
				new StandardAnalyzer(), boosts);
		// 通过parser可以指定查询表达式来进行创建查询对象
		Query query = parser.parse("java");
		System.out.println(query);

		doSearch(query);
	}
}
