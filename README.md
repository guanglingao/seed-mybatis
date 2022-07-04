## 用法

1. 新建项目目录（文件夹）
2. 在项目目录下创建<code>pom.xml</code>文件，文件内容为
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>demo</artifactId>
    <version>1.0.0-SNAPSHOT</version>

    <name>demo</name>
    <description>demo</description>

    <build>
        <plugins>
            <plugin>
                <groupId>org.seed.mybatis</groupId>
                <artifactId>code-generate-maven-plugin</artifactId>
                <version>1.1.14</version>
            </plugin>
        </plugins>
    </build>

</project>

```
3. 在<code>pom.xml</code>同级目录下，创建文件<code>code-generate-meta.toml</code>，内容为：
```
[application]
## 作者
author = "guanglin.gao"
## 项目根路径
basePackage = "com.example"
## 服务名称
serviceName = "member"

[database]
## 数据库连接参数
driverClass = "org.postgresql.Driver"
jdbcUrl = "jdbc:postgresql://localhost:5432/balance"
userName = "postgres"
password = "123456"

[tables]
## 表设置
# 包含
include = "*"
# 排除
exclude = "user,foo,bar"
# 生成类名去除前缀
prefix = "t_,v_"
# 生成类名去除后缀
suffix = "^_[0-9]*$,__"

```
4. 使用开发工具（例：IDEA）打开项目，识别为Maven项目
5. 在开发工具（右侧？）Maven工具集中，展开<code>Plugins</code>菜单
6. 选择展开<code>code-generate</code>插件，可执行选项有Single、WithApi；Single不生成此微服务项目的Api（基于OpenFeign的对外接口）
```
code-generate:Single
code-generate:WithApi
```
## IService中预置的方法，IService被生成的Service继承

1. 保存或修改，当数据库存在记录（按照PK查询）执行UPDATE，否则执行INSERT
```
int saveOrUpdate(E entity)
// 返回受影响的行数
```
2. 保存或修改，忽略null字段，当数据库存在记录执行UPDATE，否则执行INSERT
```
int saveOrUpdateIgnoreNull(E entity) 
// 返回受影响的行数
```
3. 删除记录（底层根据id删除），在有逻辑删除字段的情况下，做UPDATE操作。
```
int delete(E entity)
// 返回受影响的行数
```
4. 根据id删除，在有逻辑删除字段的情况下，做UPDATE操作
```
int deleteById(I id)
// 返回受影响的行数
```
5. 根据多个主键id删除，在有逻辑删除字段的情况下，做UPDATE操作
```
int deleteByIds(Collection<I> ids)
// 返回受影响的行数
```
6. 根据指定字段值删除，在有逻辑删除字段的情况下，做UPDATE操作<
```
int deleteByColumn(String column, Object value)
// 返回受影响的行数
```
7. 根据条件删除，在有逻辑删除字段的情况下，做UPDATE操作
```
int deleteByQuery(Query query)
// 返回受影响的行数
```
8. 强制删除（底层根据id删除），忽略逻辑删除字段，执行DELETE语句
```
int forceDelete(E entity) 
// 返回受影响的行数
```
9. 根据id强制删除，忽略逻辑删除字段，执行DELETE语句
```
int forceDeleteById(I id)
// 返回受影响的行数
```
10. 根据条件强制删除，忽略逻辑删除字段，执行DELETE语句
```
int forceDeleteByQuery(Query query)
// 返回受影响的行数
```
11. 保存，保存所有字段
```
int save(E entity)
// 返回受影响的行数
```
12. 保存，忽略null字段
```
int saveIgnoreNull(E entity)
// 返回受影响的行数
```
13. 批量保存
```
int saveBatch(Collection<E> entities)
// 返回受影响的行数
```
14. 批量保存,忽略重复行
```
int saveMultiSet(Collection<E> entities)
// 返回受影响的行数
```
15. 批量保存，去除重复行，通过对象equal方法判断重复数据，实体类需要实现equals方法
```
int saveUnique(Collection<E> entities)
// 返回受影响的行数
```
16. 批量保存，去除重复行，指定比较器判断
```
int saveUnique(Collection<E> entities, Comparator<E> comparator)
// 返回受影响的行数
```
17. 根据主键查询
```
E getById(I id)
```
18. 根据主键查询强制查询，忽略逻辑删除字段
```
E forceById(I id)
```
19. 根据条件查找单条记录
```
E getByQuery(Query query)
```
20. 查询单条数据并返回指定字段
```
E getBySpecifiedColumns(List<String> columns, Query query)
```
21. 查询单条数据返回指定字段并转换到指定类中
```
<T> T getBySpecifiedColumns(List<String> columns, Query query, Class<T> clazz)
```
22. 查询某一行某个字段值
```
<T> T getColumnValue(String column, Query query, Class<T> clazz)
// 返回单值，查不到返回null
```
23. 根据字段查询一条记录
```
E getByColumn(String column, Object value) 
// 返回实体对象，没有返回null
```
24. 查询总记录数
```
long getCount(Query query)
```
25. 根据字段查询结果集
```
List<E> listByColumn(String column, Object value)
```
26. 查询结果集
```
List<E> list(Query query)
```
27. 根据多个主键值查询
```
List<E> listByIds(Collection<I> ids)
```
28. 根据多个字段值查询结果集
```
List<E> listByArray(String column, Object[] values)
```
29. 根据字段多个值查询结果集
```
List<E> listByCollection(String column, Collection<?> values)
```
30. 查询返回指定的列，使用实体类集合包装返回
```
List<E> listBySpecifiedColumns(List<String> columns, Query query)
```
31. 查询返回指定的列，返指定类集合
```
<T> List<T> listBySpecifiedColumns(List<String> columns, Query query, Class<T> clazz) 
```
32. 查询指定列，返指定列集合
```
<T> List<T> listColumnValues(String column, Query query, Class<T> clazz)
```
33. 查询并返回指定的列，返回分页数据
```
<T> PageInfo<T> pageBySpecifiedColumns(List<String> columns, Query query, Class<T> clazz)
```
34. 分页查询
```
PageInfo<E> page(Query query)
```
35. 查询结果集，并转换结果集中的记录
```
<T> PageInfo<T> page(Query query, Supplier<T> target)
// 例：PageInfo<UserVO> pageInfo = mapper.page(query, UserVO::new));
```
36. 查询结果集，并转换结果集中的记录，转换处理每一行
```
<R> PageInfo<R> page(Query query, Function<E, R> converter)
例： 
PageInfo<UserVO> page = mapper.page(query, user -> {
    UserVO userVO = new UserVO());
    BeanUtils.copyProperties(user, userVO));
    return userVO;
  }));
}
```
37. 查询结果集，并转换结果集中的记录，转换处理list
```
<R> PageInfo<R> pageAndConvert(Query query, Function<List<E>, List<R>> converter)
例：
PageInfo<UserVO> pageInfo = mapper.pageAndConvert(query, list -> {
    List<UserVO> retList = new ArrayList<>(list.size());
        for (TUser tUser : list) {
        UserVO userVO = new UserVO());
        BeanUtils.copyProperties(tUser, userVO));
        retList.add(userVO));
        }
    return retList;
    }));
