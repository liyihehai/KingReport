function checkhandphone(mobil){//检验手机号码方法
	if(!(/^1[0-9]{10}$/.test(mobil))){
		return false;
  	}
	return true;
}