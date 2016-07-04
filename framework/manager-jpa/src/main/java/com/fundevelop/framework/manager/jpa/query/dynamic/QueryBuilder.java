package com.fundevelop.framework.manager.jpa.query.dynamic;

import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.framework.manager.jpa.query.SearchFilter;
import com.fundevelop.persistence.entity.hibernate.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.transform.ResultTransformer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Transient;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 动态查询Query构造器.
 * <p>描述:根据查询结构类中的注解来动态构造Query对象</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/12 16:20
 */
public class QueryBuilder {
    /**
     * 获取Query构造器.
     */
    public static QueryBuilder getQueryBuilder(EntityManager entityManager, Class<? extends BaseEntity<?>> returnType) {
        return new QueryBuilder(entityManager, returnType);
    }

    /**
     * 根据Java属性名获取数据库表字段名称.
     */
    public static String getColumnName(String columnName) {
        return strategy.columnName(columnName);
    }

    /**
     * 创建查询对象.
     */
    public Query createQuery(List<SearchFilter> filters) {
        return createQuery(filters, false);
    }

    /**
     * 创建查询对象.
     */
    public Query createQuery(List<SearchFilter> filters, boolean distinct) {
        return createQuery(filters, (Sort)null, distinct);
    }

    /**
     * 创建查询对象.
     */
    public Query createQuery(List<SearchFilter> filters, Pageable pageable) {
        return createQuery(filters, pageable, false);
    }

    /**
     * 创建查询对象.
     */
    public Query createQuery(List<SearchFilter> filters, Pageable pageable, boolean distinct) {
        Query query = createQuery(filters, pageable==null?null:pageable.getSort(), distinct);

        if (pageable != null) {
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        return query;
    }

    /**
     * 创建查询对象.
     */
    public Query createQuery(List<SearchFilter> filters, Sort sort) {
        return createQuery(filters, sort, false);
    }

    /**
     * 创建查询对象.
     */
    public Query createQuery(List<SearchFilter> filters, Sort sort, boolean distinct) {
        processTables();
        processSelect();
        processFieldType();

        String whereSql = getWhere(filters);
        String sortSql = "";

        if (sort != null) {
            for (Iterator<Sort.Order> iterator = sort.iterator(); iterator.hasNext();) {
                Sort.Order order = iterator.next();
                String fields = processParams(order.getProperty(), null, false);

                if (fields != null) {
                    if (StringUtils.isEmpty(sortSql)) {
                        sortSql += " order by ";
                    } else {
                        sortSql += ",";
                    }

                    sortSql += fields + " " + order.getDirection().name();
                }
            }
        }

        // 先处理主表
        String fromSql = tableAliasMap.get(mainTableAlias) + " " + mainTableAlias;
        // 处理关联表
        processLinkTable();
        // 处理其他表
        fromSql += getJoinTables();
        // 处理左关联表
        fromSql += getLeftJoinTables();

        sql.append("select ").append(distinct?"distinct ":"").append(StringUtils.join(selectColumns, ","));
        sql.append(" from ").append(fromSql);

        countSql.append("select ");

        if (distinct) {
            countSql.append(" count(distinct ").append(mainTableAlias).append(".").append(mainTablePkColumn).append(") ");
        } else {
            countSql.append(" count(*) ");
        }

        countSql.append(" from ").append(fromSql);

        String joinSql = StringUtils.join(joinColumns, " and ");

        if (!StringUtils.isEmpty(joinSql)) {
            sql.append(" where ").append(joinSql);
            countSql.append(" where ").append(joinSql);
        }

        if (!StringUtils.isEmpty(whereSql)) {
            if (StringUtils.isEmpty(joinSql)) {
                sql.append(" where ");
                countSql.append(" where ");
            } else {
                sql.append(" and ");
                countSql.append(" and ");
            }

            sql.append(whereSql);
            countSql.append(whereSql);
        }

        sql.append(sortSql);

        Query query = entityManager.createNativeQuery(sql.toString());

        query.unwrap(SQLQuery.class).setResultTransformer(new ResultTransformer() {
            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                try {
                    Object object = returnType.newInstance();

                    for (int i=0; i < aliases.length; i++) {
                        setFieldValue(aliases[i].toLowerCase(), object, tuple[i]);
                    }

                    return object;
                } catch (Exception e) {
                    throw new DynamicQueryException("将动态查询结果放入查询计划Bean中出现异常", e);
                }
            }

            @SuppressWarnings("rawtypes")
            @Override
            public List transformList(List collection) {
                return collection;
            }
        });

        for (int i=0; i<params.size(); i++) {
            query.setParameter("param"+(i+1), params.get(i));
        }

        return query;
    }

