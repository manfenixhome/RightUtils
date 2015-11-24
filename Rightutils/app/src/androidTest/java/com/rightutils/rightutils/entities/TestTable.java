package com.rightutils.rightutils.entities;

import com.github.andreyrage.leftdb.annotation.ColumnDAO;
import com.github.andreyrage.leftdb.annotation.TableName;
import com.rightutils.rightutils.collections.RightList;

/**
 * app
 * Created by Slawa on 29.09.2015.
 */
@TableName("TestTable")
public class TestTable {

    @ColumnDAO
    RightList<Worker> workers;

    public TestTable() {
    }

    public TestTable(RightList<Worker> workers) {
        this.workers = workers;
    }

    public RightList<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(RightList<Worker> workers) {
        this.workers = workers;
    }

}
