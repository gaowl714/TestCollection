### 笔记

#### 慢查询日志

**相关参数**

- slow_query_log：是否开启慢查询日志，1表示开启，0表示关闭。
- log-slow-queries  ：旧版（5.6以下版本）MySQL数据库慢查询日志存储路径。可以不设置该参数，系统则会默认给一个缺省的文件host_name-slow.log
- slow-query-log-file：新版（5.6及以上版本）MySQL数据库慢查询日志存储路径。可以不设置该参数，系统则会默认给一个缺省的文件host_name-slow.log
- long_query_time ：慢查询阈值，当查询时间多于设定的阈值时（秒），记录日志。
- log_queries_not_using_indexes：未使用索引的查询（包括未超过阈值的查询、full index scan）也被记录到慢查询日志中（可选项）。
- log_output：日志存储方式。log_output='FILE'表示将日志存入文件，默认值是'FILE'。log_output='TABLE'表示将日志存入数据库，这样日志信息就会被写入到mysql.slow_log表中。MySQL数据库支持同时两种日志存储方式，配置的时候以逗号隔开即可，如：log_output='FILE,TABLE'。日志记录到系统的专用日志表中，要比记录到文件耗费更多的系统资源，因此对于需要启用慢查询日志，又需要能够获得更高的系统性能，那么建议优先记录到文件。

使用 ``set global 参数名 = 值`` 修改数据库的值，如：

```
set global long_query_time = 10;
```

修改数据库的值会在重启后失效，**永久生效**需要在my.cnf配置文件中配置。

**日志分析工具：**mysqldumpslow

```
-s, 是表示按照何种方式排序

    c: 访问计数

    l: 锁定时间

    r: 返回记录

    t: 查询时间

    al:平均锁定时间

    ar:平均返回记录数

    at:平均查询时间

	-t, 是top n的意思，即为返回前面多少条的数据；

	-g, 后边可以写一个正则匹配模式，大小写不敏感的；

比如:

得到返回记录集最多的10个SQL。

mysqldumpslow -s r -t 10 /database/mysql/mysql06_slow.log

得到访问次数最多的10个SQL。

mysqldumpslow -s c -t 10 /database/mysql/mysql06_slow.log

得到按照时间排序的前10条里面含有左连接的查询语句。

mysqldumpslow -s t -t 10 -g “left join” /database/mysql/mysql06_slow.log

另外建议在使用这些命令时结合 | 和more 使用 ，否则有可能出现刷屏的情况。

mysqldumpslow -s r -t 20 /mysqldata/mysql/mysql06-slow.log | more
```

#### 随笔

当你需要扩容的时候，也就是需要再多搭建一些备库来增加系统的读能力的时候，现在常见的做法也是用全量备份加上应用 binlog 来实现的 

redo log 用于保证 crash-safe 能力。**innodb_flush_log_at_trx_commit** 这个参数设置成 1 的时候，表示每次事务的 redo log 都直接持久化到磁盘。这个参数我建议你设置成 1，这样可以保证 MySQL 异常重启之后数据不丢失。 

innodb_log_file_size：指定每个redo日志大小，默认值48MB

innodb_log_files_in_group：指定日志文件组中redo日志文件数量，默认为2

innodb_log_group_home_dir：指定日志文件组所在路劲，默认值./，指mysql的数据目录datadir

> ### Optimizing InnoDB Redo Logging
>
> Consider the following guidelines for optimizing redo logging:
>
> - Consider increasing the size of the [log buffer](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_log_buffer). A large log buffer enables large [transactions](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_transaction) to run without a need to write the log to disk before the transactions [commit](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_commit). Thus, if you have transactions that update, insert, or delete many rows, making the log buffer larger saves disk I/O. Log buffer size is configured using the [`innodb_log_buffer_size`](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_log_buffer_size) configuration option.
>
> - Configure the [`innodb_log_write_ahead_size`](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_log_write_ahead_size) configuration option to avoid “read-on-write”. This option defines the write-ahead block size for the redo log. Set [`innodb_log_write_ahead_size`](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_log_write_ahead_size) to match the operating system or file system cache block size. Read-on-write occurs when redo log blocks are not entirely cached to the operating system or file system due to a mismatch between write-ahead block size for the redo log and operating system or file system cache block size.
>
>   Valid values for [`innodb_log_write_ahead_size`](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_log_write_ahead_size) are multiples of the `InnoDB` log file block size (2n). The minimum value is the `InnoDB` log file block size (512). Write-ahead does not occur when the minimum value is specified. The maximum value is equal to the [`innodb_page_size`](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_page_size) value. If you specify a value for [`innodb_log_write_ahead_size`](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_log_write_ahead_size) that is larger than the [`innodb_page_size`](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_page_size) value, the [`innodb_log_write_ahead_size`](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_log_write_ahead_size) setting is truncated to the [`innodb_page_size`](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_page_size) value.
>
>   Setting the [`innodb_log_write_ahead_size`](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_log_write_ahead_size) value too low in relation to the operating system or file system cache block size results in read-on-write. Setting the value too high may have a slight impact on `fsync` performance for log file writes due to several blocks being written at once.

