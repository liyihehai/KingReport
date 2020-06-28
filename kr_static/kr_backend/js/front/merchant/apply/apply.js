function getApplyWaysName(applyWays) {
    if (!applyWays || applyWays<=0 || applyWays>4)
        return '未知';
    else if (applyWays == 1)
        return '操作员申请';
    else if (applyWays == 2)
        return '网站自助申请';
    else if (applyWays == 3)
        return 'APP自助申请';
    else if (applyWays == 4)
        return '业务员申请';
}
function getApplyStateName(applyState){
    if (applyState==undefined || applyState==null || applyState<-1 || applyState>4)
        return '未知';
    else if (applyState == -1)
        return '已删除';
    else if (applyState == 0)
        return '编辑';
    else if (applyState == 1)
        return '已通过';
    else if (applyState == 2)
        return '待审核';
    else if (applyState == 3)
        return '未通过';
    else if (applyState == 4)
        return '待分配';
}