    /**
     * 获取统计查询对象.
     */
    public Query getCountQuery() {
        if (countSql.length() == 0) {
            throw new DynamicQueryException("请先调用createQuery方法后再调用该方法.");
        }

        Query query = entityManager.createNativeQuery(countSql.toString());

        for (int i=0; i<params.size(); i++) {
            query.setParameter("param"+(i+1), params.get(i));
        }

        return query;
    }

    /**
     * 获取右连接的表.
     */
    private String getJoinTables() {
        StringBuffer joinTable = new StringBuffer("");

        if (fromTables.size() > 0) {
            if (usedLeftJoinTables.size() > 0) {
                for (String table : fromTables) {
                    joinTable.append(" join ").append(table).append(" ");
                }
            } else {
                joinTable.append("," + StringUtils.join(fromTables, ","));
            }
        }

        return joinTable.toString();
    }

    /**
     * 获取需要左连接的表.
     */
    private String getLeftJoinTables() {
        StringBuffer leftJoinTable = new StringBuffer("");

        if (usedLeftJoinTables.size() > 0) {
            for (String jtAlias : usedLeftJoinTables) {
                leftJoinTable.append(" left join ")
                        .append(tableAliasMap.get(jtAlias)).append(" ").append(jtAlias)
                        .append(" on ").append(StringUtils.join(tableJoinColumns.get(jtAlias), " and "))
                        .append(" ");
            }
        }

        return leftJoinTable.toString();
    }

    /**
     * 分析处理要查询的表.
     */
    private void processTables() {
        // 获取主表
        Table mainTable = returnType.getAnnotation(Table.class);

        if (mainTable != null) {
            mainTableAlias = strategy.tableName(mainTable.alias());
            mainTablePkColumn = strategy.columnName(mainTable.pkColumn());

            if (StringUtils.isEmpty(mainTableAlias)) {
                mainTableAlias = strategy.classToTableName(returnType.getName());
            }

            tableAliasMap.put(mainTableAlias, strategy.tableName(mainTable.name()));
        } else {
            throw new DynamicQueryException("无法确定查询的主表：没有在"+returnType.getName()+"中使用@Table注解指定主表");
        }

        // 获取关联表
        processJoinTable(returnType.getAnnotation(JoinTable.class));

        JoinTables joinTables = returnType.getAnnotation(JoinTables.class);

        if (joinTables != null) {
            for (JoinTable joinTable : joinTables.value()) {
                processJoinTable(joinTable);
            }
        }
    }

    /**
     * 解析关联表.
     * @param joinTable 关联表注解信息
     */
    private void processJoinTable(JoinTable joinTable) {
        if (joinTable != null) {
            String jtAlias = strategy.tableName(joinTable.alias());
            if (joinTable.name().indexOf(" from ") != -1) {
                tableAliasMap.put(jtAlias, joinTable.name());
            } else {
                tableAliasMap.put(jtAlias, strategy.tableName(joinTable.name()));
            }

            JoinColumn[] columns = joinTable.columns();
            List<String> joinColumns = new ArrayList<>();

            for (JoinColumn column : columns) {
                String col1 = mainTableAlias+"."+mainTablePkColumn;
                String col2 = jtAlias+"."+strategy.columnName(column.name());

                if (!"".equals(column.referencedColumnName())) {
                    int pos = column.referencedColumnName().indexOf(".");

                    if (pos != -1) {
                        String joinTableAlias = column.referencedColumnName().substring(0, pos);

                        if (!joinTableAlias.equals(jtAlias) && !joinTableAlias.equals(mainTableAlias)) {
                            List<String> linkTables = joinTableMap.get(jtAlias);

                            if (linkTables == null) {
                                linkTables = new ArrayList<>();
                                joinTableMap.put(jtAlias, linkTables);
                            }

                            linkTables.add(joinTableAlias);
                        }

                        col1 = joinTableAlias + "." + strategy.columnName(column.referencedColumnName().substring(pos+1));
                    } else {
                        col1 = mainTableAlias+"."+strategy.columnName(column.referencedColumnName());
                    }
                }

                int pos = column.name().indexOf(".");

                if (pos != -1) {
                    String joinTableAlias = column.name().substring(0, pos);

                    if (!joinTableAlias.equals(jtAlias) && !joinTableAlias.equals(mainTableAlias)) {
                        List<String> linkTables = joinTableMap.get(jtAlias);

                        if (linkTables == null) {
                            linkTables = new ArrayList<>();
                            joinTableMap.put(jtAlias, linkTables);
                        }

                        linkTables.add(joinTableAlias);
                    }

                    col2 = joinTableAlias + "." + strategy.columnName(column.name().substring(pos+1));
                }

                joinColumns.add(col1 + "=" + col2);
            }

            tableJoinColumns.put(jtAlias, joinColumns);

            if ("left".equalsIgnoreCase(joinTable.joinType())) {
                leftJoinTables.add(jtAlias);
            }
        }
    }

