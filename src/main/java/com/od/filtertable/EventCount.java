package com.od.filtertable;

/**
* User: nick
* Date: 03/07/13
* Time: 19:07
*/
public class EventCount {
    
    int structureChangeEvents;
    int dataChangeEvents;
    int updateEvents;
    int rowInsertEvents;
    int rowDeleteEvents;

    public int getDataChangeEvents() {
        return dataChangeEvents;
    }

    public int getRowDeleteEvents() {
        return rowDeleteEvents;
    }

    public int getRowInsertEvents() {
        return rowInsertEvents;
    }

    public int getStructureChangeEvents() {
        return structureChangeEvents;
    }

    public int getUpdateEvents() {
        return updateEvents;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder("EventCount-[");
        sb.append("structureChange:").append(structureChangeEvents);
        sb.append("dataChange:").append(dataChangeEvents);
        sb.append("updates:").append(updateEvents);
        sb.append("rowInsert").append(rowInsertEvents);
        sb.append("rowDelete").append(rowDeleteEvents);
        sb.append("]");
        return sb.toString();
    }
}
