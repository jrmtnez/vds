package jrmr.vds.model.ext.word2vec;

import java.util.*;

public class HuffmanTree {

    public static void make(Collection<? extends HuffmanNode> nodes){

        TreeSet<HuffmanNode> tree = new TreeSet<HuffmanNode>(nodes);

        while (tree.size() > 1){
            HuffmanNode left = tree.pollFirst();
            HuffmanNode right = tree.pollFirst();
            HuffmanNode parent = left.merge(right);
            tree.add(parent);
        }
    }

    public static List<HuffmanNode> getPath(HuffmanNode leafNode){

        List<HuffmanNode> nodes = new ArrayList<HuffmanNode>();
        for (HuffmanNode hn = leafNode ; hn != null; hn = hn.getParent()){
            nodes.add(hn);
        }
        Collections.reverse(nodes);

        return nodes;
    }
}
