package jrmr.vds.model.text.graph.impl;

import org.apache.commons.collections15.Factory;

@SuppressWarnings("rawtypes")
public class EdgeFactory implements Factory {
    private int e = 0;
    public Edge create() {
        return (new Edge(e++));
    }
}
