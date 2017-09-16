package me.patamon.test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import me.patamon.SearchTrie;
import me.patamon.utils.StringUtil;
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

        //> 输出:
        /*
        电脑元件/零配件
        电脑元件/零配件（将删除）
        电脑护目镜
        电脑组装与维修
        电脑切换器/分配器
        电脑周边
        电脑储物组合
        电脑罩/电脑套/电脑防尘罩
        电脑IT培训
        电脑+电视插座
        */
    }


    /**
     * 拼音测试
     */
    @Test
    public void pinyinTest() {
        // 索引单词, 并且忽略大小写
        lines.forEach(it -> searchTrie.put(StringUtil.lowerCase(it), it));

        // 搜索
        String prefix = "Dian";
        prefix = StringUtil.removeSpecialChars(prefix); // 去除特殊字符(如果存在的话)
        List<String> results = searchTrie.search(StringUtil.lowerCase(prefix), -1);
        results.forEach(System.out::println);
    }

}
