package com.zxx.tinycat.core.http.handler;


import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RestfulPathTreeRoot {
    // key: GET POST DELETE PUT ,  value: node
    List<Pair<String, RestFulPathTreeNode>> nodePairs = new ArrayList<>();

    public HttpGeneralHandlerInterface search(String method, String url, Map<String, String> restParameter) {
        List<String> path = new ArrayList<>(Arrays.asList(url.split("/")));
        for (final Pair<String, RestFulPathTreeNode> nodePair : nodePairs) {
            if (nodePair.getKey().equals(method)) {
                HttpGeneralHandlerInterface search = nodePair.getValue().search(path, 0, restParameter);
                if (search != null) {
                    return search;
                }
            }
        }
        return null;
    }


    private boolean isRestfulParam(String pathPart) {
        if (pathPart.startsWith("{") && pathPart.endsWith("}")) {
            return true;
        }
        return false;
    }

    public void registerNode(String method, String url, HttpGeneralHandlerInterface handler) throws Exception {
        RestFulPathTreeNode root = getOrCreateRoot(method);
        String[] parts = url.split("/");
        RestFulPathTreeNode currentNode = root;

        for (String part : parts) {
            if (!part.isEmpty()) {
                boolean isParam = isRestfulParam(part);
                String nodeValue = isParam ? part.substring(1, part.length() - 1) : part;

                RestFulPathTreeNode foundNode = null;
                for (RestFulPathTreeNode subnode : currentNode.subnodes) {
                    if (subnode.isParam == isParam && (isParam ? subnode.paramName.equals(nodeValue) : subnode.node.equals(nodeValue))) {
                        foundNode = subnode;
                        break;
                    }
                }

                if (foundNode == null) {
                    foundNode = new RestFulPathTreeNode();
                    foundNode.isParam = isParam;
                    foundNode.paramName = isParam ? nodeValue : null;
                    foundNode.node = !isParam ? nodeValue : null;
                    currentNode.subnodes.add(foundNode);
                }

                currentNode = foundNode;
            }
        }

        // 如果末端节点已经有一个处理器，则抛出异常
        if (currentNode.handler != null) {
            throw new Exception("The URL '" + url + "' is already registered with a different handler.");
        }

        // 为末端节点设置处理器
        currentNode.handler = handler;
    }

    private RestFulPathTreeNode getOrCreateRoot(String method) {
        for (Pair<String, RestFulPathTreeNode> pair : nodePairs) {
            if (pair.getKey().equals(method)) {
                return pair.getValue();
            }
        }
        // 如果没有找到对应的根节点，则创建一个新的根节点并添加到列表中
        RestFulPathTreeNode root = new RestFulPathTreeNode();
        root.setIsRoot(true);
        nodePairs.add(new Pair<>(method, root));
        return root;
    }
}
