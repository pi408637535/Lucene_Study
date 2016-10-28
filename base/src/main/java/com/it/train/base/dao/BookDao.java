package com.it.train.base.dao;

import com.it.train.base.model.Book;

import java.util.List;

public interface BookDao {

	public List<Book> queryBookList() throws Exception;
}
