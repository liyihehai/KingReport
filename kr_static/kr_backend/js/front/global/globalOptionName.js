var GlobalOptionName={
    getPmCompanyPersonName:function (pmCompanyPerson){
    if (!pmCompanyPerson || pmCompanyPerson<=0 || pmCompanyPerson>2)
        return '未知';
    else if (pmCompanyPerson == 1)
        return '公司';
    else if (pmCompanyPerson == 2)
        return '个人';
    }
}