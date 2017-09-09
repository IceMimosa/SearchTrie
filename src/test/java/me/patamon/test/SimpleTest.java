package me.patamon.test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import me.patamon.SearchTrie;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Desc: 简单测试
 * <p>
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/9/9
 */
public class SimpleTest {

    private SearchTrie<String> searchTrie;
    private List<String> lines;

    @Before
    public void setup() throws IOException {
        searchTrie = new SearchTrie<>(true);
        URL url = Resources.getResource("taobao.txt");
        lines = Files.readLines(new File(url.getFile()), Charsets.UTF_8)
                .stream().map(String::trim).collect(Collectors.toList());
    }

    /**
     * 简单测试
     */
    @Test
    public void simpleTest() {
        // 索引单词
        lines.forEach(it -> searchTrie.put(it, it));

        // 搜索
        List<String> results = searchTrie.search("电脑", 10);
        results.forEach(System.out::println);
    }
}
