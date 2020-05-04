
function SecretModel() {
    // AES 加密，AES-128, AES-192, AES-256
	// AES-128: key和iv都是16个字节，16*8=128bit
	// AES-192: key和iv都是24个字节，24*8=128bit
	// AES-256: key和iv都是32个字节，32*8=128bit
	// iv 似乎一般都是纯数字
	// 例如：AES_encrypt_CBC('需要加密的内容', '1234567890123456', '1234567890654321');
    this.AES_CBC_encrypt=function (message, key, iv) {
        // utf8字符串—>WordArray对象，WordArray是一个保存32位整数的数组，相当于转成了二进制
        var keyHex = CryptoJS.enc.Utf8.parse(key); //
        var ivHex = CryptoJS.enc.Utf8.parse(iv);
        var messageHex = CryptoJS.enc.Utf8.parse(message);
        var encrypted = CryptoJS.AES.encrypt(messageHex, keyHex, {
            iv: ivHex,
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.Pkcs7
        });
        return encrypted.toString();// base64结果
        //return encrypted.ciphertext.toString();   // 二进制结果
    }
	// AES CBC模式解密
    this.AES_CBC_decrypt=function (messageBase64, key, iv) {
        // 如果加密后没有转成base64,那么先要转成base64再传入
        //let encryptedHexStr = CryptoJS.enc.Hex.parse(word);   // 从二进制文本转成二进制
        //messageBase64 = CryptoJS.enc.Base64.stringify(encryptedHexStr);    // 转成base64
        var keyHex = CryptoJS.enc.Utf8.parse(key);
        var ivHex = CryptoJS.enc.Utf8.parse(iv);
        var decrypt = CryptoJS.AES.decrypt(messageBase64, keyHex, {
            iv: ivHex,
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.Pkcs7
        });
        var decryptedStr = decrypt.toString(CryptoJS.enc.Utf8);
        return decryptedStr.toString();
    }
    // AES ECB模式加密，没有iv
    this.AES_ECB_encrypt=function (message, key) {
        // utf8字符串—>WordArray对象，WordArray是一个保存32位整数的数组，相当于转成了二进制
        var keyHex = CryptoJS.enc.Utf8.parse(key); //
        var messageHex = CryptoJS.enc.Utf8.parse(message);
        var encrypted = CryptoJS.AES.encrypt(messageHex, keyHex, {
            mode: CryptoJS.mode.ECB,
            padding: CryptoJS.pad.Pkcs7
        });
        return encrypted.toString();// base64结果
        //return encrypted.ciphertext.toString();   // 二进制结果
    }
	// AES ECB模式解密
    this.AES_ECB_decrypt=function (messageBase64, key) {
        // 如果加密后没有转成base64,那么先要转成base64再传入
        //let encryptedHexStr = CryptoJS.enc.Hex.parse(word);   // 从二进制文本转成二进制
        //messageBase64 = CryptoJS.enc.Base64.stringify(encryptedHexStr);    // 转成base64
        var keyHex = CryptoJS.enc.Utf8.parse(key);
        var decrypt = CryptoJS.AES.decrypt(messageBase64, keyHex, {
            mode: CryptoJS.mode.ECB,
            padding: CryptoJS.pad.Pkcs7
        });
        var decryptedStr = decrypt.toString(CryptoJS.enc.Utf8);
        return decryptedStr.toString();
    }
}


 