# snowflake-plus

This is a snowflake id extension library (in a clustered environment)

## 背景

在集群环境下,同一个service因为配置文件中的dataCenterId和workerId相同，高并发下可能造成多个instance节点生成的ID是重复的。 为了避免此问题，snowflake-plus可以作为一种方案，其目的是借助一些中间件让每个instance(节点)注册不同的workerId

> 同一个service使其保持相同的dataCenterId

## 计划(注册类型)
- [X] MANUAL(常规/根据配置文件参数指定)
- [X] REDIS(采用redis注册给每个实例分配dataCenterId和workerId)
- [ ] ZOOKEEPER
- [ ] KUBERNETES
- [ ] CUSTOMIZED

## 安装

git clone && mvn install

## 使用

```
<dependency>
    <groupId>com.anthonyzero</groupId>
    <artifactId>snowflake-plus</artifactId>
    <version>1.0.0</version>
</dependency>
```

application.yml
```
snowflake-plus:
  cluster:
    registrar: redis
    data-center-id: 1
```

## 维护者

[@AnthonyZero](https://github.com/AnthonyZero)

## 如何贡献

非常欢迎你的加入！[提一个 Issue](https://github.com/AnthonyZero/snowflake-plus/issues/new) 或者提交一个 Pull Request。
