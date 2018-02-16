package com.demo.test.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import jxl.read.biff.BiffException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;

/*
 * 
 * @author 留仙洞_sageliu
 * @Description 自动生成测试代码的工具类,生成所有模块的测试类
 *
 */
public class TestCaseFactoryForAll {
	public static void main(String[] args) {

		final String caseFolder = "src/com/demo/test/testcase/";
		File sourceFile = null;
		String sheetName = null;
		int sheetNum = 0;
		for (int i = 0; i < getFunctionNum(); i++) { // 第一层循环 取得模块的个数
			try {

				String functionName = getFunctionName(i);// 获得每轮循环的 模块名

				functionName = functionName.replaceFirst(
						functionName.substring(0, 1),
						functionName.substring(0, 1).toLowerCase());
				// 如果包名不存在，就新建
				File functionPackage = new File(caseFolder + "/" + functionName);
				if (functionPackage.exists()) {
					System.out.println(functionName + "包已经存在，自动跳过！");
					System.out.println("正在生成用例到" + functionName + "包下，请稍等...");
				} else {
					functionPackage.mkdir();
					System.out.println(functionName + "包已创建！");
					System.out.println("正在生成用例到" + functionName + "包下，请稍等...");
				}

				for (int j = 0; j < getSheetNum(getExcelRelativePath(i)); j++) { // 第二层循环，根据传入的模块文件路径获得
																					// 模块中sheet数量
																					// 也就是用例个数
					if (j == getSheetNum(getExcelRelativePath(i)) - 1) {

						break;
					}
					try {
						sheetName = getSheetName(j + 1, getExcelRelativePath(i)); // 取得sheetName
																					// 只进行一次全局异常捕获
						sheetNum = getSheetNum(getExcelRelativePath(i));
					} catch (BiffException e1) {
						e1.printStackTrace();
					}
					sourceFile = new File(caseFolder
							+ functionName.replaceFirst(functionName.substring(
									0, 1), functionName.substring(0, 1)
									.toLowerCase())
							+ File.separator
							+ functionName.replaceFirst(functionName.substring(
									0, 1), functionName.substring(0, 1)
									.toUpperCase()) + "Page_" + sheetName
							+ "_Test.java");// 创建测试用例源码，指定存放路径
					FileWriter writer = new FileWriter(sourceFile);

					// 生成测试用例代码的头文件
					writer.write("package com.demo.test.testcase."
							+ functionName
							+ "; \n"
							+ "import org.testng.annotations.Test; \n"
							+ "import com.demo.test.base.BaseParpare; \n "
							+ "import com.demo.test.utils.SuperAction; \n"
							+ "public class "
							+ functionName.replaceFirst(functionName.substring(
									0, 1), functionName.substring(0, 1)
									.toUpperCase()) + "Page_" + sheetName
							+ "_Test extends BaseParpare{ \n");

					// @Test的主体部分，也就是测试用例的方法
					String firstLetter = sheetName.substring(
							sheetName.indexOf("_") + 1).substring(0, 1);
					String others = sheetName.substring(
							sheetName.indexOf("_") + 1).substring(1);
					String function = firstLetter.toLowerCase() + others;
					writer.write("@Test \n"
							+ " public void"
							+ " "
							+ function
							+ "() { \n"
							+ "SuperAction.parseExcel(\""
							+ functionName.replaceFirst(functionName.substring(
									0, 1), functionName.substring(0, 1)
									.toUpperCase()) + "\",\"" + sheetName
							+ "\",seleniumUtil);\n" + " }\n");

					// 代码结尾大括号
					writer.write("}");
					writer.close();
				}
			} catch (IOException e) {
				Assert.fail("IO异常", e);
			}
			System.out.println("模块[" + getFunctionName(i) + "] 的用例已经生成完毕，共计："
					+ (sheetNum - 1) + "条，请到" + caseFolder
					+ getFunctionName(i).toLowerCase() + "路径下查阅！");
		}

	}

	/*
	 * 获得当前路径下模块个数
	 */
	public static int getFunctionNum() {
		String path = "res/testcase";
		File file = new File(path);
		File[] array = file.listFiles();
		return array.length;
	}

	/*
	 * 获得模块名字 也就是excel 表名
	 */
	public static String getFunctionName(int index) {
		String excelCasePath = "res/testcase";
		String functionName = "";
		File file = new File(excelCasePath);

		File[] array = file.listFiles();
		if (array[index].isFile()) {
			functionName = array[index].getName().substring(0,
					array[index].getName().lastIndexOf("."));
		}

		return functionName;
	}

	/*
	 * 获得excel的相对路径
	 */
	public static String getExcelRelativePath(int index) {
		String path = "res/testcase";
		String functionName = "";
		File file = new File(path);
		File[] array = file.listFiles();
		functionName = array[index].getPath();
		return functionName;
	}

	/*
	 * 获得当前excel的sheet数量 - 每个模块的用例数
	 */
	public static int getSheetNum(String filePath)
			throws FileNotFoundException, IOException {
		int casesNum = 0;
		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(
				filePath)));
		casesNum = workbook.getNumberOfSheets();

		return casesNum;
	}

	public static String getSheetName(int sheetIndex, String filePath)
			throws BiffException, IOException {
		String casesName = "";
		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(filePath));
		casesName = workbook.getSheetName(sheetIndex);

		return casesName;

	}

}
