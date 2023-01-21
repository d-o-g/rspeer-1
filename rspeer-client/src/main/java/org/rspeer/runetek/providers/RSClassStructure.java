package org.rspeer.runetek.providers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface RSClassStructure extends RSNode {
    int[] getErrors();
	Field[] getFields();
	Method[] getMethods();
	byte[][][] getMethodArgs();
}