    /**
     * 处理关联表.
     */
    private void processLinkTable() {
        List<String> usedTablesT = new ArrayList<>(usedTables.size());
        boolean addLinkTable = false;

        do {
            addLinkTable = false;
            usedTablesT.clear();
            usedTablesT.addAll(usedTables);

            for (String useTable : usedTablesT) {
                if (joinTableMap.containsKey(useTable)) {
                    List<String> linkTables = joinTableMap.get(useTable);

                    for (String linkTable : linkTables) {
                        if (!usedTables.contains(linkTable) && !usedLeftJoinTables.contains(linkTable)) {
                            String fromTable = tableAliasMap.get(linkTable) + " " + linkTable;

                            if (!mainTableAlias.equals(linkTable)) {
                                if (leftJoinTables.contains(linkTable)) {
                                    if (!usedLeftJoinTables.contains(linkTable)) {
                                        usedLeftJoinTables.add(linkTable);
                                    }
                                } else if (!fromTables.contains(fromTable)) {
                                    addLinkTable = true;
                                    usedTables.add(linkTable);
                                    fromTables.add(fromTable);
                                    joinColumns.addAll(tableJoinColumns.get(linkTable));
                                }
                            }
                        }
                    }
                }
            }
        } while (addLinkTable);
    }

    /**
     * 分析处理要获取的字段.
     */
    private void processSelect() {
        IgnoreProperties ignoreProperties = returnType.getAnnotation(IgnoreProperties.class);
        Map<String, String> outMap = new HashMap<String, String>();

        if (ignoreProperties != null) {
            IgnoreProperty[] properties = ignoreProperties.value();

            for (IgnoreProperty pro : properties) {
                outMap.put(pro.name(), "");
            }
        }

        processBean(mainTableAlias, returnType, "", outMap);
    }

    /**
     * 解析Bean中的字段.
     * @param alias 表别名
     * @param beanClass 类
     * @param parentFieldname 上级字段名称
     * @param outMap 忽略字段集合
     */
    private void processBean(String alias, Class<?> beanClass, String parentFieldname, Map<String, String> outMap) {
        Field[] fields = getFields(beanClass);

        for (Field field : fields) {
            if (outMap==null || !outMap.containsKey(parentFieldname+field.getName())) {
                Column column = field.getAnnotation(Column.class);

                if (!field.getType().getName().startsWith("java") && !field.getType().isEnum()) {
                    String rTableAlias = field.getName().toLowerCase();

                    if (column != null && !"".equals(column.table())) {
                        rTableAlias = column.table().toLowerCase();
                    }

                    if (tableAliasMap.containsKey(rTableAlias)) {
                        processBean(rTableAlias, field.getType(), parentFieldname+field.getName()+".", outMap);
                    }

                    continue;
                }

                String columnName = field.getName();

                if (column != null && !"".equals(column.name())) {
                    columnName = column.name();

                    if (columnName.indexOf(".") != -1) {
                        String subTableAlias = columnName.split("[.]")[0];
                        String fromTable = tableAliasMap.get(subTableAlias) + " " + subTableAlias;

                        if (!mainTableAlias.equals(subTableAlias)) {
                            if (leftJoinTables.contains(subTableAlias)) {
                                processLeftJoinTable(subTableAlias);
                            } else if (!fromTables.contains(fromTable)) {
                                usedTables.add(subTableAlias);
                                fromTables.add(fromTable);
                                joinColumns.addAll(tableJoinColumns.get(subTableAlias));
                            }
                        }
                    }
                }

                //根据查询表字段动态加入查询表
                String jtAlias = strategy.tableName(alias);
                String fromTable = tableAliasMap.get(jtAlias) + " " + jtAlias;

                if (!mainTableAlias.equals(jtAlias)) {
                    if (leftJoinTables.contains(jtAlias)) {
                        processLeftJoinTable(jtAlias);
                    } else if (!fromTables.contains(fromTable)) {
                        usedTables.add(jtAlias);
                        fromTables.add(fromTable);
                        joinColumns.addAll(tableJoinColumns.get(jtAlias));
                    }
                }

                selectColumns.add(processColumn(strategy.columnName(columnName),alias) + " as " + strategy.columnName(parentFieldname.replace(".", "_")+field.getName()));
            }
        }
    }