**sync_binlog** 这个参数设置成 1 的时候，表示每次事务的 binlog 都持久化到磁盘。这个参数我也建议你设置成 1，这样可以保证 MySQL 异常重启之后 binlog 不丢失。 

### 基础架构：一条SQL查询语句是如何执行的？

一条语句的执行顺序

![](.\picture\0d2070e8f84c4801adbfa03bda1f98d9.png)

大体来说，MySQL 可以分为 **Server 层**和**存储引擎层**两部分。 

Server 层包括**连接器、查询缓存、分析器、优化器、执行器**等，涵盖 MySQL 的大多数核心服务功能，以及所有的**内置函数**（如日期、时间、数学和加密函数等），所有跨存储引擎的功能都在这一层实现，比如**存储过程、触发器、视图**等。 

而存储引擎层负责数据的**存储和提取**。其架构模式是插件式的，支持 InnoDB、MyISAM、Memory 等多个存储引擎。现在最常用的存储引擎是 InnoDB，它从 MySQL 5.5.5 版本开始成为了默认存储引擎。 

#### 连接器

第一步，你会先连接到这个数据库上，这时候接待你的就是连接器。连接器负责跟客户端建立连接、获取权限、维持和管理连接。连接命令一般是这么写的： 

```sql
mysql -h$ip -P$port -u$user -p
```

连接完成后，如果你没有后续的动作，这个连接就处于空闲状态，你可以在 **show processlist** 命令中看到它。文本中这个图是 show processlist 的结果，其中的 Command 列显示为“Sleep”的这一行，就表示现在系统里面有一个空闲连接。 

![](.\picture\f2da4aa3a672d48ec05df97b9f992fed.png)

客户端如果太长时间没动静，连接器就会自动将它断开。这个时间是由参数 **wait_timeout** 控制的，默认值是 8 小时。 

数据库里面，**长连接是指连接成功后，如果客户端持续有请求，则一直使用同一个连接。短连接则是指每次执行完很少的几次查询就断开连接，下次查询再重新建立一个**。 

建立连接的过程通常是比较复杂的，所以我建议你在使用中要尽量减少建立连接的动作，也就是尽量使用长连接。但是全部使用长连接后，你可能会发现，有些时候 MySQL 占用内存涨得特别快，这是因为 MySQL 在执行过程中临时使用的内存是管理在连接对象里面的。这些资源会在连接断开的时候才释放。所以如果长连接累积下来，可能导致内存占用太大，被系统强行杀掉（OOM），从现象看就是 MySQL 异常重启了。 

怎么解决这个问题呢？你可以考虑以下两种方案。

1. 定期断开长连接。使用一段时间，或者程序里面判断执行过一个占用内存的大查询后，断开连接，之后要查询再重连。
2. 如果你用的是 MySQL 5.7 或更新版本，可以在每次执行一个比较大的操作后，通过执行mysql_reset_connection 来重新初始化连接资源。这个过程不需要重连和重新做权限验证，但是会将连接恢复到刚刚创建完时的状态。 

#### ~~查询缓存~~ 

以 key-value 对的形式，被直接缓存在内存中。key 是查询的语句，value 是查询的结果。 

**但是大多数情况下我会建议你不要使用查询缓存，为什么呢？因为查询缓存往往弊大于利。**

查询缓存的失效非常频繁，只要有对一个表的更新，这个表上所有的查询缓存都会被清空。因此很可能你费劲地把结果存起来，还没使用呢，就被一个更新全清空了。对于更新压力大的数据库来说，查询缓存的命中率会非常低。除非你的业务就是有一张静态表，很长时间才会更新一次。比如，一个系统配置表，那这张表上的查询才适合使用查询缓存。

好在 MySQL 也提供了这种“按需使用”的方式。你可以将参数 query_cache_type 设置成 DEMAND，这样对于默认的 SQL 语句都不使用查询缓存。而对于你确定要使用查询缓存的语句，可以用 SQL_CACHE 显式指定，像下面这个语句一样： 

```sql
mysql> select SQL_CACHE * from T where ID=10；
```

需要注意的是，**MySQL 8.0 版本直接将查询缓存的整块功能删掉了**，也就是说 8.0 开始彻底没有这个功能了。 

#### 分析器 

首先，MySQL 需要知道你要做什么，因此需要对 SQL 语句做解析。 

分析器先会做**“词法分析”**。你输入的是由多个字符串和空格组成的一条 SQL 语句，MySQL 需要识别出里面的字符串分别是什么，代表什么。

