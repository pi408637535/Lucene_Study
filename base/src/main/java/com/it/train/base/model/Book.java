package com.it.train.base.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by wode4 on 2016/10/28.
 */
@Setter
@Getter
public class Book
{
    // 图书ID
    private Integer id;
    // 图书名称
    private String name;
    // 图书价格
    private Float price;
    // 图书图片
    private String pic;
    // 图书描述
    private String description;
}
