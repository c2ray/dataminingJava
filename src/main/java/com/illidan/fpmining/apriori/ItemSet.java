package com.illidan.fpmining.apriori;

import java.util.ArrayList;

/**
 * @author Illidan
 */
public class ItemSet extends ArrayList<Integer> {
    
    private static final long serialVersionUID = 1760328779973519942L;
    
    @Override
    public boolean equals(Object o) {
        // todo 如果每个元素都相同, 则表示相同
        if (o instanceof ArrayList) {
            return ((ArrayList<?>) o).containsAll(this);
        }
        return super.equals(o);
    }
}