MySQL 从你输入的"select"这个关键字识别出来，这是一个查询语句。它也要把字符串“T”识别成“表名 T”，把字符串“ID”识别成“列 ID”。

做完了这些识别以后，就要做**“语法分析”**。根据词法分析的结果，语法分析器会根据语法规则，判断你输入的这个 SQL 语句是否满足 MySQL 语法。

如果你的语句不对，就会收到“You have an error in your SQL syntax”的错误提醒，比如下面这个语句 select 少打了开头的字母“s”。 

```sql
mysql> elect * from t where ID=1;

ERROR 1064 (42000): You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'elect * from t where ID=1' at line 1
```

#### 优化器 

经过了分析器，MySQL 就知道你要做什么了。在开始执行之前，还要先经过优化器的处理。 

优化器是在表里面有多个索引的时候，决定使用哪个索引；或者在一个语句有多表关联（join）的时候，决定各个表的连接顺序。比如你执行下面这样的语句，这个语句是执行两个表的 join： 

```
mysql> select * from t1 join t2 using(ID)  where t1.c=10 and t2.d=20;
```

- 既可以先从表 t1 里面取出 c=10 的记录的 ID 值，再根据 ID 值关联到表 t2，再判断 t2 里面 d 的值是否等于 20。
- 也可以先从表 t2 里面取出 d=20 的记录的 ID 值，再根据 ID 值关联到 t1，再判断 t1 里面 c 的值是否等于 10。 

这两种执行方法的逻辑结果是一样的，但是执行的效率会有不同，**而优化器的作用就是决定选择使用哪一个方案**。 

#### 执行器 

MySQL 通过分析器知道了你要做什么，通过优化器知道了该怎么做，于是就进入了执行器阶段，开始执行语句。 

开始执行的时候，**要先判断一下你对这个表 T 有没有执行查询的权限**，如果没有，就会返回没有权限的错误，如下所示 (在工程实现上，如果命中查询缓存，会在查询缓存返回结果的时候，做权限验证。查询也会在优化器之前调用 precheck 验证权限)。 

```sql
mysql> select * from T where ID=10;

ERROR 1142 (42000): SELECT command denied to user 'b'@'localhost' for table 'T'
```

比如我们这个例子中的表 T 中，ID 字段**没有索引**，那么执行器的执行流程是这样的：

1. 调用 InnoDB 引擎接口取这个表的第一行，判断 ID 值是不是 10，如果不是则跳过，如果是则将这行存在结果集中。
2. 调用引擎接口取“下一行”，重复相同的判断逻辑，直到取到这个表的最后一行。
3. 执行器将上述遍历过程中所有满足条件的行组成的记录集作为结果集返回给客户端。 

**对于有索引的表**，执行的逻辑也差不多。第一次调用的是“**取满足条件的第一行**”这个接口，之后循环取“**满足条件的下一行**”这个接口，这些接口都是引擎中已经定义好的。 

你会在数据库的**慢查询日志**中看到一个 rows_examined 的字段，表示这个语句执行过程中扫描了多少行。这个值就是在执行器每次调用引擎获取数据行的时候累加的。

在有些场景下，执行器调用一次，在引擎内部则扫描了多行，**因此引擎扫描行数跟 rows_examined 并不是完全相同的**。我们后面会专门有一篇文章来讲存储引擎的内部机制，里面会有详细的说明。 

### 日志系统：一条SQL更新语句是如何执行的？

Redo log不是记录数据页“更新之后的状态”，而是记录这个页 “做了什么改动”。  

Binlog有两种模式，statement 格式的话是记sql语句， row格式会记录行的内容，记两条，更新前和更新后都有 

#### 重要的日志模块：redo log 

> 不知道你还记不记得《孔乙己》这篇文章，酒店掌柜有一个粉板，专门用来记录客人的赊账记录。如果赊账的人不多，那么他可以把顾客名和账目写在板上。但如果赊账的人多了，粉板总会有记不下的时候，这个时候掌柜一定还有一个专门记录赊账的账本。
>
> 如果有人要赊账或者还账的话，掌柜一般有两种做法：
>
> - 一种做法是直接把账本翻出来，把这次赊的账加上去或者扣除掉；
> - 另一种做法是先在粉板上记下这次的账，等打烊以后再把账本翻出来核算。
>
> 在生意红火柜台很忙时，掌柜一定会选择后者，因为前者操作实在是太麻烦了。首先，你得找到这个人的赊账总额那条记录。你想想，密密麻麻几十页，掌柜要找到那个名字，可能还得带上老花镜慢慢找，找到之后再拿出算盘计算，最后再将结果写回到账本上。
>
> 这整个过程想想都麻烦。相比之下，还是先在粉板上记一下方便。你想想，如果掌柜没有粉板的帮助，每次记账都得翻账本，效率是不是低得让人难以忍受？ 

