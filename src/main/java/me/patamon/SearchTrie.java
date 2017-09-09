package me.patamon;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.patamon.utils.PinyinUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Desc: 字典树, 用于简单前缀搜索. (非线程安全)
 *  主要功能:
 *      1. 中文前缀匹配. 如搜 "保险", 结果为["保险", "保险箱", "保险柜", ...]
 *      2. 中文拼音前缀匹配. 如搜 "baoxian" 或者 "baoxi" 或者 "bxian", 结果为["保险", "保险箱", "保险柜", ...]
 *      3. 中文拼音首字母前缀匹配. 如搜 "bx", 结果为["保险", "保险箱", "保险柜", ...]
 *      4. 中文和拼音混合前缀匹配 (未实现)
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/8/27
 */
public class SearchTrie<T> implements Serializable {

    /**
     * 字典树的根
     */
    @Getter
    private TrieNode<T> root;

    /**
     * 统计所有单词的数量
     */
    @Getter
    private int wordCount;

    /**
     * 是否使用拼音搜索
     */
    @Getter
    private boolean pinyin;

    public SearchTrie() {
        this(false);
    }

    public SearchTrie(boolean pinyin) {
        this.root = new TrieNode<>();
        this.pinyin = pinyin;
    }

    /**
     * 将一个单词加入到数中
     * @param word      单词
     * @param object    额外额对象
     */
    public void put(String word, T object) {
        if (Strings.isNullOrEmpty(word)) {
            return;
        }
        word = word.trim();

        TrieNode<T> parent = this.root;
        Map<Character, TrieNode<T>> children;
        TrieNode<T> child;
        for (int i = 0; i < word.length(); i++) {
            children = parent.getChildren();
            if (children == null) {
                children = Maps.newHashMap();
                parent.setChildren(children);
            }

            char c = word.charAt(i);

            child = children.get(c);
            boolean newChild = false;
            if (child == null) {
                newChild = true;
                child = new TrieNode<>(parent, c, this.pinyin);

                children.put(c, child);
            }

            if (word.length() == i + 1) {
                child.setTail(true);
                child.increment();
                // 遍历到最后以后, 且是新增的节点, 总单词数量加一
                if (newChild) {
                    wordCount++;
                }
                child.setObject(object);
            }
            parent = child;
        }
    }

    /**
     * 搜索所有是节点
     * @param pres   前缀搜索词
     * @param limit  结果数量, -1表示全部
     */
    public List<TrieNode<T>> searchTrieNodes(String pres, int limit) {
        List<TrieNode<T>> nodes = Lists.newArrayList();
        if (Strings.isNullOrEmpty(pres)) {
            return nodes;
        }
        pres = pres.trim();
        // 记录所有匹配到(父)结尾
        Set<TrieNode<T>> parents = Sets.newHashSet();

        // 从单词本身搜索
        TrieNode<T> fromWord = getFromWord(pres);
        if (fromWord != null) {
            parents.add(fromWord);
        }

        // 单词匹配不上, 使用单词的拼音进行匹配
        if (this.pinyin && fromWord == null) {
            parents.addAll(getFromPinyinFirstCode(pres));
            parents.addAll(getFromPinyin(pres));
        }

        // 递归所有匹配到的(父)节点
        for (TrieNode<T> p : parents) {
            if (p.isTail()) {
                nodes.add(p);
            }
            Map<Character, TrieNode<T>> children = p.getChildren();
            if (children == null) {
                continue;
            }
            for (TrieNode<T> node : children.values()) {
                getNext(nodes, node, limit);
            }
        }
        return nodes;
    }

    private void getNext(List<TrieNode<T>> nodes, TrieNode<T> node, int limit) {
        if (node == null || (limit != -1 && nodes.size() >= limit)) {
            return;
        }

        if (node.isTail()) {
            nodes.add(node);
        }
        Map<Character, TrieNode<T>> children = node.getChildren();
        if (children == null) {
            return;
        }

        for (TrieNode<T> nextNode : children.values()) {
            if (limit != -1 && nodes.size() >= limit) {
                break;
            }
            getNext(nodes, nextNode, limit);
        }
    }


    // 从单词本身做前缀搜索
    private TrieNode<T> getFromWord(String pres) {
        TrieNode<T> parent = this.root;
        boolean end = false;

        for (int i = 0; i < pres.length(); i++) {
            char pre = pres.charAt(i);

            Map<Character, TrieNode<T>> children = parent.getChildren();
            if (children == null) {
                break;
            } else {
                TrieNode<T> child = children.get(pre);
                if (child == null) {
                    break;
                }
                parent = child;
            }
            // 匹配到结果
            if (pres.length() == i + 1) {
                end = true;
            }
        }
        // 如果未到搜索词结尾, 表示匹配不上
        if (!end) {
            return null;
        }
        return parent;
    }


