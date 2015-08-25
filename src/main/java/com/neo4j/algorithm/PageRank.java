package com.neo4j.algorithm;

import com.neo4j.engine.algointerface.VertexAlgorithm;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yangyi on 2015/8/19.
 */
public class PageRank implements VertexAlgorithm{
    protected final String attName = "PageRank";
    protected Map<Long, Double> result = new HashMap<>();
    protected long nodeCount = 0;
    protected double dampingFactor = 0.85;
    protected long stablePart = 0L;

    public PageRank(GraphDatabaseService g ){
        try(Transaction tx = g.beginTx()){
            for(Node n : GlobalGraphOperations.at(g).getAllNodes()){
                nodeCount ++;
            }
            tx.success();
        }
    }

    @Override
    public void init(Node node) {
        node.setProperty(attName, 1.0 / this.nodeCount);
    }

    @Override
    public void apply(Node node) {
        double rank = 0.0;
        for(Relationship relationship : node.getRelationships()){
            Node otherNode = relationship.getOtherNode(node);
            double nextRank = (double)otherNode.getProperty(attName);
            rank += nextRank / otherNode.getDegree();
        }
        rank *= dampingFactor;
        node.setProperty(attName, rank);
    }

    @Override
    public void collectResult(Node node) {
        this.result.put(node.getId(), (double)node.getProperty(attName));
    }

    @Override
    public int getMaxIterations() {
        return 32;
    }

    @Override
    public long getStablePart() {
        return stablePart;
    }

    @Override
    public void reSetStablePart() {
        this.stablePart = 0L;
    }

    @Override
    public String getAttributeName() {
        return attName;
    }

    @Override
    public String getName() {
        return attName;
    }

    @Override
    public Object getResult() {
        return result;
    }
}
