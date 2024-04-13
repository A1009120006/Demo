package com.mys.example.demo.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhrasePrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
 
@Slf4j
@Service
@RequiredArgsConstructor
public class ESDocumentServiceImpl implements ESDocumentService {
 
    //同步客户端
    private final ElasticsearchClient elasticsearchClient;
 
    // 异步客户端
    private final ElasticsearchAsyncClient elasticsearchAsyncClient;
 
    @Override
    public <T> IndexResponse createByFluentDSL(String idxName, String idxId, T document) throws Exception {
        return elasticsearchClient.index(idx -> idx
                .index(idxName)
                .id(idxId)
                .document(document));
    }
 
    /**
     * BuilderPattern 方式创建文档
     * @param idxName 索引名
     * @param idxId 索引id
     * @param document 文档对象
     */
    @Override
    public <T> IndexResponse createByBuilderPattern(String idxName, String idxId, T  document) throws Exception {
        IndexRequest.Builder<Object> indexReqBuilder = new IndexRequest.Builder<>();
        indexReqBuilder.index(idxName);
        indexReqBuilder.id(idxId);
        indexReqBuilder.document(document);
        return elasticsearchClient.index(indexReqBuilder.build());
    }
 
    /**
     * json方式创建文档
     * @param idxName 索引名
     * @param idxId 索引id
     * @param jsonContent json字符串
     */
    @Override
    public IndexResponse createByJson(String idxName, String idxId, String jsonContent) throws Exception {
        return elasticsearchClient.index(i -> i
                .index(idxName)
                .id(idxId)
                .withJson(new StringReader(jsonContent))
        );
    }
 
    /**
     *  异步方式创建文档
     * @param idxName 索引名
     * @param idxId 索引id
     * @param document 文档
     * @param action 操作
     */
    @Override
    public <T> void createAsync(String idxName, String idxId, T document, BiConsumer<IndexResponse, Throwable> action) {
        elasticsearchAsyncClient.index(idx -> idx
                .index(idxName)
                .id(idxId)
                .document(document)
        ).whenComplete(action);
    }
 
    /**
     * 批量方式创建文档
     * @param idxName 索引名
     * @param documents 要增加的对象集合
     */
    @Override
    public <T> BulkResponse bulkCreate(String idxName, List<T> documents) throws Exception {
         BulkRequest.Builder br = new BulkRequest.Builder();
         documents.forEach(document ->{
                    ESDocument esDocument = (ESDocument) document;
                    br.operations(op -> op.index(idx -> idx
                            .index(idxName)
                            .id(esDocument.getId().toString())
                            .document(esDocument)));
                    });
        return elasticsearchClient.bulk(br.build());
    }
 
    /**
     *
     * @param idxName 索引名称
     * @param docId 文档id
     * @param tClass 返回的类型
     * @param map 修改内容的map
     * Map<String, Object> map = new HashMap<>();
     *         map.put("age", 35);
     * 把年龄改成35
     */
    @Override
    public <T> Result updateById(String idxName, String docId, Class<T> tClass, Map<String,Object> map) throws IOException {
        UpdateResponse<T> response = elasticsearchClient.update(e -> e.index(idxName).id(docId).doc(map), tClass);
        return response.result();
    }
 
    /**
     * 文档id查询信息
     * @param idxName 索引名
     * @param docId 文档id
     */
    @Override
    public <T> T getById(String idxName, String docId,Class<T> tClass) throws IOException {
        GetResponse<T> response = elasticsearchClient.get(g -> g
                        .index(idxName)
                        .id(docId),
                tClass);
        return response.found() ? response.source() : null;
    }
 
    /**
     * 根据索引名称和文档id查询ObjectNode
     * @param idxName 索引名
     * @param docId 文档id
     */
    @Override
    public ObjectNode getObjectNodeById(String idxName, String docId) throws IOException {
        GetResponse<ObjectNode> response = elasticsearchClient.get(g -> g
                        .index(idxName)
                        .id(docId),
                ObjectNode.class);
 
        return response.found() ? response.source() : null;
    }
 
    /**
     * 单条输出
     * @param idxName 索引名
     * @param docId 文档id
     */
    @Override
    public Boolean deleteById(String idxName, String docId) throws IOException {
        DeleteResponse delete = elasticsearchClient.delete(d -> d
                .index(idxName)
                .id(docId));
        return delete.forcedRefresh();
    }
 
    /**
     * 批量删除
     * @param idxName 索引名
     * @param docIds 要删除的文档id集合
     */
    @Override
    public BulkResponse bulkDeleteByIds(String idxName, List<String> docIds) throws Exception {
        BulkRequest.Builder br = new BulkRequest.Builder();
        // 将每一个对象都放入builder中
        docIds.forEach(id -> br
                        .operations(op -> op
                                .delete(d -> d
                                        .index(idxName)
                                        .id(id))));
        return elasticsearchClient.bulk(br.build());
    }

    List<UserVo> getUserVoList(String INDEX_NAME, String userName) throws IOException {
        List<UserVo> userList = new ArrayList<>();
        // 构建查询条件列表
        List<Query> queryList = new ArrayList<>();

        // MatchPhrasePrefixQuery
        // MatchPhraseQuery精确匹配
        //此方法可以实现字段的模糊查询,其他MatchPhraseQuery,MatchAllQuery,MatchQuery,MatchBoolPrefixQuery,MatchNoneQuery查询的具体用法含义请自行百度
        Query byName = MatchPhrasePrefixQuery.of(m-> m
                .field("userName")
                .query(userName))._toQuery();
        queryList.add(byName);

        //RangeQuery 年龄大小范围查询
        Query ageStart = RangeQuery.of(r -> r
                .field("age")
                .gte(JsonData.of(10))
        )._toQuery();

        Query ageEnd = RangeQuery.of(r -> r
                .field("age")
                .lte(JsonData.of(90))
        )._toQuery();
        queryList.add(ageStart);
        queryList.add(ageEnd);


        //查询后显示的字段，写几个显示几个
        String[] sources = new String[]{"userName","age","id","email","version","height","createTime","updateTime"};
        SearchResponse<UserVo> response = elasticsearchClient.search(s -> s
                        .index(INDEX_NAME)
                        .query(q -> q.bool(b -> b.must(queryList)))
                        .sort(sort -> sort.field(f -> f.field("id").order(SortOrder.Asc)))
                        .source(sc -> sc
                                .filter(f -> f
                                        .includes(Arrays.asList(sources))
                                )
                        )
                        //分页，注意大数据量的查询用此不合适
                        .from(0)
                        .size(10000)//注意此种方式最多查10000条，超过就报错
                ,UserVo.class
        );
        List<Hit<UserVo>> hits = response.hits().hits();
        for (Hit<UserVo> hit: hits) {
            userList.add(hit.source());
        }
        return userList;
    }
}