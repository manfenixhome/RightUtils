package com.rightutils.rightutils.entities;

import com.rightutils.rightutils.collections.RightList;
import com.rightutils.rightutils.db.ColumnDAO;
import com.rightutils.rightutils.db.TableName;

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
