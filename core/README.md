#### 语法

1. Query对象使用
```
// WHERE id = ? OR username = ?
Query query = new Query()
    .eq("id", 6)
    .orEq("username", "jim");
```
2. Query 复杂拼接
```
// WHERE (id = ? OR id between ? and ?) AND ( money > ? OR state = ? )
Query query = new Query()
    .and(q -> q.eq("id", 3).orBetween("id", 4, 10))
    .and(q -> q.gt("money", 1).orEq("state", 1));

// WHERE ( id = ? AND username = ? ) OR ( money > ? AND state = ? )
Query query = new Query()
    .and(q -> q.eq("id", 3).eq("username", "jim"))
    .or(q -> q.gt("money", 1).eq("state", 1));
```

3. 增删改查
```
        PageInfo<TUser> pageInfo = userService.page(query);

        userService.saveIgnoreNull(user);
        
        userService.updateIgnoreNull(user);

        userService.deleteById(id);
```
4. 复杂SQL可使用**Mapper.xml文件；使用MyBatis-Mapper原DTD，映射至MapperInterface（与原MyBatis用法一样）