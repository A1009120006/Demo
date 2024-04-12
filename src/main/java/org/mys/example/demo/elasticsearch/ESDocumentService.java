package org.mys.example.demo.elasticsearch;

import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
 
/**
 * 公用es服务接口
 */
public interface ESDocumentService {
 
    /**
     * 新增一个文档，此种方式若发现没有索引，会自动创建一个索引
     * @param idxName 索引名
     * @param idxId 索引id
     * @param document 文档对象
     */
    <T> IndexResponse createByFluentDSL(String idxName, String idxId, T document) throws Exception;
 
    /**
     * 新增一个文档，此种方式若发现没有索引，会自动创建一个索引
     * @param idxName 索引名
     * @param idxId 索引id
     * @param document 文档对象
     */
    <T> IndexResponse createByBuilderPattern(String idxName, String idxId, T document) throws Exception;
 
    /**
     * 用JSON字符串创建文档，此种方式若发现没有索引，会自动创建一个索引
     * @param idxName 索引名
     * @param idxId 索引id
     * @param jsonContent json字符串
     */
    IndexResponse createByJson(String idxName, String idxId, String jsonContent) throws Exception;
 
 
    /**
     * 异步新增文档，此种方式若发现没有索引，会自动创建一个索引
     * @param idxName 索引名
     * @param idxId 索引id
     * @param document 文档
     * @param action 操作
     */
    <T> void createAsync(String idxName, String idxId, T document, BiConsumer<IndexResponse, Throwable> action);
 
    /**
     * 批量增加文档
     * @param idxName 索引名
     * @param documents 要增加的对象集合
     * @return 批量操作的结果
     */
    <T> BulkResponse bulkCreate(String idxName, List<T> documents) throws Exception;
 
 
    /**
     * 根据文档id查找文档
     * @param idxName 索引名
     * @param docId 文档id
     * @return Object类型的查找结果
     */
    <T> T getById(String idxName, String docId ,Class<T> tClass) throws IOException;
 
    /**
     *
     * @param idxName 索引名称
     * @param docId 文档id
     * @param tClass 返回的类型
     * @param map 修改内容的map
     */
    <T> Result updateById(String idxName, String docId, Class<T> tClass, Map<String,Object> map) throws IOException;
 
    /**
     * 根据文档id查找文档，返回类型是ObjectNode
     * @param idxName 索引名
     * @param docId 文档id
     * @return ObjectNode类型的查找结果
     */
    ObjectNode getObjectNodeById(String idxName, String docId) throws IOException;
 
    /**
     * 根据文档id删除文档
     * @param idxName 索引名
     * @param docId 文档id
     * @return Object类型的查找结果
     */
    Boolean deleteById(String idxName, String docId) throws IOException;
 
    /**
     * 批量删除文档
     * @param idxName 索引名
     * @param docIds 要删除的文档id集合
     */
    BulkResponse bulkDeleteByIds(String idxName, List<String> docIds) throws Exception;
 
}