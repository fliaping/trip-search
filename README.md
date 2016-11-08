# trip-search

## 毕业设计 -- 旅游垂直搜索引擎

本项目实现了具有位置感知功能的旅游景点垂直搜索引擎。以开源搜索框架Solr和Lucene为基础，以另外一些开源项目例如Heritrix、webmagic、Zookeeper、Ionic、gradle、jetty等为工具，并在相关文档和技术博客的帮助下，完成了整个垂直搜索引擎系统的开发。用到的技术主要有网络爬虫、HTML解析、中文分词、文档索引、空间搜索、RESTful Web Service、Ajax、Hybrid App、容器技术Docker、SolrCloud、集群等。


编写的系列开发文章[《用Solr构建垂直搜索引擎》](https://www.gitbook.com/book/fliaping/create-your-vertical-search-engine-with-solr/details)发布在Gitbook，当然也希望大家去我的博客转转[【总述】用Solr构建垂直搜索引擎](https://blog.fliaping.com/create-your-vertical-search-engine-with-solr/)



本repository中的代码主要是爬虫模块、网页抽取模块、搜索服务、客户端的代码。

```
.
├── ExtractHtml
├── README.md
├── SearchApp
├── SearchService
├── SightSpider
└── SolrHome
```

* `ExtractHtml `  前期单独使用jsoup解析html文件的模块
* `README.md`  readme文档（本文档）
* `SearchApp`  客户端程序的源码模块
* `SearchService`   搜索服务API模块
* `SightSpider`   扩展webmagic进行网页爬取的模块，并包含利用jsoup和XPath进行html解析的模块
* `SolrHome` Solr的Home目录，忽略了索引文件和日志文件，主要是Solr的配置文件。
