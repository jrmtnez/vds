package jrmr.vds.model.text.graph.impl;

import org.apache.commons.collections15.Factory;

@SuppressWarnings("rawtypes")
public class VertexFactory implements Factory {
    private int n = 0;
    public Vertex create() {
        return (new Vertex(n++));
    }
}

