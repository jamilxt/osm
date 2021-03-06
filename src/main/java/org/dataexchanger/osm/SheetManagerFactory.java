package org.dataexchanger.osm;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetManagerFactory {

    private SheetManager sheetManager;
    private List<Map<String,String>> exported ;

    public SheetManagerFactory(SheetManager sheetManager) {
        this.exported = new ArrayList<>();
        this.sheetManager = sheetManager;
    }

    public <T> void export(List<T> objects) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        String className = objects.get(0).getClass().getName();
        Map<String, List<String>> columnNamesMap = sheetManager.getMappedColumnNames();
        List<String> columnNames = columnNamesMap.get(className);
        for (T object : objects) {
            Map<String, String> columnValue = new HashMap<>();
            for (String columnName : columnNames) {
                Class clazz = object.getClass();
                String value = "";
                if (columnName.contains("_")) {
                    String propertyName = columnName.split("_")[0];
                    Object obj = clazz.getMethod(getMethodName(propertyName)).invoke(object);
                    String aggragatedClassName = obj.getClass().getName();
                    Class aggragatedClass = Class.forName(aggragatedClassName);
                    value = aggragatedClass.getMethod("getId").invoke(obj).toString();

                }
                else {
                    value = clazz.getMethod(getMethodName(columnName)).invoke(object).toString();
                }
                columnValue.put(columnName, value);
            }
            exported.add(columnValue);
        }
        System.out.println(exported.toString());
    }

    private String getMethodName(String columnName) {
        StringBuilder methodNameBuilder = new StringBuilder();
        return methodNameBuilder.append("get")
                .append(columnName.substring(0, 1).toUpperCase())
                .append(columnName.substring(1))
                .toString();

    }
}
