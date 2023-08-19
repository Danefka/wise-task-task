package ru.leti.wise.task.task.service.graph;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.GraphGrpc;
import ru.leti.wise.task.graph.GraphGrpc.GenerateGraphRequest;
import ru.leti.wise.task.graph.GraphOuterClass.Graph;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class GraphGrpcService {

    private final GraphStubHolder graphStubHolder;

    public Graph getGraph(int edgeCount, int vertexCount, boolean isDirect) {
        var request = GenerateGraphRequest.newBuilder()
                .setEdgeCount(edgeCount)
                .setVertexCount(vertexCount)
                .setIsDirect(isDirect)
                .build();

        return graphStubHolder.get().generateRandomGraph(request).getGraph();
    }

    public Graph createGraph(Graph graph) {
        var request = GraphGrpc.CreateGraphRequest.newBuilder()
                .setGraph(graph)
                .build();

        return graphStubHolder.get().createGraph(request).getGraph();
    }

    public Graph getGraphById(UUID id) {
        var request = GraphGrpc.GetGraphByIdRequest.newBuilder()
                .setId(id.toString())
                .build();

        return graphStubHolder.get().getGraphById(request).getGraph();
    }
}
