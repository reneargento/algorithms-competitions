package com.br.algs.reference.algorithms.search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by rene on 21/04/17.
 */
//This implementation is currently for directed graphs, but with 2 simple modifications can be used for undirected graphs
public class DijkstraShortestPath {

    private class Graph {

        private class Vertex {
            int id;
            boolean processed;
            LinkedList<Edge> edgesAssociated;
        }

        private class Edge {
            Vertex vertex1;
            Vertex vertex2;
            int length;
        }

        HashMap<Integer, Vertex> vertices;
        LinkedList<Edge> edges;

        public Graph() {
            vertices = new HashMap<>();
            edges = new LinkedList<>();
        }

        //O(1)
        public void addVertex(int vertexId) {
            Vertex vertex = new Vertex();
            vertex.id = vertexId;
            vertex.processed = false;
            vertex.edgesAssociated = new LinkedList<>();

            vertices.put(vertexId, vertex);
        }

        //O(1)
        public void addEdge(int vertexId1, int vertexId2, int length) {
            if(length < 0) {
                throw new UnsupportedOperationException("Edge length cannot be negative");
            }

            if(vertices.get(vertexId1) == null) {
                addVertex(vertexId1);
            }
            if(vertices.get(vertexId2) == null) {
                addVertex(vertexId2);
            }

            Vertex vertex1 = vertices.get(vertexId1);
            Vertex vertex2 = vertices.get(vertexId2);

            Edge edge = new Edge();
            edge.vertex1 = vertex1;
            edge.vertex2 = vertex2;
            edge.length = length;

            edges.add(edge);

            vertices.get(vertexId1).edgesAssociated.add(edge);
            vertices.get(vertexId2).edgesAssociated.add(edge);
        }

        //O(1)
        public int getVerticesCount() {
            return vertices.size();
        }

        //O(1)
        public void setVertexProcessed(int vertexId) {
            vertices.get(vertexId).processed = true;
        }

        //O(V)
        public void clearProcessedVertices() {
            for(int vertexId : vertices.keySet()) {
                vertices.get(vertexId).processed = false;
            }
        }
    }

    //By definition
    private static final int UNCOMPUTED_DISTANCE = 1000000;

    private static int[] computedShortestPathDistances;

    //O(n * log(m))
    private static void computeShortestPath(Graph graph, int sourceVertex) {

        int verticesCount = graph.getVerticesCount();

        computedShortestPathDistances = new int[verticesCount + 1];

        setDefaultDistances();

        //Distance from S to itself is 0
        computedShortestPathDistances[sourceVertex] = 0;
        graph.vertices.get(sourceVertex).processed = true;

        //Using Java's priority queue
        PriorityQueue<Graph.Edge> heap = new PriorityQueue<Graph.Edge>(10, new Comparator<Graph.Edge>() {
            @Override
            public int compare(Graph.Edge edge1, Graph.Edge edge2) {
                return edge1.length - edge2.length;
            }
        });

        //Add edges associated to the first vertex to the heap
        for(Graph.Edge edge : graph.vertices.get(sourceVertex).edgesAssociated) {
            //Directed graph
            if(edge.vertex1.id == sourceVertex) {
                heap.add(edge);
            }

            //Undirected graph
            //heap.add(edge);
        }

        while (heap.size() > 0) {
            Graph.Edge edge = heap.poll();

            if(!edge.vertex1.processed) {
                graph.setVertexProcessed(edge.vertex1.id);
                computeAndAddEdgeToHeap(graph, heap, edge, edge.vertex1.id);
            } else if(!edge.vertex2.processed) {
                graph.setVertexProcessed(edge.vertex2.id);
                computeAndAddEdgeToHeap(graph, heap, edge, edge.vertex2.id);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void computeAndAddEdgeToHeap(Graph graph, PriorityQueue heap, Graph.Edge edge, int vertexId) {
        computedShortestPathDistances[vertexId] = edge.length;

        for(Graph.Edge newEdge : graph.vertices.get(vertexId).edgesAssociated) {
            //if (!newEdge.vertex1.processed || !newEdge.vertex2.processed) { //Undirected graph
            if (!newEdge.vertex2.processed) {
                newEdge.length = edge.length + newEdge.length;
                heap.add(newEdge);
            }
        }
    }

    private static void setDefaultDistances() {
        for (int i=0; i < computedShortestPathDistances.length; i++) {
            computedShortestPathDistances[i] = UNCOMPUTED_DISTANCE;
        }
    }

}