而粉板和账本配合的整个过程，其实就是 MySQL 里经常说到的 **WAL 技术**，WAL 的全称是 Write-Ahead Logging，它的关键点就是先写日志，再写磁盘，也就是先写粉板，等不忙的时候再写账本。 

具体来说，当有一条记录需要更新的时候，InnoDB 引擎就会先把记录写到 redo log（粉板）里面，并更新内存，这个时候更新就算完成了。同时，InnoDB 引擎会在适当的时候，将这个操作记录更新到磁盘里面，而这个更新往往是在系统比较空闲的时候做，这就像打烊以后掌柜做的事。 

如果今天赊账的不多，掌柜可以等打烊后再整理。但如果某天赊账的特别多，粉板写满了，又怎么办呢？这个时候掌柜只好放下手中的活儿，把粉板中的一部分赊账记录更新到账本中，然后把这些记录从粉板上擦掉，为记新账腾出空间。

与此类似，InnoDB 的 redo log 是固定大小的，比如可以配置为一组 4 个文件，每个文件的大小是 1GB，那么这块“粉板”总共就可以记录 4GB 的操作。从头开始写，写到末尾就又回到开头循环写，如下面这个图所示。 

![](.\picture\16a7950217b3f0f4ed02db5db59562a7.png)

write pos 是当前记录的位置，一边写一边后移，写到第 3 号文件末尾后就回到 0 号文件开头。checkpoint 是当前要擦除的位置，也是往后推移并且循环的，擦除记录前要把记录更新到数据文件。

write pos 和 checkpoint 之间的是“粉板”上还空着的部分，可以用来记录新的操作。如果 write pos 追上 checkpoint，表示“粉板”满了，这时候不能再执行新的更新，得停下来先擦掉一些记录，把 checkpoint 推进一下。

有了 redo log，InnoDB 就可以保证即使数据库发生异常重启，之前提交的记录都不会丢失，这个能力称为 **crash-safe**。

要理解 crash-safe 这个概念，可以想想我们前面赊账记录的例子。只要赊账记录记在了粉板上或写在了账本上，之后即使掌柜忘记了，比如突然停业几天，恢复生意后依然可以通过账本和粉板上的数据明确赊账账目。 

#### 重要的日志模块：binlog 

前面我们讲过，MySQL 整体来看，其实就有两块：一块是 Server 层，它主要做的是 MySQL 功能层面的事情；还有一块是引擎层，负责存储相关的具体事宜。上面我们聊到的粉板 **redo log 是 InnoDB 引擎特有的日志**，而 Server 层也有自己的日志，称为 binlog（归档日志）。 

> 最开始 MySQL 里并没有 InnoDB 引擎。MySQL 自带的引擎是 MyISAM，但是 MyISAM 没有 crash-safe 的能力，binlog 日志只能用于归档。而 InnoDB 是另一个公司以插件形式引入 MySQL 的，既然只依靠 binlog 是没有 crash-safe 能力的，所以 InnoDB 使用另外一套日志系统——也就是 redo log 来实现 crash-safe 能力。

这两种日志有以下三点不同：

1. redo log 是 InnoDB 引擎特有的；binlog 是 MySQL 的 Server 层实现的，所有引擎都可以使用。
2. redo log 是物理日志，记录的是“在某个数据页上做了什么修改”；binlog 是逻辑日志，记录的是这个语句的原始逻辑，比如“给 ID=2 这一行的 c 字段加 1 ”。
3. redo log 是循环写的，空间固定会用完；binlog 是可以追加写入的。“追加写”是指 binlog 文件写到一定大小后会切换到下一个，并不会覆盖以前的日志。 

有了对这两个日志的概念性理解，我们再来看执行器和 InnoDB 引擎在执行这个简单的 update 语句时的内部流程：

1. 执行器先找引擎取 ID=2 这一行。ID 是主键，引擎直接用树搜索找到这一行。如果 ID=2 这一行所在的数据页本来就在内存中，就直接返回给执行器；否则，需要先从磁盘读入内存，然后再返回。
2. 执行器拿到引擎给的行数据，把这个值加上 1，比如原来是 N，现在就是 N+1，得到新的一行数据，再调用引擎接口写入这行新数据。
3. 引擎将这行新数据更新到内存中，同时将这个更新操作记录到 redo log 里面，此时 redo log 处于 prepare 状态。然后告知执行器执行完成了，随时可以提交事务。
4. 执行器生成这个操作的 binlog，并把 binlog 写入磁盘。
5. 执行器调用引擎的提交事务接口，引擎把刚刚写入的 redo log 改成提交（commit）状态，更新完成。 

![](.\picture\2e5bff4910ec189fe1ee6e2ecc7b4bbe.png)

