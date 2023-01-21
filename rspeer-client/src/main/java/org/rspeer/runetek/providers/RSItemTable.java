package org.rspeer.runetek.providers;

import org.rspeer.runetek.api.commons.ArrayUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public interface RSItemTable extends RSNode {

    int[] getIds();

	int[] getStackSizes();

	default boolean contains(int... idsToFind) {
	    int[] ids = getIds();
	    if (ids == null) {
	        return false;
        }
        for (int id : ids) {
	        for (int idToFind : idsToFind) {
	            if (id == idToFind) {
	                return true;
                }
            }
        }
        return false;
    }

    default boolean containsAll(int... idsToFind) {
        int[] ids = getIds();
        if (ids == null) {
            return false;
        }
        for (int id : idsToFind) {
            if (!contains(id)) {
                return false;
            }
        }
        return true;
    }

    default int getCount(boolean includeStacks, int... idsToFind) {
	    int[] stacks = getStackSizes();
	    int[] ids = getIds();
	    if (ids == null || stacks == null) {
	        return 0;
        }

        int count = 0;
        for (int i = 0; i < ids.length; i++) {
	        int currentId = ids[i];
	        int currentStack = stacks[i];
	        if (ArrayUtils.contains(idsToFind, currentId)){
                count += includeStacks ? currentStack : 1;
            }
        }
        return count;
    }
}