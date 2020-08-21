package main;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;

public class Main {

    public static void main(String[] args) {
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(
                StreamExecutionEnvironment.getExecutionEnvironment(),
                EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build()
        );

        tEnv.executeSql("create table base (\n" +
                "\tbase_id int primary key,\n" +
                "\tbase_location varchar(20)\n" +
                ") WITH (\n" +
                "\t'connector' = 'kafka',\n" +
                "\t'topic' = 'test-base',\n" +
                "\t'properties.group.id' = 'testGroup',\n" +
                "\t'scan.startup.mode' = 'latest-offset',\n" +
                "\t'properties.bootstrap.servers' = '127.0.0.1:9092',\n" +
                "\t'format' = 'canal-json',\n" +
                "\t'canal-json.ignore-parse-errors'='true'\n" +
                ")"
        );
        tEnv.executeSql("create table stuff(\n" +
                "\tstuff_id int primary key,\n" +
                "\tstuff_base_id int,\n" +
                "\tstuff_name varchar(20)\n" +
                ") WITH (\n" +
                "\t'connector' = 'kafka',\n" +
                "\t'topic' = 'test-stuff',\n" +
                "\t'properties.group.id' = 'testGroup',\n" +
                "\t'scan.startup.mode' = 'latest-offset',\n" +
                "\t'properties.bootstrap.servers' = '127.0.0.1:9092',\n" +
                "\t'format' = 'canal-json',\n" +
                "\t'canal-json.ignore-parse-errors'='true'\n" +
                ")"
        );
        tEnv.executeSql("create table wide_stuff(\n" +
                "\tstuff_id int primary key,\n" +
                "\tbase_id int,\n" +
                "\tbase_location varchar(20),\n" +
                "\tstuff_name varchar(20)\n" +
                ") WITH (\n" +
                "\t'connector'  = 'jdbc',\n" +
                "\t'url'        = 'jdbc:mysql://127.0.0.1:4000/test?rewriteBatchedStatements=true',\n" +
                "\t'table-name' = 'wide_stuff',\n" +
                "\t'driver'     = 'com.mysql.cj.jdbc.Driver',\n" +
                "\t'username'   = 'root',\n" +
                "\t'password'   = ''\n" +
                ")"
        );
        printSource(tEnv, "base");
        printSource(tEnv, "stuff");

        tEnv.executeSql("CREATE TABLE print_wide_stuff WITH ('connector' = 'print') LIKE wide_stuff (EXCLUDING ALL)");
        Table t = tEnv.sqlQuery(
                "select stuff.stuff_id, base.base_id, base.base_location, stuff.stuff_name\n" +
                    "from stuff inner join base\n" +
                    "on stuff.stuff_base_id = base.base_id"
        );
        t.executeInsert("wide_stuff");
        t.executeInsert("print_wide_stuff");
    }

    /**
     * 将源表的任何更改打印在屏幕上，原理是创建一个名为 print_${source}, sink = print 的表
     * @param tEnv 表环境
     * @param source 表名：必须已经被创建，必须是 source 表
     */
    static void printSource(StreamTableEnvironment tEnv, String source) {
        tEnv.executeSql("CREATE TABLE print_" + source + " WITH ('connector' = 'print') LIKE " + source + " (EXCLUDING ALL)");
        tEnv.from(source).executeInsert("print_" + source);
    }
}


/*
测试语句：

delete from base;
delete from stuff;

insert into base values (1, 'beijing');
insert into stuff values (1, 1, 'zz');
insert into stuff values (2, 1, 't');
insert into base values (2, 'shanghai');
insert into stuff values (3, 2, 'qq');
update stuff set stuff_name = 'qz' where stuff_id = 3;
delete from stuff where stuff_name = 't';
*/