##### 两阶段提交 

由于 redo log 和 binlog 是两个独立的逻辑，如果不用两阶段提交，要么就是先写完 redo log 再写 binlog，或者采用反过来的顺序。我们看看这两种方式会有什么问题。

```sql
mysql> update T set c=c+1 where ID=2;
```

假设当前 ID=2 的行，字段 c 的值是 0，再假设执行 update 语句过程中在写完第一个日志后，第二个日志还没有写完期间发生了 crash，会出现什么情况呢？

- **先写 redo log 后写 binlog。**假设在 redo log 写完，binlog 还没有写完的时候，MySQL 进程异常重启。由于我们前面说过的，redo log 写完之后，系统即使崩溃，仍然能够把数据恢复回来，所以恢复后这一行 c 的值是 1。但是由于 binlog 没写完就 crash 了，这时候 binlog 里面就没有记录这个语句。因此，之后备份日志的时候，存起来的 binlog 里面就没有这条语句。然后你会发现，如果需要用这个 binlog 来恢复临时库的话，由于这个语句的 binlog 丢失，这个临时库就会少了这一次更新，恢复出来的这一行 c 的值就是 0，与原库的值不同。
- **先写 binlog 后写 redo log。**如果在 binlog 写完之后 crash，由于 redo log 还没写，崩溃恢复以后这个事务无效，所以这一行 c 的值是 0。但是 binlog 里面已经记录了“把 c 从 0 改成 1”这个日志。所以，在之后用 binlog 来恢复的时候就多了一个事务出来，恢复出来的这一行 c 的值就是 1，与原库的值不同。 

redo log 和 binlog 都可以用于表示事务的提交状态，而两阶段提交就是让这两个状态保持逻辑上的一致。 

### 事务隔离：为什么你改了我还看不见？

#### 隔离性与隔离级别 

提到事务，你肯定会想到 ACID（Atomicity、Consistency、Isolation、Durability，即原子性、一致性、隔离性、持久性），今天我们就来说说其中 I，也就是“隔离性”。 

当数据库上有多个事务同时执行的时候，就可能出现脏读（dirty read）、不可重复读（non-repeatable read）、幻读（phantom read）的问题，为了解决这些问题，就有了“隔离级别”的概念。

在谈隔离级别之前，你首先要知道，你隔离得越严实，效率就会越低。因此很多时候，我们都要在二者之间寻找一个平衡点。SQL 标准的事务隔离级别包括：读未提交（read uncommitted）、读提交（read committed）、可重复读（repeatable read）和串行化（serializable ）。下面我逐一为你解释： 

- 读未提交是指，一个事务还没提交时，它做的变更就能被别的事务看到。
- 读提交是指，一个事务提交之后，它做的变更才会被其他事务看到。
- 可重复读是指，一个事务执行过程中看到的数据，总是跟这个事务在启动时看到的数据是一致的。当然在可重复读隔离级别下，未提交变更对其他事务也是不可见的。
- 串行化，顾名思义是对于同一行记录，“写”会加“写锁”，“读”会加“读锁”。当出现读写锁冲突的时候，后访问的事务必须等前一个事务执行完成，才能继续执行。 

```sql
insert into T(c) values(1);
```

![](.\picture\7dea45932a6b722eb069d2264d0066f8.png)

我们来看看在不同的隔离级别下，事务 A 会有哪些不同的返回结果，也就是图里面 V1、V2、V3 的返回值分别是什么。

- 若隔离级别是“读未提交”， 则 V1 的值就是 2。这时候事务 B 虽然还没有提交，但是结果已经被 A 看到了。因此，V2、V3 也都是 2。
- 若隔离级别是“读提交”，则 V1 是 1，V2 的值是 2。事务 B 的更新在提交后才能被 A 看到。所以， V3 的值也是 2。
- 若隔离级别是“可重复读”，则 V1、V2 是 1，V3 是 2。之所以 V2 还是 1，遵循的就是这个要求：事务在执行期间看到的数据前后必须是一致的。
- 若隔离级别是“串行化”，则在事务 B 执行“将 1 改成 2”的时候，会被锁住。直到事务 A 提交后，事务 B 才可以继续执行。所以从 A 的角度看， V1、V2 值是 1，V3 的值是 2。 

在实现上，数据库里面会**创建一个视图**，访问的时候以视图的逻辑结果为准。**在“可重复读”隔离级别下，这个视图是在事务启动时创建的**，整个事务存在期间都用这个视图。**在“读提交”隔离级别下，这个视图是在每个 SQL 语句开始执行的时候创建的**。这里需要注意的是，“读未提交”隔离级别下直接返回记录上的最新值，没有视图概念；而“串行化”隔离级别下直接用加锁的方式来避免并行访问。 

### 事务隔离的实现 

