# 基于字典树的前缀匹配

# 1. 简介
适合数据量较小的简单前缀搜索。

* 支持拼音搜索，不支持中文和拼音混合搜索。
* 支持对象的存储。

# 2. 使用

* 参考Test

* 例子 

```java
// 初始化单词
List<String> words = ImmutableList.of("毒药之灵", "金坷垃", "电学", "电视机", "干杯", "点点滴滴");
SearchTrie<String> searchTrie = new SearchTrie<>(true); // 支持pinyin
words.forEach(it -> searchTrie.put(it, it));
// 搜索
searchTrie.search("dian", -1);
```

* 结果

```
[电学, 电视机, 点点滴滴]
```
