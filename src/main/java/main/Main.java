package main;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.api.java.utils.ParameterTool;
import static main.Sqls.*;

public class Main {

    public static void main(String[] args) {
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(
                StreamExecutionEnvironment.getExecutionEnvironment(),
                EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build()
        );

        ParameterTool parameter = ParameterTool.fromArgs(args);
        String destination_host = parameter.get("dest_host", "127.0.0.1");
        String source_host = parameter.get("source_host", "127.0.0.1");

        //test(tEnv, source_host, destination_host);
        tpcc(tEnv, source_host, destination_host);
    }
    static void test(StreamTableEnvironment tEnv, String source_host, String destination_host) {
        tEnv.executeSql(getDDL("base") + KafkaSource(source_host, "test", "base"));
        tEnv.executeSql(getDDL("stuff") + KafkaSource(source_host, "test", "stuff"));
        tEnv.executeSql(getDDL("wide_stuff") + JDBCSink(destination_host, "test", "wide_stuff"));

        printSource(tEnv, "base");
        printSource(tEnv, "stuff");
        tEnv.executeSql(getDDL("print_wide_stuff", "wide_stuff") + PrintSink());

        tEnv.executeSql("insert into print_base select * from base");
        tEnv.executeSql("insert into print_stuff select * from stuff");

        Table t = tEnv.sqlQuery(
                "select stuff.stuff_id, base.base_id, base.base_location, stuff.stuff_name\n" +
                "from stuff inner join base\n" +
                "on stuff.stuff_base_id = base.base_id"
        );
        t.executeInsert("wide_stuff");
        t.executeInsert("print_wide_stuff");
    }

    static void tpcc(StreamTableEnvironment tEnv, String source_host, String destination_host) {
        String[] tpccSourceTableNames = {"customer", "district", "history", "item", "new_order", "order_line", "orders", "stock", "warehouse"};
        for(String tableName: tpccSourceTableNames) {
            tEnv.executeSql(getDDL(tableName) + KafkaSource(source_host, "tpcc", tableName));
        }

        String[] tpccSinkTableNames = {"wide_customer_warehouse", "wide_new_order", "wide_order_line_district"};
        for(String tableName: tpccSinkTableNames) {
            tEnv.executeSql(getDDL(tableName) + JDBCSink(destination_host, "tpcc", tableName));
        }

        tEnv.sqlQuery(
                "select ol_o_id, ol_d_id, ol_w_id, ol_number, ol_i_id, ol_supply_w_id," +
                        " ol_delivery_d, ol_quantity, ol_amount, ol_dist_info, d_name," +
                        " d_street_1, d_street_2, d_city, d_state, d_zip, d_tax, d_ytd, d_next_o_id " +
                "from order_line left join district " +
                "on order_line.ol_d_id = district.d_id " +
                "and order_line.ol_w_id = district.d_w_id"
        ).executeInsert("wide_order_line_district");

        tEnv.sqlQuery(
                "select no_o_id, no_d_id, no_w_id, d_name, d_street_1, d_street_2, d_city, " +
                "d_state, d_zip, d_tax, d_ytd, d_next_o_id, w_name, w_street_1, " +
                "w_street_2, w_city, w_state, w_zip, w_tax, w_ytd " +
                        "from new_order left join district " +
                        "on new_order.no_d_id = district.d_id " +
                        "and new_order.no_w_id = district.d_w_id " +
                "left join warehouse " +
                "on new_order.no_w_id = warehouse.w_id"
        ).executeInsert("wide_new_order");

        tEnv.sqlQuery(
                "select c_id, c_d_id, c_w_id, c_first, c_middle, c_last, c_street_1, c_street_2," +
                        " c_city, c_state, c_zip, c_phone, c_since, c_credit, c_credit_lim," +
                        " c_discount, c_balance, c_ytd_payment, c_payment_cnt, c_delivery_cnt," +
                        " c_data, w_name, w_street_1, w_street_2, w_city, w_state, w_zip, w_tax, w_ytd " +
                "from customer left join warehouse " +
                "on customer.c_w_id=warehouse.w_id"
        ).executeInsert("wide_customer_warehouse");
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
