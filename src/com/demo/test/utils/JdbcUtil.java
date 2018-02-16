package com.demo.test.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

public class JdbcUtil {
	public static Logger logger = Logger.getLogger(JdbcUtil.class.getName());
	// 创建静态全局变量
	static Connection conn;
	static Statement statement;

	public static Connection getConnection() {
		String jdbc_url = null;
		String jdbc_db = null;
		String jdbc_driver = null;
		String jdbc_name = null;
		String jdbc_password = null;
		try {
			Properties prop = new Properties();
			// InputStream inStream =
			// JdbcUtil.class.getResourceAsStream("config/database.properties");
			InputStream inStream = new FileInputStream(new File("config/database.properties"));
			prop.load(inStream);
			jdbc_url = prop.getProperty("jdbc_url");
			jdbc_db = prop.getProperty("jdbc_db");
			jdbc_driver = prop.getProperty("jdbc_driver");
			jdbc_name = prop.getProperty("jdbc_name");
			jdbc_password = prop.getProperty("jdbc_password");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Connection con = null; // 创建用于连接数据库的Connection对象
		try {
			Class.forName(jdbc_driver);// 加载Mysql数据驱动
			// con = DriverManager.getConnection(jdbc_url + jdbc_db, jdbc_name,
			// jdbc_password);// 创建数据连接
			con = DriverManager.getConnection(jdbc_url + jdbc_db + "?user=" + jdbc_name + "&password=" + jdbc_password + "&useUnicode=true&characterEncoding=utf-8");// 创建数据连接
		} catch (Exception e) {
			System.out.println("数据库连接失败" + e.getMessage());
		}
		return con; // 返回所建立的数据库连接
	}

	/* 插入数据记录，并输出插入的数据记录数 */
	public static void insert(String sql) {
		conn = getConnection(); // 首先要获取连接，即连接到数据库
		try {
			logger.info("jdbc插入数据开始");

			statement = (Statement) conn.createStatement(); // 创建用于执行静态sql语句的Statement对象
			int count = statement.executeUpdate(sql); // 执行插入操作的sql语句，并返回插入数据的个数
			System.out.println("更新 " + count + " 条数据"); // 输出插入操作的处理结果
			statement.close();
			conn.close(); // 关闭数据库连接
			logger.info("jdbc插入数据结束");
		} catch (SQLException e) {
			System.out.println("插入数据失败" + e.getMessage());
		}
	}

	/* 更新符合要求的记录，并返回更新的记录数目 */
	public static void update(String sql) {
		conn = getConnection(); // 同样先要获取连接，即连接到数据库
		try {
			logger.info("jdbc更新数据开始");
			// String sql =
			// "update staff set wage='2200' where name = 'lucy'";// 更新数据的sql语句
			statement = (Statement) conn.createStatement(); // 创建用于执行静态sql语句的Statement对象，st属局部变量
			int count = statement.executeUpdate(sql);// 执行更新操作的sql语句，返回更新数据的个数
			System.out.println("更新 " + count + " 条数据"); // 输出更新操作的处理结果
			statement.close();
			conn.close(); // 关闭数据库连接
			logger.info("jdbc更新数据结束");
		} catch (SQLException e) {
			System.out.println("更新数据失败");
		}
	}

	/* 查询数据库，输出符合要求的记录的情况 */
	public static String query(String sql,String queryContent) {
		conn = getConnection(); // 同样先要获取连接，即连接到数据库
		 String queryContentTemp = "";
		try {
			statement = (Statement) conn.createStatement(); // 创建用于执行静态sql语句的Statement对象，st属局部变量
			ResultSet rs = statement.executeQuery(sql); // 执行sql查询语句，返回查询数据的结果集
			while (rs.next()) { // 判断是否还有下一个数据
				// 根据字段名获取相应的值
				queryContentTemp = rs.getString(queryContent);
			}
			rs.close();
			statement.close();
			conn.close(); // 关闭数据库连接
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return queryContentTemp;
	}

	/* 删除符合要求的记录，输出情况 */
	public static void delete(String sql) {
		conn = getConnection(); // 同样先要获取连接，即连接到数据库
		try {
			logger.info("jdbc删除数据开始");
			// String sql = "delete from staff  where name = 'lili'";//
			// 删除数据的sql语句
			statement = (Statement) conn.createStatement(); // 创建用于执行静态sql语句的Statement对象，st属局部变量
			int count = statement.executeUpdate(sql);// 执行sql删除语句，返回删除数据的数量
			System.out.println("agent表中删除了【 " + count + " 】条数据。"); // 输出删除操作的处理结果
			statement.close();
			conn.close(); // 关闭数据库连接
			logger.info("jdbc删除数据结束");
		} catch (SQLException e) {
			System.out.println("删除数据失败");
		}
	}
}