```
38. 查询结果集，并转换结果集中的记录，并对记录进行额外处理
```
<R> PageInfo<R> page(Query query, Supplier<R> target, Consumer<R> format)
例：
PageInfo<UserVO> page = mapper.page(query, UserVO::new, userVO -> {
    System.out.println(userVO.getUsername());
    }));
```
39. 查询返回easyui结果集
```
PageEasyui<E> pageEasyui(Query query)
```
40. 查询返回easyui结果集，并转换结果集中的记录
```
<T> PageEasyui<T> pageEasyui(Query query, Class<T> clazz)
```
41. 更新，更新所有字段
```
int update(E entity) 
// 返回影响行数
```
42. 更新，忽略null字段
```
int updateIgnoreNull(E entity)
// 返回影响行数
```
43. 根据条件更新
```
int updateByQuery(E entity, Query query) 
// 返回影响行数
例：
  Query query = new Query().eq("state", 2));
  TUser user = new TUser());
  user.setUsername("李四"));
  int i = mapper.updateByQuery(user, query));
```
44. 根据条件更新，更新实参为Map类型对象，设置列值为map中value值，key为数据库字段名
```
int updateByMap(Map<String, Object> setBlock, Query query)
例：
  Map<String, Object> setBlock = new LinkedHashMap<>());
  setBlock.put("username", "李四2"));
  setBlock.put("remark", "123"));
  int i = mapper.updateByMap(setBlock, query));
