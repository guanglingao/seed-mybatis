package org.seed.mybatis.core.ext.code.util;



import org.seed.mybatis.core.SeedMybatisConfig;
import org.seed.mybatis.core.annotation.*;
import org.seed.mybatis.core.ext.code.generator.LogicDeleteDefinition;
import org.seed.mybatis.core.util.ClassUtil;
import org.seed.mybatis.core.util.StringUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;


public class FieldUtil {

    private FieldUtil() {}


	private static final String DOT = ".";
    
	/**
	 * 过滤"."
	 * 
	 * @param field 字段名
	 * @return 过滤‘.’符号
	 */
	public static String dotFilter(String field) {
		if (isNotEmpty(field) && field.contains(DOT)) {
			String[] words = field.split("\\.");
			StringBuilder ret = new StringBuilder();
			for (String str : words) {
				ret.append(upperFirstLetter(str));
			}
			return ret.toString();
		}
		return field;
	}

	/**
	 * 将第一个字母转换成大写
	 * 
	 * @param str 内容
	 * @return 返回原字符串且第一个字符大写
	 */
	public static String upperFirstLetter(String str) {
		if (isNotEmpty(str)) {
			String firstUpper = String.valueOf(str.charAt(0)).toUpperCase();
			str = firstUpper + str.substring(1);
		}
		return str;
	}

	/**
	 * 将第一个字母转换成小写
	 * 
	 * @param str 内容
	 * @return 返回原字符串且第一个字母小写
	 */
	public static String lowerFirstLetter(String str) {
		if (isNotEmpty(str)) {
			String firstLower = String.valueOf(str.charAt(0)).toLowerCase();
			str = firstLower + str.substring(1);
		}
		return str;
	}
	
	public static final char UNDERLINE = '_';