在 MySQL 中，实际上每条记录在更新的时候都会同时记录一条回滚操作(undo log)。记录上的最新值，通过回滚操作，都可以得到前一个状态的值。

假设一个值从 1 被按顺序改成了 2、3、4，在回滚日志里面就会有类似下面的记录。 

![](.\picture\d9c313809e5ac148fc39feff532f0fee.png)

当前值是 4，但是在查询这条记录的时候，不同时刻启动的事务会有不同的 read-view。如图中看到的，在视图 A、B、C 里面，这一个记录的值分别是 1、2、4，**同一条记录在系统中可以存在多个版本，就是数据库的多版本并发控制（MVCC）**。对于 read-view A，要得到 1，就必须将当前值依次执行图中所有的回滚操作得到。同时你会发现，即使现在有另外一个事务正在将 4 改成 5，这个事务跟 read-view A、B、C 对应的事务是不会冲突的。 

你一定会问，回滚日志总不能一直保留吧，什么时候删除呢？答案是，在不需要的时候才删除。也就是说，系统会判断，当没有事务再需要用到这些回滚日志时，回滚日志会被删除。**什么时候才不需要了呢？就是当系统里没有比这个回滚日志更早的 read-view 的时候。** (如上图所示，没有read-view A后，read-view B在不需要的时候才被删除 )

基于上面的说明，我们来讨论一下为什么建议你尽量**不要使用长事务**。长事务意味着系统里面会存在很老的事务视图。由于这些事务随时可能访问数据库里面的任何数据，所以这个事务提交之前，数据库里面它可能用到的回滚记录都必须保留，这就会导致大量占用存储空间。在 MySQL 5.5 及以前的版本，回滚日志是跟数据字典一起放在 ibdata 文件里的，即使长事务最终提交，回滚段被清理，文件也不会变小。我见过数据只有 20GB，而回滚段有 200GB 的库。最终只好为了清理回滚段，重建整个库。除了对回滚段的影响，长事务还占用锁资源，也可能拖垮整个库，这个我们会在后面讲锁的时候展开。 

#### 事务的启动方式 

如前面所述，长事务有这些潜在风险，我当然是建议你尽量避免。其实很多时候业务开发同学并不是有意使用长事务，通常是由于误用所致。MySQL 的事务启动方式有以下几种：

1. 显式启动事务语句， begin 或 start transaction。配套的提交语句是 commit，回滚语句是 rollback。
2. set autocommit=0，这个命令会将这个线程的自动提交关掉。意味着如果你只执行一个 select 语句，这个事务就启动了，而且并不会自动提交。这个事务持续存在直到你主动执行 commit 或 rollback 语句，或者断开连接。 

**建议使用 set autocommit=1, 通过显式语句的方式来启动事务。** 

但是有的开发同学会纠结“多一次交互”的问题。对于一个需要频繁使用事务的业务，第二种方式每个事务在开始时都不需要主动执行一次 “begin”，减少了语句的交互次数。如果你也有这个顾虑，我建议你使用 **commit work and chain** (提交并开启下一段事务)语法。 

你可以在 information_schema 库的 innodb_trx 这个表中查询长事务，比如下面这个语句，用于查找持续时间超过 60s 的事务。

```sql
select * from information_schema.innodb_trx where TIME_TO_SEC(timediff(now(),trx_started))>60 
```

### 深入浅出索引（上）

#### 索引的常见模型 

索引的出现是为了提高查询效率，但是实现索引的方式却有很多种，所以这里也就引入了索引模型的概念。可以用于提高读写效率的数据结构很多，这里我先给你介绍三种常见、也比较简单的数据结构，它们分别是**哈希表、有序数组和搜索树**。 

