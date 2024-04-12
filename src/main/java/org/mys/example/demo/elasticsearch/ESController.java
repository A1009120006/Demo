package org.mys.example.demo.elasticsearch;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.mys.example.demo.common.BaseRes;
import org.mys.example.demo.common.ResUtil;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * elasticsearch 测试
 * @author hulei
 * @date 2023/8/27 16:27
 */
@RestController
@RequestMapping("/ES")
@RequiredArgsConstructor
public class ESController {

    private final ESDocumentService documentDemoService;

    private final ElasticsearchClient elasticsearchClient;

    private final static String INDEX_NAME = "uservo";


    @GetMapping("/getById")
    public UserVo getById(@RequestParam("id") long id) throws Exception {
        UserVo userVo = documentDemoService.getById(INDEX_NAME, Long.toString(id), UserVo.class);
        System.out.println("object ->" + userVo);
        return userVo;
    }

    @PostMapping("/updateById")
    public Result updateById(@RequestParam("id") String id) throws IOException {
        Map<String,Object> map = new HashMap(){{
            put("version",288765);
        }};
        return documentDemoService.updateById(INDEX_NAME,id,UserVo.class,map);
    }

    /**
     * 排序查询，范围查询,浅分页查询
     */
    @GetMapping("/searchRequest")
    public Object searchRequest(@RequestParam("userName") String userName) throws IOException {
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
        /*
         RangeQuery 年龄大小范围查询
                  Query ageStart = RangeQuery.of(r -> r
                          .field("age")
                          .gte(JsonData.of(10))
                  )._toQuery();
          <p>
                  Query ageEnd = RangeQuery.of(r -> r
                          .field("age")
                          .lte(JsonData.of(90))
                  )._toQuery();
                  queryList.add(ageStart);
                  queryList.add(ageEnd);
         */

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
        return  userList;
    }


    /**
     * 查询所有，但是只能查询前一万条数据(我造了一亿数据)
     */
    @GetMapping("/queryAll")
    @SuppressWarnings("unchecked")
    public BaseRes<Object> queryAll() throws IOException {
        List userList = new LinkedList<>();
        SearchResponse<UserVo> searchResponse = elasticsearchClient.search(searchRequestBuilder ->{
            searchRequestBuilder
                    .index(INDEX_NAME)
                    .sort(sort -> sort.field(f -> f.field("id").order(SortOrder.Asc)))
                    .from(0)
                    .size(10000)//注意此种方式最多查10000条，超过就报错,如果不写from，size就默认只查10条
            ;
            return searchRequestBuilder;
        } , UserVo.class);
        System.out.println(searchResponse);
        System.out.println("花费的时长：" + searchResponse.took());

        HitsMetadata<UserVo> hitsMetadata = searchResponse.hits();
        System.out.println(hitsMetadata.total());
        assert hitsMetadata.total() != null;
        System.out.println("符合条件的总文档数量：" + hitsMetadata.total().value());
        List<Hit<UserVo>> hitList = searchResponse.hits().hits();  //注意：第一个hits() 与 第二个hits()的区别
        for (Hit<UserVo> hit : hitList) {
            assert hit.source() != null;
            userList.add(hit.source());
        }
        return ResUtil.success(userList);
    }

    @GetMapping("/fuzzQuery")
    @SuppressWarnings("unchecked")
    public BaseRes<Object> fuzzQuery(@RequestParam("userName") String userName) throws IOException {
        List userList = new LinkedList<>();
        //模糊查询
        SearchResponse<UserVo> searchResponse = elasticsearchClient.search(srBuilder -> srBuilder
                        .index(INDEX_NAME)
                        // 模糊查询Fuzzy
                        .query(queryBuilder -> queryBuilder
                                .fuzzy(fuzzyQueryBuilder -> fuzzyQueryBuilder
                                        .field("userName")
                                        .value(userName)
                                        .fuzziness("2"))//fuzziness代表可以与关键词有误差的字数，可选值为0、1、2这三项
                        )
                        .from(0)
                        .size(10000)
                , UserVo.class);
        HitsMetadata<UserVo> hitsMetadata = searchResponse.hits();
        List<Hit<UserVo>> hitList = hitsMetadata.hits();  //注意：第一个hits() 与 第二个hits()的区别
        for (Hit<UserVo> hit : hitList) {
            assert hit.source() != null;
            userList.add(hit.source());
        }
        assert hitsMetadata.total() != null;
        return ResUtil.success(userList);
    }