```
45. Query对象创建后，可使用“链式”设置查询参数
## 复杂SQL查询
在生成的文件中，路径<code>main/resources/mapper</code>下有示例XmlMapper文件<code>DemoComplexSqlMapper.xml</code>; 使用时可复制此文件用来编写复杂SQL，建议直接返回指定类型，SeedMyBatis组件会自动完成驼峰转化（数据库字段下划线->JavaBean驼峰）。
关于SeedMyBatis的详细用法，请参照下面链接：
## [seed-mybatis语法](https://gitee.com/glin/seed-mybatis/blob/master/core/README.md)
## [生成接口文档](https://gitee.com/glin/seed-mybatis/blob/master/code-generate-maven-plugin/SmartDoc.md)
## [BeanSearcher用法](https://gitee.com/glin/seed-mybatis/blob/master/code-generate-maven-plugin/BeanSearcher.md)


## Controller 参数解析

1. 日期类型解析需要使用<code>@DateTimeFormat</code>注解
```
import org.springframework.format.annotation.DateTimeFormat;

@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private Date createTime;
```

## 分库分表
1. 分库使用Spring框架的AbstractRoutingDataSource; 配置多数据源的方式如下：
```
spring: 
  datasource:
    default:
      username: ${OPERATOR_JDBC_USERNAME:postgres}
      password: ${OPERATOR_JDBC_PASSWORD:123456}
      driver-class-name: org.postgresql.Driver
      url: ${OPERATOR_JDBC_URL:jdbc:postgresql://localhost:5432/balance}
    other:
      username: ${OPERATOR_JDBC_USERNAME:postgres}
      password: ${OPERATOR_JDBC_PASSWORD:123456}
      driver-class-name: org.postgresql.Driver
      url: ${OPERATOR_JDBC_URL:jdbc:postgresql://localhost:5432/balance}
```
2. 使用代码生成工具，能够自动生成应用启动Application，需要排除数据库自动配置
```
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
```
3. 生成的数据源导出Bean，需要按需配置（DataSourceConfiguration）
```
    @Bean(name = "defaultDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.default")
    public DataSourceProperties defaultDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "otherDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.other")
    public DataSourceProperties otherDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Primary
    @Bean
    public RoutingDataSource routingDataSource(@Qualifier("defaultDataSourceProperties") DataSourceProperties defaultDataSourceProperties,
                                               @Qualifier("otherDataSourceProperties") DataSourceProperties otherDataSourceProperties) {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(defaultDataSourceProperties.initializeDataSourceBuilder().build());
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("default", defaultDataSourceProperties.initializeDataSourceBuilder().build());
        dataSourceMap.put("other", otherDataSourceProperties.initializeDataSourceBuilder().build());
        routingDataSource.setTargetDataSources(dataSourceMap);
        return routingDataSource;
    }
```
4. 注解<code>@Scatter</code>使用在方法上，<code>by</code>的值，标识按照此***参数名***进行分库分表计算，计算逻辑需自定义在<code>strategy</code>中；
分库分表策略需实现接口<code>org.seed.mybatis.springboot.scatter.ShardingStrategy</code>
```
    @Scatter(by = "id",strategy = AuthenticationStrategy.class)
    public List<Admin> listById(String id){
        return listByColumn(AdminColumn.id,id);
    }
```
5. 分库分表策略接口<code>org.seed.mybatis.springboot.scatter.ShardingStrategy</code>
* <code>getDataSourceId</code> 获取数据源ID；与application.yml(.properties)中spring.datasource的***子级***的名称一致，并且在<code>DataSourceConfiguration</code>中也应如此配置。
* <code>getTablePrefix</code> 表名前缀
* <code>getTableSuffix</code> 表名后缀
* <code>setBasis</code> 设置分库分表***基准值***，即按照此值执行数据源名称计算和表名前缀、后缀计算；应在实现类中定义变量接收此值，并在执行上述3个方法时，以此值进行计算。
6. 分库分表逻辑和定义，应在***Service层***实现
7. DataSourceConfiguration中，主数据源导出需使用类型<code>org.seed.mybatis.springboot.scatter.RoutingDataSource</code>
8. 可使用命令式切换数据源
```
RoutingDataSourceContext.setDataSourceKey(String key);
```