1. 哈希表是一种以键 - 值（key-value）存储数据的结构，我们只要输入待查找的键即 key，就可以找到其对应的值即 Value。哈希的思路很简单，把值放在数组里，用一个哈希函数把 key 换算成一个确定的位置，然后把 value 放在数组的这个位置。不可避免地，多个 key 值经过哈希函数的换算，会出现同一个值的情况。处理这种情况的一种方法是，拉出一个链表。 **哈希表这种结构适用于只有等值查询的场景**，不适合区间查询，比如 Memcached 及其他一些 NoSQL 引擎。 
2. 而**有序数组在等值查询和范围查询场景中的性能就都非常优秀**。还是上面这个根据身份证号查名字的例子，如果我们使用有序数组来实现的话，示意图如下所示： 如果仅仅看查询效率，有序数组就是最好的数据结构了。但是，**在需要更新数据的时候就麻烦了**，你往中间插入一个记录就必须得挪动后面所有的记录，成本太高。所以，**有序数组索引只适用于静态存储引擎**，比如你要保存的是 2017 年某个城市的所有人口信息，这类不会再修改的数据。 
3. 二叉搜索树的特点是：**父节点左子树所有结点的值小于父节点的值**，右子树所有结点的值大于父节点的值。时间复杂度是 O(log(N))。 当然为了维持 O(log(N)) 的查询复杂度，你就需要保持这棵树是平衡二叉树。为了做这个保证，更新的时间复杂度也是 O(log(N))。 **实际上大多数的数据库存储却并不使用二叉树**。其原因是，索引不止存在内存中，还要写到磁盘上。你可以想象一下一棵 100 万节点的平衡二叉树，树高 20。一次查询可能需要访问 20 个数据块。在机械硬盘时代，从磁盘随机读一个数据块需要 10 ms 左右的寻址时间。也就是说，对于一个 100 万行的表，如果使用二叉树来存储，单独访问一个行可能需要 20 个 10 ms 的时间，这个查询可真够慢的。 为了让一个查询尽量少地读磁盘，就必须让查询过程访问尽量少的数据块。那么，我们就不应该使用二叉树，**而是要使用“N 叉”树。这里，“N 叉”树中的“N”取决于数据块的大小。** 以 InnoDB 的一个整数字段索引为例，这个 N 差不多是 1200。这棵树高是 4 的时候，就可以存 1200 的 3 次方个值，这已经 17 亿了。考虑到树根的数据块总是在内存中的，一个 10 亿行的表上一个整数字段的索引，查找一个值最多只需要访问 3 次磁盘。其实，树的第二层也有很大概率在内存中，那么访问磁盘的平均次数就更少了。 

不管是哈希还是有序数组，或者 N 叉树，它们都是不断迭代、不断优化的产物或者解决方案。数据库技术发展到今天，跳表、LSM 树等数据结构也被用于引擎设计中，这里我就不再一一展开了。 

#### InnoDB 的索引模型 

在 InnoDB 中，表都是根据主键顺序以索引的形式存放的，这种存储方式的表称为索引组织表。又因为前面我们提到的，InnoDB 使用了 B+ 树索引模型，所以数据都是存储在 B+ 树中的。 

```sql
mysql> create table T(
id int primary key, 
k int not null, 
name varchar(16),
index (k))engine=InnoDB;
```

表中 R1~R5 的 (ID,k) 值分别为 (100,1)、(200,2)、(300,3)、(500,5) 和 (600,6)，两棵树的示例示意图如下。 

![](.\picture\dcda101051f28502bd5c4402b292e38d.png)

从图中不难看出，根据叶子节点的内容，索引类型分为主键索引和非主键索引。

- **主键索引的叶子节点存的是整行数据**。在 InnoDB 里，主键索引也被称为聚簇索引（clustered index）。
- **非主键索引的叶子节点内容是主键的值**。在 InnoDB 里，非主键索引也被称为二级索引（secondary index）。 

根据上面的索引结构说明，我们来讨论一个问题：**基于主键索引和普通索引的查询有什么区别？** 

- 如果语句是 select * from T where ID=500，即主键查询方式，则只需要搜索 ID 这棵 B+ 树；
- 如果语句是 select * from T where k=5，即普通索引查询方式，则需要先搜索 k 索引树，得到 ID 的值为 500，再到 ID 索引树搜索一次。**这个过程称为回表**。 

也就是说，基于非主键索引的查询需要多扫描一棵索引树。因此，**我们在应用中应该尽量使用主键查询**。 

#### 索引维护 

B+ 树为了维护索引**有序性**，在插入新值的时候需要做必要的维护。以上面这个图为例，如果插入新的行 ID 值为 700，则只需要在 R5 的记录后面插入一个新记录。如果新插入的 ID 值为 400，就相对麻烦了，需要逻辑上挪动后面的数据，空出位置。

而更糟的情况是，如果 R5 所在的数据页已经满了，根据 B+ 树的算法，这时候需要申请一个新的数据页，然后挪动部分数据过去。这个过程称为页分裂。在这种情况下，性能自然会受影响。 

除了性能外，页分裂操作还影响数据页的利用率。原本放在一个页的数据，现在分到两个页中，整体空间利用率降低大约 50%。 

当然有分裂就有合并。当相邻两个页由于删除了数据，利用率很低之后，会将数据页做合并。合并的过程，可以认为是分裂过程的逆过程。 

所以，从性能和存储空间方面考量，自增主键往往是更合理的选择。有没有什么场景适合用业务字段直接做主键的呢？还是有的。比如，有些业务的场景需求是这样的：

1. 只有一个索引；
2. 该索引必须是唯一索引。

你一定看出来了，这就是典型的 KV 场景。由于没有其他索引，所以也就不用考虑其他索引的叶子节点大小的问题。 

### 深入浅出索引（下）

由于辅助索引的值存的是主键的键，所以在通过辅助索引查询的时候，会现在辅助索引树查询，得到结果后再去主键索引树上得到结果，**回到主键索引树搜索的过程，我们称为回表。**