    /**
     * 批量新增数据(这里手动模拟插入了一亿条数据,点了很多次，每次生成3000000条数据，多了会报错
     * start范围从1,3000001,6000001,9000001,12000001,15000001,18000001,21000001,24000001,27000001,30000001,33000001,36000001,39000001,42000001,
     * 45000001,48000001,51000001,54000001,57000001,60000001,63000001,66000001,69000001,72000001,75000001,78000001,81000001,84000001,87000001,
     * 90000001,93000001,96000001,99000001
     * 开始每次增加300万
     * 好测试数据查询速度)
     */
    @PostMapping("/bulkCreate")
    public void bulkCreate(@RequestParam("start") int start) {
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int j = 1; j <= 10 ; j++){
            // 构造文档集合
            int finalJ = j;
            int finalJ1 = j;
            CompletableFuture<Void> future = CompletableFuture.runAsync(()->{
                List<Object> list = new ArrayList<>();
                for (int i = start+300000*(finalJ1 -1); i <= (start == 1 ? 0 : start-1)+300000 * finalJ; i++) {
                    UserVo userVO = new UserVo();
                    userVO.setId((long) i);
                    userVO.setUserName("胡磊batch" + i);
                    userVO.setAge(i);
                    userVO.setEmail("ss.com");
                    userVO.setCreateTime(new Date());
                    userVO.setUpdateTime(new Date());
                    userVO.setHeight(12D);
                    list.add(userVO);
                }
                // 批量新增
                BulkResponse response;
                try {
                    response = documentDemoService.bulkCreate(INDEX_NAME, list);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                List<BulkResponseItem> items = response.items();
                for (BulkResponseItem item : items) {
                    System.out.println("BulkResponseItem.toString() -> " + item.toString());
                }
            });
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
    }

    /**
     * 此种方式若发现没有索引，会自动创建一个索引
     */
    @PostMapping("/createByFluentDSL")
    public void createByFluentDSL() throws Exception {
        // 构建文档数据
        UserVo userVO = new UserVo();
        userVO.setId(1L);
        userVO.setUserName("胡磊batch1");
        userVO.setAge(29);
        userVO.setCreateTime(new Date());
        userVO.setUpdateTime(new Date());
        userVO.setEmail("ss.com");
        userVO.setVersion(1);
        userVO.setHeight(12D);

        // 新增一个文档
        IndexResponse response = documentDemoService.createByFluentDSL(INDEX_NAME, userVO.getId().toString(), userVO);
        System.out.println("response.forcedRefresh() -> " + response.forcedRefresh());
        System.out.println("response.toString() -> " + response);
    }

    @PostMapping("/createByJson")
    public void createByJson() throws Exception {
        // 构建文档数据
        UserVo userVO = new UserVo();
        userVO.setId(1L);
        userVO.setUserName("胡磊batch1");
        userVO.setAge(29);
        userVO.setCreateTime(new Date());
        userVO.setUpdateTime(new Date());
        userVO.setEmail("ss.com");
        userVO.setVersion(1);
        userVO.setHeight(12D);
        // 新增一个文档
        IndexResponse response = documentDemoService.createByJson(INDEX_NAME, userVO.getId().toString(), JSON.toJSONString(userVO));
        System.out.println("response.toString() -> " + response.toString());
    }

    @PostMapping("/createAsync")
    public void createAsync() {
        // 构建文档数据
        UserVo userVO = new UserVo();
        userVO.setId(1L);
        userVO.setUserName("胡磊batch1");
        userVO.setAge(29);
        userVO.setCreateTime(new Date());
        userVO.setUpdateTime(new Date());
        userVO.setEmail("ss.com");
        userVO.setVersion(1);
        userVO.setHeight(12D);
        documentDemoService.createAsync(INDEX_NAME, userVO.getId().toString(), userVO, (indexResponse, throwable) -> {
            // throwable必须为空
            // 验证结果
            System.out.println("response.toString() -> " + indexResponse.toString());
        });
    }
}