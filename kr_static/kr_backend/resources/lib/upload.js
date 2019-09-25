var upload = upload || {};

upload.uploadFile = function(file,success,successFN,fail,outtime,timeout){
	//loading.show("上传中");
  if(!file){console.error('lack Parameters');return false;}
  outtime = outtime || 15000;
  var xhr = new XMLHttpRequest();
  var formdata = new FormData();
  formdata.append('file',file);
  formdata.append('app_key', "2e9f62408333988bf55f1a2c4c75f538");
	formdata.append('version', "v1");
  formdata.append('filename',file.name);
  formdata.append('filesize',file.size);
  formdata.append('filetype',file.type);
  formdata.append('lastModified',file.lastModified);
  formdata.append('lastModifiedDate',file.lastModifiedDate);
  xhr.onreadystatechange = function(){
      if(xhr.readyState === 4){
          if(xhr.status === 200){
              success && success.apply(null,[xhr.responseText]);
              if (successFN)
              	successFN();
          }else{
              fail && fail.apply(null,[xhr.responseText]);
          }
      }
//    loading.hide();
  };
  xhr.open('POST', "http://img.qianjb.com:8080/formUpload.action");
  xhr.timeout = outtime;
  xhr.ontimeout = function () { 
      timeout && timeout.apply(null);
  };
  xhr.send(formdata);
};

upload.getToken = function(){
	var xhr = new XMLHttpRequest();
  var formdata = new FormData();
  formdata.append('file',file);
  formdata.append('uptoken', "2e9f62408333988bf55f1a2c4c75f538:");
	formdata.append('version', "v1");
  formdata.append('filename',file.name);
  formdata.append('filesize',file.size);
  formdata.append('filetype',file.type);
  formdata.append('lastModified',file.lastModified);
  formdata.append('lastModifiedDate',file.lastModifiedDate);
  xhr.open('GET', "http://img.qianjb.com:8080/token.action");
  xhr.onreadystatechange = function(){
      if(xhr.readyState === 4){
          if(xhr.status === 200){
              success && success.apply(null,[xhr.responseText]);
              if (successFN)
              	successFN();
          }else{
              fail && fail.apply(null,[xhr.responseText]);
          }
      }
  };
  xhr.send(formdata);
}

upload.generateUptoken=function(){
	var app_key = "";
	var app_secret = "";
	
}
