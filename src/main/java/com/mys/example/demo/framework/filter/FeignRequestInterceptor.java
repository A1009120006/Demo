//package com.mys.example.demo.filter;
//
//import org.springframework.stereotype.Component;
//
//@Component
//public class FeignRequestInterceptor implements RequestInterceptor {
//
//    @Override
//    public void apply(RequestTemplate requestTemplate) {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//
//        // 设置自定义header
//        // 设置request中的attribute到header以便转发到Feign调用的服务
//        Enumeration<String> reqAttrbuteNames = request.getAttributeNames();
//        if (reqAttrbuteNames != null) {
//            while (reqAttrbuteNames.hasMoreElements()) {
//                String attrName = reqAttrbuteNames.nextElement();
//                if (!"customizedRequestHeader".equalsIgnoreCase(attrName)) {
//                    continue;
//                }
//                Map<String,String> requestHeaderMap = (Map)request.getAttribute(attrName);
//                for (Map.Entry<String, String> entry : requestHeaderMap.entrySet()) {
//                    requestTemplate.header(entry.getKey(), entry.getValue());
//                }
//                break;
//            }
//        }
//    }
//}