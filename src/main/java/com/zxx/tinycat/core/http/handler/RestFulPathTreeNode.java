package com.zxx.tinycat.core.http.handler;


import com.zxx.tinycat.core.http.handler.HttpGeneralHandlerInterface;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class RestFulPathTreeNode {
    Boolean isRoot = false;
    //是否是restful风格参数
    Boolean isParam;
    //参数名字
    String paramName;
    // /api/v1/user/123/info
    // /api/v1/user/{userId}/info userInfoHandler
    // /api/v1/order/{orderId}/info  orderInfoHandler
    // 节点名字 比如api 或者 v1 或者 order
    String node;
    List<RestFulPathTreeNode> subnodes = new ArrayList<>();
    HttpGeneralHandlerInterface handler;
    public HttpGeneralHandlerInterface search(List<String> path, int index, Map<String, Object> restParameter) {
        if (!isRoot) {
            if (isParam) {
                restParameter.put(paramName, path.get(index));
            }

            if (node != null && !node.equals(path.get(index))) {
                return null;
            }
            if (subnodes == null || subnodes.isEmpty()) {
                if (index == path.size() -1) {
                    return handler;
                }
                return null;
            }
        }
        for (final RestFulPathTreeNode subnode : subnodes) {
            final HttpGeneralHandlerInterface h = subnode.search(path, index + 1, restParameter);
            if (h != null) {
                return h;
            }
        }
        return null;
    }
}