通过索引优化避免回表：

#### 覆盖索引 

将需要查询的结果放到辅助索引树上，在查询时可以直接返回结果，不需要回表，例如：

```sql
select ID from T where k between 3 and 5;
```

**由于覆盖索引可以减少树的搜索次数，显著提升查询性能，所以使用覆盖索引是一个常用的性能优化手段。** 

建立联合索引后（KEY(A,B)）查询B条件A不需要回表

#### 最左前缀原则 

**B+ 树这种索引结构，可以利用索引的“最左前缀”，来定位记录。** 

在查询条件中，最左条件中可以应用在前N个字段，也可以N个字符

##### 在建立联合索引的时候，如何安排索引内的字段顺序。 

这里我们的评估标准是，索引的复用能力。因为可以支持最左前缀，所以当已经有了 (a,b) 这个联合索引后，一般就不需要单独在 a 上建立索引了。因此，**第一原则是，如果通过调整顺序，可以少维护一个索引，那么这个顺序往往就是需要优先考虑采用的。** 

如果有各自的查询，b需要索引，那么只能新建索引了，**这时候，我们要考虑的原则就是空间了** 。字段小的单独创建索引。

#### 索引下推 

```sql
mysql> select * from tuser where name like '张%' and age=10 and ismale=1;
```



![无索引](.\picture\b32aa8b1f75611e0759e52f5915539ac.jpg)



![有索引](.\picture\76e385f3df5a694cc4238c7b65acfe1b.jpg)



可以在索引遍历过程中，对索引中包含的字段先做判断，直接过滤掉不满足条件的记录，减少回表次数。 

### 全局锁和表锁 ：给表加个字段怎么有这么多阻碍？

**根据加锁的范围，MySQL 里面的锁大致可以分成全局锁、表级锁和行锁三类。** 

#### 全局锁 

MySQL 提供了一个加全局读锁的方法，命令是 **Flush tables with read lock** (FTWRL)。之后其他线程的以下语句会被阻塞：数据更新语句（数据的增删改）、数据定义语句（包括建表、修改表结构等）和更新类事务的提交语句。 使用**unlock tables**可以解锁。

**全局锁的典型使用场景是，做全库逻辑备份。** 

但是让整库都只读，听上去就很危险：

- 如果你在主库上备份，那么在备份期间都不能执行更新，业务基本上就得停摆；
- 如果你在从库上备份，那么备份期间从库不能执行主库同步过来的 binlog，会导致主从延迟。 

官方自带的逻辑备份工具是 mysqldump。当 mysqldump 使用参数–single-transaction 的时候，导数据之前就会启动一个事务，来确保拿到一致性视图。而由于 MVCC 的支持，这个过程中数据是可以正常更新的。 

**一致性读是好，但前提是引擎要支持这个隔离级别。** MyISAM不支持。

**既然要全库只读，为什么不使用 set global readonly=true 的方式呢？** 

- 一是，在有些系统中，readonly 的值会被用来做其他逻辑，比如用来判断一个库是主库还是备库。因此，修改 global 变量的方式影响面更大，我不建议你使用。
- 二是，在异常处理机制上有差异。如果执行 FTWRL 命令之后由于客户端发生异常断开，那么 MySQL 会自动释放这个全局锁，整个库回到可以正常更新的状态。而将整个库设置为 readonly 之后，如果客户端发生异常，则数据库就会一直保持 readonly 状态，这样会导致整个库长时间处于不可写状态，风险较高。 

#### 表级锁

MySQL 里面表级别的锁有两种：一种是表锁，一种是元数据锁（meta data lock，MDL)。 

 **表锁的语法是lock tables … read/write**。举个例子, 如果在某个线程 A 中执行 lock tables t1 read, t2 write; 这个语句，则其他线程写 t1、读写 t2 的语句都会被阻塞。同时，线程 A 在执行 unlock tables 之前，也只能执行读 t1、读写 t2 的操作。连写 t1 都不允许，自然也不能访问其他表。 

**另一类表级的锁是 MDL（metadata lock)。**MDL 不需要显式使用，在访问一个表的时候会被自动加上。 为避免DDL和DML的冲突，DML加MDL读锁，DDL加MDL写锁。

如何安全的给表加字段：事务1查询拿到读锁，事务2想要进行DDL操作的时候需要等待事务1锁释放，如果此时还有别的DML操作，也会被阻塞住，因为这里设计的是公平锁，如果后进来的DML操作依然可以拿到读锁，那么DDL操作有可能会被饿死。

解决方法：插入前检查是否有长事务。或者使用语句：ALTER TABLE tbl_name NOWAIT add column ...ALTER TABLE tbl_name WAIT N add column ... ，加入等待时间