    /**
     * 处理左连接表顺序.
     */
    private void processLeftJoinTable(String tableAlias) {
        if (!usedLeftJoinTables.contains(tableAlias)) {
            if (joinTableMap.containsKey(tableAlias)) {
                List<String> linkTables = joinTableMap.get(tableAlias);

                for (String linkTable : linkTables) {
                    if (!usedTables.contains(linkTable) && !usedLeftJoinTables.contains(linkTable)) {
                        String fromTable = tableAliasMap.get(linkTable) + " " + linkTable;

                        if (!mainTableAlias.equals(linkTable)) {
                            if (leftJoinTables.contains(linkTable)) {
                                processLeftJoinTable(linkTable);
                            }
                        }
                    }
                }
            }

            usedLeftJoinTables.add(tableAlias);
        }
    }

    /**
     * 解析字段.
     */
    private String processColumn(String column, String alias) {
        String tableAlias = "";
        String[] cols = column.split("_");
        String t = "";

        for (int i=0; i < cols.length; i++) {
            if (!"".equals(t)) {
                t += "_";
            }

            t += cols[i];

            if (tableAliasMap.containsKey(t)) {
                tableAlias = t;
                column = StringUtils.join(cols, "_", i+1, cols.length);
                break;
            }
        }

        if (StringUtils.isEmpty(tableAlias)) {
            tableAlias = alias;
        }

        if (!StringUtils.isEmpty(tableAlias)) {
            tableAlias += ".";
        }

        return tableAlias + column;
    }

    /**
     * 解析查询字段类型.
     */
    private void processFieldType() {
        processFieldType(returnType.getAnnotation(FieldType.class));

        FieldTypes fieldTypes = returnType.getAnnotation(FieldTypes.class);

        if (fieldTypes != null) {
            for (FieldType field : fieldTypes.value()) {
                processFieldType(field);
            }
        }
    }

    /**
     * 解析查询字段类型.
     * @param fieldType 字段类型注解
     */
    private void processFieldType(FieldType fieldType) {
        if (fieldType != null) {
            fieldTypeMap.put(fieldType.name().toLowerCase(), fieldType.type());
        }
    }