    // 从单词拼音首字母进行前缀匹配
    private List<TrieNode<T>> getFromPinyinFirstCode(String pres) {
        List<TrieNode<T>> parents = Lists.newArrayList();
        parents.add(this.root);

        boolean end = false;
        for (int i = 0; i < pres.length(); i++) {
            char pre = pres.charAt(i);

            List<TrieNode<T>> ps = Lists.newArrayList();
            for (TrieNode<T> parent : parents) {
                Map<Character, TrieNode<T>> children = parent.getChildren();
                if (children == null) {
                    break;
                } else {
                    for(TrieNode<T> child : children.values()) {
                        if (child == null || child.getPy() == null) {
                            continue;
                        }
                        if (child.getPy().contains(pre)) {
                            ps.add(child);
                        }
                    }
                }
            }
            // 如果所有的(父)节点都没有匹配上的子节点, 返回
            parents = ps;
            if (ps.isEmpty()) {
                break;
            }
            // 匹配到搜索词结尾
            if (pres.length() == i + 1) {
                end = true;
            }
        }

        // 如果到到搜索词结尾, 表示匹配不上
        if (!end) {
            return Lists.newArrayList();
        }
        return parents;
    }

    // 从单词拼音进行匹配
    private List<TrieNode<T>> getFromPinyin(String pres) {
        List<TrieNode<T>> nodes = Lists.newArrayList();
        walkChild(nodes, this.root, pres);

        return nodes;
    }

    // 递归匹配子节点
    private void walkChild(List<TrieNode<T>> nodes, TrieNode<T> node, String pres) {
        if ("".equals(pres)) { // 匹配到搜索词结尾
            nodes.add(node);
            return;
        }
        Map<Character, TrieNode<T>> children = node.getChildren();
        if (children == null) {
            return;
        }

        for (TrieNode<T> child : children.values()) {
            if (child == null || child.getPinyin().isEmpty()) {
                continue;
            }

            String pinyin = findMaxPrefix(pres, child.getPinyin());
            if ("".equals(pinyin)) {
                continue;
            }

            walkChild(nodes, child, pres.substring(pinyin.length(), pres.length()));
        }
    }

    // 获取最长前缀匹配串
    // Strings#commonPrefix
    private static String findMaxPrefix(String source, List<String> targets) {
        String maxPrefix = "";
        for (String target : targets) {
            int minLength = Math.min(source.length(), target.length());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < minLength; i++) {
                if (source.charAt(i) == target.charAt(i)) {
                    sb.append(source.charAt(i));
                } else {
                    break;
                }
            }
            if (maxPrefix.length() < sb.length()) {
                maxPrefix = sb.toString();
            }
        }
        return maxPrefix;
    }


    /**
     * 搜索所有的前缀相关的节点, 并返回 {@link SearchTrie#put(String, Object)} 存入的 Object 对象
     * @param pres   前缀搜索词
     * @param limit  结果的数量. -1表示返回所有
     */
    public List<T> search(String pres, int limit) {
        return searchTrieNodes(pres, limit).stream().map(TrieNode::getObject).collect(Collectors.toList());
    }

    /**
     * 字典树的节点
     */
    @Data
    @NoArgsConstructor
    @ToString(of = {"value", "object"})
    @EqualsAndHashCode(of = {"value", "object"})
    static class TrieNode<T> implements Serializable {

        /**
         * 父节点
         */
        TrieNode<T> parent = null;

        /**
         * 子节点
         */
        Map<Character, TrieNode<T>> children;

        /**
         * 存储的字母值
         */
        char value;

        /**
         * 是否是单词节点(即末尾)
         */
        boolean tail;

        /**
         * 存储字母值的拼音列表, 可能是多音字
         */
        List<String> pinyin;

        /**
         * 存储字母值的拼音首字母列表, 可能是多音字
         */
        List<Character> py;

        /**
         * 该单词的数量
         */
        int count;

        /**
         * 存储的额外对象
         */
        T object;

        public TrieNode(TrieNode<T> parent, char value, boolean toPinyin) {
            this.parent = parent;
            this.value = value;
            if (toPinyin) {
                pinyin = PinyinUtil.getCode(value);
                py = PinyinUtil.getFirstCode(value);
            }
        }

        public void increment() {
            this.count++;
        }
    }

}
