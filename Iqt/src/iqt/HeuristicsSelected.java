/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iqt;

/**
 *
 * @author Arlino
 */
public class HeuristicsSelected {
    
    private boolean havingToWhereSelected, removeGroupbySelected, moveFunctionSelected, orToUnionSelected, 
                    allToSubquerySelected, anyToSubquerySelected, someToSubquerySelected, inToJoinSelected, 
                    moveAtithmetcExpressionSelected, removeDistinctSelected, temporaryTableToSubQuerySelected;
    private boolean havingToWhereExecuted, removeGroupbyExecuted, moveFunctionExecuted, orToUnionExecuted, 
                    allToSubqueryExecuted, anyToSubqueryExecuted, someToSubqueryExecuted, inToJoinExecuted, 
                    moveAtithmetcExpressionExecuted, removeDistinctExecuted, temporaryTableToSubQueryExecuted;
    
    public void setAllToNotExecuted(){
        this.havingToWhereExecuted = false;
        this.removeGroupbyExecuted = false;
        this.moveFunctionExecuted = false;
        this.orToUnionExecuted = false;
        this.allToSubqueryExecuted = false;
        this.anyToSubqueryExecuted = false;
        this.someToSubqueryExecuted = false;
        this.inToJoinExecuted = false;
        this.moveAtithmetcExpressionExecuted = false;
        this.removeDistinctExecuted = false;
        this.temporaryTableToSubQueryExecuted = false;
    }
    
    public void setAllSelected(){
        this.setAllToSubquerySelected(true);
        this.setAnyToSubquerySelected(true);
        this.setHavingToWhereSelected(true);
        this.setInToJoinSelected(true);
        this.setMoveAtithmetcExpressionSelected(true);
        this.setMoveFunctionSelected(true);
        this.setOrToUnionSelected(true);
        this.setRemoveDistinctSelected(true);
        this.setRemoveGroupbySelected(true);
        this.setSomeToSubquerySelected(true);
        this.setTemporaryTableToSubQuerySelected(true);
    }

    public boolean isTemporaryTableToSubQueryExecuted() {
        return temporaryTableToSubQueryExecuted;
    }

    public void setTemporaryTableToSubQueryExecuted(boolean temporaryTableToSubQueryExecuted) {
        this.temporaryTableToSubQueryExecuted = temporaryTableToSubQueryExecuted;
    }

    public boolean isTemporaryTableToSubQuerySelected() {
        return temporaryTableToSubQuerySelected;
    }

    public void setTemporaryTableToSubQuerySelected(boolean temporaryTableToSubQuerySelected) {
        this.temporaryTableToSubQuerySelected = temporaryTableToSubQuerySelected;
    }

    public boolean isHavingToWhereSelected() {
        return havingToWhereSelected;
    }

    public void setHavingToWhereSelected(boolean havingToWhereSelected) {
        this.havingToWhereSelected = havingToWhereSelected;
    }

    public boolean isAllToSubquerySelected() {
        return allToSubquerySelected;
    }

    public void setAllToSubquerySelected(boolean allToSubquery) {
        this.allToSubquerySelected = allToSubquery;
    }

    public boolean isAnyToSubquerySelected() {
        return anyToSubquerySelected;
    }

    public void setAnyToSubquerySelected(boolean anyToSubquerySelected) {
        this.anyToSubquerySelected = anyToSubquerySelected;
    }

    public boolean isInToJoinSelected() {
        return inToJoinSelected;
    }

    public void setInToJoinSelected(boolean inToJoinSelected) {
        this.inToJoinSelected = inToJoinSelected;
    }

    public boolean isMoveAtithmetcExpressionSelected() {
        return moveAtithmetcExpressionSelected;
    }

    public void setMoveAtithmetcExpressionSelected(boolean moveAtithmetcExpressionSelected) {
        this.moveAtithmetcExpressionSelected = moveAtithmetcExpressionSelected;
    }

    public boolean isMoveFunctionSelected() {
        return moveFunctionSelected;
    }

    public void setMoveFunctionSelected(boolean moveFunctionSelected) {
        this.moveFunctionSelected = moveFunctionSelected;
    }

    public boolean isOrToUnionSelected() {
        return orToUnionSelected;
    }

    public void setOrToUnionSelected(boolean orToUnion) {
        this.orToUnionSelected = orToUnion;
    }

    public boolean isRemoveDistinctSelected() {
        return removeDistinctSelected;
    }

    public void setRemoveDistinctSelected(boolean removeDistinctSelected) {
        this.removeDistinctSelected = removeDistinctSelected;
    }

    public boolean isRemoveGroupbySelected() {
        return removeGroupbySelected;
    }

    public void setRemoveGroupbySelected(boolean removeGroupby) {
        this.removeGroupbySelected = removeGroupby;
    }

    public boolean isSomeToSubquerySelected() {
        return someToSubquerySelected;
    }

    public void setSomeToSubquerySelected(boolean someToSubquery) {
        this.someToSubquerySelected = someToSubquery;
    }

    public boolean isAllToSubqueryExecuted() {
        return allToSubqueryExecuted;
    }

    public void setAllToSubqueryExecuted(boolean allToSubqueryExecuted) {
        this.allToSubqueryExecuted = allToSubqueryExecuted;
    }

    public boolean isAnyToSubqueryExecuted() {
        return anyToSubqueryExecuted;
    }

    public void setAnyToSubqueryExecuted(boolean anyToSubqueryExecuted) {
        this.anyToSubqueryExecuted = anyToSubqueryExecuted;
    }

    public boolean isHavingToWhereExecuted() {
        return havingToWhereExecuted;
    }

    public void setHavingToWhereExecuted(boolean havingToWhereExecuted) {
        this.havingToWhereExecuted = havingToWhereExecuted;
    }

    public boolean isInToJoinExecuted() {
        return inToJoinExecuted;
    }

    public void setInToJoinExecuted(boolean inToJoinExecuted) {
        this.inToJoinExecuted = inToJoinExecuted;
    }

    public boolean isMoveAtithmetcExpressionExecuted() {
        return moveAtithmetcExpressionExecuted;
    }

    public void setMoveAtithmetcExpressionExecuted(boolean moveAtithmetcExpressionExecuted) {
        this.moveAtithmetcExpressionExecuted = moveAtithmetcExpressionExecuted;
    }

    public boolean isMoveFunctionExecuted() {
        return moveFunctionExecuted;
    }

    public void setMoveFunctionExecuted(boolean moveFunctionExecuted) {
        this.moveFunctionExecuted = moveFunctionExecuted;
    }

    public boolean isOrToUnionExecuted() {
        return orToUnionExecuted;
    }

    public void setOrToUnionExecuted(boolean orToUnionExecuted) {
        this.orToUnionExecuted = orToUnionExecuted;
    }

    public boolean isRemoveDistinctExecuted() {
        return removeDistinctExecuted;
    }

    public void setRemoveDistinctExecuted(boolean removeDistinctExecuted) {
        this.removeDistinctExecuted = removeDistinctExecuted;
    }

    public boolean isRemoveGroupbyExecuted() {
        return removeGroupbyExecuted;
    }

    public void setRemoveGroupbyExecuted(boolean removeGroupbyExecuted) {
        this.removeGroupbyExecuted = removeGroupbyExecuted;
    }

    public boolean isSomeToSubqueryExecuted() {
        return someToSubqueryExecuted;
    }

    public void setSomeToSubqueryExecuted(boolean someToSubqueryExecuted) {
        this.someToSubqueryExecuted = someToSubqueryExecuted;
    }
}