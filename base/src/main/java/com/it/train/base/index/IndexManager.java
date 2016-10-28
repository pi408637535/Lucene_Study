package com.it.train.base.index;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import com.it.train.base.dao.BookDao;
import com.it.train.base.dao.impl.BookDaoImpl;
import com.it.train.base.model.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;



/**
 *  
 */
public class IndexManager {

	@Test
	public void createIndex() throws Exception {

		// 1、采集数据
		BookDao dao = new BookDaoImpl();
		List<Book> list = dao.queryBookList();
		// 2、创建document对象
		List<Document> docList = new ArrayList<Document>();
		Document doc;
		for (Book book : list) {
			doc = new Document();
			// 创建Field
			// 参数：Field域的域名、域的内容、是否存储
			/**
			 * // 图书ID Field idField = new TextField("id",
			 * book.getId().toString(), Store.YES);
			 * 
			 * // 图书名称 Field nameField = new TextField("name", book.getName(),
			 * Store.YES);
			 * 
			 * // 图书价格 Field priceField = new TextField("price", book.getPrice()
			 * .toString(), Store.YES);
			 * 
			 * // 图书图片地址 Field picField = new TextField("pic", book.getPic(),
			 * Store.YES);
			 * 
			 * // 图书描述 Field descriptionField = new TextField("description",
			 * book.getDescription(), Store.YES);
			 **/

			// 图书ID
			// 不分词、不索引、存储 StoredField
			Field idField = new StoredField("id", book.getId().toString());

			// 图书名称
			// 分词、索引、存储 TextField
			Field nameField = new TextField("name", book.getName(), Store.YES);

			// 图书价格
			// 分词、索引、存储 float类型 FloatField
			Field priceField = new FloatField("price", book.getPrice(),
					Store.YES);

			// 图书图片地址
			// 不分词、不索引、存储
			Field picField = new StoredField("pic", book.getPic());

			// 图书描述
			// 分词、索引、不存储
			Field descriptionField = new TextField("description",
					book.getDescription(), Store.NO);

			//在创建索引时设置加权值
			if(book.getId() == 4){
				descriptionField.setBoost(1000f);
			}
			doc.add(idField);
			doc.add(nameField);
			doc.add(priceField);
			doc.add(picField);
			doc.add(descriptionField);

			docList.add(doc);
		}
		// 3、创建Directory对象
		// 通过open方法打开索引库的地址所在的索引库
		Directory directory = FSDirectory.open(new File("E:\\Lucene Index"));
		// 4、创建IndexWriter对象
		// 创建分词器，使用的是标准分词器standardAnalyzer
//		Analyzer analyzer = new StandardAnalyzer();
		//创建ikanalyzer中文分词器
		Analyzer analyzer = new IKAnalyzer();
		
		IndexWriterConfig cfg = new IndexWriterConfig(Version.LUCENE_47,
				analyzer);
		IndexWriter writer = new IndexWriter(directory, cfg);
		// 5、通过writer对象进行索引的增删改操作
		for (Document document : docList) {
			writer.addDocument(document);
		}
		// 6、关闭writer
		writer.close();
	}

	private IndexWriter getWriter() {
		// 3、创建Directory对象
		// 通过open方法打开索引库的地址所在的索引库
		try {
			Directory directory = FSDirectory.open(new File(
					"E:\\Lucene Index"));
			// 4、创建IndexWriter对象
			// 创建分词器，使用的是标准分词器standardAnalyzer
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig cfg = new IndexWriterConfig(
					Version.LUCENE_47, analyzer);
			return new IndexWriter(directory, cfg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Test
	public void deleteIndex() throws Exception {
		IndexWriter writer = getWriter();

		// 通过writer根据条件删除
		// term的构造参数：第一个是域名、第二个是词
		writer.deleteDocuments(new Term("name", "java"));

		// 关闭writer
		writer.close();
	}

	@Test
	public void deleteIndex2() throws Exception {
		IndexWriter writer = getWriter();

		// 通过writer来全部删除（慎用！！！）
		writer.deleteAll();

		// 关闭writer
		writer.close();
	}

	@Test
	public void createIndex2() throws Exception {
		IndexWriter writer = getWriter();

		//修改索引就是先根据term参数删除索引，再根据doc参数添加新索引
		Document doc = new Document();
		doc.add(new TextField("name","java 编程思想者",Store.YES));

		writer.addDocument(doc);
		// 关闭writer
		writer.close();
	}
	
	@Test
	public void updateIndex() throws Exception {
		IndexWriter writer = getWriter();

		//修改索引就是先根据term参数删除索引，再根据doc参数添加新索引
		Document doc = new Document();
		doc.add(new TextField("name","java 编程思想者02",Store.YES));

		writer.updateDocument(new Term("name", "java"), doc);
		// 关闭writer
		writer.close();
	}
}