	/**
	 * 驼峰转下划线
	 * @param param 内容
	 * @return 返回转换后的字符串
	 */
	public static String camelToUnderline(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			int preIndex = i - 1;
			int nextIndex = i + 1;
			// 是否需要变为小写字母
			boolean needToLower = (
					Character.isUpperCase(c) 
					&& preIndex > 0
					&& Character.isLowerCase(param.charAt(preIndex))
				)
				||
				(
						Character.isUpperCase(c) 
						&& nextIndex < len 
						&& Character.isLowerCase(param.charAt(nextIndex))
				);
			
			if (needToLower) {
				if(i > 0) {
					sb.append(UNDERLINE);
				}
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String formatField(String field) {
		// t.`username`
		String[] arr = field.split("\\.");
		if (arr.length == 2) {
			field = arr[1];
		}
		// 去除首尾`（mysql）
		field = StringUtil.trimLeadingCharacter(field, '`');
		field = StringUtil.trimTrailingCharacter(field, '`');
		// 去除首尾"（pgsql）
		field = StringUtil.trimLeadingCharacter(field, '"');
		field = StringUtil.trimTrailingCharacter(field, '"');
		// 去除首尾[]（SqlServer）
		field = StringUtil.trimLeadingCharacter(field, '[');
		field = StringUtil.trimTrailingCharacter(field, ']');
		return field;
	}

	/**
	 * 下划线转驼峰
	 * @param param 内容
	 * @return 返回转换后的字符串
	 */
	public static String underlineToCamel(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (c == UNDERLINE) {
				if (++i < len) {
					sb.append(Character.toUpperCase(param.charAt(i)));
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 字段是否被transient关键字修饰或有@Transient注解
	 * @param field 字段
	 * @return 是返回true
	 */
	public static boolean isTransientField(Field field) {
		return Modifier.isTransient(field.getModifiers());
	}

	private static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}
	
	private static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}

	public static boolean isPk(Field field, SeedMybatisConfig config) {
		String pkName = FieldUtil.getPkAnnotation(field)
				.map(PrimaryKey::name)
				.orElse("");
		if ("".equals(pkName)) {
			pkName = config.getGlobalIdName();
		}
		return pkName.equalsIgnoreCase(FieldUtil.camelToUnderline(field.getName()));
	}

	/**
	 * 是否是主键并且没有指定主键策略
	 * @param field 字段
	 * @return true：是
	 */
	public static boolean isPkStrategyNone(Field field, SeedMybatisConfig config) {
		if (!isPk(field, config)) {
			return false;
		}
		return getPkStrategy(field)
				.orElse(KeyStrategy.AUTO) == KeyStrategy.NONE;
	}

	public static Optional<Table> getTableAnnotation(Field field) {
		Class<?> entityClass = field.getDeclaringClass();
		return getTableAnnotation(entityClass);
	}

	public static Optional<Table> getTableAnnotation(Class<?> entityClass) {
		Table table = ClassUtil.findAnnotation(entityClass, Table.class);
		return Optional.ofNullable(table);
	}

	/**
	 * 返回主键策略
	 * @param field 字段
	 * @return 主键策略
	 */
	public static Optional<KeyStrategy> getPkStrategy(Field field) {
		return getPkAnnotation(field).map(PrimaryKey::strategy);
	}

	/**
	 * 返回主键配置
	 * @param field 字段
	 * @return 主键策略
	 */
	public static Optional<PrimaryKey> getPkAnnotation(Field field) {
		return getTableAnnotation(field).map(Table::key);
	}

	/**
	 * 是否主键自增
	 * @param field 字段
	 * @return true：是
	 */
	public static boolean isIncrement(Field field) {
		return getPkStrategy(field).orElse(KeyStrategy.NONE) == KeyStrategy.AUTO;
	}

	/**
	 * 是否是UUID字段
	 * @param field 字段
	 * @return true：是
	 */
	public static boolean isUuid(Field field) {
		return getPkStrategy(field)
				.orElse(KeyStrategy.NONE) == KeyStrategy.UUID;
	}

	/**
	 * 是否是seq字段
	 * @param field 字段
	 * @return true：是
	 */
	public static boolean isSequence(Field field) {
		return isNotEmpty(getSequenceName(field));
	}

	/**
	 * 返回seq名称
	 * @param field 字段
	 * @return 没有返回null
	 */
	public static String getSequenceName(Field field) {
		return getTableAnnotation(field)
				.map(Table::key)
				.map(PrimaryKey::sequenceName)
				.orElse(null);
	}

	/**
	 * 是否是乐观锁字段
	 */
	public static boolean isVersionColumn(Field field) {
		return getColumnAnnotation(field)
				.map(Column::version)
				.orElse(false);
	}

	public static String getColumnName(Field field, SeedMybatisConfig config) {
		String name = getColumnAnnotation(field)
				.map(Column::name)
				.orElse(null);
		// 没有注解使用java字段名
		if (isEmpty(name)) {
			String javaFieldName = field.getName();
			// 如果开启了驼峰转下划线形式
			name = config.isCamel2underline() ? FieldUtil.camelToUnderline(field.getName()) : javaFieldName;
		}
		return name;
	}

	public static Optional<Column> getColumnAnnotation(Field field) {
		return Optional.ofNullable(field.getAnnotation(Column.class));
	}

	public static boolean hasTableAnnotation(Field field) {
		Class<?> declaringClass = field.getDeclaringClass();
		return ClassUtil.findAnnotation(declaringClass, Table.class) != null;
	}

	/**
	 * 懒加载对象字段名
	 */
	public static String getLazyEntityColumnName(Field field, SeedMybatisConfig config) {
		Boolean lazy = getColumnAnnotation(field)
				.map(Column::lazyFetch)
				.orElse(false);
		if (lazy) {
			return getColumnName(field, config);
		}
		LazyFetch annotation = field.getAnnotation(LazyFetch.class);
		if (annotation != null) {
			String column = annotation.column();
			if ("".equals(column)) {
				throw new IllegalArgumentException("必须指定LazyFetch.column值");
			}
		}
		return null;
	}

	public static LogicDeleteDefinition getLogicDeleteDefinition(Field field) {
		Column column = field.getAnnotation(Column.class);
		if (column != null && column.logicDelete()) {
			LogicDeleteDefinition logicDeleteDefinition = new LogicDeleteDefinition();
			logicDeleteDefinition.setDeleteValue(column.deleteValue());
			logicDeleteDefinition.setNotDeleteValue(column.notDeleteValue());
			return logicDeleteDefinition;
		}
		LogicDelete logicDelete = field.getAnnotation(LogicDelete.class);
		if (logicDelete != null) {
			LogicDeleteDefinition logicDeleteDefinition = new LogicDeleteDefinition();
			logicDeleteDefinition.setDeleteValue(logicDelete.deleteValue());
			logicDeleteDefinition.setNotDeleteValue(logicDelete.notDeleteValue());
			return logicDeleteDefinition;
		}
		return null;
	}
}