    /**
     * 根据字段名为Bean赋值.
     * @param column 字段名
     * @param bean Bean实例
     * @param value 值
     */
    private void setFieldValue(String column, Object bean, Object value) throws Exception {
        String fieldName = column;

        if (column.indexOf("_") != -1) {
            fieldName = fieldName(column2Field(column, bean.getClass()));
        }

        int pos = fieldName.indexOf(".");

        if (pos != -1) {
            Field field = getField(bean.getClass(), fieldName.substring(0,pos));

            if (field != null) {
                PropertyDescriptor property = org.springframework.beans.BeanUtils.getPropertyDescriptor(bean.getClass(),field.getName());

                if (property != null) {
                    Method readMethod = property.getReadMethod();
                    if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                        readMethod.setAccessible(true);
                    }
                    Object sbean = readMethod.invoke(bean);

                    if (sbean == null) {
                        sbean = field.getType().newInstance();
                    }

                    Method writeMethod = property.getWriteMethod();
                    if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                        writeMethod.setAccessible(true);
                    }
                    writeMethod.invoke(bean, sbean);

                    setFieldValue(fieldName.substring(pos+1), sbean, value);
                }
            }
        } else {
            Field field = getField(bean.getClass(), fieldName);

            if (field != null) {
                PropertyDescriptor property = org.springframework.beans.BeanUtils.getPropertyDescriptor(bean.getClass(),field.getName());

                if (property != null) {
                    Method writeMethod = property.getWriteMethod();
                    if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                        writeMethod.setAccessible(true);
                    }
                    writeMethod.invoke(bean, BeanUtils.convertValue(field.getType(), value));
                }
            }
        }
    }

    /**
     * 处理查询参数.
     * @param fieldName 字段名称
     * @param value 字段值
     * @param checkFieldType 是否验证数据类型
     * @return SQL中字段名称
     */
    private String processParams(String fieldName, Object value, boolean checkFieldType) {
        return processParams(fieldName, value, true, checkFieldType);
    }

    /**
     * 处理查询参数.
     * @param fieldName 字段名称
     * @param value 字段值
     * @param convertValue 是否对值进行类型转换
     * @param checkFieldType 是否验证数据类型
     * @return SQL中字段名称
     */
    private String processParams(String fieldName, Object value, boolean convertValue, boolean checkFieldType) {
        Map<String, Field> fieldMap = getFieldMap(returnType);
        Object[] fields = processField(fieldMap, fieldName, mainTableAlias, "");

        try {
            if (fields != null) {
                if (value != null) {
                    if (((Field)fields[0]).getType().isEnum()) {
                        Object v = convertValue?BeanUtils.convertValue(((Field)fields[0]).getType(), value):value;

                        if (EnumType.class.isInstance(v)) {
                            params.add(((EnumType)v).getCode());
                        } else {
                            params.add(v);
                        }
                    } else {
                        params.add(convertValue?BeanUtils.convertValue(((Field)fields[0]).getType(), value):value);
                    }
                }
                return (String)fields[1];
            } else {
                fields = processField(fieldName, checkFieldType);
                if (value != null) {
                    if (((Class<?>)fields[0]).isEnum()) {
                        Object v = convertValue?BeanUtils.convertValue((Class<?>)fields[0], value):value;

                        if (EnumType.class.isInstance(v)) {
                            params.add(((EnumType)v).getCode());
                        } else {
                            params.add(v);
                        }
                    } else {
                        params.add(convertValue?BeanUtils.convertValue((Class<?>)fields[0], value):value);
                    }
                }
                return (String)fields[1];
            }
        } catch (Exception ex) {
            throw new DynamicQueryException("构造["+fieldName+"]的查询条件时发生错误。", ex);
        }
    }

    /**
     * 解析字段.
     * @param fieldName 字段名
     * @param checkFieldType 是否验证数据类型
     */
    private Object[] processField(String fieldName, boolean checkFieldType) throws Exception {
        if (!checkFieldType || fieldTypeMap.containsKey(fieldName.toLowerCase())) {
            int pos = fieldName.indexOf(".");

            if (pos != -1) {
                String pFieldName = fieldName.substring(0,pos).toLowerCase();

                if (tableAliasMap.containsKey(pFieldName)) {
                    // 根据条件字段动态加入查询表
                    String jtAlias = strategy.tableName(pFieldName);
                    String fromTable = tableAliasMap.get(jtAlias) + " " + jtAlias;

                    if (!mainTableAlias.equals(jtAlias)) {
                        if (leftJoinTables.contains(jtAlias)) {
                            processLeftJoinTable(jtAlias);
                        } else if (!fromTables.contains(fromTable)) {
                            usedTables.add(jtAlias);
                            fromTables.add(fromTable);
                            joinColumns.addAll(tableJoinColumns.get(jtAlias));
                        }
                    }

                    return new Object[]{fieldTypeMap.get(fieldName.toLowerCase()), processColumn(strategy.columnName(fieldName.substring(pos+1)),pFieldName.toLowerCase())};
                } else {
                    throw new Exception(fieldName+"中使用的表别名不存在，请在类["+returnType.getName()+"]上添加@JoinTable或@JoinTables注解");
                }
            } else {
                return new Object[]{fieldTypeMap.get(fieldName.toLowerCase()), processColumn(strategy.columnName(fieldName),mainTableAlias)};
            }
        }

        throw new Exception("无法获取字段["+fieldName+"]的数据类型，请在类["+returnType.getName()+"]上添加注解@FieldType来进行说明.");
    }

    /**
     * 解析字段.
     * @param fieldMap Bean中的属性集合
     * @param fieldName 字段名
     * @param tableAlias 表别名
     * @param parentFieldname 上级字段名
     */
    private Object[] processField(Map<String, Field> fieldMap, String fieldName, String tableAlias, String parentFieldname) {
        int pos = fieldName.indexOf(".");

        if (pos != -1) {
            String pFieldName = fieldName.substring(0,pos);
            Field field =  fieldMap.get(pFieldName);

            if (field != null) {
                if (!field.getType().getName().startsWith("java") && !field.getType().isEnum()) {
                    Column column = field.getAnnotation(Column.class);
                    String rTableAlias = field.getName().toLowerCase();

                    if (column != null && !"".equals(column.table())) {
                        rTableAlias = column.table().toLowerCase();
                    }

                    if (tableAliasMap.containsKey(rTableAlias)) {
                        // 根据条件字段动态加入查询表
                        String jtAlias = strategy.tableName(rTableAlias);
                        String fromTable = tableAliasMap.get(jtAlias) + " " + jtAlias;

                        if (!mainTableAlias.equals(jtAlias)) {
                            if (leftJoinTables.contains(jtAlias)) {
                                processLeftJoinTable(jtAlias);
                            } else if (!fromTables.contains(fromTable)) {
                                usedTables.add(jtAlias);
                                fromTables.add(fromTable);
                                joinColumns.addAll(tableJoinColumns.get(jtAlias));
                            }
                        }

                        return processField(getFieldMap(field.getType()), fieldName.substring(pos+1), rTableAlias, parentFieldname+field.getName()+".");
                    }
                }
            } else {
                logger.warn("在查询计划类["+returnType.getName()+"]中没有发现属性："+fieldName);
            }
        } else {
            Field field =  fieldMap.get(fieldName);

            if (field != null) {
                Column column = field.getAnnotation(Column.class);
                String columnName = fieldName;

                if (column != null && !"".equals(column.name())) {
                    columnName = column.name();
                }

                // 根据条件字段动态加入查询表
                String jtAlias = strategy.tableName(tableAlias);
                String fromTable = tableAliasMap.get(jtAlias) + " " + jtAlias;

                if (!mainTableAlias.equals(jtAlias)) {
                    if (leftJoinTables.contains(jtAlias)) {
                        processLeftJoinTable(jtAlias);
                    } else if (!fromTables.contains(fromTable)) {
                        usedTables.add(jtAlias);
                        fromTables.add(fromTable);
                        joinColumns.addAll(tableJoinColumns.get(jtAlias));
                    }
                }

                return new Object[]{field, processColumn(strategy.columnName(columnName),tableAlias), strategy.columnName(parentFieldname.replace(".", "_")+field.getName())};
            } else {
                logger.warn("在查询计划类["+returnType.getName()+"]中没有发现属性："+fieldName);
            }
        }

        return null;
    }

    /**
     * 创建WHERE条件SQL语句.
     */
    private String getWhere(List<SearchFilter> filters) {
        List<String> columns = new ArrayList<>(filters.size());

        if (filters != null && !filters.isEmpty()) {
            for (SearchFilter filter : filters) {
                String whereEpr = processFilter(filter);

                if (StringUtils.isNotBlank(whereEpr)) {
                    columns.add(whereEpr);
                }
            }
        }

        return StringUtils.join(columns, " and ");
    }

    private String processFilter(SearchFilter filter) {
        String whereEpr = buildFilter(filter);

        if (filter.hasOrFilter()) {
            List<String> columns = new ArrayList<>(filter.getOrFilters().size()+1);

            if (StringUtils.isNotBlank(whereEpr)) {
                columns.add(whereEpr);
            }

            for (SearchFilter orFilter : filter.getOrFilters()) {
                whereEpr = buildFilter(orFilter);

                if (StringUtils.isNotBlank(whereEpr)) {
                    columns.add(whereEpr);
                }
            }

            return "(" + StringUtils.join(columns, " or ") + ")";
        } else {
            return whereEpr;
        }
    }

    private String buildFilter(SearchFilter filter) {
        String whereEpr = null;
        boolean isLeftJoinTableColumn = false;
        String fieldName = filter.fieldName;

        if (fieldName.indexOf("@lj") != -1) {
            isLeftJoinTableColumn = true;
            fieldName = fieldName.replace("@lj", "");
        }

        switch (filter.operator) {
            case EQ:
                whereEpr = equal(fieldName, filter.value);
                break;
            case NE:
                whereEpr = notEqual(fieldName, filter.value);
                break;
            case LT:
                whereEpr = lessThan(fieldName, filter.value);
                break;
            case LE:
                whereEpr = lessThanOrEqualTo(fieldName, filter.value);
                break;
            case GT:
                whereEpr = greaterThan(fieldName, filter.value);
                break;
            case GE:
                whereEpr = greaterThanOrEqualTo(fieldName, filter.value);
                break;
            case IN:
                whereEpr = in(fieldName, filter.value);
                break;
            case NI:
                whereEpr = notIn(fieldName, filter.value);
                break;
            case CN:
                whereEpr = like(fieldName, "%" + filter.value + "%");
                break;
            case NC:
                whereEpr = notLike(fieldName, "%" + filter.value + "%");
                break;
            case NU:
                whereEpr = isNull(fieldName);
                break;
            case NN:
                whereEpr = isNotNull(fieldName);
                break;
            case BLANK:
                whereEpr = isBlank(fieldName);
                break;
            case NBLANK:
                whereEpr = isNotBlank(fieldName);
                break;
            default:
                throw new RuntimeException("未知的查询比较符");
        }

        if (StringUtils.isNotBlank(whereEpr)) {
            if (isLeftJoinTableColumn) {
                if (!usedLeftJoinTables.isEmpty()) {
                    if (fieldName.indexOf(".") != -1) {
                        String jtAlias = fieldName.split("[.]")[0];

                        if (usedLeftJoinTables.contains(jtAlias)) {
                            addFilterToLeftJoinColumns(jtAlias, whereEpr);
                        }
                    } else {
                        Map<String, Field> fieldMap = getFieldMap(returnType);
                        Object[] fields = processField(fieldMap, fieldName, mainTableAlias, "");
                        String fName = (String)fields[1];

                        if (fName.indexOf(".") != -1) {
                            String jtAlias = fName.split("[.]")[0];

                            if (usedLeftJoinTables.contains(jtAlias)) {
                                addFilterToLeftJoinColumns(jtAlias, whereEpr);
                            }
                        }
                    }
                }

                return null;
            }
        }

        return whereEpr;
    }

    private void addFilterToLeftJoinColumns(String jtAlias, String filter) {
        List<String> joinColumns = tableJoinColumns.get(jtAlias);

        if (joinColumns == null) {
            joinColumns = new ArrayList<>();
        }

        joinColumns.add(filter);

        tableJoinColumns.put(jtAlias, joinColumns);
    }

    private String equal(String fieldName, Object value) {
        return processParams(fieldName, value) + " = :param"+params.size();
    }

    private String notEqual(String fieldName, Object value) {
        return processParams(fieldName, value) + " <> :param"+params.size();
    }

    private String notLike(String fieldName, String value) {
        return processParams(fieldName, value, false) + " not like :param"+params.size();
    }

    private String like(String fieldName, String value) {
        return processParams(fieldName, value, false) + " like :param"+params.size();
    }

    private String greaterThan(String fieldName, Object value) {
        return processParams(fieldName, value) + " > :param"+params.size();
    }

    private String lessThan(String fieldName, Object value) {
        return processParams(fieldName, value) + " < :param"+params.size();
    }

    private String greaterThanOrEqualTo(String fieldName, Object value) {
        return processParams(fieldName, value) + " >= :param"+params.size();
    }

    private String lessThanOrEqualTo(String fieldName, Object value) {
        return processParams(fieldName, value) + " <= :param"+params.size();
    }

    private String notIn(String fieldName, Object value) {
        return processParams(fieldName, value, false, true) + " not in (:param"+params.size()+")";
    }

    private String in(String fieldName, Object value) {
        return processParams(fieldName, value, false, true) + " in (:param"+params.size()+")";
    }

    private String isNull(String fieldName) {
        return processParams(fieldName, null) + " is null";
    }

    private String isNotNull(String fieldName) {
        return processParams(fieldName, null) + " is not null";
    }

    private String isBlank(String fieldName) {
        return processParams(fieldName, null) + " = ''";
    }

    private String isNotBlank(String fieldName) {
        return processParams(fieldName, null) + " != ''";
    }

    /**
     * 处理查询参数.
     * @param fieldName 字段名称
     * @param value 字段值
     * @return SQL中字段名称
     */
    private String processParams(String fieldName, Object value) {
        return processParams(fieldName, value, true, true);
    }

    /**
     * 将字段名转为属性名.
     * @param column 字段名
     * @param bean Bean对应的类名
     * @return 属性名
     */
    private static String column2Field(String column, Class<?> bean) {
        String fieldName = "";
        String[] cols = column.split("_");

        for (int i=cols.length-1; i>=0; i--) {
            String t = column2Field(cols, i);

            if (getField(bean, t) != null) {
                fieldName = t;
                column = StringUtils.join(cols, "_", i+1, cols.length);
                break;
            }
        }

        if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(column)) {
            fieldName += ".";
        }

        return fieldName + column;
    }

    /**
     * 将字段名转换为属性名.
     * @param cols 字段名分段名称数组
     * @param endIndex 拼接结束索引
     * @return 属性名
     */
    private static String column2Field(String[] cols, int endIndex) {
        String fieldName = null;

        if (cols != null && cols.length > 0) {
            if (endIndex>=cols.length) {
                endIndex = cols.length - 1;
            }

            fieldName = cols[0];

            for (int i=1; i <= endIndex; i++) {
                fieldName += cols[i].substring(0, 1).toUpperCase();
                fieldName += cols[i].substring(1);
            }
        }

        return fieldName;
    }

    /**
     * 将字段名转成属性名.
     * @param column 字段名
     * @return 属性名
     */
    private static String fieldName(String column) {
        StringBuilder sb = new StringBuilder();
        String[] cols = column.split("_");
        sb.append(cols[0]);

        for (int i=1; i<cols.length; i++) {
            sb.append(cols[i].substring(0, 1).toUpperCase());
            sb.append(cols[i].substring(1));
        }

        return sb.toString();
    }

    /**
     * 获取类中属性信息.
     * @param clazz 类
     * @param fieldName 属性名
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        if (clazz != null && fieldName != null && !"".equals(fieldName.trim())) {
            Map<String, Field> fieldMap = getFieldMap(clazz);
            int pos = fieldName.indexOf(".");

            if (pos != -1) {
                String pFieldName = fieldName.substring(0,pos);
                Field field =  fieldMap.get(pFieldName);

                if (field != null) {
                    return getField(field.getType(), fieldName.substring(pos+1));
                }
            } else {
                return fieldMap.get(fieldName);
            }
        }

        return null;
    }

    /**
     * 获取类中的所有属性（包含父类）.
     * 排除serialVersionUID及使用@Transient注解属性
     */
    public static Field[] getFields(Class<?> clazz) {
        Map<String, Field> fieldMap = getFieldMap(clazz);
        Field[] fields = new Field[fieldMap.values().size()];
        fieldMap.values().toArray(fields);

        return fields;
    }

    /**
     * 获取类中的所有属性（包含父类）.
     * 排除serialVersionUID及使用@Transient注解属性
     */
    public static Map<String, Field> getFieldMap(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>();
        getFields(clazz, fieldMap);

        return fieldMap;
    }

    /**
     * 获取类中的所有属性（包含父类）.
     * 排除serialVersionUID及使用@Transient注解属性
     */
    private static void getFields(Class<?> clazz, Map<String, Field> fieldMap) {
        Map<String, Field> cached = null;

        synchronized (classFieldCache) {
            cached = classFieldCache.get(clazz);
        }
        if (cached != null) {
            fieldMap.putAll(cached);
            return;
        }

        if (clazz.getSuperclass() != null) {
            getFields(clazz.getSuperclass(), fieldMap);
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if ("serialVersionUID".equals(field.getName())) continue;
            if (field.getAnnotation(Transient.class) != null) continue;

            PropertyDescriptor property = org.springframework.beans.BeanUtils.getPropertyDescriptor(clazz,field.getName());

            if (property == null) continue;

            Method readMethod = property.getReadMethod();

            if (readMethod != null && readMethod.getAnnotation(Transient.class) != null) continue;

            fieldMap.put(field.getName(), field);
        }

        Map<String, Field> fieldMapT = new HashMap<>();
        fieldMapT.putAll(fieldMap);

        synchronized (classFieldCache) {
            classFieldCache.put(clazz, fieldMapT);
        }
    }

    /**
     * 构造函数.
     */
    private QueryBuilder(EntityManager entityManager, Class<? extends BaseEntity<?>> returnType) {
        this.entityManager = entityManager;
        this.returnType = returnType;
    }

    private Logger logger = Logger.getLogger(getClass());
    private static final NamingStrategy strategy = ImprovedNamingStrategy.INSTANCE;
    /** 类属性缓存. */
    private static final Map<Class<?>, Map<String, Field>> classFieldCache = new HashMap<>();

    private EntityManager entityManager;
    /** 结果封装Bean. */
    private Class<? extends BaseEntity<?>> returnType;

    /** 存储使用的表. */
    private Map<String, String> tableAliasMap = new HashMap<>(2);
    /** 存储表关联字段. */
    private Map<String, List<String>> tableJoinColumns = new HashMap<>(2);
    /** 主表别名. */
    private String mainTableAlias;
    /** 主表主键字段名. */
    private String mainTablePkColumn = "id";

    /** 关联字段. */
    private List<String> joinColumns = new ArrayList<>();
    /** 表关联. */
    private Map<String, List<String>> joinTableMap = new HashMap<>();
    /** 查询表. */
    private List<String> fromTables = new ArrayList<>();
    /** 查询使用到的表. */
    private List<String> usedTables = new ArrayList<>();
    /** 查询使用到的左关联表. */
    private List<String> usedLeftJoinTables = new ArrayList<>();
    /** 左关联表. */
    private List<String> leftJoinTables = new ArrayList<>();
    /** 选择字段. */
    private List<String> selectColumns = new ArrayList<>();
    /** 字段类型. */
    private Map<String, Class<?>> fieldTypeMap = new HashMap<>();

    /** SQL语句. */
    private StringBuilder sql = new StringBuilder();
    private StringBuilder countSql = new StringBuilder();
    /** 查询参数. */
    private List<Object> params = new ArrayList<>();
}
